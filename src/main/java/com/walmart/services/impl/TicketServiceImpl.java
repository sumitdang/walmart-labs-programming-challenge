package com.walmart.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.walmart.pojo.db.BlockedSeat;
import com.walmart.pojo.db.Reservation;
import com.walmart.pojo.db.Venue;
import com.walmart.pojo.ui.Row;
import com.walmart.pojo.ui.Seat;
import com.walmart.pojo.ui.SeatHold;
import com.walmart.repository.BlockedSeatRepository;
import com.walmart.repository.ReservationRepository;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;
import com.walmart.services.TicketService;
import com.walmart.utils.Constants;

/**
 * Implementation class for Ticket Service
 * 
 * @author sumitdang
 *
 */
@Service
public class TicketServiceImpl implements TicketService {

	private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

	@Autowired
	private VenueRepository venueRepo;

	@Autowired
	private ReservationRepository reservationRepo;

	@Autowired
	private SeatService seatService;

	@Autowired
	private BlockedSeatRepository blockedSeatRepo;

	/**
	 * The number of seats in the venue that are neither held nor reserved
	 *
	 * @return the number of tickets available in the venue
	 */
	public int numSeatsAvailable() {
		Venue venue = venueRepo.getVenueById(1);

		Integer availableSeats = (venue.getRows() * venue.getColumns());
		Integer unavailableSeats = reservationRepo.getSeatCountForActiveReservations();

		if (unavailableSeats != null) {
			availableSeats -= unavailableSeats;
		}
		return availableSeats;
	}

	/**
	 * Find and hold the best available seats for a customer
	 *
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information
	 */
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {

		SeatHold seatHold = new SeatHold();
		if (numSeats > numSeatsAvailable()) {
			seatHold.setStatus(HttpStatus.NO_CONTENT);
		} else {
			Venue venue = venueRepo.getVenueById(1);
//			String seatingStart = "FRONT";
//			String seatingDirection = venue.getSeatingDirection();
//			String seatingStrategy = venue.getSeatingStrategy();
//
//			String algorithm = new StringBuilder().append(seatingStart).append("_").append(seatingDirection).append("_")
//					.append(seatingStrategy).toString();
//			log.info("Seating Algorithm : {}", algorithm);
			List<Row> seats = seatService.getSeats();

			List<Seat> bestSeats = seatService.getBestSeatsRecursive(seats, numSeats, new ArrayList<Seat>());
			seatHold = seatService.holdSeats(bestSeats, customerEmail);
		}
		return seatHold;
	}

	/**
	 * Method to confirm seats held for a specific customer
	 *
	 * @param seatHoldId
	 *            the seat hold identifier
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 */
	@Transactional
	public String reserveSeats(int seatHoldId, String customerEmail) {
		Reservation reservation = reservationRepo.getReservationById(seatHoldId);
		if (reservation == null) {
			return Constants.ERROR_RESERVATION_NOT_FOUND;
		} else if (Constants.RESERVATION_EXPIRED.equals(reservation.getStatus())) {
			return Constants.ERROR_RESERVATION_EXPIRED;
		} else if (StringUtils.isNotBlank(customerEmail) && !customerEmail.equals(reservation.getCustomerEmail())) {
			return Constants.ERROR_EMAIL_MISMATCHED;
		} else {
			reservation.setStatus(Constants.RESERVATION_CONFIRMED);
			reservation.setUpdatedDate(LocalDateTime.now());
			reservationRepo.saveAndFlush(reservation);
			List<Integer> reservationIds = new ArrayList<Integer>();
			reservationIds.add(seatHoldId);
			List<BlockedSeat> blockedSeats = blockedSeatRepo.getBlockedSeatsByReservationIds(reservationIds);
			for (BlockedSeat blockedSeat : blockedSeats) {
				blockedSeat.setStatus(Constants.SEAT_BOOKED);
			}
			blockedSeatRepo.saveAll(blockedSeats);

			return Constants.RESERVATION_LABEL + String.valueOf(seatHoldId);
		}
	}
}
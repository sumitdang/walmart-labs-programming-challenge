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
			String seatingStart = "FRONT";
			String seatingDirection = venue.getSeatingDirection();
			String seatingStrategy = venue.getSeatingStrategy();

			String algorithm = new StringBuilder().append(seatingStart).append("_").append(seatingDirection).append("_")
					.append(seatingStrategy).toString();
			log.info("Seating Algorithm : {}", algorithm);
			List<Row> seats = seatService.getSeats();

			List<Seat> bestSeats = getBestSeatsRecursive(seats, numSeats, new ArrayList<Seat>());
			seatHold = holdSeats(bestSeats, customerEmail);
			log.info("seatHold.getSeatHoldId() : {}", seatHold.getSeatHoldId());
		}
		return seatHold;
	}

	/**
	 * Recursive Method to look up best seats
	 * 
	 * @param seats
	 * @param numSeats
	 * @param temporaryHeldSeats
	 * @return
	 */
	public List<Seat> getBestSeatsRecursive(List<Row> seats, Integer numSeats, List<Seat> temporaryHeldSeats) {
		for (Row row : seats) {
			if (row.getAvailableSeats() >= numSeats) {
				List<Seat> bestSeats = getBestSeats(row, numSeats, temporaryHeldSeats);
				if (bestSeats.size() == numSeats) {
					temporaryHeldSeats.addAll(getBestSeats(row, numSeats, temporaryHeldSeats));
					return temporaryHeldSeats;
				}
			}
		}

		// Recursive call with half length if required number of seats are not
		// available in same row.
		getBestSeatsRecursive(seats, numSeats - (numSeats / 2), temporaryHeldSeats);
		getBestSeatsRecursive(seats, numSeats / 2, temporaryHeldSeats);

		return temporaryHeldSeats;
	}

	/**
	 * Helper Method to look for consecutive seats in a row.
	 * 
	 * @param row
	 * @param numSeats
	 * @return
	 */
	public List<Seat> getBestSeats(Row row, Integer numSeats, List<Seat> temporaryHeldSeats) {
		String rowView = row.getView();
		String[] rowSeats = StringUtils.normalizeSpace(rowView).split(StringUtils.SPACE);

		List<Seat> consecutiveSeats = new ArrayList<Seat>();
		List<Seat> bestSeats = new ArrayList<Seat>();

		Integer column = 0;
		for (String status : rowSeats) {
			++column;
			Seat currentSeat = new Seat(row.getRowNumber(), column);
			if (Constants.SEAT_OPEN.equalsIgnoreCase(status) && (temporaryHeldSeats == null
					|| (temporaryHeldSeats != null && !temporaryHeldSeats.contains(currentSeat)))) {
				consecutiveSeats.add(currentSeat);
				if (consecutiveSeats.size() == numSeats) {
					return consecutiveSeats;
				}
			}
		}
		if (bestSeats.size() < consecutiveSeats.size()) {
			bestSeats = consecutiveSeats;

		}
		return bestSeats;
	}

	/**
	 * Method to hold seats for a specific customer
	 * 
	 * @param seats
	 * @param customerEmail
	 * @return
	 */
	@Transactional
	SeatHold holdSeats(List<Seat> seats, String customerEmail) {
		SeatHold seatHold = new SeatHold();
		try {
			Reservation reservation = new Reservation();
			reservation.setCustomerEmail(customerEmail);
			reservation.setNumSeats(seats.size());
			reservation.setStatus(Constants.RESERVATION_HOLD);
			reservation.setCreatedDate(LocalDateTime.now());
			Reservation savedReservation = reservationRepo.saveAndFlush(reservation);

			List<BlockedSeat> blockedSeats = new ArrayList<BlockedSeat>();
			for (Seat seat : seats) {
				BlockedSeat blockedSeat = new BlockedSeat();
				String seatId = new StringBuilder().append(String.valueOf(seat.getRow())).append(Constants.HYPHEN)
						.append(seat.getColumn()).toString();
				blockedSeat.setSeatId(seatId);
				blockedSeat.setRowNum(seat.getRow());
				blockedSeat.setColumnNum(seat.getColumn());
				blockedSeat.setStatus(Constants.SEAT_HOLD);
				blockedSeat.setReservationId(savedReservation.getReservationId());
				blockedSeats.add(blockedSeat);
			}

			blockedSeatRepo.saveAll(blockedSeats);

			seatHold.setSeatHoldId(savedReservation.getReservationId());
			seatHold.setBlockedSeats(blockedSeats);
			seatHold.setStatus(HttpStatus.CREATED);
		} catch (Exception ex) {
			log.error("Seat Hold Exception : {}", ex.getMessage());
			seatHold.setStatus(HttpStatus.NO_CONTENT);
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
		if (Constants.RESERVATION_EXPIRED.equals(reservation.getStatus())) {
			return Constants.ERROR_EMAIL_MISMATCHED;
		} else if (!reservation.getCustomerEmail().equals(customerEmail)) {
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

			return String.valueOf(seatHoldId);
		}
	}
}
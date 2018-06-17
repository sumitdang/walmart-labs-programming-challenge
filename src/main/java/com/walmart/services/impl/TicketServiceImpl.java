package com.walmart.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.pojo.db.BlockedSeat;
import com.walmart.pojo.db.Reservation;
import com.walmart.pojo.db.Venue;
import com.walmart.pojo.ui.SeatHold;
import com.walmart.repository.ReservationRepository;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;
import com.walmart.services.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

	private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

	@Autowired
	private VenueRepository venueRepo;

	@Autowired
	private ReservationRepository reservationRepo;

	@Autowired
	private SeatService seatService;

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
		Venue venue = venueRepo.getVenueById(1);
		String seatingStart = "FRONT";
		String seatingDirection = venue.getSeatingDirection();
		String seatingStrategy = venue.getSeatingStrategy();

		String algorithm = new StringBuilder().append(seatingStart).append("_").append(seatingDirection).append("_")
				.append(seatingStrategy).toString();
		log.info("Seating Algorithm : {}", algorithm);
		List<String> seatMap = seatService.getSeatMap();

		StringBuilder seatString = new StringBuilder();
		for (Integer counter = 0; counter < numSeats; counter++) {
			seatString.append(" O ");
		}
		Reservation reservation = new Reservation();

		List<BlockedSeat> blockedSeats = new ArrayList<BlockedSeat>();
		switch (algorithm) {
		case "FRONT_LEFT_LINE":
			Integer row = 0;
			for (String seats : seatMap) {
				String[] split = seats.split(" ", -1);
				row++;
				if (StringUtils.contains(seats, seatString)) {
					Integer column = StringUtils.indexOf(seatString, seats);
					reservation.setCustomerEmail(customerEmail);
					reservation.setNumSeats(numSeats);
					reservation.setStatus("HOLD");
					reservation.setCreatedDate(LocalDateTime.now());
					Reservation savedReservation = reservationRepo.saveAndFlush(reservation);

				}
			}

			break;
		}

		return null;
	}

	/**
	 * Commit seats held for a specific customer
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
		reservation.setStatus("CONFIRMED");
		reservation.setUpdatedDate(LocalDateTime.now());
		reservationRepo.saveAndFlush(reservation);
		return "RES"+String.valueOf(seatHoldId);
	}
}
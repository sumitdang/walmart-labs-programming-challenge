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
import com.walmart.utils.Constants;

@Service
public class SeatServiceImpl implements SeatService {

	private static final Logger log = LoggerFactory.getLogger(SeatServiceImpl.class);

	@Autowired
	private VenueRepository venueRepo;

	@Autowired
	private BlockedSeatRepository blockedSeatRepo;

	@Autowired
	private ReservationRepository reservationRepo;

	/**
	 * This method will give full seat status at any given point in time, in human
	 * readable format. O = Available X = Blocked/Reserved
	 *
	 * @return List<Seat>
	 */
	public List<Row> getSeats() {
		List<Row> seatsMap = new ArrayList<Row>();
		Venue venue = venueRepo.getVenueById(1);
		List<BlockedSeat> blockedSeats = blockedSeatRepo.getAllBlockedSeats();

		Boolean blockedSeatsFlag = (blockedSeats != null && !blockedSeats.isEmpty());

		Integer totalRows = venue.getRows();
		Integer totalColumns = venue.getColumns();
		Integer availableCount;
		StringBuilder sb = null;
		for (Integer row = 1; row <= totalRows; row++) {
			availableCount = 0;
			sb = new StringBuilder();

			for (Integer column = 1; column <= totalColumns; column++) {
				BlockedSeat seat = findMatch(blockedSeats, row, column);
				sb.append(StringUtils.SPACE);
				if (blockedSeatsFlag && seat != null) {
					if (Constants.SEAT_HOLD.equalsIgnoreCase(seat.getStatus())) {
						sb.append(Constants.SEAT_HOLD);
					} else {
						sb.append(Constants.SEAT_BOOKED);
					}
				} else {
					sb.append(Constants.SEAT_OPEN);
					availableCount++;
				}
				sb.append(StringUtils.SPACE);
			}
			Row rowStatus = new Row();
			rowStatus.setRowNumber(row);
			rowStatus.setAvailableSeats(availableCount);
			rowStatus.setView(sb.toString());
			seatsMap.add(rowStatus);
		}

		return seatsMap;
	}

	/**
	 * Private Helper Method to check if blockedSeats List contains current seat. This
	 * method can qualify to be a static utility method.
	 * 
	 * @param blockedSeats
	 * @param row
	 * @param column
	 * @return
	 */
	private BlockedSeat findMatch(List<BlockedSeat> blockedSeats, Integer row, Integer column) {
		for (BlockedSeat blockedSeat : blockedSeats) {
			if (blockedSeat.getRowNum().intValue() == row.intValue()
					&& blockedSeat.getColumnNum().intValue() == column.intValue())
				return blockedSeat;
		}
		return null;
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
	 * Private Helper Method to look for consecutive seats in a row.
	 * 
	 * @param row
	 * @param numSeats
	 * @return
	 */
	private List<Seat> getBestSeats(Row row, Integer numSeats, List<Seat> temporaryHeldSeats) {
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
			} else {
				consecutiveSeats.removeAll(consecutiveSeats);
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
	public SeatHold holdSeats(List<Seat> seats, String customerEmail) {
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

}
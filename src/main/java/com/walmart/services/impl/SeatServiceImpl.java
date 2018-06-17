package com.walmart.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.pojo.db.BlockedSeat;
import com.walmart.pojo.db.Venue;
import com.walmart.repository.BlockedSeatRepository;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;

@Service
public class SeatServiceImpl implements SeatService {

	@Autowired
	private VenueRepository venueRepo;

	@Autowired
	private BlockedSeatRepository blockedSeatRepo;

	/**
	 * Details of every seat for an eventId
	 *
	 * @return List<Seat>
	 */
	public List<String> getSeatMap() {
		List<String> seatMap = new ArrayList<String>();
		Venue venue = venueRepo.getVenueById(1);
		List<BlockedSeat> blockedSeats = blockedSeatRepo.getAllBlockedSeats();

		Boolean blockedSeatsFlag = (blockedSeats != null && !blockedSeats.isEmpty());

		Integer totalRows = venue.getRows();
		Integer totalColumns = venue.getColumns();
		StringBuilder sb = null;
		for (Integer row = 1; row <= totalRows; row++) {
			sb = new StringBuilder();
			for (Integer column = 1; column <= totalColumns; column++) {
				if (blockedSeatsFlag && contains(blockedSeats, row, column)) {
					sb.append(" X ");
				} else {
					sb.append(" O ");
				}
			}
			seatMap.add(sb.toString());
		}

		return seatMap;
	}

	/**
	 * Method to check if blockedSeat List contains current seat.
	 * 
	 * @param blockedSeats
	 * @param row
	 * @param column
	 * @return
	 */
	public Boolean contains(List<BlockedSeat> blockedSeats, Integer row, Integer column) {
		for (BlockedSeat blockedSeat : blockedSeats) {
			if (blockedSeat.getRowNum().intValue() == row.intValue()
					&& blockedSeat.getColumnNum().intValue() == column.intValue())
				return true;
		}
		return false;
	}

}
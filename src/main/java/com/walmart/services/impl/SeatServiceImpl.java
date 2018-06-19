package com.walmart.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.pojo.db.BlockedSeat;
import com.walmart.pojo.db.Venue;
import com.walmart.pojo.ui.Row;
import com.walmart.repository.BlockedSeatRepository;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;
import com.walmart.utils.Constants;

@Service
public class SeatServiceImpl implements SeatService {

	@Autowired
	private VenueRepository venueRepo;

	@Autowired
	private BlockedSeatRepository blockedSeatRepo;

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
	 * Helper Method to check if blockedSeats List contains current seat. This
	 * method can qualify to be a static utility method.
	 * 
	 * @param blockedSeats
	 * @param row
	 * @param column
	 * @return
	 */
	public BlockedSeat findMatch(List<BlockedSeat> blockedSeats, Integer row, Integer column) {
		for (BlockedSeat blockedSeat : blockedSeats) {
			if (blockedSeat.getRowNum().intValue() == row.intValue()
					&& blockedSeat.getColumnNum().intValue() == column.intValue())
				return blockedSeat;
		}
		return null;
	}

}
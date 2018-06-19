package com.walmart.pojo.ui;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.walmart.pojo.db.BlockedSeat;

public class SeatHold {

	private Integer seatHoldId;
	private List<BlockedSeat> blockedSeats;
	private HttpStatus status;

	public Integer getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(Integer seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	public List<BlockedSeat> getBlockedSeats() {
		return blockedSeats;
	}

	public void setBlockedSeats(List<BlockedSeat> blockedSeats) {
		this.blockedSeats = blockedSeats;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

}

package com.walmart.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.walmart.pojo.ui.Row;
import com.walmart.pojo.ui.Seat;
import com.walmart.pojo.ui.SeatHold;

@Service
public interface SeatService {
	/**
	 * Details of every seat for an eventId
	 *
	 * @return List<Seat>
	 */
	public List<Row> getSeats();

	/**
	 * Recursive Method to look up best seats
	 * 
	 * @param seats
	 * @param numSeats
	 * @param temporaryHeldSeats
	 * @return
	 */
	public List<Seat> getBestSeatsRecursive(List<Row> seats, Integer numSeats, List<Seat> temporaryHeldSeats);

	/**
	 * Method to hold seats for a specific customer
	 * 
	 * @param seats
	 * @param customerEmail
	 * @return
	 */
	SeatHold holdSeats(List<Seat> seats, String customerEmail);

}
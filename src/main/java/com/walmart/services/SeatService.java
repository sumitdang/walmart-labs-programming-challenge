package com.walmart.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.walmart.pojo.ui.Row;

@Service
public interface SeatService {
	/**
	 * Details of every seat for an eventId
	 *
	 * @return List<Seat>
	 */
	public List<Row> getSeats();

}
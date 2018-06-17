package com.walmart.services;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface SeatService {
	/**
	 * Details of every seat for an eventId
	 *
	 * @return List<Seat>
	 */
	List<String> getSeatMap();

}
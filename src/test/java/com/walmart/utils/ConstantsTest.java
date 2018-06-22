package com.walmart.utils;

import org.springframework.stereotype.Service;

@Service
public class ConstantsTest {

	// Seat Status
	public static final String SEAT_OPEN = "O";
	public static final String SEAT_HOLD = "H";
	public static final String SEAT_BOOKED = "X";

	// Reservation Status
	public static final String RESERVATION_EXPIRED = "EXPIRED";
	public static final String RESERVATION_HOLD = "HOLD";
	public static final String RESERVATION_CONFIRMED = "CONFIRMED";

	public static final String HYPHEN = "-";

	// Error Message
	public static final String ERROR_RESERVATION_NOT_FOUND = "NOT_FOUND - Your reservation is not found in our database.";
	public static final String ERROR_RESERVATION_EXPIRED = "EXPIRED - Your reservation is timed-out, please book again.";
	public static final String ERROR_EMAIL_MISMATCHED = "ERROR - Hold and Confirm request customer email don't match.";

	// Reservation Constants
	public static final String RESERVATION_LABEL ="RESERVATION# ";
}
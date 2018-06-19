package com.walmart.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.walmart.pojo.db.BlockedSeat;
import com.walmart.pojo.db.Reservation;
import com.walmart.repository.BlockedSeatRepository;
import com.walmart.repository.ReservationRepository;
import com.walmart.utils.Constants;

/**
 * This service class needs to run in Transaction mode (either update all tables
 * or none) as it is changing state for reservations from HOLD to EXPIRED, and
 * also deleting seats associated with those reservations from blocked-seats
 * table.
 * 
 * If we need to track blocked seats for reporting purpose we can create a
 * isBlocked flag to soft delete seats. For the sake of simplicity, I am hard
 * deleting these seats for now.
 * 
 * @author sumitdang
 *
 */
@Service
@Transactional
public class SchedulerService {

	private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

	@Autowired
	private ReservationRepository reservationRepo;

	@Autowired
	private BlockedSeatRepository blockedSeatRepo;

	// Scheduled for every minute
	@Scheduled(cron = "0 */2 * ? * *", zone = "America/New_York")
	public void cleanupReservations() {
		log.info("scheduler started");
		List<Reservation> holdReservations = reservationRepo.getHoldStatusReservations();
		Integer reservationsToExpire = (holdReservations != null && !holdReservations.isEmpty())
				? holdReservations.size()
				: 0;

		// There should be no in-memory variables or database calls if there are zero
		// reservations on HOLD.
		if (reservationsToExpire > 0) {
			List<Integer> reservationIds = new ArrayList<Integer>();
			for (Reservation reservation : holdReservations) {
				reservation.setStatus(Constants.RESERVATION_EXPIRED);
				reservation.setUpdatedDate(LocalDateTime.now());
				reservationIds.add(reservation.getReservationId());
			}
			reservationRepo.saveAll(holdReservations);
			if (reservationIds != null && !reservationIds.isEmpty()) {
				List<BlockedSeat> blockedSeats = blockedSeatRepo.getBlockedSeatsByReservationIds(reservationIds);
				blockedSeatRepo.deleteAll(blockedSeats);
			}
		}
		log.info("{} reservations are set to EXPIRED state ! ", reservationsToExpire);
		log.info("scheduler ended");
	}

}

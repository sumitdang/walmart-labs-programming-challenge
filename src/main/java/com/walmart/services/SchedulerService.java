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

@Service
@Transactional
public class SchedulerService {

	private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

	@Autowired
	private ReservationRepository reservationRepo;

	@Autowired
	private BlockedSeatRepository blockedSeatRepo;

	// Scheduled for every minute
	@Scheduled(cron = "0 * * ? * *", zone = "America/New_York")
	public void cleanupReservations() {
		log.info("scheduler started");
		List<Integer> reservationIds = new ArrayList<Integer>();
		List<Reservation> holdReservations = reservationRepo.getHoldStatusReservations();
		for (Reservation reservation : holdReservations) {
			reservation.setStatus("EXPIRED");
			reservation.setUpdatedDate(LocalDateTime.now());
			reservationIds.add(reservation.getReservationId());
		}
		reservationRepo.saveAll(holdReservations);

		List<BlockedSeat> blockedSeats = blockedSeatRepo.getBlockedSeatsByReservationIds(reservationIds);
		blockedSeatRepo.deleteAll(blockedSeats);
		log.info("scheduler ended");
	}

}

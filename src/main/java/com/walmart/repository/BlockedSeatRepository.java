package com.walmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.walmart.pojo.db.BlockedSeat;

public interface BlockedSeatRepository extends JpaRepository<BlockedSeat, Integer> {

	@Query(value = "select b.* from public.blocked_seat b", nativeQuery = true)
	public List<BlockedSeat> getAllBlockedSeats();
	
	@Query(value = "select b.* from public.blocked_seat b where b.reservation_id in (:reservationIds)", nativeQuery = true)
	public List<BlockedSeat> getBlockedSeatsByReservationIds(@Param("reservationIds") List<Integer> reservationIds);

}

package com.walmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.walmart.pojo.db.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	@Query(value = "select sum(r.num_seats) from public.reservation r where r.status IN ('HOLD', 'CONFIRMED')", nativeQuery = true)
	public Integer getSeatCountForActiveReservations();

	@Query(value = "select r.* from public.reservation r where r.status = 'HOLD' and r.created_date < NOW() - INTERVAL '2 minutes'; ", nativeQuery = true)
	public List<Reservation> getHoldStatusReservations();
	
	@Query(value = "select r.* from public.reservation r where r.reservation_id = :reservationId", nativeQuery = true)
	public Reservation getReservationById(@Param("reservationId") Integer reservationId);

}

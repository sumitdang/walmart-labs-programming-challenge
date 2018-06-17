package com.walmart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.walmart.pojo.db.Venue;

public interface VenueRepository extends JpaRepository<Venue, Integer> {

	@Query(value = "select v.* from public.venue v", nativeQuery = true)
	public List<Venue> getAllVenues();

	@Query(value = "select v.* from public.venue v where v.venue_id = :venueId", nativeQuery = true)
	public Venue getVenueById(@Param("venueId") Integer venueId);
}

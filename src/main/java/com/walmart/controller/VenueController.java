package com.walmart.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.pojo.db.Venue;
import com.walmart.pojo.ui.SeatHold;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;
import com.walmart.services.TicketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <pre>
 * This controller holds the logic for Venue and Event details with an
 * assumption that we have only 1 venue. 
 * 
 * If in future business team wants to extend this application, we can split this controller into 
 * VenueController and EventController.
 * 
 * Database is already designed to hold multiple Venues.
 * </pre>
 * 
 * 
 * @author sumitdang
 *
 */
@RestController
@RequestMapping(value = "/walmart/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Events", description = "Endpoint pertaining to /Events")
public class VenueController {

	private static final Logger log = LoggerFactory.getLogger(VenueController.class);

	@Autowired
	private VenueRepository venueRepo;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private SeatService seatService;

	/**
	 * This method is used to get available seat count
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/venue/seats/availablecount/")
	@ApiOperation(value = "Get available seat count")
	public ResponseEntity<Integer> getAllSeatDetailsByEventId() throws Exception {
		log.info("GET /venue/seats/availablecount/");
		Integer count = ticketService.numSeatsAvailable();
		return new ResponseEntity<Integer>(count, HttpStatus.OK);
	}

	/**
	 * This method is used to Hold seats
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/venue/seats/hold/{numSeats}/")
	@ApiOperation(value = "Hold seats")
	public ResponseEntity<SeatHold> holdSeats(
			@ApiParam(value = "numSeats", required = true) @PathVariable Integer numSeats,
			@RequestParam(value = "customerEmail", required = true) String customerEmail) throws Exception {
		log.info("POST /venue/seats/hold/{}/?customerEmail=", numSeats, customerEmail);
		SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);

		return new ResponseEntity<SeatHold>(seatHold, HttpStatus.OK);
	}

	/**
	 * This method is used to Book seats
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/venue/seats/book/{seatHoldId}/")
	@ApiOperation(value = "Book seats")
	public ResponseEntity<String> bookSeats(
			@ApiParam(value = "seatHoldId", required = true) @PathVariable Integer seatHoldId,
			@RequestParam(value = "customerEmail", required = true) String customerEmail) throws Exception {
		log.info("POST /venue/seats/book/{}/?customerEmail=", seatHoldId, customerEmail);
		String reservationId = ticketService.reserveSeats(seatHoldId, customerEmail);

		return new ResponseEntity<String>(reservationId, HttpStatus.OK);
	}

	/**
	 * This method is used to Get Venue details
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/venue/")
	@ApiOperation(value = "Get venue details")
	public ResponseEntity<List<Venue>> getAllVenues() throws Exception {
		log.info("GET /venue/");
		List<Venue> venues = venueRepo.getAllVenues();
		return new ResponseEntity<List<Venue>>(venues, HttpStatus.OK);
	}

	/**
	 * This method is used to get seat map.
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/venue/seats/map/")
	@ApiOperation(value = "Get seat map")
	public ResponseEntity<List<String>> getSeatMap() throws Exception {
		log.info("GET /venue/seats/map/");
		List<String> seatMap = seatService.getSeatMap();

		return new ResponseEntity<List<String>>(seatMap, HttpStatus.OK);
	}
}

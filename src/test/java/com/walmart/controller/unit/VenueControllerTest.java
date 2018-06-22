package com.walmart.controller.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.walmart.controller.VenueController;
import com.walmart.pojo.db.Venue;
import com.walmart.pojo.ui.Row;
import com.walmart.pojo.ui.SeatHold;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;
import com.walmart.services.TicketService;
import com.walmart.utils.ConstantsTest;

@RunWith(MockitoJUnitRunner.class)
public class VenueControllerTest {
	@InjectMocks
	VenueController venueControllerMock;

	@Mock
	TicketService ticketServiceMock;

	@Mock
	VenueRepository venueRepoMock;

	@Mock
	SeatService seatServiceMock;

	Integer availableCountMock;
	Integer numSeatsMock;
	String customerEmailMock;
	SeatHold seatHoldMock200;
	SeatHold seatHoldMock204;
	Integer seatHoldIdMock;
	List<Venue> venueListMock;
	List<Row> seatMapMock;

	@Before
	public void init() {
		availableCountMock = 10;
		numSeatsMock = 5;
		customerEmailMock = "email@walmart.com";
		seatHoldMock200 = new SeatHold();
		seatHoldMock200.setStatus(HttpStatus.OK);
		seatHoldMock204 = new SeatHold();
		seatHoldMock204.setStatus(HttpStatus.NO_CONTENT);
		seatHoldIdMock = 1;
		venueListMock = new ArrayList<Venue>();
		venueListMock.add(mock(Venue.class));

		seatMapMock = new ArrayList<Row>();
		seatMapMock.add(mock(Row.class));

	}

	@Test
	public void testGetSeatAvailableCount() throws Exception {

		when(ticketServiceMock.numSeatsAvailable()).thenReturn(availableCountMock);

		ResponseEntity<Integer> response = venueControllerMock.getSeatAvailableCount();
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody());
		assertEquals(response.getBody(), availableCountMock);
		System.out.println("UNIT_TEST : VenueControllerTest -> testGetSeatAvailableCount()");
	}

	@Test
	public void testHoldSeats() throws Exception {

		when(ticketServiceMock.findAndHoldSeats(numSeatsMock, customerEmailMock)).thenReturn(seatHoldMock204);
		seatHoldMock204.setStatus(HttpStatus.NO_CONTENT);

		ResponseEntity<SeatHold> response204 = venueControllerMock.holdSeats(numSeatsMock, customerEmailMock);
		assertEquals(response204.getStatusCode(), HttpStatus.NO_CONTENT);
		assertNull(response204.getBody());

		when(ticketServiceMock.findAndHoldSeats(numSeatsMock, customerEmailMock)).thenReturn(seatHoldMock200);
		ResponseEntity<SeatHold> response200 = venueControllerMock.holdSeats(numSeatsMock, customerEmailMock);
		assertEquals(response200.getStatusCode(), HttpStatus.OK);
		assertNotNull(response200.getBody());
		assertEquals(response200.getBody(), seatHoldMock200);

		System.out.println("UNIT_TEST : VenueControllerTest -> findAndHoldSeats()");
	}

	@Test
	public void testReserveSeats() throws Exception {
		when(ticketServiceMock.reserveSeats(seatHoldIdMock, customerEmailMock))
				.thenReturn(ConstantsTest.ERROR_RESERVATION_NOT_FOUND);
		ResponseEntity<String> response = venueControllerMock.reserveSeats(seatHoldIdMock, customerEmailMock);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
		assertNotNull(response.getBody());
		assertEquals(response.getBody(), ConstantsTest.ERROR_RESERVATION_NOT_FOUND);

		when(ticketServiceMock.reserveSeats(seatHoldIdMock, customerEmailMock))
				.thenReturn(ConstantsTest.ERROR_RESERVATION_EXPIRED);
		response = venueControllerMock.reserveSeats(seatHoldIdMock, customerEmailMock);
		assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
		assertNotNull(response.getBody());
		assertEquals(response.getBody(), ConstantsTest.ERROR_RESERVATION_EXPIRED);

		when(ticketServiceMock.reserveSeats(seatHoldIdMock, customerEmailMock))
				.thenReturn(ConstantsTest.ERROR_EMAIL_MISMATCHED);
		response = venueControllerMock.reserveSeats(seatHoldIdMock, customerEmailMock);
		assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
		assertNotNull(response.getBody());
		assertEquals(response.getBody(), ConstantsTest.ERROR_EMAIL_MISMATCHED);

		when(ticketServiceMock.reserveSeats(seatHoldIdMock, customerEmailMock))
				.thenReturn(ConstantsTest.RESERVATION_LABEL);
		response = venueControllerMock.reserveSeats(seatHoldIdMock, customerEmailMock);
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody());
		assertEquals(response.getBody(), ConstantsTest.RESERVATION_LABEL);

		System.out.println("UNIT_TEST : VenueControllerTest -> testReserveSeats()");
	}

	@Test
	public void testGetVenueDetails() throws Exception {
		when(venueRepoMock.getVenueDetails()).thenReturn(venueListMock);
		ResponseEntity<List<Venue>> response = venueControllerMock.getVenueDetails();
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody());

		System.out.println("UNIT_TEST : VenueControllerTest -> testGetVenueDetails()");
	}

	@Test
	public void testGetSeatView() throws Exception {
		when(seatServiceMock.getSeats()).thenReturn(seatMapMock);
		ResponseEntity<List<String>> response = venueControllerMock.getSeatView();
		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody());

		System.out.println("UNIT_TEST : VenueControllerTest -> testGetSeatView()");
	}

}

package com.walmart.services.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.walmart.pojo.db.BlockedSeat;
import com.walmart.pojo.db.Reservation;
import com.walmart.pojo.db.Venue;
import com.walmart.pojo.ui.Row;
import com.walmart.pojo.ui.Seat;
import com.walmart.pojo.ui.SeatHold;
import com.walmart.repository.BlockedSeatRepository;
import com.walmart.repository.ReservationRepository;
import com.walmart.repository.VenueRepository;
import com.walmart.services.SeatService;
import com.walmart.services.impl.TicketServiceImpl;
import com.walmart.utils.ConstantsTest;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {
	@InjectMocks
	@Spy
	TicketServiceImpl ticketServiceImplMock;

	@Mock
	VenueRepository venueRepoMock;

	@Mock
	ReservationRepository reservationRepoMock;

	@Mock
	BlockedSeatRepository blockedSeatRepoMock;

	@Mock
	SeatService seatServiceMock;

	Integer unAvailableCountMock;
	Integer totalSeatCountMock;
	Venue venueMock;
	List<Row> seatMapMock;
	List<Seat> seatsMock;

	SeatHold seatHoldMock;
	Reservation reservationMock;
	String customerEmailMock;
	String mismatchCustomerEmailMock;

	List<Integer> reservationIds;
	List<BlockedSeat> blockedSeatsBlank;
	List<BlockedSeat> blockedSeats;

	Integer reservationNumberMock;
	Integer seatIdMock;
	String badMessage;
	

	@Before
	public void init() {
		unAvailableCountMock = 10;
		totalSeatCountMock = 100;
		venueMock = new Venue();
		venueMock.setColumns(10);
		venueMock.setRows(10);
		seatMapMock = new ArrayList<Row>();
		seatMapMock.add(mock(Row.class));

		seatsMock = new ArrayList<Seat>();
		seatsMock.add(mock(Seat.class));

		seatHoldMock = mock(SeatHold.class);
		reservationMock = mock(Reservation.class);
		reservationMock.setStatus(ConstantsTest.RESERVATION_EXPIRED);
		customerEmailMock = "email@walmart.com";
		mismatchCustomerEmailMock = "email2@walmart.com";

		reservationIds = new ArrayList<Integer>();
		reservationIds.add(1);

		blockedSeatsBlank = new ArrayList<BlockedSeat>();
		blockedSeats = new ArrayList<BlockedSeat>();
		blockedSeats.add(mock(BlockedSeat.class));
		blockedSeats.add(mock(BlockedSeat.class));
		seatIdMock = reservationNumberMock = 1;

		badMessage = "Bad Message";

	}

	@Test
	public void testNumSeatsAvailable() {
		// Test case when number of blocked seats = null
		when(venueRepoMock.getVenueById(1)).thenReturn(venueMock);
		when(reservationRepoMock.getSeatCountForActiveReservations()).thenReturn(null);
		Integer response = ticketServiceImplMock.numSeatsAvailable();
		assertNotNull(response);
		assertEquals(response, totalSeatCountMock);

		// Test case when number of blocked seats > 0
		when(reservationRepoMock.getSeatCountForActiveReservations()).thenReturn(unAvailableCountMock);
		response = ticketServiceImplMock.numSeatsAvailable();
		assertNotNull(response);
		Integer expectedValue = (totalSeatCountMock - unAvailableCountMock);
		assertEquals(response, expectedValue);

		System.out.println("UNIT_TEST : TicketServiceImplTest -> testNumSeatsAvailable()");

	}

	@Test
	public void testFindAndHoldSeats() {
		when(venueRepoMock.getVenueById(ArgumentMatchers.anyInt())).thenReturn(venueMock);
		when(reservationRepoMock.getSeatCountForActiveReservations()).thenReturn(null);
		when(ticketServiceImplMock.numSeatsAvailable()).thenReturn(unAvailableCountMock);

		SeatHold response = ticketServiceImplMock.findAndHoldSeats(ArgumentMatchers.anyInt(),
				ArgumentMatchers.anyString());
		assertNull(response);
		
		when(seatServiceMock.getSeats()).thenReturn(seatMapMock);
		when(seatServiceMock.getBestSeatsRecursive(ArgumentMatchers.anyList(), ArgumentMatchers.anyInt(),
				ArgumentMatchers.anyList())).thenReturn(seatsMock);
		when(seatServiceMock.holdSeats(ArgumentMatchers.anyList(), ArgumentMatchers.anyString()))
				.thenReturn(seatHoldMock);

		response = ticketServiceImplMock.findAndHoldSeats(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
		assertNotNull(response);

		System.out.println("UNIT_TEST : TicketServiceImplTest -> testFindAndHoldSeats()");

	}

	@Test
	public void testReserveSeats() {
		when(reservationRepoMock.getReservationById(ArgumentMatchers.anyInt())).thenReturn(null);
		String response = ticketServiceImplMock.reserveSeats(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
		assertNotNull(response);
		assertEquals(response, ConstantsTest.ERROR_RESERVATION_NOT_FOUND);

		when(reservationRepoMock.getReservationById(ArgumentMatchers.anyInt())).thenReturn(reservationMock);
		when(reservationMock.getStatus()).thenReturn(ConstantsTest.RESERVATION_EXPIRED);
		response = ticketServiceImplMock.reserveSeats(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
		assertNotNull(response);
		assertEquals(response, ConstantsTest.ERROR_RESERVATION_EXPIRED);

		when(reservationMock.getStatus()).thenReturn(badMessage);
		when(reservationMock.getCustomerEmail()).thenReturn(mismatchCustomerEmailMock);
		response = ticketServiceImplMock.reserveSeats(1, customerEmailMock);
		assertNotNull(response);
		assertEquals(response, ConstantsTest.ERROR_EMAIL_MISMATCHED);

		when(reservationMock.getCustomerEmail()).thenReturn(customerEmailMock);
		when(blockedSeatRepoMock.getBlockedSeatsByReservationIds(reservationIds)).thenReturn(blockedSeatsBlank);
		response = ticketServiceImplMock.reserveSeats(1, customerEmailMock);
		assertNotNull(response);
		assertEquals(StringUtils.substringBefore(response, StringUtils.SPACE),
				StringUtils.substringBefore(ConstantsTest.RESERVATION_LABEL, StringUtils.SPACE));

		when(blockedSeatRepoMock.getBlockedSeatsByReservationIds(reservationIds)).thenReturn(blockedSeats);
		response = ticketServiceImplMock.reserveSeats(1, customerEmailMock);
		assertNotNull(response);
		assertEquals(StringUtils.substringBefore(response, StringUtils.SPACE),
				StringUtils.substringBefore(ConstantsTest.RESERVATION_LABEL, StringUtils.SPACE));

		System.out.println("UNIT_TEST : TicketServiceImplTest -> testReserveSeats()");

	}

}

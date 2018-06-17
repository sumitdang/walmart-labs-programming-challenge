CREATE TABLE venue (venue_id serial PRIMARY KEY, name VARCHAR(50), rows Integer, columns Integer, seating_direction VARCHAR(10) DEFAULT 'LEFT', seating_strategy VARCHAR(10) DEFAULT 'LINE');
INSERT INTO venue (name, rows, columns) values ('Walmart Theatre', 10,10);

CREATE TABLE reservation (reservation_id serial PRIMARY KEY, num_seats Integer, customer_email VARCHAR(50), status VARCHAR(10), 
created_date TIMESTAMP WITH TIME ZONE NOT NULL, updated_date TIMESTAMP WITH TIME ZONE);

CREATE TABLE blocked_seat (seat_id serial PRIMARY KEY, row_num Integer, column_num Integer, reservation_id INTEGER REFERENCES reservation(reservation_id));


COMMENT ON TABLE venue IS 'This table contains attributes for a venue';
COMMENT ON column venue.venue_id IS 'Primary Key for venue table';
COMMENT ON column venue.name IS 'Venue name';
COMMENT ON column venue.rows IS 'Total Rows in this venue';
COMMENT ON column venue.columns IS 'Total Columns in this venue';
COMMENT ON column venue.seating_direction IS 'Admin attribute to decide seating direction for this venue, possible values - Left, Right, Center';
COMMENT ON column venue.seating_strategy IS 'Admin attribute to decide seating strategy for this venue, possible values - Line, Block';

COMMENT ON TABLE reservation IS 'This table will hold all details for an reservation';
COMMENT ON column reservation.reservation_id IS 'Primary Key for reservation table';
COMMENT ON column reservation.num_seats IS 'Number of seats held/booked in this reservation';
COMMENT ON column reservation.customer_email IS 'User email used to hold/book this reservation';
COMMENT ON column reservation.status IS 'Status of this reservation, possible values - Hold, Confirmed';
COMMENT ON column reservation.created_date IS 'Created Timestamp of this reservation';
COMMENT ON column reservation.updated_date IS 'Updated Timestamp of this reservation';

COMMENT ON TABLE blocked_seat IS 'This table will hold all details blocked seats';
COMMENT ON column blocked_seat.seat_id IS 'Primary Key for blockedSeat table';
COMMENT ON column blocked_seat.row_num IS 'Seat row number';
COMMENT ON column blocked_seat.column_num IS 'Seat column number';
COMMENT ON column blocked_seat.reservation_id IS 'Foreign Key referencing reservation table, to map seat to reservation';

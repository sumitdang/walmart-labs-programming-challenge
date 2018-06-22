Timestamp 06/21/2018 00:05: This is the final commit to this project and represents my final version for my submission.

# walmart-labs-programming-challenge

## Walmart Labs Ticket Service
Implementation of an artificially-intelligent seat reservation system that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.

### Assumptions
1. The venue for which this ticket service facilitates has a seating arrangement reflecting close to that of the spec. Specifically, the seating arrangement is represented as N by M seats only.
Other seating arrangements outside N X M pattern are outside of scope of this submission.
2. As there is no criteria for the "best seating arrangement" is defined in the spec for holding or reserving seats. 
3. TicketService interface had primitive data types in method signature. I have added ResponseEntity classes in Controller to return HttpStatus codes in API response.
4. A customer hold and reserve any number of seats any number of times.
5. A seat hold's expiration is determined by the duration of the current time and seat hold creation time exceeding the expiration time threshold (in seconds). Threshold is configurable in source code.

#### Seat selection algorithm:

Algorithm Assumptions:
Given problem qualifies for Greedy algorithm because of following real-life behaviors.
1. As a customer, I would like to have the best seat which is close to the stage.
2. As a customer reserving for a big group, I would like to get maximum seats together and in case of group split I would like to have balanced/half split and look for consecutive seats again.
3. As the venue owner, I would like to reserve/sell maximum number of seats.
4. As the venue owner, I would like to keep my customers happy by meeting #1 and #2 above.

Based on the assumptions above I came up with a **Greedy seat selection algorithm** for this system as follows:
* System will start from the front row and assign all seats in the same row if possible. When seat is not available in the current row system move to the row behind to look for required numbers of seats in the same row.
* If after scanning all rows, seat selection in the same row is failed. System recursively looks for numSeats by splitting original number of seats in half, e.g. - say numSeats = 17 but there are no rows with 17 seats available, system will split 17 as 9,8 and then start looking for seats recursively. If 9 seats are again not available in same row, System will split and look for 5,4 and so on.
* Worst case scenario: Code can end up split input numSeats to 1,1,1,... lookup array and in that case our look up algorithm will end up reserving scattered seats.

##### Complexity of my algorithm:
* Best Case Scenario - O(1) : Match found in first row.
* Average case Scenario - O(log n) : Match found in any row, with or without split.
* Worst Case Scenario -  O(n^m) : This is when we end up splitting numSeats to 1,1,1,...

### Instructions

#### Software Requirements:
* Java 1.8 or above.
* Spring Tool Suite (STS) : I have used STS to come up with a SpringBoot Application, so for demo purpose STS is a preferred IDE.
* Postgres : Backend database for the application.
* Git : Git command line or Git Desktop both are acceptable to checkout code from github.
* Postman : Optional software to trigger API requests, serves same purpose as swagger.

#### Installation


Go to postgres database and create a new database walmart_db with default user (postgres).

Checkout codebase from master branch from github using following commands in github:

```
https://github.com/sumitdang/walmart-labs-programming-challenge.git
cd walmart-labs-programming-challenge
```

#### Building

Open STS, click File Menu -> Import... -> Maven -> Existing Maven project.
Select path of project checkout folder.
Click Finish.

Once project is imported, right click on Project folder -> Maven -> Update Project... -> Finish
This should download all required maven dependencies.

Once all errors are resolved, right click on Project folder -> Rus As -> Spring Boot App

Check if app is up and running by hitting following url on browser : http://localhost:8080/swagger-ui.html

#### Swagger URL
Once the application is up and running, you should be able to access API using swagger link : http://localhost:8080/swagger-ui.html

##### API End-points

* **GET /walmart/v1/venue/**: Returns the details for venue attributes.
* **GET /walmart/v1/venue/seats/availablecount/**: Returns available seats' count at any point in time.
* **POST /walmart/v1/venue/seats/hold/{numSeats}/**: End-point to hold given number of seats in the venue using algorithm above.
* **POST /walmart/v1/venue/seats/reserve/{seatHoldId}/**: End-point to reserve seats for a seatHoldId.
* **GET /walmart/v1/venue/seats/view/**: End-point to print visual map for all seats in the venue.
	O = Available,
	H = Hold,
	X = Reserved.

### Design Overview
System design aligns to a basic spring boot app and a request is served as follows:

1. System supports GET and POST APIs for now and is scalable to support PUT and DELETE in future. All API end-points are configured in VenueController.java
2. VenueController gets the request from the browser/postman and delegates to matching method for the provided URL pattern and request method.
3. Selected method can then call a Repository class directly to fetch results using JPA native queries. In case we need some processing on the database results before returning them back, method will call a Service class -> ServiceImpl class -> Repository call and business logic.
3. System extends Spring scheduler to automatically set held reservations to expired state and free up all blocked seats.
4. Ideally we should have a DAO layer in the system, but for the sake of simplicity DAO layer is intentionally skipped at this time.
5. System is designed to use Utility classes and Constants file where ever possible.

### Database Overview
System relies on 3 tables:
1. Venue : This table contains attributes for a venue
2. Reservation : Transactional table to hold all details for a reservation
3. Blocked_seats : Transactional table to hold all blocked seats at any given point in time.

### Exception Handling
1. Seat hold and confirm should be done using same email id, status code - 204.
2. Confirmation request on an Expired holdId returns a proper error message, status code - 404.
3. Look up on invalid holdId returns a proper error message, status code - 404.

### Future scalability options
1. Project is scalable to hold multiple venues. We can configure our endpoints to /venues/{venueId}/ in future.
2. We can add more seat selection algorithm like BACK_LEFT_BLOCK, LEFT_CENTER_LINE, as of now we have FRONT_LEFT_LINE pattern i.e., start seating from (front row - left to right - line seating arrangement).
3. Support Venue seating patterns outside N X M.
4. Add Reporting APIs to fetch Daily, Monthly and Annual booking percentage and pattern reports.
5. Add Billing component to reserveSeats functionality.
6. Add Email validator.
7. JWT implementation for Customer login and account security before returning API responses.
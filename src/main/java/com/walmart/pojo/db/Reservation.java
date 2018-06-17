package com.walmart.pojo.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.walmart.utilities.LocalDateTimeConverter;

@Entity
@Table(name = "reservation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reservation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "reservation_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reservationId;

	@Column(name = "num_seats")
	private Integer numSeats;

	@Column(name = "customer_email")
	private String customerEmail;

	@Column(name = "status")
	private String status;

	@Column(name = "created_date")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime createdDate;

	@Column(name = "updated_date")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime updatedDate;

	public Integer getReservationId() {
		return reservationId;
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}

	public Integer getNumSeats() {
		return numSeats;
	}

	public void setNumSeats(Integer numSeats) {
		this.numSeats = numSeats;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

}
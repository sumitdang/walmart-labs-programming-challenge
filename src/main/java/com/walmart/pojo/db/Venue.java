package com.walmart.pojo.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "venue")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Venue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "venue_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer venueId;

	@Column(name = "name")
	private String name;

	@Column(name = "rows")
	private Integer rows;

	@Column(name = "columns")
	private Integer columns;

	@Column(name = "seating_direction")
	private String seatingDirection;

	@Column(name = "seating_strategy")
	private String seatingStrategy;

	@Transient
	private Integer totalSeats;

	public Integer getVenueId() {
		return venueId;
	}

	public void setVenueId(Integer venueId) {
		this.venueId = venueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public String getSeatingDirection() {
		return seatingDirection;
	}

	public void setSeatingDirection(String seatingDirection) {
		this.seatingDirection = seatingDirection;
	}

	public String getSeatingStrategy() {
		return seatingStrategy;
	}

	public void setSeatingStrategy(String seatingStrategy) {
		this.seatingStrategy = seatingStrategy;
	}

	public Integer getTotalSeats() {
		return this.getRows() * this.getColumns();
	}

	// skipping - setTotalSeats() - as totalSeats is a derived values

}
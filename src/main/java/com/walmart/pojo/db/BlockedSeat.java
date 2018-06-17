package com.walmart.pojo.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "blocked_seat")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockedSeat implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "seat_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer seatId;

	@Column(name = "row_num")
	private Integer rowNum;

	@Column(name = "column_num")
	private Integer columnNum;

	@Column(name = "reservation_id")
	private Integer reservationId;

	public Integer getSeatId() {
		return seatId;
	}

	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Integer getColumnNum() {
		return columnNum;
	}

	public void setColumnNum(Integer columnNum) {
		this.columnNum = columnNum;
	}

	public Integer getReservationId() {
		return reservationId;
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && (this.getClass() == obj.getClass())) {
			BlockedSeat blockedSeat = (BlockedSeat) obj;
			if (this.getRowNum().intValue() == blockedSeat.getRowNum().intValue()
					&& this.getColumnNum().intValue() == blockedSeat.getColumnNum().intValue()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}

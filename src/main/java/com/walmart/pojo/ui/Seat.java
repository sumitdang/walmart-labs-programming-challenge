package com.walmart.pojo.ui;

public class Seat {

	private Integer row;
	private Integer column;

	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}

	public Seat() {
	}

	public Seat(Integer row, Integer column) {
		this.row = row;
		this.column = column;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && (this.getClass() == obj.getClass())) {
			Seat Seat = (Seat) obj;
			if (this.getRow().intValue() == Seat.getRow().intValue()
					&& this.getColumn().intValue() == Seat.getColumn().intValue()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

}

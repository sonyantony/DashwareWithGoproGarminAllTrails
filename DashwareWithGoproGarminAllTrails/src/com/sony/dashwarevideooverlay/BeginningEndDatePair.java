package com.sony.dashwarevideooverlay;
import java.util.Date;

public class BeginningEndDatePair {
	private Date beginningDate;
	private Date endate;

	public BeginningEndDatePair() {
	}

	public BeginningEndDatePair(Date beginningDate, Date endate) {
		super();
		this.beginningDate = beginningDate;
		this.endate = endate;
	}

	public Date getBeginningDate() {
		return beginningDate;
	}

	public void setBeginningDate(Date beginningDate) {
		this.beginningDate = beginningDate;
	}

	public Date getEndate() {
		return endate;
	}

	public void setEndate(Date endate) {
		this.endate = endate;
	}

}

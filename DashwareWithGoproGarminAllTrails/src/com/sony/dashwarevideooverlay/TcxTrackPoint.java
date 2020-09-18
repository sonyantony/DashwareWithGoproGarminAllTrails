package com.sony.dashwarevideooverlay;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class TcxTrackPoint  {

	private long milliSecsSinceBeginning;
	private Date dt;
	private double lat;
	private double lng;
	private double alt;
	private double dist;
	private long hr = 1234567890;
	private double speedMtrPerSec;
	private long cadence;
	private BeginningEndDatePair begEndDates;

	public TcxTrackPoint(long milliSecsSinceBeginning, Date dt, double lat, double lng, double alt, double dist,
			long hr, double speedMtrPerSec, long cadence, BeginningEndDatePair begEndDates) {
		super();
		this.milliSecsSinceBeginning = milliSecsSinceBeginning;
		this.dt = dt;
		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
		this.dist = dist;
		this.hr = hr;
		this.speedMtrPerSec = speedMtrPerSec;
		this.cadence = cadence;
		this.begEndDates = begEndDates;
	}

	public BeginningEndDatePair getBegEndDates() {
		return begEndDates;
	}

	public void setBegEndDates(BeginningEndDatePair begEndDates) {
		this.begEndDates = begEndDates;
	}

	public long getMilliSecsSinceBeginning() {
		return milliSecsSinceBeginning;
	}

	public void setMilliSecsSinceBeginning(long secsSinceBeginning) {
		this.milliSecsSinceBeginning = secsSinceBeginning;
	}

	public TcxTrackPoint() {
	}

	public TcxTrackPoint(long secsSinceBeginning) {
		this.milliSecsSinceBeginning = secsSinceBeginning;
	}

	public Date getDt() {
		return dt;
	}

	public void setDt(Date dt) {
		this.dt = dt;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist < 0.0 ? 0.0 : dist;
	}

	public long getHr() {
		return hr;
	}

	public void setHr(long hr) {
		this.hr = hr;
	}

	public double getSpeedMtrPerSec() {
		return speedMtrPerSec;
	}

	public void setSpeedMtrPerSec(double speedMtrPerSec) {
		this.speedMtrPerSec = speedMtrPerSec < 0.0 ? 0.0 : speedMtrPerSec;
	}

	public long getCadence() {
		return cadence;
	}

	public void setCadence(long cadence) {
		this.cadence = cadence;
	}

	public String toString(long timeLineInSecs) {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS000");
		sdf.setTimeZone(tz);
		String s = sdf.format(dt);
//		sdf = new SimpleDateFormat("h:mm a");
//		String ss = sdf.format(dt);
		if (hr != 1234567890) {
//			return timeLineInSecs + ".0," + s + "," + ss + "," + lat + "," + lng + "," + alt + "," + dist + "," + hr
//					+ "," + speedMtrPerSec + "," + cadence;
			return timeLineInSecs + ".0," + s + "," + lat + "," + lng + "," + alt + "," + dist + "," + hr + ","
					+ speedMtrPerSec + "," + cadence;
		} else {
//			return timeLineInSecs + ".0," + s + "," + ss + "," + lat + "," + lng + "," + alt + "," + dist + ",,,";
			return timeLineInSecs + ".0," + s + "," + lat + "," + lng + "," + alt + "," + dist + ",,,";
		}
	}

}

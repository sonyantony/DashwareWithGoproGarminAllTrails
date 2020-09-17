package com.sony.dashwarevideooverlay;
import java.util.Date;

public class Mp4File {
	private String filename;
	private Date startDt;
	private Date endDt;
	private float durationInSecs;

	public Mp4File(String filename, Date startDt, Date endDt, float duration) {
		super();
		this.filename = filename;
		this.startDt = startDt;
		this.endDt = endDt;
		this.durationInSecs = duration;
	}

	public float getDurationInSecs() {
		return durationInSecs;
	}

	public void setDurationInSecs(float duration) {
		this.durationInSecs = duration;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getStartDt() {
		return startDt;
	}

	public void setStartDt(Date startDt) {
		this.startDt = startDt;
	}

	public Date getEndDt() {
		return endDt;
	}

	public void setEndDt(Date endDt) {
		this.endDt = endDt;
	}

	@Override
	public String toString() {
		return "Mp4File [filename=" + filename + ", startDt=" + startDt + ", endDt=" + endDt + ", duration=" + durationInSecs
				+ "]\n";
	}

}

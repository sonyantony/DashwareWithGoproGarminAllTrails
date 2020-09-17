package com.sony.dashwarevideooverlay;
import java.util.Comparator;
import java.util.Date;

public class Mp4FileComparator implements Comparator<Mp4File> {

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public int compare(Mp4File o1, Mp4File o2) {
		
		Date d1 = o1.getStartDt() ;
		Date d2 = o2.getStartDt() ;
		return d1.compareTo(d2);
	}

}

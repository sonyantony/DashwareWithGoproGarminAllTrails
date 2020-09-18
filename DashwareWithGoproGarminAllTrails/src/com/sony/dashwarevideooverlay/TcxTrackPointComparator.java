package com.sony.dashwarevideooverlay;

import java.util.Comparator;

public class TcxTrackPointComparator implements Comparator<TcxTrackPoint> {

	@Override
	public int compare(TcxTrackPoint o1, TcxTrackPoint o2) {
		return (int) (o1.getDt().getTime() - o2.getDt().getTime());
	}

}

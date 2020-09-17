package com.sony.dashwarevideooverlay;

import java.util.function.Predicate;

public class TpDateSelectorPredicate implements Predicate<TcxTrackPoint> {

	private long intervalStart;
	private long intervalEnd;

	TpDateSelectorPredicate(long intervalStart, long intervalEnd) {
		this.intervalStart = intervalStart;
		this.intervalEnd = intervalStart;
	}

	@Override
	public boolean test(TcxTrackPoint t) {
		return intervalStart <= t.getMilliSecsSinceBeginning() && t.getMilliSecsSinceBeginning() >= intervalEnd;
	}

}

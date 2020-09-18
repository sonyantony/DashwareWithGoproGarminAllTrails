package com.sony.dashwarevideooverlay;
import java.util.Date;
import java.util.List;

//import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

//import org.apache.commons.math3.analysis.interpolation.DividedDifferenceInterpolator;
//import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;


public class TcxTrackPointInterpolator {

	private List<TcxTrackPoint> tcxData;

	private PolynomialSplineFunction latInterpolator;
	private PolynomialSplineFunction lngInterpolator;
	private PolynomialSplineFunction altInterpolator;
	private PolynomialSplineFunction distInterpolator;
	private PolynomialSplineFunction hrInterpolator;
	private PolynomialSplineFunction speedMtrPerSecInterpolator;
	private PolynomialSplineFunction cadenceInterpolator;
//	private PolynomialFunctionNewtonForm latInterpolator;
//	private PolynomialFunctionNewtonForm lngInterpolator;
//	private PolynomialFunctionNewtonForm altInterpolator;
//	private PolynomialFunctionNewtonForm distInterpolator;
//	private PolynomialFunctionNewtonForm hrInterpolator;
//	private PolynomialFunctionNewtonForm speedMtrPerSecInterpolator;
//	private PolynomialFunctionNewtonForm cadenceInterpolator;

	public TcxTrackPointInterpolator(List<TcxTrackPoint> tcxData) {
		this.tcxData = tcxData;
		latInterpolator = createDoubleDataInterpolator(tp -> tp.getLat());
		lngInterpolator = createDoubleDataInterpolator(tp -> tp.getLng());
		altInterpolator = createDoubleDataInterpolator(tp -> tp.getAlt());
		distInterpolator = createDoubleDataInterpolator(tp -> tp.getDist());
		if (tcxData.get(0).getHr() != 1234567890) {
			hrInterpolator = createDoubleDataInterpolator(tp -> tp.getHr());
			speedMtrPerSecInterpolator = createDoubleDataInterpolator(tp -> tp.getSpeedMtrPerSec());
			cadenceInterpolator = createDoubleDataInterpolator(tp -> tp.getCadence());
		}
	}

	private PolynomialSplineFunction createDoubleDataInterpolator(DoubleDataFetcher fetcher) {
//	private PolynomialFunctionNewtonForm createDoubleDataInterpolator(DoubleDataFetcher fetcher) {
		double[] milliSecsSinceBeginningArray = new double[tcxData.size()];
		double[] t = new double[tcxData.size()];
		TcxTrackPoint tpArray[] = tcxData.toArray(new TcxTrackPoint[0]);
		for (int i = 0; i < tpArray.length; i++) {
			milliSecsSinceBeginningArray[i] = tpArray[i].getMilliSecsSinceBeginning();
			t[i] = fetcher.fetch(tpArray[i]);
		}
//		return new SplineInterpolator().interpolate(milliSecsSinceBeginningArray, t);
		return new LinearInterpolator().interpolate(milliSecsSinceBeginningArray, t);
//		return new DividedDifferenceInterpolator().interpolate(milliSecsSinceBeginningArray, t);
	}

	public TcxTrackPoint getTrackPoint(long milliSecsSinceBeginning) {
		TcxTrackPoint tp = new TcxTrackPoint(milliSecsSinceBeginning);
		tp.setLat(latInterpolator.value(milliSecsSinceBeginning));
		tp.setLng(lngInterpolator.value(milliSecsSinceBeginning));
		tp.setAlt(altInterpolator.value(milliSecsSinceBeginning));
		tp.setDist(distInterpolator.value(milliSecsSinceBeginning));
		if (hrInterpolator != null) {
			tp.setHr((long) hrInterpolator.value(milliSecsSinceBeginning));
			tp.setSpeedMtrPerSec(speedMtrPerSecInterpolator.value(milliSecsSinceBeginning));
			tp.setCadence((long) cadenceInterpolator.value(milliSecsSinceBeginning));
		}
		Date b = tcxData.get(0).getDt();
		tp.setDt(new Date(b.getTime() + milliSecsSinceBeginning));
		return tp;
	}

	public boolean isWithinDomain(Date d) {
		Date b = tcxData.get(0).getDt();
		Date e = tcxData.get(tcxData.size() - 1).getDt();
		return b.getTime() <= d.getTime() && d.getTime() <= e.getTime();
	}

	public TcxTrackPoint getTrackPoint(Date d) {
		Date b = tcxData.get(0).getDt();
//		long milliSecsSinceBeginning = d.getTime() - b.getTime();
		long milliSecsSinceBeginning = d.getTime() - tcxData.get(0).getBegEndDates().getBeginningDate().getTime() ;
		return getTrackPoint(milliSecsSinceBeginning);
	}

	public long getDomainError(Date d) {
		Date b = tcxData.get(0).getDt();
		Date e = tcxData.get(tcxData.size() - 1).getDt();
		if (d.getTime() < b.getTime()) {
			return b.getTime() - d.getTime();
		} else if (d.getTime() > e.getTime()) {
			return d.getTime() - e.getTime();
		}
		return 0l;
	}

	public TcxTrackPoint getExtrapolatedTrackPoint(Date d) {
		Date b = tcxData.get(0).getDt();
		Date e = tcxData.get(tcxData.size() - 1).getDt();
		if (d.getTime() < b.getTime()) {
			TcxTrackPoint t = getTrackPoint(b);
			t.setDt(d);
			return t;
		} else if (d.getTime() > e.getTime()) {
			TcxTrackPoint t = getTrackPoint(e);
			t.setDt(d);
			return t;
		}
		return getTrackPoint(d);
	}
}

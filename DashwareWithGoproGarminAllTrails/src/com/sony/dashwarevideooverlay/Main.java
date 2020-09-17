package com.sony.dashwarevideooverlay;

/*************************************************************************************************
 * 
 * java -Dgarmin.tcx.file="d:\SharedWithRHEL\ttt\tcx_file.tcx"
 * 		-Dmediaduration.file="d:\SharedWithRHEL\ttt\media_duration_file.csv"
 *  	-Dalltrails.tcx.file="d:\SharedWithRHEL\ttt\alltrails.tcx"
 *  	-Doutput.file="d:\SharedWithRHEL\ttt\output.csv"
 *  	-classpath "D:\EclipseWorkspace\DashWareGarminTCXAndAllTrails\bin;D:\Apache Commons Math\commons-math3-3.6.1\commons-math3-3.6.1.jar"
 *  	 Main
 * 
 * 
 * 
*************************************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

	private static ArrayList<TcxTrackPoint> garminTcxData = new ArrayList<>();
	private static ArrayList<TcxTrackPoint> allTrailsTcxData = new ArrayList<>();
	private static ArrayList<Mp4File> mp4Files = new ArrayList<>();

	private static String previousDt;

//	private static TcxTrackPointInterpolator garminTpInterpolator;
//	private static TcxTrackPointInterpolator alltrailsTpInterpolator;
	private static String outputCsvFileName;
	private static Writer outputCsvFileWriter;
	private static long timeLineInSecs = 0;
	private static String nlSep = System.getProperty("line.separator");
	private static BeginningEndDatePair garminBeginningEndDates;
	private static BeginningEndDatePair allTrailsBeginningEndDates;

	public static void main(String[] args) throws Exception {
		initialize(args);
		writeCsvFile();
	}

	private static void writeCsvFile() throws IOException {
		outputCsvFileWriter = new FileWriter(outputCsvFileName);
		outputCsvFileWriter.write(
//				"TimelineInSecs,DateTimeInEDT,TimeText,Latitude,Longitude,AltitudeInMeters,DistanceTravelledMeters,HRInBPM,SpeedInm/s,CadenceInSPM"
				"TimelineInSecs,DateTimeInEDT,Latitude,Longitude,AltitudeInMeters,DistanceTravelledMeters,HRInBPM,SpeedInm/s,CadenceInSPM"
						+ nlSep);
		for (Mp4File mp4File : mp4Files) {
//			System.out.println(mp4File.getStartDt() + "::::" + mp4File.getEndDt());
			processMp4File(mp4File);
		}
	}

	private static void processMp4File(Mp4File mp4File) throws IOException {
		long numSecs2iterate = (mp4File.getEndDt().getTime() - mp4File.getStartDt().getTime()) / 1000;
//		long numSecs2iterate = (long)mp4File.getDurationInSecs() ;
		long mp4FileStartTime = mp4File.getStartDt().getTime();
		TpDateSelectorPredicate p = new TpDateSelectorPredicate(mp4FileStartTime, mp4File.getEndDt().getTime());
		List<TcxTrackPoint> garminTcxDataSubSet = garminTcxData.stream().filter(p).collect(Collectors.toList()) ;
		TcxTrackPointInterpolator garminTpInterpolator = new TcxTrackPointInterpolator(garminTcxDataSubSet);
		List<TcxTrackPoint> allTrailsTcxDataSubSet = allTrailsTcxData.stream().filter(p).collect(Collectors.toList()) ;
		TcxTrackPointInterpolator alltrailsTpInterpolator = new TcxTrackPointInterpolator(allTrailsTcxDataSubSet);
		TcxTrackPoint t = null;
		for (long i = 0; i < numSecs2iterate; i++) {
			Date d = new Date(mp4FileStartTime + i * 1000);

			if (alltrailsTpInterpolator.isWithinDomain(d)) {
				t = alltrailsTpInterpolator.getTrackPoint(d);
				if (garminTpInterpolator.isWithinDomain(d)) {
					TcxTrackPoint g = garminTpInterpolator.getTrackPoint(d);
					t.setHr(g.getHr());
					t.setSpeedMtrPerSec(g.getSpeedMtrPerSec());
					t.setCadence(g.getCadence());
				}
			} else if (garminTpInterpolator.isWithinDomain(d)) {
				t = garminTpInterpolator.getTrackPoint(d);
			} else {
				long garminDomainError = garminTpInterpolator.getDomainError(d);
				long allTrailsDomainError = alltrailsTpInterpolator.getDomainError(d);
				t = garminDomainError <= allTrailsDomainError ? garminTpInterpolator.getExtrapolatedTrackPoint(d)
						: alltrailsTpInterpolator.getExtrapolatedTrackPoint(d);
			}
			outputCsvFileWriter.write(t.toString(timeLineInSecs) + nlSep);
			outputCsvFileWriter.flush();
			timeLineInSecs++;
		}
	}

	private static void initialize(String[] args) throws Exception {
		String garminTcxFileName = getSystemProperty("garmin.tcx.file");
		String allTrailsTcxFilename = getSystemProperty("alltrails.tcx.file");
		String mp4ListFileName = getSystemProperty("mediaduration.file");
		outputCsvFileName = getSystemProperty("output.file");
		processMp4List(mp4ListFileName);
		parseTcxFile(garminTcxFileName);
		parseTcxFile(allTrailsTcxFilename);
//		garminTpInterpolator = new TcxTrackPointInterpolator(garminTcxData);
//		alltrailsTpInterpolator = new TcxTrackPointInterpolator(allTrailsTcxData);
	}

	private static String getSystemProperty(String key) throws Exception {
		String s = System.getProperty(key);
		if (null == s) {
			throw new Exception("Error : System Property " + key + " Not Set.");
		}
		return s;
	}

	private static void processMp4List(String mp4ListFile) throws Exception {
		try (BufferedReader reader = new BufferedReader(new FileReader(mp4ListFile))) {
			String line = reader.readLine();
			while (line != null) {
				String[] s = line.split(",");
				Date startDt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSSX").parse(s[1]);
				// Date endDt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSSX").parse(s[2]);
				float duration = Float.valueOf(s[3]);
				Date endDt = new Date(startDt.getTime() + (long) duration * 1000);
				mp4Files.add(new Mp4File(s[0], startDt, endDt, duration));
				line = reader.readLine();
			}
		}
		if (mp4Files.size() == 0) {
			throw new Exception("Error : No Mp4 Files Listed in " + mp4ListFile);
		}
		Collections.sort(mp4Files, new Mp4FileComparator());
	}

	private static void parseTcxFile(String tcxFileName)
			throws ParserConfigurationException, SAXException, IOException, ParseException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(tcxFileName));
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("Trackpoint");
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element trkPt = (Element) node;
				processTrackPoint(trkPt);
			}
		}
	}

	private static void processTrackPoint(Element trkPt) throws ParseException {
		NodeList nl = trkPt.getElementsByTagName("LatitudeDegrees");
		if (nl.getLength() == 0) {
			return;
		}
		String s = trkPt.getElementsByTagName("Time").item(0).getTextContent();
		if (s.equals(previousDt)) {
			return;
		} else {
			previousDt = s;
		}

		double lat = Double.valueOf(trkPt.getElementsByTagName("LatitudeDegrees").item(0).getTextContent());
		double lng = Double.valueOf(trkPt.getElementsByTagName("LongitudeDegrees").item(0).getTextContent());
		double alt = Double.valueOf(trkPt.getElementsByTagName("AltitudeMeters").item(0).getTextContent());
		double dist = Double.valueOf(trkPt.getElementsByTagName("DistanceMeters").item(0).getTextContent());

		TcxTrackPoint tp = new TcxTrackPoint();

		tp.setLat(lat);
		tp.setLng(lng);
		tp.setAlt(alt);
		tp.setDist(dist);

		if (trkPt.getElementsByTagName("HeartRateBpm").getLength() != 0) {
			Date dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(s);
			tp.setDt(dt);
			if (garminBeginningEndDates == null) {
				garminBeginningEndDates = new BeginningEndDatePair();
				garminBeginningEndDates.setBeginningDate(dt);
			}
			garminBeginningEndDates.setEndate(dt);
			long milliSecsSinceBeginning = (dt.getTime() - garminBeginningEndDates.getBeginningDate().getTime());
			tp.setMilliSecsSinceBeginning(milliSecsSinceBeginning);
			long hr = Long.valueOf(trkPt.getElementsByTagName("HeartRateBpm").item(0).getTextContent().trim());
			double speedMtrPerSec = Double.valueOf(trkPt.getElementsByTagName("ns3:Speed").item(0).getTextContent());
			long cadence = Long.valueOf(trkPt.getElementsByTagName("ns3:RunCadence").item(0).getTextContent());
			tp.setHr(hr);
			tp.setSpeedMtrPerSec(speedMtrPerSec);
			tp.setCadence(cadence);
			tp.setBegEndDates(garminBeginningEndDates);
			garminTcxData.add(tp);
		} else {
			Date dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(s);
			tp.setDt(dt);
			if (allTrailsBeginningEndDates == null) {
				allTrailsBeginningEndDates = new BeginningEndDatePair();
				allTrailsBeginningEndDates.setBeginningDate(dt);
			}
			allTrailsBeginningEndDates.setEndate(dt);
			long milliSecsSinceBeginning = (dt.getTime() - allTrailsBeginningEndDates.getBeginningDate().getTime());
			tp.setMilliSecsSinceBeginning(milliSecsSinceBeginning);
			tp.setBegEndDates(allTrailsBeginningEndDates);
			allTrailsTcxData.add(tp);
		}

	}

}

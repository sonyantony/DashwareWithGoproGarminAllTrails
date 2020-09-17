package com.sony.dashwarevideooverlay;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestScratch {

	public static void main(String[] args) {
		String s1 = "2015-10-18 00:00:03.505000";
		String s2 = "2020-08-09 15:28:00.000000";
		try {
			Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(s1.substring(0, 23));
			Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(s2.substring(0, 23));
			System.out.println(date2.getTime()-date1.getTime());
			System.out.println(new Date(date2.getTime() ));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}


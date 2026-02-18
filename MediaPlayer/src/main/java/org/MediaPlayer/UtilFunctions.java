package org.MediaPlayer;

public class UtilFunctions {
	public static String getLengthString(int sec) {
		int min = sec/60;
		int hour = min/60;
		int day = hour/24;
		String ret = "";
		if(day > 0) ret = day + "d";
		if(hour > 0) ret += (hour%24) + "h";
		if(min > 0) ret += (min%60) + "m";
		if(sec > 0) ret += (sec%60) + "s";
		return ret;
	}
	public static Integer getInt(String pos) {
		try {
			int i = Integer.parseInt(pos);
			return i;
		}
		catch(Exception e) {
			System.out.println("Not a Number");
		}
		return null;
	}

}

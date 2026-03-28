package Tools.Files;

import java.util.ArrayList;
import java.util.List;

import Tools.Files.Data.DataType;
import Tools.Files.Data.Return2;

public class Util {
	public static Return2<String, String> getStringPart(String s) {
		int i = 0;
		int nameLength = 0;
		String lengthString = "";
		while(i < s.length() && s.charAt(i) != ':') {
			lengthString += s.charAt(i);
			i++;
		}
		if(s.length() == 0 || s.charAt(i) != ':') return null;
		nameLength = Integer.valueOf(lengthString);
		i++;
		String part = "";
		for(int j = 0; j < nameLength; j++) {
			part += s.charAt(i);
			i++;
		}
		return new Return2<String, String>(s.substring(i), part);
	}
	public static String cut(String s, String begins) {
		if(s != null && s.length() >= begins.length() && s.substring(0, begins.length()).equals(begins)) return s.substring(begins.length());
		return null;
	}
	public static String getSign(String s) {
		String ret = "";
		int length = s.length();
		for(int i = 0; i < length; i++) {
			if(s.charAt(i) == ':') return ret;
			ret += s.charAt(i);
		}
		return ret;
	}
	public static boolean hasContent(String s) {
		int length = s.length();
		for(int i = 0; i < length; i++) {
			if(!Character.isWhitespace(s.charAt(i))) return true;
		}
		return false;
	}
	public static String getSendable(char seperator, DataType... ar) {
		StringBuilder ret = new StringBuilder();
		for(DataType data: ar) {
			if(!ret.isEmpty()) ret.append(seperator);
			ret.append(Util.getSendable(data.getData()));
		}
		return ret.toString();
	}
	public static String getSendable(String s) {
		return s.length() + ":" + s;
	}
	public static String getSendable(int i) {
		return getSendable("" + i);
	}
	public static Return2<String, List<String>> getStringParts(String s, char betweenSeperator, int times) {
		List<String> list = new ArrayList<>();
		Return2<String, String> ret2 = getStringPart(s);
		list.add(ret2.two);
		s = ret2.one;
		while(s.length() > 0 && --times != 0) {
			if(s.charAt(0) != betweenSeperator) break;
			ret2 = getStringPart(s.substring(1));
			list.add(ret2.two);
			s = ret2.one;
		}
		return new Return2<String, List<String>>(s, list);
	}
    public static String[] getNameAndType(String s) {
    	String name, type = "";
        int lastDotPos = s.lastIndexOf('.');
        if(lastDotPos >= 0) {
        	name = s.substring(0, lastDotPos);
        	type = s.substring(lastDotPos);
        }
        else name = s;
        return new String[] {name, type};
    }
}

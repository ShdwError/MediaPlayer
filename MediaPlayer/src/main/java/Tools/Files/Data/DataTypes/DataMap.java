package Tools.Files.Data.DataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import Tools.Files.Data.DataType;
import Tools.Files.Data.Return2;
import Tools.Files.Data.Util;

public class DataMap<T extends DataType> extends DataType {
	private Map<String, T> data;
	private Supplier<T> supplier;
	public DataMap(Supplier<T> supplier) {
		super();
		this.data = new HashMap<>();
		this.supplier = supplier;
	}
	public DataMap(Supplier<T> supplier, Map<String, T> data) {
		this.data = data;
		this.supplier = supplier;
		this.created = true;
	}
	public Map<String, T> get() {
		this.created = true;
		return this.data;
	}
	
	@Override
	public void setData(String s) {
		created = true;
		data.clear();
		if(s.length() < 2) return;
		while(!s.isEmpty()) {
			if(s.charAt(0) != '[') throw new Error("Cant read Map");
			s = s.substring(1);
			Return2<String, List<String>> ret2 = Util.getStringParts(s);
			s = ret2.one;
			String mapping = ret2.two.get(0);
			String mapData = ret2.two.get(1);
			T mapDataType = supplier.get();
			mapDataType.setData(mapData);
			data.put(mapping, mapDataType);
			if(s.length() > 1) s = s.substring(2);
			else if(s.length() > 0) s = s.substring(1);
		}
	}
	@Override
	public String getData() {
		if(!created) return "";
		List<String> list = new ArrayList<>();
		data.forEach((m, d) -> {
			list.add("[" + Util.getSendable(m) + "," + Util.getSendable(d.getData()) + "]");
		});
		String ret = "";
		for(int i = 0; i < list.size(); i++) {
			if(ret.length() > 0) ret += ",";
			ret += list.get(i);
		}
		return ret;
	}
	@Override
	public DataMap<T> copy() {
		return new DataMap<T>(supplier);
	}
}

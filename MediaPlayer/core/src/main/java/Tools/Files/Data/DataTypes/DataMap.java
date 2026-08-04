package Tools.Files.Data.DataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.Data.Exceptions.InvalidFormatException;
import Tools.Files.Util;
import Tools.Files.Data.DataType;
import Tools.Files.Data.Return2;

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
	public void setData(String s) throws DataTypeException {
		created = true;
		data.clear();
		if(s.length() < 3) return;
		while(!s.isEmpty()) {
			if(s.charAt(0) != '[') throw new InvalidFormatException("Cant read Map");
			s = s.substring(1);
			Return2<List<String>, String> ret2 = Util.getStringParts(s, ',', 2);
			s = ret2.two;
			String mapping = ret2.one.get(0);
			String mapData = ret2.one.get(1);
			T mapDataType = supplier.get();
			mapDataType.setData(mapData);
			data.put(mapping, mapDataType);
			if(s.length() > 1) s = s.substring(2);
			else if(!s.isEmpty()) s = s.substring(1);
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
	public DataMap<T> instance() {
		return new DataMap<T>(supplier);
	}
	@SuppressWarnings("unchecked")
	@Override
	public DataMap<T> copy() {
		DataMap<T> ret = new DataMap<T>(supplier);
		this.data.forEach((key, val) -> {
			ret.get().put(key, (T) val.copy());
		});
		return ret;
	}
}

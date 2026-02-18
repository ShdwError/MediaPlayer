package Tools.Files.Data.DataTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import Tools.Files.Util;
import Tools.Files.Data.DataType;
import Tools.Files.Data.Return2;

public class DataArray<T extends DataType> extends DataType {
	private List<T> data;
	private Supplier<T> supplier;
	public DataArray(Supplier<T> supplier) {
		super();
		this.data = new ArrayList<>();
		this.supplier = supplier;
	}
	public DataArray(Supplier<T> supplier, List<T> data) {
		this.data = data;
		this.supplier = supplier;
		this.created = true;
	}
	public List<T> get() {
		this.created = true;
		return this.data;
	}
	@Override
	public void setData(String s) {
		data.clear();
		int length = s.length()-2;
		if(s.length() == 0) return;
		if(s.charAt(0) != '[' || s.charAt(length+1) != ']') throw new Error("Cant read array");
		s = s.substring(1);
		
		while(s.length() > 1) {
			Return2<String, String> r2 = Util.getStringPart(s);
			s = r2.one.substring(1);
			T dataAdd = supplier.get();
			dataAdd.setData(r2.two);
			data.add(dataAdd);
		}
	}
	@Override
	public String getData() {
		String ret = "[";
		int length = data.size();
		for(int i = 0; i < length; i++) {
			if(i > 0) ret += ",";
			String d = data.get(i).getData();
			ret += Util.getSendable(d);
		}
		return ret + "]";
	}
	public void add(String s) {
		T dataAdd = supplier.get();
		dataAdd.setData(s);
		data.add(dataAdd);
	}
	@Override
	public DataArray<T> instance() {
		return new DataArray<>(supplier);
	}
	@Override
	public DataArray<T> copy() {
		DataArray<T> ret = new DataArray<>(supplier);
		for(T entry: data) {
			ret.get().add(entry);
		}
		return ret;
	}
}

package Tools.Core.Files.Data.DataTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import Tools.Core.Files.Util;
import Tools.Core.Files.Data.DataParser;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.Return2;
import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Core.Files.Data.Exceptions.InvalidFormatException;

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
	public void setData(String s) throws DataTypeException {
		data.clear();
		if(s.isEmpty()) return;
		DataParser parser = DataParser.start("[").parseWhile(',').expect("]");
		List<String> parsed = parser.parse(s).getList(0);
		for(String part: parsed) {
			T dataAdd = supplier.get();
			dataAdd.setData(part);
			data.add(dataAdd);
		}
		this.created = true;
	}
	@Override
	public String getData() {
		StringBuilder ret = new StringBuilder("[");
		int length = data.size();
		for(int i = 0; i < length; i++) {
			if(i > 0) ret.append(",");
			String d = data.get(i).getData();
			ret.append(Util.getSendable(d));
		}
		return ret + "]";
	}
	public void add(String s) throws DataTypeException {
		T dataAdd = supplier.get();
		dataAdd.setData(s);
		data.add(dataAdd);
	}
}

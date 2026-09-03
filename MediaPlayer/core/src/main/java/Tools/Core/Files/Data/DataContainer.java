package Tools.Core.Files.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Core.Files.DataSystem;
import Tools.Core.Files.Data.Exceptions.DataTypeException;

public class DataContainer<D extends DataAdapter> {
	public Map<String, DataType> data;
	public D adapter;
	public DataContainer(D adapter) {
		this.adapter = adapter;
		data = new HashMap<>();
		adapter.createMapping(data);
	}
	public void setData(String s, String data) throws DataTypeException {
		this.data.get(s).setData(data);
	}
	public DataType getData(String s) {
		return this.data.get(s);
	}

}

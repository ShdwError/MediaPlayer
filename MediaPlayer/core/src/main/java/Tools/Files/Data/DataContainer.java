package Tools.Files.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.DataSystem;

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

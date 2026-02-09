package Tools.Files.Data;

import java.util.HashMap;
import java.util.Map;

import Tools.Files.DataSystem;

public class DataContainer {
	public DataSystem system;
	public Map<String, DataType> data;
	public DataAdapter adapter;
	public DataContainer(DataSystem system) {
		this.system = system;
		data = new HashMap<>();
		createData();
	}
	public void createData() {
		system.containerData.forEach((s,a) -> {
			data.put(s, a.copy());
		});
	}
	public void addAdapter() {
		if(system.adapter != null) {
			adapter = system.adapter.get();
			adapter.createData(data);
			adapter.dataContainer = this;
		}
	}
	public void setData(String s, String data) {
		this.data.get(s).setData(data);
	}
	public DataType getData(String s) {
		return this.data.get(s);
	}

}

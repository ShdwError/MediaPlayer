package Tools.Files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import Tools.Files.Data.DataAdapter;
import Tools.Files.Data.DataContainer;
import Tools.Files.Data.DataType;
import Tools.Files.Data.Util;
import Tools.Files.Data.DataTypes.DataString;

public class DataSystem {
	private FileManager fileManager;
	public Map<String, DataContainer> dataContainers;
	public Supplier<DataAdapter> adapter;
	public Map<String, DataType> containerData;
	public DataSystem(FileManager fileManager) {
		this.fileManager = fileManager;
		dataContainers = new HashMap<>();
		containerData = new HashMap<>();
		
		adapter = null;
	}
	public void setAdapter(Supplier<DataAdapter> adapter) {
		this.adapter = adapter;
	}
	public void addContainerData(String mapping, DataType data) {
		containerData.put(mapping, data);
	}
	public void read() throws IOException {
		List<String> lines = fileManager.read();
		dataContainers.clear();
		DataContainer container = new DataContainer(this);
		String id = "";
		boolean hasData = false;
		for(String s: lines) {
			if(s.equals("/")) {
				container.addAdapter();
				dataContainers.put(id, container);
				container = new DataContainer(this);
				hasData = false;
				id = "";
			}
			else {
				String sign = Util.getSign(s);
				String data = s.substring(sign.length()+1);
				if(sign.equals("Id")) id = data;
				container.setData(sign, data);
				hasData = true;
			}
		}
		if(hasData) {
			container.addAdapter();
			dataContainers.put(id, container);
		}
	}
	public void save() throws IOException {
		List<String> lines = new ArrayList<>();
		dataContainers.forEach((name, account) -> {
			account.data.forEach((sign, data) -> {
				lines.add(sign + ":" + data.getData());
			});
			lines.add("/");
		});
		fileManager.write(lines);
	}
	public DataContainer getOrCreate(String id) {
		if(dataContainers.containsKey(id)) return get(id);
		else return createNewDataContainer(id);
	}
	public DataContainer get(String id) {
		return dataContainers.get(id);
	}
	public DataContainer createNewDataContainer(String id) {
		if(dataContainers.containsKey(id)) return null;
		DataContainer container = new DataContainer(this);
		container.addAdapter();
		if(containerData.containsKey("Id"))
			container.setData("Id", id);
		dataContainers.put(id, container);
		return container;
	}
	public void changeFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public FileManager getFileManager() {
		return fileManager;
	}

}

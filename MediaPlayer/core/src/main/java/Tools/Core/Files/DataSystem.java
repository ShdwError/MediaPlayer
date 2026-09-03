package Tools.Core.Files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataContainer;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.DataString;
import Tools.Core.Files.Data.Exceptions.DataTypeException;

public class DataSystem<D extends DataAdapter> {
	private GenericFileManager fileManager;
	public Map<String, DataContainer<D>> dataContainers;
	public Supplier<D> adapter;
	public DataSystem(GenericFileManager fileManager, Supplier<D> adapter) {
		this.fileManager = fileManager;
		dataContainers = new HashMap<>();
		
		this.adapter = adapter;
	}
	public void read() throws IOException, DataTypeException {
		List<String> lines = fileManager.read();
		dataContainers.clear();
		DataContainer<D> container = new DataContainer<>(adapter.get());
		container.adapter.dataSystem = this;
		String id = "";
		boolean hasData = false;
		for(String s: lines) {
			if(s.equals("/")) {
				dataContainers.put(id, container);
				container = new DataContainer<>(adapter.get());
				container.adapter.dataSystem = this;
				hasData = false;
				id = "";
			}
			else {
				String[] split = Util.split(s);
				String sign = split[0];
				String data = split[1];
				if(sign.equals("Id")) id = data;
				container.setData(sign, data);
				hasData = true;
			}
		}
		if(hasData) {
			dataContainers.put(id, container);
			container.adapter.dataSystem = this;
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
		//Remove last "/"
		if(!lines.isEmpty())
			lines.remove(lines.size()-1);
		fileManager.write(lines);
	}
	public D getOrCreate(String id) {
		if(dataContainers.containsKey(id)) return get(id);
		return createNewDataContainer(id);
	}
	public D getOrCreate() {
		return getOrCreate("");
	}
	public D get(String id) {
		return dataContainers.get(id).adapter;
	}
	public D createNewDataContainer(String id) {
		if(dataContainers.containsKey(id)) return null;
		DataContainer<D> container = new DataContainer<>(adapter.get());
		if(container.data.containsKey("Id")) {
            try {
                container.setData("Id", id);
            } catch (DataTypeException ignored) {}
        }
		dataContainers.put(id, container);
		container.adapter.dataSystem = this;
		return container.adapter;
	}
	public void changeFileManager(GenericFileManager fileManager) {
		this.fileManager = fileManager;
	}
	public GenericFileManager getFileManager() {
		return fileManager;
	}

}

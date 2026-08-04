package Tools.Files.Data;

import java.util.Map;

import Tools.Files.DataSystem;

public abstract class DataAdapter {
	public DataSystem dataSystem;
	public abstract void createMapping(Map<String, DataType> data);

}

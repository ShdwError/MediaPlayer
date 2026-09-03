package Tools.Core.Files.Data;

import java.util.Map;

import Tools.Core.Files.DataSystem;

public abstract class DataAdapter {
	public DataSystem dataSystem;
	public abstract void createMapping(Map<String, DataType> data);

}

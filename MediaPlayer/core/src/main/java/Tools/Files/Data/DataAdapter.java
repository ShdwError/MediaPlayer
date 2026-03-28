package Tools.Files.Data;

import java.util.Map;

public abstract class DataAdapter {
	public DataContainer dataContainer;
	public abstract void createData(Map<String, DataType> data);

}

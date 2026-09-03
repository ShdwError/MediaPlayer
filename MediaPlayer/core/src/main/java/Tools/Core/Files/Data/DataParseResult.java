package Tools.Core.Files.Data;

import java.util.List;

public class DataParseResult {
	private final List<List<String>> data;
	public DataParseResult(List<List<String>> data) {
		this.data = data;
	}
	public String getString(int index) {
		return data.get(index).get(0);
	}
	public List<String> getList(int index) {
	    return data.get(index);
	}

}

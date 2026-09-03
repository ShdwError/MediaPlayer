package Tools.Core.Files.Data.DataTypes;

import Tools.Core.Files.Data.DataType;

public class DataString extends DataType {
	private String data;
	public DataString() {
		super();
		this.data = "";
	}
	public DataString(String data) {
		this.set(data);
	}
	public void set(String data) {
		this.created = true;
		this.data = data;
	}
	public String get() {
		return this.data;
	}
	public DataString copy() {
		return new DataString(data);
	}
	
	
	@Override
	public void setData(String s) {
		created = true;
		this.data = s;
	}
	@Override
	public String getData() {
		return data;
	}
}

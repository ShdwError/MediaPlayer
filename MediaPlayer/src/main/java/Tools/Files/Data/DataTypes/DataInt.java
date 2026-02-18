package Tools.Files.Data.DataTypes;

import Tools.Files.Data.DataType;

public class DataInt extends DataType {
	private int data;
	public DataInt() {
		super();
		this.data = 0;
	}
	public DataInt(int data) {
		this.set(data);
	}
	public void set(int data) {
		this.data = data;
		this.created = true;
	}
	public int get() {
		return this.data;
	}
	public int add(int i) {
		this.created = true;
		return this.data += i;
	}
	
	
	
	
	@Override
	public void setData(String s) {
		if(s.length() == 0) return;
		created = true;
		this.data = Integer.valueOf(s);
	}
	@Override
	public String getData() {
		if(!created) return "";
		return "" + data;
	}
	@Override
	public DataInt instance() {
		return new DataInt();
	}
	@Override
	public DataInt copy() {
		return new DataInt(data);
	}

}

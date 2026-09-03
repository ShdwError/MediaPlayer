package Tools.Core.Files.Data.DataTypes;

import Tools.Core.Files.Data.DataType;

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
	public int multiply(int times) {
		this.created = true;
		return this.data *= times;
	}
	public DataInt copy() {
		return new DataInt(data);
	}
	
	
	@Override
	public void setData(String s) {
		if(s.isEmpty()) return;
		created = true;
		this.data = Integer.parseInt(s);
	}
	@Override
	public String getData() {
		if(!created) return "";
		return "" + data;
	}

}

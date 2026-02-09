package Tools.Files.Data.DataTypes;

import Tools.Files.Data.DataType;

public class DataDouble extends DataType {
	private double data;
	public DataDouble() {
		super();
		this.data = 0;
	}
	public DataDouble(double data) {
		this.set(data);
	}
	public void set(double data) {
		this.data = data;
		this.created = true;
	}
	public double get() {
		return this.data;
	}
	@Override
	public void setData(String s) {
		if(s.length() == 0) return;
		created = true;
		this.data = Double.valueOf(s);
	}
	@Override
	public String getData() {
		if(!created) return "";
		return "" + data;
	}
	@Override
	public DataDouble copy() {
		return new DataDouble();
	}

}

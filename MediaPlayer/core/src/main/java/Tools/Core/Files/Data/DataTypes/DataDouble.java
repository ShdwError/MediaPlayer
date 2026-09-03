package Tools.Core.Files.Data.DataTypes;

import Tools.Core.Files.Data.DataType;

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
	public DataDouble copy() {
		return new DataDouble(data);
	}
	@Override
	public void setData(String s) {
		if(s.isEmpty()) return;
		created = true;
		this.data = Double.parseDouble(s);
	}
	@Override
	public String getData() {
		if(!created) return "";
		return "" + data;
	}

}

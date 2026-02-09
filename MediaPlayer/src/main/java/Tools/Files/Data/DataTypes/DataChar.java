package Tools.Files.Data.DataTypes;

import Tools.Files.Data.DataType;

public class DataChar extends DataType {
	private char data;
	public DataChar() {
		super();
		this.data = ' ';
	}
	public DataChar(char data) {
		this.set(data);
	}
	public void set(char data) {
		this.data = data;
		this.created = true;
	}
	@Override
	public void setData(String s) {
		if(s.length() == 0) return;
		created = true;
		if(s.length() != 1) throw new Error("Cant read char");
		data = s.charAt(0);
	}
	@Override
	public String getData() {
		if(!created) return "";
		return "" + data;
	}
	@Override
	public DataChar copy() {
		return new DataChar();
	}
}

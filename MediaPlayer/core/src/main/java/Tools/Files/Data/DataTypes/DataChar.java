package Tools.Files.Data.DataTypes;

import Tools.Files.Data.DataType;
import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.Data.Exceptions.InvalidFormatException;

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
	public void setData(String s) throws DataTypeException {
		if(s.isEmpty()) return;
		created = true;
		if(s.length() != 1) throw new InvalidFormatException("Cant read char");
		data = s.charAt(0);
	}
	@Override
	public String getData() {
		if(!created) return "";
		return "" + data;
	}
	@Override
	public DataChar instance() {
		return new DataChar();
	}
	@Override
	public DataChar copy() {
		return new DataChar(data);
	}
}

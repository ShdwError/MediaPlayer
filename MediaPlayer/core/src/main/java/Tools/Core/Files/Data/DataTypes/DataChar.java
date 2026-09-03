package Tools.Core.Files.Data.DataTypes;

import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Core.Files.Data.Exceptions.InvalidFormatException;

public class DataChar extends DataType {
	private char data;
	public DataChar() {
		super();
		this.data = ' ';
	}
	public DataChar(char data) {
		this.set(data);
	}
	public char get() {
		return data;
	}
	public void set(char data) {
		this.data = data;
		this.created = true;
	}
	public DataChar copy() {
		return new DataChar(data);
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
}

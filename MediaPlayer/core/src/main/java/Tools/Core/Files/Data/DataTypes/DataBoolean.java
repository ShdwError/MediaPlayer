package Tools.Core.Files.Data.DataTypes;

import Tools.Core.Files.Data.*;
import Tools.Core.Files.Data.Exceptions.InvalidFormatException;

public class DataBoolean extends DataType {
	private boolean data;
	public DataBoolean() {
		super();
		this.data = false;
	}
	public DataBoolean(boolean data) {
		set(data);
	}
	public void set(boolean data) {
		this.data = data;
		this.created = true;
	}
	public boolean get() {
		return data;
	}
	public DataBoolean copy() {
		return new DataBoolean(data);
	}
	@Override
	public void setData(String s) throws InvalidFormatException {
		switch(s) {
		case "0b": data = false; created = true; break;
		case "1b": data = true; created = true; break;
		case "": break;
		default: throw new InvalidFormatException("Cant read boolean");
		}
	}

	@Override
	public String getData() {
		if(!created) return "";
		if(data) return "1b";
		return "0b";
	}

}

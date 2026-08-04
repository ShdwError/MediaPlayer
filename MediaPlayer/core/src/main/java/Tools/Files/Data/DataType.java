package Tools.Files.Data;

import Tools.Files.Data.Exceptions.DataTypeException;

public abstract class DataType {
	public boolean created;
	public DataType()  {
		created = false;
	}
	public abstract void setData(String s) throws DataTypeException;
	public abstract String getData();
	public abstract DataType instance();
	public abstract DataType copy();
	@Override
	public String toString() {
		return getData();
	}
}

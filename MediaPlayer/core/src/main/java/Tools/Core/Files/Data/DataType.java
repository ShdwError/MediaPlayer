package Tools.Core.Files.Data;

import java.util.Objects;

import Tools.Core.Files.Data.Exceptions.DataTypeException;

public abstract class DataType {
	public boolean created;
	public DataType()  {
		created = false;
	}
	public abstract void setData(String s) throws DataTypeException;
	public abstract String getData();
	@Override
	public String toString() {
		return getData();
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof DataType other)
			return Objects.equals(this.getData(), other.getData());
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(getData());
	}
}

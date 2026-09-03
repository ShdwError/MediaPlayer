package Tools.Core.Files.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import Tools.Core.Files.Util;
import Tools.Core.Files.Data.DataTypes.DataString;
import Tools.Core.Files.Data.Exceptions.DataTypeException;
import Tools.Core.Files.Data.Exceptions.UnknownDataTypeException;

public abstract class DataTypedClass<T extends DataSubtype> extends DataType {
	private Map<String, Supplier<T>> typeMap;
	private DataParser parser;
	private DataString type;
	private T data;
	public DataTypedClass()  {
		this(new HashMap<>());
	}
	public DataTypedClass(Map<String, Supplier<T>> typeMap) {
		this.typeMap = typeMap;
		this.parser = DataParser.start("{").parse(',', 2).expect("}");
		
		this.type = new DataString();
		createTypes();
		
	}
	public DataTypedClass(DataString type, T data) {
		this(new HashMap<>(), type, data);
	}
	public DataTypedClass(Map<String, Supplier<T>> typeMap, DataString type, T data) {
		this.typeMap = typeMap;
		this.parser = DataParser.start("{").parse(',', 2).expect("}");
		
		this.type = type;
		this.data = data;
		created = true;
		createTypes();
	}
	public void addType(String type, Supplier<T> supplier) {
		typeMap.put(type, supplier);
	}
	public T get() {
		return data;
	}
	public String getType() {
		return type.get();
	}
	public abstract void createTypes();
	@Override
	public void setData(String s) throws DataTypeException {
		DataParseResult result = parser.parse(s);
		
		type.set(result.getString(0));
		
		Supplier<T> prototype = typeMap.get(type.get());
		if(prototype == null)
			throw new UnknownDataTypeException(type + " is not a valid type");
		
		data = prototype.get();
		data.setData(result.getString(1));
		data.setDataType(type.get());
		created = true;
	}
	@Override
	public String getData() {
		if(!created)
			return "";
		return "{" + Util.getSendable(',', type, data) + "}";
	}

}

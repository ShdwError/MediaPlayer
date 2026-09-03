package Tools.Core.Files.Data;

import Tools.Core.Files.Data.Exceptions.DataTypeException;

public abstract class DataClass extends DataType {
	private ClassMapper classMapper;
	public void createData(DataType... items)  {
		this.classMapper = new ClassMapper(items);
	}
	@Override
	public void setData(String s) throws DataTypeException {
		if(s == null) return;
		classMapper.setData(s);
		
		this.created = true;
	}
	@Override
	public String getData() {
		if(created)
			return classMapper.getData();
		return "";
	}
}

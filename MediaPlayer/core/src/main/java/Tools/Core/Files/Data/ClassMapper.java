package Tools.Core.Files.Data;

import Tools.Core.Files.Util;
import Tools.Core.Files.Data.Exceptions.DataTypeException;

public class ClassMapper {
	private final DataType[] items;
	private DataParser parser;
	public ClassMapper(DataType... items) {
		this.items = items;
		this.parser = DataParser.start("{").parse(',', items.length).expect("}");
	}
	public void setData(String s) throws DataTypeException {
		DataParseResult result = parser.parse(s);
		for(int i = 0; i < items.length; i++) {
			items[i].setData(result.getString(i));
		}
	}
	public String getData() {
		return "{" + Util.getSendable(',', items) + "}";
	}

}

package org.mediaplayer.core.DataTypes;

import java.util.List;

import org.mediaplayer.core.UtilFunctions;

import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.Data.Exceptions.InvalidFormatException;
import Tools.Files.DataParser;
import Tools.Files.Util;
import Tools.Files.Data.DataType;
import Tools.Files.Data.DataTypes.*;

public class DataPlaylistEntry extends DataType {
	public DataString id;
	public DataMap<DataString> tags;
	public DataPlaylistEntry() {
		super();
		this.id = new DataString();
		this.tags = new DataMap<>(DataString::new);
	}
	public DataPlaylistEntry(DataString id, DataMap<DataString> tags) {
		this.id = id;
		this.tags = tags;
		this.created = true;
	}
	public int getLoopAmount() {
		DataString ds = tags.get().get("LoopAmount");
		if(ds == null) return 0;
		return UtilFunctions.getInt(ds.get());
	}
	public void setLoopAmount(int i) {
		created = true;
		tags.get().put("LoopAmount", new DataString("" + i));
	}
	public String getForcedNext() {
		DataString ds = tags.get().get("ForcedNext");
		if(ds == null) return null;
		return ds.get();
	}
	public void setForcedNext(String id) {
		created = true;
		tags.get().put("ForcedNext", new DataString(id));
	}
	
	
	
	@Override
	public void setData(String s) throws DataTypeException {
		if(s == null) return;
		DataParser parser = DataParser.start("{").parse().parse().expect("}");
		List<List<String>> parsed = parser.parse(s);
		id.setData(parsed.get(0).get(0));
		tags.setData(parsed.get(1).get(0));
		
		this.created = true;
	}
	@Override
	public String getData() {
		if(created)
			return "{" + Util.getSendable(',', id, tags) + "}";
		return "";
	}
	@Override
	public DataType instance() {
		return new DataPlaylistEntry();
	}
	@Override
	public DataPlaylistEntry copy() {
		return new DataPlaylistEntry(id.copy(), tags.copy());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DataPlaylistEntry dpe) {
			return dpe.id.get().equals(id.get());
		}
		return super.equals(obj);
	}

}

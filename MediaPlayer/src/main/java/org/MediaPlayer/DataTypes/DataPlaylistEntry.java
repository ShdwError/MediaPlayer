package org.MediaPlayer.DataTypes;

import java.util.List;

import org.MediaPlayer.UtilFunctions;

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
	public void setData(String s) {
		if(s == null) return;
		
		int length = s.length()-2;
		if(s.length() < 3) return;
		if(s.charAt(0) != '{' || s.charAt(length+1) != '}') throw new Error("Cant read PlaylistEntry");
		s = s.substring(1);
		List<String> parts = Util.getStringParts(s, ',', 2).two;
		
		id.setData(parts.get(0));
		tags.setData(parts.get(1));
		
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

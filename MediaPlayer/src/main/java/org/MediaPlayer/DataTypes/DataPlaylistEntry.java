package org.MediaPlayer.DataTypes;

import java.util.ArrayList;
import java.util.List;

import org.MediaPlayer.TrackEntry;

import Tools.Files.Data.DataType;
import Tools.Files.Data.Return2;
import Tools.Files.Data.Util;
import Tools.Files.Data.DataTypes.*;

public class DataPlaylistEntry extends DataType {
	public DataString id;
	public DataInt loopAmount;
	public DataArray<DataString> tags;
	public DataPlaylistEntry() {
		super();
		this.id = new DataString();
		this.loopAmount = new DataInt();
		this.tags = new DataArray<>(DataString::new);
	}
	public DataPlaylistEntry(DataString id, DataInt loopAmount, DataArray<DataString> tags) {
		this.id = id;
		this.loopAmount = loopAmount;
		this.tags = tags;
		this.created = true;
	}
	@Override
	public void setData(String s) {
		int length = s.length()-2;
		if(s.length() == 0) return;
		if(s.charAt(0) != '{' || s.charAt(length+1) != '}') throw new Error("Cant read PlaylistEntry");
		s = s.substring(1);
		
		//id
		Return2<String, String> r2 = Util.getStringPart(s);
		s = r2.one.substring(1);
		id.setData(r2.two);
		
		//loopAmount
		r2 = Util.getStringPart(s);
		s = r2.one.substring(1);
		loopAmount.setData(r2.two);
		
		//tags
		r2 = Util.getStringPart(s);
		tags.setData(r2.two);
	}
	@Override
	public String getData() {
		return "{" + Util.getSendable(id.getData()) + "," + Util.getSendable(loopAmount.getData()) + "," + Util.getSendable(tags.getData()) + "}";
	}
	@Override
	public DataType copy() {
		return new DataPlaylistEntry();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() == TrackEntry.class) {
			TrackEntry entry = (TrackEntry) obj;
			return entry.id.equals(id.get());
		}
		return super.equals(obj);
	}

}

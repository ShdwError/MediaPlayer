package org.mediaplayer.core;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.*;

public class DataPlaylistEntry extends DataClass {
	public DataString id;
	public DataMap<DataString, DataString> tags;
	public DataPlaylistEntry() {
		this.id = new DataString();
		this.tags = new DataMap<>(DataString::new, DataString::new);
		
		createData(id, tags);
	}
	public DataPlaylistEntry(DataString id) {
		this(id, new DataMap<>(DataString::new, DataString::new));
	}
	public DataPlaylistEntry(DataString id, DataMap<DataString, DataString> tags) {
		this.id = id;
		this.tags = tags;
		
		this.created = true;
		createData(id, tags);
	}
	public int getLoopAmount() {
		DataString ds = tags.get().get(new DataString("LoopAmount"));
		if(ds == null) return 0;
		return UtilFunctions.getInt(ds.get());
	}
	public void setLoopAmount(int i) {
		created = true;
		tags.put(new DataString("LoopAmount"), new DataString("" + i));
	}
	public String getForcedNext() {
		DataString ds = tags.get().get(new DataString("ForcedNext"));
		if(ds == null) return null;
		return ds.get();
	}
	public void setForcedNext(String id) {
		created = true;
		tags.put(new DataString("ForcedNext"), new DataString(id));
	}
	
	public DataPlaylistEntry copy() {
		DataMap<DataString, DataString> tagsCopy = new DataMap<>(DataString::new, DataString::new);
		tags.get().forEach((s, t) -> {
			tagsCopy.get().put(s.copy(), t.copy());
		});
		return new DataPlaylistEntry(id.copy(), tagsCopy);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DataPlaylistEntry dpe) {
			return dpe.id.equals(id);
		}
		return false;
	}
}

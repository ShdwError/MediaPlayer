package org.mediaplayer.core.connection.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataTypes.*;

public class ActionKey extends DataClass {
	private DataString type;
	private List<DataString> ids;
	public ActionKey() {
		this.type = new DataString();
		this.ids = new ArrayList<>();
		createData(type, new DataArray<>(DataString::new, ids));
	}
	public ActionKey(String typeString, DataString... idAr) {
		this.type = new DataString(typeString);
		
		this.ids = new ArrayList<>(List.of(idAr));
		createData(type, new DataArray<>(DataString::new, ids));
		created = true;
	}
	@Override
    public boolean equals(Object obj) {
        if(obj instanceof ActionKey other)
        	return this.type.equals(other.type) && this.ids.equals(other.ids);
        return false;
    }
    @Override
    public int hashCode() {
        return Objects.hash(type, ids);
    }
}

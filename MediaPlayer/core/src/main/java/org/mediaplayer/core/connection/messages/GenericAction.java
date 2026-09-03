package org.mediaplayer.core.connection.messages;

import Tools.Core.Files.Data.DataSubtype;
import Tools.Core.Files.Data.DataTypes.DataDate;

public abstract class GenericAction extends DataSubtype {
	public DataDate date;
	public boolean isAfter(GenericAction other) {
		return date.get().isAfter(other.date.get());
	}
	public abstract ActionKey getKey();
}

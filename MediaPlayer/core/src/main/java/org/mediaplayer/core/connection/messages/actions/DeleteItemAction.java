package org.mediaplayer.core.connection.messages.actions;

import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataTypes.DataDate;
import Tools.Core.Files.Data.DataTypes.DataString;

public class DeleteItemAction extends GenericAction {
	private DataString itemId;
	public DeleteItemAction()  {
		this.date = new DataDate();
		this.itemId = new DataString();
		
		createData(date, itemId);
	}
	public DeleteItemAction(LocalDateTime dateTime, String itemId) {
		this(new DataDate(dateTime), new DataString(itemId));
	}
	public DeleteItemAction(DataDate date, DataString itemId) {
		this.date = date;
		this.itemId = itemId;
		
		createData(date, itemId);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(getDataType(), itemId);
	}
	public String getId() {
		return itemId.get();
	}
	
}

package org.mediaplayer.core.connection.messages.actions;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.GenericAction;

import Tools.Core.Files.Data.DataClass;
import Tools.Core.Files.Data.DataTypes.*;

public class RenameItemAction extends GenericAction {
	private DataString itemId;
	private DataString newPath;
	
	public RenameItemAction()  {
		this.date = new DataDate();
		this.itemId = new DataString();
		this.newPath = new DataString();
		
		createData(date, itemId, newPath);
	}
	public RenameItemAction(LocalDateTime dateTime, String trackId, String newPath) {
		this(new DataDate(dateTime), new DataString(trackId), new DataString(newPath));
	}
	public RenameItemAction(DataDate date, DataString trackId, DataString newPath) {
		this.date = date;
		this.itemId = trackId;
		this.newPath = newPath;
		
		createData(date, this.itemId, this.newPath);
		created = true;
	}
	@Override
	public ActionKey getKey() {
		return new ActionKey(getDataType(), itemId);
	}
	public String getId() {
		return itemId.get();
	}
	public Path getPath() {
		return Path.of(newPath.get());
	}
}

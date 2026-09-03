package org.mediaplayer.core.connection.messages;

import java.util.ArrayList;
import java.util.List;

import Tools.Core.Files.Data.DataTypes.DataArray;
import Tools.Core.Files.Data.DataTypes.DataInt;

public class Change extends GenericMessage {
	private List<Action> actions;
	private DataInt version;
	public Change() {
		this.actions = new ArrayList<>();
		this.version = new DataInt();
		createData(new DataArray<Action>(Action::new, actions), version);
	}
	public Change(DataInt version, Action... actions) {
		this(List.of(actions), version);
	}
	public Change(List<Action> actions, DataInt version) {
		this.actions = actions;
		this.version = version;
		createData(new DataArray<Action>(Action::new, actions), version);
		created = true;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setVersion(int version) {
		this.version.set(version);
		created = true;
	}
	public int getVersion()  {
		return version.get();
	}
	public void addAction(Action action)  {
		actions.add(action);
		created = true;
	}
}

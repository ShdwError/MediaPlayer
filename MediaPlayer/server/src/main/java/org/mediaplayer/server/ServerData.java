package org.mediaplayer.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mediaplayer.core.connection.messages.Action;
import org.mediaplayer.core.connection.messages.ActionKey;
import org.mediaplayer.core.connection.messages.Change;
import org.mediaplayer.core.connection.messages.Sync;

import Tools.Core.Files.Data.DataAdapter;
import Tools.Core.Files.Data.DataType;
import Tools.Core.Files.Data.DataTypes.DataArray;
import Tools.Core.Files.Data.DataTypes.DataMap;

public class ServerData extends DataAdapter {
	public List<Action> versions;
	public Map<ActionKey, Action> actionMap;
	public ServerData() {
		this.versions = new ArrayList<>();
		this.actionMap = new ConcurrentHashMap<>();
	}
	
	@Override
	public void createMapping(Map<String, DataType> data) {
		data.put("versions", new DataArray<Action>(Action::new, versions)); 
		data.put("actionMap", new DataMap<ActionKey, Action>(ActionKey::new, Action::new));
	}
	public boolean isValid(Action action) {
		ActionKey key = action.get().getKey();
		Action prev = actionMap.get(key);
		return prev == null || !prev.get().isAfter(action.get());
	}
	public void addAction(Action action) {
		ActionKey key = action.get().getKey();
		Action prev = actionMap.get(key);
		if(prev != null && prev.get().isAfter(action.get())) {
			//Error
			return;
		}
		else {
			synchronized(versions) {
				versions.add(action);
			}
			actionMap.put(key, action);
			return;
		}
		
	}
	public int currentVersion() {
		return versions.size();
	}
	public Change syncFrom(int version) {
		Change change = new Change();
		
		if(version < 0) version = 0;
		synchronized(versions) {
			for(int i = version; i < versions.size(); i++) {
				change.addAction(versions.get(i));
			}
			change.setVersion(currentVersion());
		}
		return change;
	}

}

package org.MediaPlayer;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Data.DataType;
import Tools.Files.Data.Util;
import Tools.Files.Data.DataTypes.*;

public class Session extends Playlist {
	public DataInt pos;
	public DataBoolean loop;
	public DataDate created;
	public DataDate lastOpened;
	public Session(Path path, String id) {
		super(path, id);
	}
	@Override
	public String getName() {
		return Util.getNameAndType(Path.of("Sessions").relativize(path).toString())[0];
	}
	public DataPlaylistEntry getCurrent() {
		if(pos.get() < size())
			return get(pos.get());
		return null;
	}
	public DataPlaylistEntry getNext() {
		pos.add(1);
		if(pos.get() >= size()) {
			if(loop.get()) pos.set(0);
			else return null;
		}
		return get(pos.get());
	}
	public void shuffle(boolean skipAktive) {
		Random random = new Random();
		int length = size();
		for(int i = length-1; i > 0; i--) {
			if(skipAktive && i == pos.get()) continue;
			int j = random.nextInt(i+1);
			if(skipAktive) 
				while(j == pos.get()) j = random.nextInt(i+1);
			DataPlaylistEntry temp = get(i);
			set(i, j);
			set(j, temp);
		}
	}

	@Override
	public void createData(Map<String, DataType> data) {
		this.description = (DataString) data.get("Description");
		createPlaylist(((DataArray<DataPlaylistEntry>) data.get("Playlist")).get());
		this.pos = (DataInt) data.get("Position");
		this.loop = (DataBoolean) data.get("Loop");
		this.created = (DataDate) data.get("CreatedOn");
		this.lastOpened = (DataDate) data.get("LastOpened");
	}

}

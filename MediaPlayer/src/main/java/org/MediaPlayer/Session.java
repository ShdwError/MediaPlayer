package org.MediaPlayer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Util;
import Tools.Files.Data.DataType;
import Tools.Files.Data.Return2;
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
	public DataPlaylistEntry moveTo(int i) {
		if(i < 0 || i >= size()) return null;
		pos.set(i);
		return get(i);
	}
	public DataPlaylistEntry getPrevious() {
		if(pos.get() == 0) return null;
		pos.add(-1);
		return get(pos.get());
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
	
	
	
	
	public void shuffle(boolean skipActive) {
		Random random = new Random();
		
	    DataPlaylistEntry currentEntry = getCurrent();
		
		int length = size();
		for(int i = length-1; i > 0; i--) {
			if(skipActive && i == pos.get()) continue;
			
			int j = random.nextInt(i+1);
			
			if(skipActive && j == pos.get()) 
				j = (j == 0) ? 1 : 0;
			
			swap(i, j);
		}
		reorganize(skipActive, currentEntry);
	}
	public void reorganize(boolean keepActive, DataPlaylistEntry currentEntry) {
		List<DataPlaylistEntry> ordered = super.reorganize();

	    if(!keepActive) return;
	    
	    //System.out.println(ordered);
	    
	    
	    int curPos = ordered.indexOf(currentEntry);
	    int shouldPos = pos.get();
	    	
	   	//where currentEntry is right now
	   	int[] curPosData = getChain(ordered, curPos);
	   	//Where currentEntry will end up
	   	int[] shouldPosData = getChain(ordered, shouldPos);
	   	
	   	int overheadLeft = (curPos - curPosData[0]) - (shouldPos - shouldPosData[0]);
	   	int overheadRight = (curPosData[1] - curPos) - (shouldPosData[1] - shouldPos);
	   	//equal length & spread
	   	if(overheadLeft == 0 && overheadRight == 0) {
	   		//System.out.println("Fall 0");
	   		for(int i = 0; i < curPosData[2]; i++) {
	   			swap(curPosData[0]+i, shouldPosData[0]+i);
	   		}
		    pos.set(get().indexOf(currentEntry));
	   		return;
	   	}
	   	List<DataPlaylistEntry> reorganized = new ArrayList<>();
	   	
	   	//currentEntry moved to the left
	   	if(shouldPos < curPos) {
	   		//Same length at left
		   	if(overheadLeft == 0) {
		   		//System.out.println("Fall 1");
		   		for(int i = 0; i < shouldPosData[0]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all currentEntries
		   		for(int i = curPosData[0]; i <= curPosData[1]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all remaining
		   		for(int i = shouldPosData[0]; i < size(); i++) {
		   			if(i < curPosData[0] || i > curPosData[1]) {
		   				reorganized.add(ordered.get(i));
		   			}
		   		}
		   	}
		   	//move entries from the left to the end
		   	//curPosChain left > shouldPosChain left
		   	else if(overheadLeft > 0) {
		   		//Check if left of the shouldPos has enough entries to move to the end
		   		List<int[]> combination = findFittingCombination(ordered, 0, shouldPosData[0], overheadLeft, new int[0]);
		   		
		   		Set<String> notAdd = new HashSet<>();
		   		List<DataPlaylistEntry> addToEnd = new ArrayList<>();
		   		
		   		if(combination != null)  {
		   			//System.out.println("Fall 3");
		   			int length = combination.size();
		   			//Go through the combination...
		   			for(int i = 1; i < length; i++) {
		   				int[] chain = combination.get(i);
		   				//... and add all entries to the "not add" Set and to the end
		   				for(int j = chain[0]; j <= chain[1]; j++) {
		   					DataPlaylistEntry entry = ordered.get(j);
		   					addToEnd.add(entry);
		   					notAdd.add(entry.id.get());
		   				}
		   			}
		   		}
		   		//Has to be taken from the right
		   		else {
			   		//System.out.println("Fall 2");
		   			//If your at the beginning, you´ve done something wrong
		   			if(shouldPosData[0] > 0) {
			   			//Get the chain to the left of shouldPos chain and move it to the end
			   			int[] chainBefore = getChain(ordered, shouldPosData[0]-1);
			   			for(int i = chainBefore[0]; i <= chainBefore[1]; i++) {
		   					DataPlaylistEntry entry = ordered.get(i);
		   					addToEnd.add(entry);
		   					notAdd.add(entry.id.get());
		   				}
			   			//Find combination on the right that can substitute the moved chain
			   			combination = findFittingCombination(ordered, shouldPosData[1]+1, ordered.size(), chainBefore[2]-overheadLeft, new int[] {curPosData[0], curPosData[1]});
			   			
			   			//If combination is null, there is nothing I can do and have to change the pos
			   			if(combination != null) {
				   			int length = combination.size();
				   			//Go through the combination...
				   			for(int i = 1; i < length; i++) {
				   				int[] chain = combination.get(i);
				   				//... and add all entries to the "not add" Set and to the front
				   				for(int j = chain[0]; j <= chain[1]; j++) {
				   					DataPlaylistEntry entry = ordered.get(j);
				   					reorganized.add(entry);
				   					notAdd.add(entry.id.get());
				   				}
				   			}
			   			}
			   		}
		   		}
		   		//Add all entries that werent flagged not to be added (the chain before or the entries added to the end)
		   		for(int i = 0; i < shouldPosData[0]; i++) {
		   			if(!notAdd.contains(ordered.get(i).id.get()))
		   				reorganized.add(ordered.get(i));
		   		}
		   		//Add all currentEntries
		   		for(int i = curPosData[0]; i <= curPosData[1]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all remaining that werent flagged not to be added (entries moved to the front)
		   		for(int i = shouldPosData[0]; i < size(); i++) {
		   			if(i < curPosData[0] || i > curPosData[1]) {
		   				if(!notAdd.contains(ordered.get(i).id.get()))
		   					reorganized.add(ordered.get(i));
		   			}
		   		}
		   		//Add all that should be at the end
		   		reorganized.addAll(addToEnd);
		   	}
		   	//Move entries from the right to the left
		   	else {
		   		//System.out.println("Fall 4");
		   		//Combination that can be added to the left
		   		List<int[]> combination = findFittingCombination(ordered, shouldPosData[1]+1, ordered.size(), -overheadLeft, new int[] {curPosData[0], curPosData[1]});
		   		Set<String> notAdd = new HashSet<>();
		   		
		   		//If combination is null, there is nothing I can do and have to change the pos
		   		if(combination != null) {
		   			int length = combination.size();
		   			//Go through the combination...
		   			for(int i = 1; i < length; i++) {
		   				int[] chain = combination.get(i);
		   				//... and add all entries to the front
		   				for(int j = chain[0]; j <= chain[1]; j++) {
		   					DataPlaylistEntry entry = ordered.get(j);
		   					reorganized.add(entry);
		   					notAdd.add(entry.id.get());
		   				}
		   			}
		   		}
		   		else System.out.println("Could not perfectly arrange curPos");
		   		for(int i = 0; i < shouldPosData[0]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all currentEntries
		   		for(int i = curPosData[0]; i <= curPosData[1]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all remaining that werent flagged not to be added (entries moved to the front)
		   		for(int i = shouldPosData[0]; i < ordered.size(); i++) {
		   			if(i < curPosData[0] || i > curPosData[1]) {
		   				if(!notAdd.contains(ordered.get(i).id.get())) {
		   					reorganized.add(ordered.get(i));
		   				}
		   			}
		   		}
		   	}
	    }
	   	//currentEntry moved to the right
	   	else {
	   		//Same length at Right
		   	if(overheadRight == 0) {
		   		//System.out.println("Fall 5");
		   		//Add all that arent in curPosData till shouldPos Chain
		   		for(int i = 0; i <= shouldPosData[1]; i++) {
		   			if(i < curPosData[0] || i > curPosData[1]) {
		   				reorganized.add(ordered.get(i));
		   			}
		   		}
		   		//Add all currentEntries
		   		for(int i = curPosData[0]; i <= curPosData[1]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all after shouldPos Chain
		   		for(int i = shouldPosData[1]+1; i < ordered.size(); i++) {
		   			reorganized.add(get(i));
		   		}
		   	}
		   	else if(overheadRight > 0) {
		   		//Check if right of the shouldPos has enough entries to move to the beginning
		   		List<int[]> combination = findFittingCombination(ordered, shouldPosData[1]+1, ordered.size(), overheadRight, new int[0]);
		   		Set<String> notAdd = new HashSet<>();
		   		
		   		if(combination != null) {
		   			//System.out.println("Fall 7");
		   			int length = combination.size();
		   			//Go through the combination...
		   			for(int i = 1; i < length; i++) {
		   				int[] chain = combination.get(i);
		   				//... and add all entries to the "not add" Set and to the front
		   				for(int j = chain[0]; j <= chain[1]; j++) {
		   					DataPlaylistEntry entry = ordered.get(j);
		   					reorganized.add(entry);
		   					notAdd.add(entry.id.get());
		   				}
		   			}
		   		}
		   		//Has to be taken from the left
		   		else {
			   		//System.out.println("Fall 6");
			   		//If your at the end, you´ve done something wrong
		   			if(shouldPosData[1]+1 < ordered.size()) {
			   			//Get the chain to the right of shouldPos chain and move it to the front
			   			int[] chainNext = getChain(ordered, shouldPosData[1]+1);
			   			for(int i = chainNext[0]; i <= chainNext[1]; i++) {
		   					DataPlaylistEntry entry = ordered.get(i);
		   					reorganized.add(entry);
		   					notAdd.add(entry.id.get());
		   				}
			   			//Find combination on the left that can substitute the moved chain
			   			combination = findFittingCombination(ordered, 0, shouldPosData[0], chainNext[2]-overheadRight, new int[] {curPosData[0], curPosData[1]});
			   			
			   			//If combination is null, there is nothing I can do and have to change the pos
			   			if(combination != null) {
				   			int length = combination.size();
				   			//Go through the combination...
				   			for(int i = 1; i < length; i++) {
				   				int[] chain = combination.get(i);
				   				//... and add all entries to the "not add" Set and to the front
				   				for(int j = chain[0]; j <= chain[1]; j++) {
				   					DataPlaylistEntry entry = ordered.get(j);
				   					reorganized.add(entry);
				   					notAdd.add(entry.id.get());
				   				}
				   			}
			   			}
			   		}
		   		}
		   		
		   		//Add all entries that werent flagged not to be added (entries added to the end)
		   		for(int i = 0; i < shouldPosData[0]; i++) {
		   			if(!notAdd.contains(ordered.get(i).id.get()))
		   				reorganized.add(ordered.get(i));
		   		}
		   		//Add all entries after shouldPos Chain except those flagged not to be added (entries moved to the front)
		   		for(int i = shouldPosData[0]; i < ordered.size(); i++) {
		   			if(!notAdd.contains(ordered.get(i).id.get()))
		   				reorganized.add(ordered.get(i));
		   		}
		   	}
		   	//Move entries from the left to the end
		   	else {
		   		//System.out.println("Fall 8");
		   		//Combination that can be added to the end
		   		List<int[]> combination = findFittingCombination(ordered, 0, shouldPosData[0], -overheadRight, new int[] {curPosData[0], curPosData[1]});
		   		Set<String> notAdd = new HashSet<>();
		   		List<DataPlaylistEntry> addToEnd = new ArrayList<>();
		   		
		   		//If combination is null, there is nothing I can do and have to change the pos
		   		if(combination != null) {
		   			int length = combination.size();
		   			//Go through the combination...
		   			for(int i = 1; i < length; i++) {
		   				int[] chain = combination.get(i);
		   				//... and add all entries to the end
		   				for(int j = chain[0]; j <= chain[1]; j++) {
		   					DataPlaylistEntry entry = ordered.get(j);
		   					addToEnd.add(entry);
		   					notAdd.add(entry.id.get());
		   				}
		   			}
		   		}
		   		else System.out.println("Could not perfectly arrange curPos");
		   		//Add all not in currentEntry that werent moved to the end
		   		for(int i = 0; i <= shouldPosData[1]; i++) {
		   			if(i < curPosData[0] || i > curPosData[1]) {
			   			if(!notAdd.contains(ordered.get(i).id.get())) {
			   				System.out.println(i);
			   				reorganized.add(ordered.get(i));
			   			}
		   			}
		   		}
		   		//Add all in curData
		   		for(int i = curPosData[0]; i <= curPosData[1]; i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		//Add all after shouldPos Chain
		   		for(int i = shouldPosData[1]+1; i < ordered.size(); i++) {
		   			reorganized.add(ordered.get(i));
		   		}
		   		
		   		//Add all moved to the end
		   		reorganized.addAll(addToEnd);
		   	}
	   	}
	   	
	   	if(reorganized.size() != ordered.size()) {
	   		System.out.println();
	   		System.out.println(reorganized.size());
	   		System.out.println(ordered.size());
	   		throw new IllegalStateException("Reorganization corrupted playlist");
	   	}
	   	
	   	//irrelevant, if no problems occurred
	    pos.set(reorganized.indexOf(currentEntry));
	    
	    set(reorganized);
	}
	private List<int[]> findFittingCombination(List<DataPlaylistEntry> ordered, int startPos, int endPos, int searchAmount, int[] ignore) {
		if(searchAmount == 0) return null;
		//First List: Possible combinations
		//Second List: Items in combination; First: length of combination
		//Array[0]: firstPos, Array[1]: lastPos, Array[2] = length
		List<List<int[]>> possible = new ArrayList<>();
		for(int i = startPos; i < endPos; i++) {
			int[] chain = getChain(ordered, i);
			i = chain[1];
			//If i in ignore
			boolean shouldIgnore = false;
			if(ignore.length % 2 == 0 && ignore.length > 1) {
				for(int j = 0; j < ignore.length-1; j+=2) {
					if(i >= ignore[j] && i <= ignore[j+1]) {
						shouldIgnore = true;
						break;
					}
				}
			}
			if(shouldIgnore) continue;
			
			if(chain[2] == searchAmount) 
				return List.of(new int[] {chain[2]}, chain);
			if(searchAmount < chain[2]) continue;
			
			//How many combinations could be accounted for with chain.length
			boolean[] combs = new boolean[searchAmount-chain[2]];
			//if length fits in possible
			for(int j = 0; j < possible.size(); j++) {
				List<int[]> list = possible.get(j);
				int curSize = list.get(0)[0];
				//Fits
				if(curSize < combs.length) {
					list.get(0)[0] += chain[2];
					list.add(chain);
					combs[curSize] = true;
				}
				//Right Combination Found
				else if(curSize == combs.length) {
					list.get(0)[0] += chain[2];
					list.add(chain);
					return list;
				}
					
			}
			//check if you searched through every combination
			boolean fitInEverything = true;
			for(boolean b: combs) {
				if(!b) fitInEverything = false;
			}
			//add new combination
			if(!fitInEverything) {
				ArrayList<int[]> add = new ArrayList<>();
				add.add(new int[] {chain[2]});
				add.add(chain);
			}
		}
		return null;
	}
	private int[] getChain(List<DataPlaylistEntry> ordered, int pos) {
		int start = pos, end = pos;
		while(start > 0 && ordered.get(start-1).getForcedNext() != null) {
			String forcedNext = ordered.get(start-1).getForcedNext();
			if(forcedNext.equals(ordered.get(start).id.get())) {
				start--;
			}
		}
		while(end < ordered.size()-1 && ordered.get(end).getForcedNext() != null) {
			String forcedNext = ordered.get(end).getForcedNext();
			if(forcedNext.equals(ordered.get(end+1).id.get())) {
				end++;
			}
		}
		return new int[] {start, end, end-start+1};
	}
	
	public void swap(int i, int j) {
		DataPlaylistEntry temp = get(i);
		set(i, get(j));
		set(j, temp);
	}

}

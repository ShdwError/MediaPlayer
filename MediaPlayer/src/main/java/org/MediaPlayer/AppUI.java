package org.MediaPlayer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Util;
import Tools.Files.Data.DataTypes.DataArray;
import Tools.Files.Data.DataTypes.DataInt;
import Tools.Files.Data.DataTypes.DataMap;
import Tools.Files.Data.DataTypes.DataString;
import javafx.application.Platform;

public class AppUI {
	App app;
	public AppUI(App app) {
		this.app = app;
	}
	public void startUI() {
		Scanner sc = new Scanner(System.in);
		//Thread nur wegen Scanner
		new Thread(() -> {
			String s = "";
			if(app.sData.currentSession.created) {
				Session session = app.sessions.get(app.sData.currentSession.get());
				if(session != null) 
					System.out.println("Continue in " + session.getName() + "?");
					s = sc.next();
					if(s.equals("yes"))
						Platform.runLater(() -> {
							app.playSession(session);
						});
			}
			printMenu();
			while(true) {
				s = sc.next();
				if(s.equals("menu")) {
					printMenu();
				}
				else if(s.equals("current")) {
					printCurrent();
				}
				else if(s.equals("in")) {
					if(app.currentTrack == null) {
						System.out.println("No current Track");
						continue;
					}
					for(String plstring: app.currentTrack.inPlaylists) {
						Playlist playlist = app.playlists.get(plstring);
						if(playlist != null) System.out.println(playlist.getName());
					}
				}
				else if(s.equals("list")) {
					s = sc.next();
					if(s.equals("current"))
						printPlaylist(app.currentSession);
					else {
						Playlist playlist = findPlaylist(s, sc);
						if(playlist != null)
							printPlaylist(playlist);
					}
				}
				else if(s.equals("stlist")) {
					printSoundtracks(false);
					System.out.println("Trackcount: " + app.soundtracks.size());
				}
				else if(s.equals("playst")) {
					printSoundtracks(true);
					s = sc.next();
					TrackEntry entry = app.soundtracks.get(s);
					app.currentSession = null;
					if(entry == null) System.out.println("Cannot find " + s);
					else Platform.runLater(() -> {
						app.playSoundtrack(entry);
					});
				}
				else if(s.equals("plist")) {
					printPlaylists(false);
				}
				else if(s.equals("pause")) {
					if(App.mediaPlayer != null)
						App.mediaPlayer.pause();
				}
				else if(s.equals("cont")) {
					if(App.mediaPlayer != null) {
						App.mediaPlayer.play();
					}
				}
				else if(s.equals("stop")) {
					app.stopSession();
				}
				else if(s.equals("rename"))  {
					s = sc.next();
					if(s.equals("current")) {
						String type = sc.next();
						s = sc.nextLine().strip();
						while(s.equals("")) sc.nextLine().strip();
						if(type.equals("session")) {
							if(app.currentSession != null) {
								Path newPath = app.currentSession.path.getParent().resolve(Path.of(s + ".txt"));
								app.renameSession(app.currentSession, app.fTree.getNextFreeFileName(newPath));
								System.out.println("Renaming Session to " + app.currentSession.getName());
							}
						}
						else if(type.equals("soundtrack")) {
							if(app.currentTrack != null) {
								Path newPath = app.currentTrack.path.getParent().resolve(Path.of(s + ".txt"));
								app.renameSoundtrack(app.currentTrack, app.fTree.getNextFreeFileName(newPath));
								System.out.println("Renamed Soundtrack to " + app.currentTrack.getName());
							}
						}
					}
					else {
						String type = s;
						if(type.equals("session")) {
							printSessions();
							String id = sc.next();
							Session session = app.sessions.get(id);
							if(session == null) continue;
							Path newPath = session.path.getParent().resolve(Path.of(s + ".txt"));
							app.renameSession(session, app.fTree.getNextFreeFileName(newPath));
						}
						else if(type.equals("playlist")) {
							printPlaylists(true);
							String id = sc.next();
							Playlist playlist = app.playlists.get(id);
							if(playlist == null) continue;
							Path newPath = playlist.path.getParent().resolve(Path.of(s + ".txt"));
							app.renamePlaylist(playlist, app.fTree.getNextFreeFileName(newPath));
						}
						else if(type.equals("soundtrack")) {
							printSoundtracks(true);
							String id = sc.next();
							TrackEntry soundtrack = app.soundtracks.get(id);
							if(soundtrack == null) continue;
							Path newPath = soundtrack.path.getParent().resolve(Path.of(s + ".txt"));
							app.renameSoundtrack(soundtrack, app.fTree.getNextFreeFileName(newPath));
						}
					}
				}
				else if(s.equals("delete")) {
					s = sc.next();
					if(s.equals("session")) {
						printSessions();
						s = sc.next();
						Session session = app.sessions.get(s);
						if(session != null) {
							System.out.println("Session deleted");
							app.deleteSession(session);
						}
						else System.out.println("Cannot find Session");
					}
					else if(s.equals("playlist")) {
						printPlaylists(true);
						s = sc.next();
						Playlist playlist = app.playlists.get(s);
						if(playlist != null) {
							System.out.println("Playlist deleted");
							app.deletePlaylist(playlist);
						}
						else System.out.println("Cannot find Playlist");
					}
					else if(s.equals("soundtrack")) {
						printSoundtracks(true);
						s = sc.next();
						TrackEntry entry = app.soundtracks.get(s);
						if(entry != null) {
							System.out.println();
							app.deleteSoundtrack(entry);
						}
						else System.out.println("Cannot find Soundtrack");
					}
					else if(s.equals("current")) {
						s = sc.next();
						if(s.equals("session")) {
							if(app.currentSession != null) app.deleteSession(app.currentSession);
							else System.out.println("No active Session");
						}
						else if(s.equals("soundtrack")) {
							if(app.currentTrack != null) app.deleteSoundtrack(app.currentTrack);
							else System.out.println("No active Soundtrack");
						}
						else {
							System.out.println("Unknown command");
						}
					}
					else {
						System.out.println("Unknown command");
					}
				}
				else if(s.equals("addto")) {
					s = sc.nextLine().strip();
					while(s.equals("")) s = sc.nextLine().strip();
					Playlist playlist = findPlaylist(s, sc);
					if(playlist != null) {
						System.out.println(app.currentTrack.getName() + " added to " + playlist.getName());
						app.currentTrack.inPlaylists.add(playlist.id);
						playlist.add(app.currentSession.getCurrent().copy());
					}
				}
				else if(s.equals("removefrom")) {
					if(app.currentTrack == null) {
						System.out.println("No active Soundtrack");
						continue;
					}
					s = sc.nextLine().strip();
					while(s.equals("")) s = sc.nextLine().strip();
					Playlist playlist = findPlaylist(s, sc);
					if(playlist != null && app.currentTrack.inPlaylists.contains(playlist.id)) {
						System.out.println(app.currentTrack.getName() + " removed from " + playlist.getName());
						app.currentTrack.inPlaylists.remove(playlist.id);
						playlist.remove(app.currentTrack);
					}
				}
				else if(s.equals("linkto")) {
					s = sc.next();
					int pos = app.currentSession.pos.get();
					Integer pos2 = null;
					Session session = app.currentSession;
					
					if(s.equals("next"))
						pos2 = pos+1;
					else 
						pos2 = UtilFunctions.getInt(s);
					
					if(pos2 == null) {
						System.out.println("Not a Number");
					}
					else if(pos2 >= session.size() || pos2 < 0) {
						System.out.println("Out of Bounds");
					}
					else if(pos2 == pos) {
						System.out.println("Same number");
					}
					else {
						TrackEntry entry2 = app.soundtracks.get(session.getId(pos2));
						if(entry2 != null) {
							System.out.println(app.currentTrack.getName() + " is now always next to " + entry2.getName());
							session.get(pos).setForcedNext(entry2.id);
						}
						session.reorganize(true, session.getCurrent());
					}
				}
				else if(s.equals("modify"))  {
					s = sc.next();
					if(s.equals("playlist")) {
						s = sc.nextLine().strip();
						while(s.equals("")) s = sc.nextLine().strip();
						Playlist playlist = findPlaylist(s, sc);
						if(playlist == null) {
							System.out.println("Playlist not found");
							continue;
						}
						do {
							printPlaylist(playlist);
							System.out.println();
							System.out.println("Modifiy Playlist");
							System.out.println("- remove *number*");
							System.out.println("- remove all *number* [...] ;");
							System.out.println("- remove from *number* [to] *number*");
							System.out.println("- move/copy *number* to *name*");
							System.out.println("- move/copy all *number* [...] to *name*");
							System.out.println("- move/copy from *number* [to] *number* to *name*");
							System.out.println("- add playlist *name*");
							System.out.println("- modify entry *number*");
							System.out.println("- finish");
							s = sc.next();
							if((s.equals("remove") || s.equals("move") || s.equals("copy")) && playlist.size() > 0) {
								boolean copy = s.equals("move") || s.equals("copy");
								boolean remove = !s.equals("copy");
								List<DataPlaylistEntry> removedTracks = new ArrayList<>();
								s = sc.next();
								if(s.equals("all")) { 
									List<String> all = readStringsTill(sc, ";");
									for(String pos: all) {
										Integer i = UtilFunctions.getInt(pos);
										if(i == null) System.out.println(pos + " is not a Number");
										else if(i < playlist.size() && i >= 0) {
											removedTracks.add(playlist.get(i));
										}
										else {
											System.out.println(i + " is Out of Bounds");
										}
									}
								}
								else if(s.equals("from")) {
									String pos1s = sc.next();
									String pos2s = sc.next();
									if(pos2s.equals("to")) pos2s = sc.next();
									Integer pos1 = UtilFunctions.getInt(pos1s);
									Integer pos2 = UtilFunctions.getInt(pos2s)+1;
									if(pos1 < 0) pos1 = 0;
									if(pos1 > playlist.size()) {
										System.out.println("First Position cannot be greater than Size of " + playlist.getName());
										continue;
									}
									if(pos2 > playlist.size()) pos2 = playlist.size();
									if(pos2 <= 0 || pos1 >= pos2) continue;
									for(int i = pos1; i < pos2; i++) {
										removedTracks.add(playlist.get(i));
									}
								}
								else {
									Integer pos = UtilFunctions.getInt(s);
									if(pos == null) {
										System.out.println("Not a Number");
									}
									else if(pos < playlist.size() && pos >= 0) {
										removedTracks.add(playlist.get(pos));
									}
									else {
										System.out.println("Out of Bounds");
									}
								}
								if(copy) {
									s = sc.next();
									if(!s.equals("to")) {
										System.out.println("Command error");
										continue;
									}
									s = sc.nextLine().strip();
									while(s.equals("")) s = sc.nextLine().strip();
									Playlist to = findPlaylist(s, sc);
									if(to == null) {
										System.out.println("Playlist " + s + " not found");
										continue;
									}
									for(DataPlaylistEntry dpe: removedTracks) {
										TrackEntry entry = app.soundtracks.get(dpe.id.get());
										if(entry != null) {
											if(remove) {
												entry.inPlaylists.remove(playlist.id);
												System.out.println(entry.getName() + " moved to " + to.getName());
											}
											else System.out.println(entry.getName() + " copied to " + to.getName());
											entry.inPlaylists.add(to.id);
											to.add(dpe.copy());
										}
										if(remove) playlist.remove(entry);
									}
								}
								else {
									for(DataPlaylistEntry dpe: removedTracks) {
										TrackEntry entry = app.soundtracks.get(dpe.id.get());
										if(entry != null) {
											entry.inPlaylists.remove(playlist.id);
											System.out.println(entry.getName() + " removed from " + playlist.getName());
										}
										playlist.remove(dpe);
									}
								}
							}
							else if(s.equals("add")) {
								s = sc.next();
								if(s.equals("playlist")) {
									s = sc.nextLine().strip();
									while(s.equals("")) s = sc.nextLine().strip();
									Playlist subplaylist = findPlaylist(s, sc);
									if(subplaylist == null) {
										System.out.println("Playlist not found");
										continue;
									}
									playlist.addSubplaylist(subplaylist.id);
								}
							}
							else if(s.equals("modify")) {
								s = sc.next();
								if(s.equals("entry")) {
									s = sc.next();
									Integer pos = UtilFunctions.getInt(s);
									if(pos == null) {
										System.out.println("Not a Number");
									}
									else if(pos >= playlist.size() || pos < 0) {
										System.out.println("Out of Bounds");
									}
									else {
										TrackEntry entry = app.soundtracks.get(playlist.getId(pos));
										if(entry != null) {
											System.out.println("Modify Entry " + entry.getName());
											System.out.println("- setnext *number*");
											s = sc.next();
											if(s.equals("setnext")) {
												s = sc.next();
												Integer pos2 = UtilFunctions.getInt(s);
												if(pos2 == null) {
													System.out.println("Not a Number");
												}
												else if(pos2 >= playlist.size() || pos2 < 0) {
													System.out.println("Out of Bounds");
												}
												else if(pos2 == pos) {
													System.out.println("Same number");
												}
												else {
													TrackEntry entry2 = app.soundtracks.get(playlist.getId(pos2));
													if(entry2 != null) {
														System.out.println(entry.getName() + " is now always next to " + entry2.getName());
														playlist.get(pos).setForcedNext(entry2.id);
													}
													playlist.reorganize();
												}
											}
										}
									}
								}
							}
						} while(!(s.equals("finish") || s.equals("quit")));
					}
				}
				else if(s.equals("play")) {
					printPlaylists(true);
					List<Playlist> sessionParts = new ArrayList<>();
					s = sc.next();
					
					boolean shuffle = false, looping = false;
					String name = "";
					boolean ownName = false;
					do {
						if(s.equals("shuffle")) shuffle = true;
						else if(s.equals("loop")) looping = true;
						else if(s.equals("as")) {
							s = sc.nextLine().strip();
							while(s.equals("")) s = sc.nextLine().strip();
							name = s;
							ownName = true;
						} 
						else {
							Playlist playlist = app.playlists.get(s);
							if(playlist != null) {
								if(!ownName) {
									if(!name.equals("")) name += "+";
									
									name += playlist.getName().replace(".txt", "");
								}
								sessionParts.add(playlist);
							}
							else System.out.println("Cannot find " + s);
						}
						s = sc.next();
					} while(!(s.equals("run") || s.equals("back")));
					
					if(s.equals("back")) continue;
					if(s.equals("quit")) break;
					
					try {
						name = name.replace("/", "-").replace("\\", "-");
						Path sessionPath = app.fTree.getNextFreeFileName(Path.of("Sessions", name + ".txt"));
						Session session = app.createSession(sessionParts, sessionPath, looping, shuffle); 
						Platform.runLater(() -> {
							app.playSession(session);
						});
						app.save();
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(s.equals("resume")) {
					System.out.println("Sessions");
					printSessions();
					s = sc.next();
					Session session = app.sessions.getOrDefault(s, null);
					if(session != null)
						app.playSession(session);
				}
				else if(s.equals("shuffle")) {
					if(app.currentSession != null) {
						app.currentSession.shuffle(true);
						printPlaylistPreview();
					}
				}
				else if(s.equals("next")) {
					if(app.currentSession == null) {
						continue;
					}
					Platform.runLater(() -> {
						app.playNextTrack();
					});	
				}
				else if(s.equals("goto")) {
					if(app.currentSession == null) {
						continue;
					}
					s = sc.next();
					Integer pos = UtilFunctions.getInt(s);
					if(pos != null && pos >= 0 && pos < app.currentSession.size()) app.moveToSessionPos(pos);
				}
				else if(s.equals("prev")) {
					Platform.runLater(() -> {
						app.playPreviousTrack();
					});	
				}
				else if(s.equals("create")) {
					printSoundtracks(true);
					List<String> tracks = new ArrayList<>();
					Set<String> uniqueTracks = new HashSet<>();
					List<String> subLists = new ArrayList<>();
					Set<String> uniqueSubLists = new HashSet<>();
					do {
						System.out.println();
						System.out.println("Creating Soundtrack");
						System.out.println("- add *id* [...];");
						System.out.println("- addpl *id* [...];");
						System.out.println("- get *id* [to] *id");
						System.out.println("- back");
						System.out.println("- create");
						System.out.println();
						s = sc.next();
						if(s.equals("add")) {
							List<String> trids = readStringsTill(sc, ";");
							for(String id: trids) {
								TrackEntry entry = app.soundtracks.get(id);
								if(entry == null) System.out.println("Cannot find " + id);
								else if(uniqueTracks.contains(id)) System.out.println(id + " is already in Playlist");
								else {
									uniqueTracks.add(id);
									tracks.add(id);
								}
							}
						}
						else if(s.equals("get")) {
							String id1 = sc.next();
							String id2 = sc.next();
							if(id2.equals("to")) id2 = sc.next();
							boolean inRange = false;
							for(Map.Entry<String, TrackEntry> e: tracksSortedByName()) {
								String id = e.getKey();
								if(id.equals(id1)) {
									System.out.print(id);
									inRange = true;
								}
								else if(inRange) {
									System.out.print(" " + id);
								}
								if(id.equals(id2)) inRange = false;
							}
						}
						else if(s.equals("addpl")) {
							printPlaylists(true);
							List<String> plids = readStringsTill(sc, ";");
							for(String id: plids) {
								Playlist playlist = app.playlists.get(id);
								if(playlist == null) System.out.println("Cannot find " + id);
								else if(uniqueSubLists.contains(id)) System.out.println(id + " is already in Sub-Playlists");
								else {
									uniqueSubLists.add(id);
									subLists.add(id);
								}
							}
						}
						else if(s.equals("create")) {
							s = sc.nextLine().strip();
							while(s.equals("")) {s = sc.nextLine().strip();}
							try {
								app.createPlaylist(Path.of("Playlists", s + ".txt"), tracks, subLists);
								System.out.println("Created Playlist " + s);
								break;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						else if(s.equals("back")) break;
						else System.out.println("Unknown command");
					} while(!s.equals("back"));
				}
				else if(s.equals("save")) {
					try {
						app.save();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(!s.equals("quit")) {
					System.out.println("Unknown command");
				}
				if(s.equals("quit")) {
					break;
				}
			}
			try {
				app.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("All saved");
			System.exit(0);
		}).start();
	}
	//Print Functions
	public void printMenu() {
		System.out.println();
		System.out.println("MediaPlayer");
		System.out.println();
		System.out.println("- menu");
		System.out.println("- stlist");
		System.out.println("- playst *id*");
		System.out.println("- plist");
		System.out.println("- pause");
		System.out.println("- cont");
		System.out.println("- stop");
		System.out.println("- play *id* [...] [loop] [shuffle] [as *name* \n] run");
		System.out.println("- resume");
		System.out.println("- shuffle");
		System.out.println("- next");
		System.out.println("- prev");
		System.out.println("- addto *name*");
		System.out.println("- linkto *pos*");
		System.out.println("- moveto *name*");
		System.out.println("- create");
		System.out.println("- rename current session/soundtrack *name*");
		System.out.println("- rename session/playlist/soundtrack *id* *name*");
		System.out.println("- delete session/playlist/soundtrack *id*");
		System.out.println("- delete current session/soundtrack");
		System.out.println("- modify playlist *name*");
		System.out.println("- save");
		System.out.println("- quit");
	}
	public void printCurrent() {
		System.out.println();
		if(app.currentSession != null) {
			System.out.println("Playlist: " + app.currentSession.getName());
			printPlaylistPreview();
		}
		else System.out.println("Soundtrack: " + app.currentTrack.getName());
	}
	public void printSoundtracks(boolean showIDs) {
		System.out.println("Soundtracks: ");
		System.out.println();
		for(Map.Entry<String, TrackEntry> e: tracksSortedByName()) {
			String id = e.getKey();
			TrackEntry track = e.getValue();
			
			System.out.println("Name: " + track.getName());
			if(showIDs) {
				System.out.println("ID: " + id);
			}
			if(!track.description.get().equals(""))
				System.out.println("Description: " + track.description);
			System.out.println("Length: " + UtilFunctions.getLengthString(track.length.get()));
		}
	}
	public void printPlaylists(boolean showIDs) {
		System.out.println("Playlists: ");
		System.out.println();
		for(Map.Entry<String, Playlist> e: playlistsSortedByName()) {
			String id = e.getKey();
			Playlist playlist = e.getValue();
			System.out.println("Name: " + playlist.getName());
			if(showIDs)
				System.out.println("ID: " + id);
			if(!playlist.description.get().equals(""))
				System.out.println("Description: " + playlist.description);
			System.out.println("Length: " + playlist.getAll(app.playlists, new HashSet<String>(), false).size());
			//System.out.println("Runtime: ?");
		};
	}
	public void printSessions() {
		app.sessions.forEach((id, session) -> {
			System.out.println("Name: " + session.getName());
			System.out.println("ID: " + id);
		});
	}
	public void printPlaylistPreview() {
		int current = app.currentSession.pos.get();
		int size = app.currentSession.size();
		
		int first = current-2 < 0 ? 0 : current-2;
		int last = current+4 > size ? size: current+4;
		
		System.out.println();
		for(int i = first; i < last; i++) {
			DataPlaylistEntry dpe = app.currentSession.get(i);
			String id = dpe.id.get();
			if(i == current)
				System.out.print("> ");
			System.out.println(i + ": " + app.soundtracks.get(id).getName());
			if(dpe.getForcedNext() != null) System.out.print("-> ");
		}
	}
	public void printPlaylist(Playlist playlist) {
		int current = -1;
		if(playlist == app.currentSession) current = app.currentSession.pos.get();
		
		List<DataPlaylistEntry> list = playlist.getAll(app.playlists, new HashSet<String>(), false);
		int size = list.size();
		for(int i = 0; i < size; i++) {
			DataPlaylistEntry dpe = list.get(i);
			String id = dpe.id.get();
			TrackEntry entry = app.soundtracks.get(id);
			
			if(i == current)
				System.out.print("> ");
			System.out.println(i + ": " + entry.getName());
			if(dpe.getForcedNext() != null) System.out.print("-> ");
			//System.out.println(id);
		}
	}
	//Find functions
	public Playlist findPlaylist(String s, Scanner sc) {
		List<Playlist> potentual = new ArrayList<>();
		Playlist playlist = app.playlists.get(s + ".txt");
		
		if(playlist != null) {
			return playlist;
		}
		
		app.playlists.values().forEach((pl) -> {
			if(pl.path.toString().toLowerCase().contains(s.toLowerCase().replace("/", "\\"))) potentual.add(pl);
		});	
		
		int potPos = 0;
		if(potentual.size() > 1) {
			System.out.println("Which one?");
			for(int i = 0; i < potentual.size(); i++) {
				System.out.println(i + ":" + potentual.get(i).getName());
			}
			String entered = sc.next();
			Integer i = UtilFunctions.getInt(entered);
			if(i == null) {
				System.out.println("Not a Number");
				return null;
			}
			else if(i < potentual.size() && i >= 0) {
				potPos = i;
			}
			else {
				System.out.println("Out of Bounds");
				return null;
			}
		}
		else if(potentual.size() == 0) {
			System.out.println("No entry found");
			return null;
		}
		return potentual.get(potPos);
	}
	public List<String> readStringsTill(Scanner sc, String till) {
		List<String> ret = new ArrayList<>();
		String s = sc.next();
		boolean stop = false;
		do {
			if(s.contains(till)) {
				stop = true;
				s = s.replace(till, "");
			}
			ret.add(s);
			s = sc.next();
		} while(!(s.equals(";") || stop));
		return ret;
	}
	public List<Map.Entry<String, TrackEntry>> tracksSortedByName() {
		return app.soundtracks.entrySet().stream()
	            .sorted(Comparator.comparing(e -> e.getValue().getName().toLowerCase()))
	            .toList();
	}
	public List<Map.Entry<String, Playlist>> playlistsSortedByName() {
		return app.playlists.entrySet().stream()
	            .sorted(Comparator.comparing(e -> e.getValue().getName().toLowerCase()))
	            .toList();
	}

}

package org.MediaPlayer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.MediaPlayer.DataTypes.DataPlaylistEntry;

import Tools.Files.Data.Util;
import Tools.Files.Data.DataTypes.DataArray;
import Tools.Files.Data.DataTypes.DataInt;
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
					TrackEntry entry = app.soundtracks.getOrDefault(s, null);
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
						String type = sc.nextLine();
						s = sc.nextLine().strip();
						if(type.equals("session")) {
							if(app.currentSession != null) {
								Path newPath = app.currentSession.path.getParent().resolve(Path.of(s + ".txt"));
								app.renameSession(app.currentSession, newPath);
							}
						}
						else if(type.equals("soundtrack")) {
							if(app.currentTrack != null) {
								Path newPath = app.currentTrack.path.getParent().resolve(Path.of(s + ".txt"));
								app.renameSoundtrack(app.currentTrack, newPath);
							}
						}
					}
					else {
						String type = s;
						if(type.equals("session")) {
							printSessions();
							String id = sc.nextLine().strip();
							Path newPath = app.sessions.get(id).path.getParent().resolve(Path.of(s + ".txt"));
							app.renameSession(app.currentSession, newPath);
						}
						else if(type.equals("playlist")) {
							printPlaylists(true);
							String id = sc.nextLine().strip();
							Path newPath = app.playlists.get(id).path.getParent().resolve(Path.of(s + ".txt"));
							app.renamePlaylist(app.currentSession, newPath);
						}
						else if(type.equals("soundtrack")) {
							printSoundtracks(true);
							String id = sc.nextLine().strip();
							Path newPath = app.soundtracks.get(id).path.getParent().resolve(Path.of(s + ".txt"));
							app.renameSoundtrack(app.currentTrack, newPath);
						}
					}
				}
				else if(s.equals("delete")) {
					s = sc.next();
					if(s.equals("session")) {
						printSessions();
						s = sc.next();
						Session session = app.sessions.getOrDefault(s, null);
						if(session != null) {
							System.out.println("Session deleted");
							app.deleteSession(session);
						}
						else System.out.println("Cannot find Session");
					}
					else if(s.equals("playlist")) {
						printPlaylists(true);
						s = sc.next();
						Playlist playlist = app.playlists.getOrDefault(s, null);
						if(playlist != null) {
							System.out.println("Playlist deleted");
							app.deletePlaylist(playlist);
						}
						else System.out.println("Cannot find Playlist");
					}
					else if(s.equals("soundtrack")) {
						printSoundtracks(true);
						s = sc.next();
						TrackEntry entry = app.soundtracks.getOrDefault(s, null);
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
						playlist.add(new DataPlaylistEntry(new DataString(app.currentTrack.id), new DataInt(0), new DataArray<DataString>(DataString::new)));
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
							Playlist playlist = app.playlists.getOrDefault(s, null);
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
					
					try {
						Path sessionPath = app.fTree.getNextFreeFileName(Path.of("Sessions", name + ".txt"));
						Session session = app.createSession(sessionParts, sessionPath, looping, shuffle); 
						Platform.runLater(() -> {
							app.playSession(session);
						});					
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
					Platform.runLater(() -> {
						app.playNextTrack();
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
						System.out.println("- get *id* *id");
						System.out.println("- back");
						System.out.println("- create");
						System.out.println();
						s = sc.next();
						if(s.equals("add")) {
							s = sc.next();
							do {
								TrackEntry entry = app.soundtracks.getOrDefault(s, null);
								if(entry == null) System.out.println("Cannot find " + s);
								else if(uniqueTracks.contains(s)) System.out.println(s + " is already in Playlist");
								else {
									uniqueTracks.add(s);
									tracks.add(s);
								}
								s = sc.next();
							} while(!(s.equals(";")));
						}
						else if(s.equals("get")) {
							String id1 = sc.next();
							String id2 = sc.next();
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
							s = sc.next();
							do {
								Playlist playlist = app.playlists.getOrDefault(s, null);
								if(playlist == null) System.out.println("Cannot find " + s);
								else if(uniqueSubLists.contains(s)) System.out.println(s + " is already in Sub-Playlists");
								else {
									uniqueSubLists.add(s);
									subLists.add(s);
								}
								s = sc.next();
							} while(!(s.equals(";")));
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
				else if(s.equals("quit")) {
					break;
				}
				else {
					System.out.println("Unknown command");
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
		System.out.println("- stlist");
		System.out.println("- playst *id*");
		System.out.println("- plist");
		System.out.println("- pause");
		System.out.println("- cont");
		System.out.println("- stop");
		System.out.println("- addto");
		System.out.println("- play *id* [...] [loop] [shuffle] [as *name* \n] run");
		System.out.println("- resume");
		System.out.println("- shuffle");
		System.out.println("- next");
		System.out.println("- moveto");
		System.out.println("- create");
		System.out.println("- rename current session/soundtrack *name*");
		System.out.println("- rename session/playlist/soundtrack *id* *name*");
		System.out.println("- delete session/playlist/soundtrack *id*");
		System.out.println("- delete current session/soundtrack");
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
			System.out.println("Length: " + getLengthString(track.length.get()));
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
			System.out.println("Length: " + playlist.fullSize(app.playlists));
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
		int last = current+4 >= size ? size-1: current+4;
		
		System.out.println();
		for(int i = first; i < last; i++) {
			String id = app.currentSession.get(i).id.get();
			if(i == current)
				System.out.print("> ");
			System.out.println(i + ": " + app.soundtracks.get(id).getName());
		}
	}
	public void printPlaylist(Playlist playlist) {
		int current = -1;
		if(playlist == app.currentSession) current = app.currentSession.pos.get();
		
		int size = playlist.size();
		List<DataPlaylistEntry> list = playlist.getAll(app.playlists);
		for(int i = 0; i < size; i++) {
			String id = list.get(i).id.get();
			if(i == current)
				System.out.print("> ");
			System.out.println(i + ": " + app.soundtracks.get(id).getName());
		}
	}
	//Find functions
	public Playlist findPlaylist(String s, Scanner sc) {
		List<Playlist> potentual = new ArrayList<>();
		Playlist playlist = app.playlists.getOrDefault(s + ".txt", null);
		
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
			try {
				int i = Integer.parseInt(entered);
				if(i < potentual.size() && i >= 0) {
					potPos = i;
				}
				else System.out.println("Out of Bounds");
				}
			catch(Exception e) {
				System.out.println("Not a Number");
			}
		}
		else if(potentual.size() == 0) {
			System.out.println("No entry found");
			return null;
		}
		return potentual.get(potPos);
	}
	private List<Map.Entry<String, TrackEntry>> tracksSortedByName() {
		return app.soundtracks.entrySet().stream()
	            .sorted(Comparator.comparing(e -> e.getValue().getName().toLowerCase()))
	            .toList();
	}
	private List<Map.Entry<String, Playlist>> playlistsSortedByName() {
		return app.playlists.entrySet().stream()
	            .sorted(Comparator.comparing(e -> e.getValue().getName().toLowerCase()))
	            .toList();
	}
	public String getLengthString(int sec) {
		int min = sec/60;
		int hour = min/60;
		int day = hour/24;
		String ret = "";
		if(day > 0) ret = day + "d";
		if(hour > 0) ret += (hour%24) + "h";
		if(min > 0) ret += (min%60) + "m";
		if(sec > 0) ret += (sec%60) + "s";
		return ret;
	}

}

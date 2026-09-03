package org.mediaplayer.server;

import org.mediaplayer.core.DataPlaylistEntry;
import org.mediaplayer.core.Playlist;
import org.mediaplayer.core.TrackEntry;
import org.mediaplayer.core.Generics.GenericAudio;
import org.mediaplayer.core.Generics.GenericUI;
import org.mediaplayer.core.connection.MediaPlayerClient;
import org.mediaplayer.desktop.DesktopFileLogic;

import Tools.Core.Files.FileManager;
import Tools.Core.Files.GenericFileManager;
import Tools.Core.Files.Data.DataTypes.DataInt;
import Tools.Core.Files.Data.DataTypes.DataString;
import Tools.Core.Util.Threadsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTest {
	ServerMock server;
	MediaPlayerClient client;
	
	ClientMock desktopClient;
	
	ServerFileLogic serverFileLogic;
	DesktopFileLogic clientFileLogic;
	
	MediaPlayerClient client2;
	DesktopFileLogic client2FileLogic;
	ClientMock desktopClient2;
	@BeforeEach
	public void setup() {
		createMockServer();
		createMockClient();
		createSoundtracks();
	}
	public void createMockServer() {
		try {
			serverFileLogic = new ServerFileLogicMock();
			serverFileLogic.create();
			server = new ServerMock();
			server.create(serverFileLogic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createMockClient() {
		try {
			clientFileLogic = new ClientFileLogicMock();
			clientFileLogic.create();
			
			GenericAudio audio = new ClientAudioMock();
			GenericUI ui = new ClientUiMock();
			GenericFileManager fileManager = new FileManagerMock();
			
			client = new MediaPlayerClient(fileManager);
			desktopClient = new ClientMock(new ThreadsystemMock(), client);
			
			client.create(clientFileLogic, audio, ui, desktopClient);
			
			server.addConnection(desktopClient, (t) -> {
				System.out.println("###Client###");
				System.out.println("Write: " + t);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createMockClient2() {
		try {
			client2FileLogic = new ClientFileLogicMock();
			client2FileLogic.create();
			
			GenericAudio audio = new ClientAudioMock();
			GenericUI ui = new ClientUiMock();
			GenericFileManager fileManager = new FileManagerMock();
			
			client2 = new MediaPlayerClient(fileManager);
			desktopClient2 = new ClientMock(new ThreadsystemMock(), client2);
			
			client2.create(client2FileLogic, audio, ui, desktopClient2);
			
			server.addConnection(desktopClient2, (t) -> {
				System.out.println("###Client 2###");
				System.out.println("Write: " + t);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createSoundtracks() {
		for(int i = 0; i < 100; i++) {
			UUID id = UUID.randomUUID();
			clientFileLogic.soundtracks.put(id.toString(), new TrackEntry(Path.of("Soundtracks", "entry" + i + ".mp3"), new DataString(), new DataInt(), id.toString()));
			serverFileLogic.soundtracks.put(id.toString(), new TrackEntry(Path.of("Soundtracks", "entry" + i + ".mp3"), new DataString(), new DataInt(), id.toString()));
		}
	}
	
	//Full-Sync-Tests
	@Test
	public void fullSyncRequestZeroChangesWithSoundtracksTest() throws ExecutionException, InterruptedException {
		Map<String, TrackEntry> before = new HashMap<>(clientFileLogic.soundtracks);
		client.requestSync();
		Map<String, TrackEntry> after = clientFileLogic.soundtracks;
		assertEquals(before.size(), after.size());
		before.forEach((id, entry) -> {
			assertTrue(after.containsKey(id));
			TrackEntry other = after.get(id);
			assertFalse(other == entry);
			assertEquals(entry, other);
		});
		assertEquals(before, after);
	}
	@Test
	public void fullSyncRequestWithChangesTest() throws ExecutionException, InterruptedException {
		Map<String, TrackEntry> serverMap = serverFileLogic.soundtracks;
		
		TrackEntry randomEntry = createRandomEntry("change");
		serverMap.put(randomEntry.id, randomEntry);
		
		Map<String, TrackEntry> beforeMap = new HashMap<>(clientFileLogic.soundtracks);
		client.requestSync();
		Map<String, TrackEntry> clientMap = new HashMap<>(clientFileLogic.soundtracks);
		assertNotEquals(beforeMap, clientMap);
		/**
		assertEquals(serverMap.size(), clientMap.size());
		serverMap.forEach((id, entry) -> {
			assertTrue(clientMap.containsKey(id));
			TrackEntry other = clientMap.get(id);
			assertFalse(other == entry);
			assertEquals(entry, other);
		});**/
		assertEquals(clientMap, serverMap);
	}
	@Test
	public void fullSyncRequestWithDeleteTest() throws ExecutionException, InterruptedException {
		Map<String, TrackEntry> serverMap = serverFileLogic.soundtracks;
		
		String deleteId = serverMap.keySet().iterator().next();
		serverMap.remove(deleteId);
		
		Map<String, TrackEntry> beforeMap = new HashMap<>(clientFileLogic.soundtracks);
		assertTrue(beforeMap.containsKey(deleteId));
		
		client.requestSync();
		Map<String, TrackEntry> clientMap = clientFileLogic.soundtracks;
		assertNotEquals(beforeMap, clientMap);
		
		assertFalse(clientMap.containsKey(deleteId));
		assertFalse(serverMap.containsKey(deleteId));
		/**
		assertEquals(serverMap.size(), clientMap.size());
		serverMap.forEach((id, entry) -> {
			assertTrue(clientMap.containsKey(id));
			TrackEntry other = clientMap.get(id);
			assertFalse(other == entry);
			assertEquals(entry, other);
		});**/
		assertEquals(clientMap, serverMap);
	}
	@Test
	public void fullSyncRequestWithPlaylistTest() throws ExecutionException, InterruptedException {
		Map<String, Playlist> beforeMap =  new HashMap<>(clientFileLogic.playlists);
		Map<String, Playlist> serverMap = serverFileLogic.playlists;
		
		UUID id = UUID.randomUUID();
		Playlist playlist = new Playlist(Path.of("Playlists/playlist.mp3"), createFirstPlaylist(5).stream().map((eId) -> new DataPlaylistEntry(new DataString(eId))).toList(), List.of(), id.toString());
		serverMap.put(id.toString(), playlist);
		
		assertNotEquals(beforeMap, serverMap);
		
		client.requestSync();
		Map<String, Playlist> clientMap = clientFileLogic.playlists;
		assertNotEquals(beforeMap, clientMap);
		assertEquals(clientMap, serverMap);
	}
	@Test
	public void fullSyncRequestWithSessionTest() {
		
	}
	@Test
	public void createPlaylistTest() throws IOException {
		List<String> ids = createFirstPlaylist(10);
		System.out.println(ids);
		Map<String, Playlist> clientMap = clientFileLogic.playlists;
		Map<String, Playlist> serverMap = serverFileLogic.playlists;
		Playlist playlist = client.createPlaylist(Path.of("Playlists/playlist.txt"), ids, List.of());
		
		assertTrue(clientMap.containsKey(playlist.id));
		assertTrue(serverMap.containsKey(playlist.id));
		
		assertEquals(clientMap, serverMap);
		assertEquals(clientMap.get(playlist.id), serverMap.get(playlist.id));
	}
	@Test
	public void createPlaylistWithSecondClientTest() throws IOException {
		createMockClient2();
		server.addConnection(desktopClient2, (t) -> {
			System.out.println("Write: " + t);
		});
		
		List<String> ids = createFirstPlaylist(10);
		Map<String, Playlist> clientMap = clientFileLogic.playlists;
		Map<String, Playlist> client2Map = client2FileLogic.playlists;
		Playlist playlist = client.createPlaylist(Path.of("Playlists/playlist.txt"), ids, List.of());
		
		assertTrue(clientMap.containsKey(playlist.id));
		assertTrue(client2Map.containsKey(playlist.id));
		
		assertEquals(clientMap, client2Map);
		assertEquals(clientMap.get(playlist.id), client2Map.get(playlist.id));
	}
	@Test
	public void createPlaylistSyncTest() throws IOException, ExecutionException, InterruptedException {
		List<String> ids = createFirstPlaylist(10);
		Map<String, Playlist> clientMap = clientFileLogic.playlists;
		
		//so that version isnt 0:
		Playlist earlierPlaylist = client.createPlaylist(Path.of("Playlists/playlist_not_important.txt"), ids, List.of());
		
		Playlist playlist = client.createPlaylist(Path.of("Playlists/playlist.txt"), ids, List.of());
		
		assertEquals(2, server.getServerData().currentVersion());
		
		createMockClient2();
		client2.getClientData().version.set(1);

		Map<String, Playlist> client2MapBefore = new HashMap<>(client2FileLogic.playlists);
		assertFalse(client2MapBefore.containsKey(playlist.id));
		
		client2.requestSync();
		Map<String, Playlist> client2Map = client2FileLogic.playlists;
		
		assertNotEquals(client2MapBefore, client2Map);
		assertFalse(client2Map.containsKey(earlierPlaylist.id));
		
		assertTrue(clientMap.containsKey(playlist.id));
		assertTrue(client2Map.containsKey(playlist.id));
		
		assertEquals(1, client2Map.size());
		assertEquals(2, client2.getClientData().version.get());
		assertEquals(2, client.getClientData().version.get());
		
		assertEquals(clientMap.get(playlist.id), client2Map.get(playlist.id));
	}
	
	
	
	
	
	private TrackEntry createRandomEntry(String name)  {
		UUID id = UUID.randomUUID();
		return new TrackEntry(Path.of("Soundtracks", name + ".mp3"), new DataString(), new DataInt(), id.toString());
	}
	private List<String> createFirstPlaylist(int length) {
		List<String> ret = new ArrayList<>();
		int i = 0;
		for(String id: clientFileLogic.soundtracks.keySet())  {
			if(i++ >= length) return ret;
			ret.add(id);
		}
		return ret;
	}
}

package ija.main;

import ija.logic.map.GameMap;
import ija.logic.map.Player;
import ija.logic.map.Watcher;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Vector;

public final class Server {
	
private static int port = 8668;
private ServerSocket server_socket;
private int client_count = 0;

public Socket client_socket;


//public Map<Long, GameMap> currentgames = new HashMap<Long, GameMap>();
/** Vector is has lower performance than list, but its thread safe */
public Vector<GameMap> currentgames = new Vector<GameMap>();
	public Server() throws Exception{
		try {
			System.out.println("DBG: starting server");
			server_socket = new ServerSocket(port,0,InetAddress.getLocalHost());
			while (true){
				/** Initialize socket for listening */
				client_socket = server_socket.accept();
				/** Client accepted,  */
				this.add_clientn();
				Spawn_Thread();
			}
		}catch (IOException e) {
			System.err.println("ERROR: Failed while creating new client session. (Socket already in use ?)");
		}finally{ 
				if (server_socket!=null)
					server_socket.close();
		        System.out.println("DBG: server[main thread] shutdowned succesfully");
        }
		
	}
	
	
	private void Spawn_Thread() throws IOException{
		Runnable r = new ClientSession(this);
		Thread thread = new Thread(r);
		thread.start();
		System.out.println("\nThread "+thread.getId()+" stared! \n");
	}
	
	synchronized void add_clientn(){
		this.client_count++;
	}
	synchronized void sub_clientn(){
		this.client_count--;
	}
	synchronized int get_clientn(){
		return this.client_count;
	}
	String getmapinfo(){
		/** vector is synchronous + it's read only operation, so no synchronizing here
		 */
		String mapinfo = "";
		GameMap tmp;
		Iterator<GameMap> itr = currentgames.iterator();
		while (itr.hasNext()){
			tmp = (GameMap) itr.next();
			mapinfo += tmp.ID +" "+tmp.MapName+" "+tmp.PlayerArr.size()+",";
		}
		return mapinfo;
	}
	synchronized String listmaps(){
		String maps= "";
		Path dir= Paths.get(System.getProperty("user.dir") + File.separator + "examples");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) {
		        maps+=file.getFileName()+",";
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		}
		return maps;
	}
	synchronized void AddMap(GameMap map){
		this.currentgames.add(map);
	}
	synchronized String Get_Positions(int mapid) {
		String positions = "";
		Iterator<Player> itr = GetID(mapid).PlayerArr.iterator();
		while (itr.hasNext()){
			Player tmp = itr.next();
			positions+=tmp.id()+" "+tmp.direction+" "+tmp.seizedField().position()+",";
		}
		return positions;
	}
	synchronized String Get_Gates(int mapid){
		String positions = "";
		GameMap currentmap = GetID(mapid);
		for (int i=0;i<currentmap.openedgates.size();i++){
			positions+=currentmap.openedgates.get(i)+" ,";
		}
		return positions;
	}
	synchronized String download_map(int mapid) {
		return this.GetID(mapid).format.replace('\n', ',');
	}
	synchronized String Get_Watchers(int mapid){
		String positions = "";
		Watcher[] WatcherArr = GetID(mapid).WatcherArr;
		for (int i=0;i<WatcherArr.length;i++){
			positions+=WatcherArr[i].direction+" "+WatcherArr[i].seizedField().position()+",";
		}
		return positions;
	}
	boolean MapExists(int mapid){
		return (mapid!=-1 && GetID(mapid)!=null);
	}
	synchronized void DeleteMap (int mapid){
		GetID(mapid).Delete(); //set delete variable, so watchers are properly shut down
		currentgames.remove(GetID(mapid)); //removes map from vector, from now, no more references should exist, and map should be deallocated.
	}
	synchronized void DeletePlayer (Player player,int mapid){
		GetID(mapid).PlayerArr.remove(player);
	}

	synchronized void setGoDelay(int delay,int mapid) {
		GetID(mapid).SleepDelay=delay;	
	}
	
	public GameMap GetID (int id){
		Iterator<GameMap> itr = currentgames.iterator();
		while (itr.hasNext()){
			GameMap map = (GameMap) itr.next();
			if (map.ID==id)
				return map;
		}
		return null;
	}
}	

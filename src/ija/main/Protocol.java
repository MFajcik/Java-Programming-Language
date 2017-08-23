package ija.main;

import ija.logic.map.GameMap;
import ija.logic.map.Player;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Protocol{
static Server server;
Player player;
int mapid=-1;

	public Protocol(Server server) {
		synchronized (server){
			Protocol.server=server;
		}
	}
	
	private boolean _joingame(int id) {
		GameMap pMap = server.GetID(id);

		/** if map doesn't exists anymore...*/
		if (pMap==null)
			return false;
		
		mapid= pMap.ID;
		synchronized(pMap){
			if (pMap==null || pMap.PlayerArr.size()>3)
				return false;
			player = pMap.createPlayer(Thread.currentThread().getId());
		}
		return true;
	}
	
	public GameMap _startgame(String filename){
		String mapfile="";
		try {
			mapfile =_readFile( System.getProperty("user.dir") + File.separator + "examples"+
				    File.separator + filename,StandardCharsets.UTF_8);
			int Width = _Get_width(mapfile);
			int Height = _Get_height(mapfile);
			if (Width<20 || Width >50 || Height <20 || Height>50){
				System.out.println("ERROR: Improper map size, this program will now exit.");
				System.exit(1);
			}
			mapfile = _Format_map(mapfile);
			GameMap map = new GameMap(Width,Height,mapfile,filename);
			server.AddMap(map);
			return map;
		}
		catch (Exception badFormat){
			return null;
		}
	}	
	
	public String processInput(String input) {
		if (player != null){
			try{
			    synchronized (server.GetID(mapid)){
					if (player.isKilled()){
						if (server.MapExists(mapid) && server.GetID(mapid).PlayerArr.size()==0)
							server.DeleteMap(mapid);
						return "KILLED";
					}
			
					if (player.isWinner()){
						if (server.MapExists(mapid))
							server.DeleteMap(mapid);
						return "WINNER";
					}
					else if (server.GetID(mapid).IsWon){
						if (server.MapExists(mapid))
							server.DeleteMap(mapid);
						return "NO MAP";
					}
			    }
			}catch (NullPointerException e){
				/** in case map does not exist, supress exception */
			}
		
		}
		
		String[] Parts=input.split(" ");
		if (Parts.length<1)
			return "UNKNOWN MESSAGE";
		
		if (Parts[0].equals("JOIN"))
		{
			if (Parts.length>1){
				int gameid=Integer.parseInt(Parts[1]);
				if (_joingame(gameid)){
					return server.download_map(gameid);
				}
				else return "JOIN FAIL";
			}
		}
		else if (Parts[0].equals("CREATE"))
		{
			if (Parts.length>1){
				GameMap map = _startgame(Parts[1]);
				if (map != null){
					if (Parts.length>2)
						server.setGoDelay(Integer.parseInt(Parts[2]),map.ID);
					
					if (_joingame(map.ID)){
						return server.download_map(map.ID);
					}
					else return "CREATE FAIL";
				}
				else return "CREATE FAIL";
			}
		}
		else if (Parts[0].equals("GETPOS")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			
			return server.Get_Positions(mapid);
		}
		else if (Parts[0].equals("GETID")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			return String.valueOf(Thread.currentThread().getId());
		}
		else if (Parts[0].equals("GETGATES")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			return server.Get_Gates(mapid);
		}
		else if (Parts[0].equals("GETWATCHERS")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			return server.Get_Watchers(mapid);
		}
		else if (Parts[0].equals("MOVE")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			if (player.Move()){
				if (player.isInFinish()){ 
					server.DeleteMap(mapid);
					return "WINNER";
				}
				return "MOVE SUCCESS";
			}
			return "MOVE FAIL";
		}
		else if (Parts[0].equals("GO")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			player.go();
			return "GOING";
			
		}
		else if (Parts[0].equals("STOP")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			player.stop();
			return "STOPPED";
		}
		else if (Parts[0].equals("OPEN")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			if (player.open()){
				return "OPEN SUCCESS";
			}
			return "OPEN FAIL";
		}
		else if (Parts[0].equals("TAKE")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			if (player.take()){
				return "TAKE SUCCESS";
			}
			return "TAKE FAIL";
		}
		else if (Parts[0].equals("TURNRIGHT")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			player.direction = 1;
			return "TURN SUCCESS";
		}
		else if (Parts[0].equals("TURNLEFT")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			player.direction = 2;
			return "TURN SUCCESS";
		}
		else if (Parts[0].equals("TURNUP")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			player.direction = 3;
			return "TURN SUCCESS";
		}
		else if (Parts[0].equals("TURNDOWN")){
			if (!server.MapExists(mapid))
				return "NO MAP";
			player.direction = 4;
			return "TURN SUCCESS";
		}
		else if (Parts[0].equals("MAPINFO")){
			return server.getmapinfo();
		}
		else if (Parts[0].equals("LISTMAPS")){
			return server.listmaps();
		}
		return "UNKNOWN MESSAGE: "+input;
	}
	
	/**
	 * @info reads the whole file content into singe string
	 * @param path - path to terrain mapping file
	 * @param encoding - encoding used within file;
	 * @return String content of selected file 
	 * @throws IOException 
	 */
	private String _readFile(String path, Charset encoding) 
	    throws IOException 
	{
	    byte[] encoded = Files.readAllBytes(Paths.get(path));
	    return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	/**
	 * seeks the value of width within string
	 * @param s string content
	 * @return integer value of width
	 * @throws IndexOutOfBoundsException 
	 */
	private int _Get_width(String s)
		throws IndexOutOfBoundsException
	{
		return Integer.parseInt(s.substring(s.indexOf("x")+1,s.indexOf("\n")));
	}
	/**
	 * seeks the value of height within string
	 * @param s string content
	 * @return integer value of height
	 * @throws IndexOutOfBoundsException
	 */
	private int _Get_height(String s) //TODO: Exception handling? (substring)
		throws IndexOutOfBoundsException
	{
		return Integer.parseInt(s.substring(0, s.indexOf("x")));
	}
	
	/**
	 * deletes first line of the string given
	 * @param s string content
	 * @return modified string
	 * @throws IndexOutOfBoundsException
	 */
	private String _Format_map(String s)
		throws IndexOutOfBoundsException
	{
		return s.substring(s.indexOf('\n')+1);
	}
	
}

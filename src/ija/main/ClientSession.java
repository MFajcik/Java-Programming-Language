package ija.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class ClientSession implements Runnable{
Server server;
Socket client_socket;
/** TODO: kedze je tu run, synchronized metody by asi mali byt tu ?? ... */
	public ClientSession(Server server){
		this.server=server;
		client_socket = server.client_socket;
	}
	public void run() {

        Protocol SP = new Protocol(server);
		try {
			System.out.println("DBG: new thread spawned");
			System.out.println("Pocet klientov je "+server.get_clientn());
			/** setup stream into/from socket **/
			PrintWriter out = new PrintWriter(server.client_socket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(server.client_socket.getInputStream()));
			
	        System.out.println("DBG: connection accepted");
	        /** Create protocol instance */
	        

		    /** Communicate */
	        out.println(server.getmapinfo());
	        out.println(server.listmaps());
	        String inputLine;
			while (true) {
				inputLine = in.readLine();
				System.out.println("Server recieved message: "+inputLine);
		        if (inputLine == null || inputLine.equals("CLOSE_SESSION"))
		        		break;
		        out.println(SP.processInput(inputLine));
		    }
		} catch (IOException e) {
			System.err.println("Socket was closed on the other side.");
		}finally{
			synchronized(server.currentgames){
				server.sub_clientn();
				if (server.MapExists(SP.mapid)){
					/** Remove player from the game */
					server.DeletePlayer(SP.player,SP.mapid);
					server.GetID(SP.mapid).PlayerArr.remove(SP.player.seizedField().leave());
					/** if last player disconnects from map, delete map */
					if (server.GetID(SP.mapid).PlayerArr.size()==0)
						server.DeleteMap(SP.mapid);
				}
			}
			try {
				client_socket.close();
			} catch (IOException e) {
				System.err.println("ERROR: Failed to close client socket.");
			}
			System.out.println("DBG:  thread session "+Thread.currentThread().getId()+ " shutting down\n clients remaining: "+server.get_clientn());
		}
	}
}

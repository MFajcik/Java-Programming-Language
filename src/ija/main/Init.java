
/**
 * main class
 * game initialization maintenance
 * @author Martin Fajčík, Jiří Palacký
 * @version 0.1
 * @since 5.4.2014
**/

package ija.main;

/** Main Class */
public class Init {
	/** Calls game handler, announces game conclusion 
	 * @param args[] command line arguments
	 * @throws Exception */
	public static void main(String args[]) throws Exception{
		new Server();
	}	
}

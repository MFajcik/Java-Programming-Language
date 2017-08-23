package ija.logic.map;

import java.util.Vector;

import ija.logic.map.MapField;
import ija.logic.map.Player;
import ija.logic.objects.Gate;
import ija.logic.objects.Wall;

/*
 * Class serves as (smart) game map
 */
public class GameMap {

	/* Array serves as map of fields */
	MapField[] FieldArr;

	public boolean IsWon = false; 
	public String format;
	/* Map name */
	public String MapName;
	boolean deleted=false;
	public int ID;
	/* GameMap Width */
	int Width;
	int Height;
	int WatcherCount;
	public Watcher[] WatcherArr;
	public int SleepDelay = 1500;
	/* Max 4 players at time */
	public Vector<Player> PlayerArr = new Vector<Player>();

	/** keeps info about gates that have been opened in this map instance */
	public Vector<Integer> openedgates = new Vector<Integer>();

	/**
	 * Class Constructor
	 * 
	 * @param w
	 *            represents map width
	 * @param h
	 *            represents map height
	 * @param format
	 *            represents input string map format, (buffered from external
	 *            file)
	 * @throws badFormat
	 *             (Invalid map format)
	 */
	public GameMap(int w, int h, String format, String mapname)
			throws Exception {
		this.WatcherCount = w / 10;
		this.format = format;
		this.MapName = mapname;
		this.ID=(int) Thread.currentThread().getId();
		FieldArr = new MapField[w * h];
		Width = w;
		
		Exception badFormat = new Exception("Invalid map format\n");
		if (format != null) {
			/** j represents white spaces */
			int j = 0;
			for (int i = 0; i < format.length(); i++) {
				char character = format.charAt(i);

				if (Character.isWhitespace(character)) { // 32 ordinal value of
															// space
					j++;
					continue;
				}
				/** Otherwise it would cause IndexOutOfBound exception */
				if ((i - j) >= FieldArr.length)
					throw badFormat;
				switch (character) {
				// three types of objects- Wall, Gate, Empty space(empty space
				// can contain key or be final)
				case 'w':
					FieldArr[i - j] = new MapField(this, i - j, "w");
					break;
				case 'g':
					FieldArr[i - j] = new MapField(this, i - j, "g");
					break;
				case 'k':
					FieldArr[i - j] = new MapField(this, i - j, "k");
					break;
				case 'f':
					FieldArr[i - j] = new MapField(this, i - j, "f");
					break;
				default:
					FieldArr[i - j] = new MapField(this, i - j, "v");
					break;
				}
			}
		}
		StartWatchers();
	}
	
	public synchronized void Delete(){
		deleted=true;
	}
	
	/**
	 * @param i
	 *            position of unknown object
	 * @return returns specific Field of the map specified my parameter
	 *         i(position)
	 */
	public MapField fieldAt(int i) {
		return (i >= 0 && i < FieldArr.length) ? FieldArr[i] : null;
	}
	
	public void StartWatchers(){
		if (WatcherCount==0)
			return;
		WatcherArr = new Watcher[WatcherCount];
		for (int i = 0; i<WatcherCount;i++){
			for (int j = FieldArr.length-1; j >= 0; j--) {
				if (FieldArr[j].canSeize()) {
					WatcherArr[i] = new Watcher(i, FieldArr[j]);
					FieldArr[j].seize(WatcherArr[i]);
					break;
				}
				if (j==0){
					/** If it reaches this code, it means that no space for watcher was found 
					 * -> system was unable to start watchers
					 */
					return;
				}
			}
		}
		Runnable r = WatcherArr[0];
		Thread thread = new Thread(r);
		thread.start();
		System.out.println("DBG: Watchers Started");
	}
	/**
	 * @param l
	 *            identificator
	 * @return if successful returns created head, otherwise null
	 */
	public Player createPlayer(long l) {
		if (PlayerArr.size() > 3)
			return null;

		for (int j = 0; j < FieldArr.length; j++) {
			if (FieldArr[j].canSeize()) {
				Player newplayer = new Player(l, FieldArr[j]);
				PlayerArr.add(newplayer);
				FieldArr[j].seize(newplayer);
				return newplayer;
			}
		}
		return null;
	}

	/**
	 * Prints out game map TODO: [DEBUGGING PURPOSES ONLY, DELETE ONTO RELEASE ]
	 */
	public void PrintMap() {
		for (int i = 0; i < FieldArr.length; i++) {
			if (PlayerArr.size() > 0 && PlayerArr.get(0).seizedField().position == i) {
				System.out.print("1");
			} else if (PlayerArr.size() > 1
					&& PlayerArr.get(1).seizedField().position == i) {
				System.out.print("2");
			} else if (PlayerArr.size() > 2
					&& PlayerArr.get(2).seizedField().position == i) {
				System.out.print("3");
			} else if (PlayerArr.size() > 3
					&& PlayerArr.get(3).seizedField().position == i) {
				System.out.print("4");
			} else if (WatcherCount > 0
					&& WatcherArr[0].seizedField().position == i) {
				System.out.print("W");
			} else if (WatcherCount > 1
					&& WatcherArr[1].seizedField().position == i) {
				System.out.print("U");
			} else if ((FieldArr[i].field) instanceof Wall) {
				System.out.print("█");
			} else if ((FieldArr[i].field) instanceof Gate) {
				if (FieldArr[i].field.canBeOpen())
					System.out.print("╬");
				else
					System.out.print("O");
			} else if ((FieldArr[i].key)) {
				System.out.print("K");
			} else if ((FieldArr[i].finalpos)) {
				System.out.print("F");
			} else
				System.out.print("_");
			if ((i + 1) % Width == 0) {
				System.out.println("");
			}
		}
	}
}

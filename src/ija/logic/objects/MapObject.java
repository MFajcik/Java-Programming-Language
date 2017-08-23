package ija.logic.objects;

import ija.logic.objects.Wall;

public abstract class MapObject {
	/**
	 * @return whether the object can be opened
	 */
	public abstract boolean canBeOpen();
	
	/**
	 * @return open operation success/failure
	 */
	public abstract boolean open();
	
	/**
	 * @return whether the object can be seized
	 */
	public abstract boolean canSeize();
	
	/**
	 * @info creates new object
	 * @param format specifies which object should be created
	 * @return created object/null
	 */
	public static MapObject create(String format){
		if (format.equals("w")){
			return new Wall("w");
		}
		else if (format.equals("g")){
			return new Gate("g");
		}
		else return null;
	}
}

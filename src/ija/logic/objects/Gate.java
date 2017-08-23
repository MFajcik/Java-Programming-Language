package ija.logic.objects;

import ija.logic.objects.MapObject;

public class Gate extends MapObject {
private boolean open=false;
private String name;
	
	/** Class constructor
	 * @param name assigns name to object
	 */
	public Gate(String name) { 
		this.name = name;
	}
	
	/**
	 * @see ija.logic.objects.MapObject#canSeize()
	 */
	@Override
	public boolean canSeize() {
		return open;
	}
	
	/**
	 * @see ija.logic.objects.MapObject#open()
	 */
	@Override
	public boolean open() {
		if (!this.open){
			open=true;
			return true;
		}
		else
			return false;
	}
	
	/**
	 * @see ija.logic.objects.MapObject#canBeOpen()
	 */
	@Override
	public boolean canBeOpen() {
		return(!this.open);
	}
	
	/**
	 * @return true if objects are equal by type
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Gate))
			return false;
		Gate other = (Gate) o;
		return (open == other.open ? 
			      (name != null ? name.equals(other.name):other.name==null):
				false);
	}
	
	/**
	 * @return object hash code
	 */
	@Override
	public int hashCode (){
		return name != null ? name.hashCode()+ (open ? 2:3):
			   MapObject.class.hashCode() + (open ? 2:3);
	}
}

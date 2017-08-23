package ija.logic.objects;


public class Wall extends MapObject {
private String name;
	/** Class constructor
	 * @param name assigns name to object
	 */
	public Wall(String name) { 
		this.name = name;
	}
	
	/**
	 * @see ija.logic.objects.MapObject#canSeize()
	 */
	@Override
	public boolean canSeize() {
		return false;
	}
	
	/**
	 * @see ija.logic.objects.MapObject#canBeOpen()
	 */
	@Override
	public boolean canBeOpen() {
		return false;
	}
	
	/**
	 * @see ija.logic.objects.MapObject#open()
	 */
	@Override
	public boolean open() {
		return false;
	}
	
	/**
	 * @return true if objects are equal by type
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Wall))
			return false;
		Wall other = (Wall) o;
		return name != null ? name.equals(other.name):other.name==null;
	}
	
	/**
	 * @return object hash code
	 */
	public int hashCode (){		//*2 secures difference with other classes hcodes
		return name != null ? name.hashCode() : MapObject.class.hashCode()*2;
	}
}

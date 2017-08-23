package ija.logic.map;

import ija.logic.objects.*;

/**
 * @info repesents field inside _map
 */
public class MapField {

public boolean key = false;
public boolean finalpos = false;
int position;
MapObject field = null;
private Player _player = null;
public GameMap _map;
	
	/**
	 * Class constructor
	 * @param gameMap map reference
	 * @param p position inside map
	 * @param type object type represented as string
	 */
	public MapField(GameMap gameMap, int p, String type){
		position=p;
		field = MapObject.create(type);
		this._map = gameMap;
		if (type.equals("k"))
			key=true;
		else if (type.equals("f"))
			finalpos = true;
	}
	
	public Player seizedPlayer(){
		return _player;
	}
	
	/**
	 * @return whether the position represents finish
	 */
	public boolean is_final(){
		return finalpos;
	}
	
	/**
	 * @return true if field contains key
	 */
	public boolean take_key(){
		if (key){
			key=false;
			return true;
		}
		else return false;
		
	}
	
	/**
	 * @return field position
	 */
	public int position(){
		return position;
	}
	
	/**
	 * @param player player placed as a reference into the field
	 * @return operation result
	 */
	public boolean seize(Player player){
		if (canSeize()){
			this._player = player;
			return true;
		}
		else return false;
	}
	
	/**
	 * @return true if player successfully left field
	 */
	public Player leave(){
		Player tmphead = _player;
		_player = null;
		return tmphead; 
	}
	
	/**
	 * @return true if field can be siezed
	 */
	public boolean canSeize(){
			return (_player == null && (field!=null ? field.canSeize() :true));
			 //if field==null, there is no object at this field ->implicit true
	}
	
	/**
	 * @return true if field was opened
	 */
	public boolean open(){
			return field == null ? false: field.open();
	}
	
	/**
	 * @return reference to field placed to the right of current
	 */
	public MapField RightPos(){
		return ((position+1)%_map.Width == 0) ? null :_map.fieldAt(position+1);
	}
	
	/**
	 * @see ija.logic.map.MapField#RightPos()
	 */
	public MapField LeftPos(){
		return (position % _map.Width == 0) ? null :_map.fieldAt(position-1);
	}
	
	/**
	 * @see ija.logic.map.MapField#RightPos()
	 */
	public MapField UpPos(){
		return _map.fieldAt(position-1*_map.Width);
	}
	
	/**
	 * @see ija.logic.map.MapField#RightPos()
	 */
	public MapField DownPos(){
		return _map.fieldAt(position+1*_map.Width);
	}
	
	/**
	 * @return objects are/aren't equal by type
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MapField))
			return false;
		MapField other = (MapField) o;
		return (other.position == position &&
				(field != null ? field.equals(other.field) : other.field == null) &&
				(_player  != null ? _player.equals(other._player)   : other._player  == null));
	}
	
	/**
	 * @return object hash code
	 */
	@Override
	public int hashCode (){
		return position + 
			   (_player != null ? _player.hashCode() :MapField.class.hashCode())+
			   (field != null ? field.hashCode() :MapField.class.hashCode());
	}
	
	/**
	 * @return whether the field can be opened
	 */
	public boolean canBeOpen(){
		return field == null ? false: field.canBeOpen();
	}
}

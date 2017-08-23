package ija.logic.map;

/**
 * @info encapsulates player actions
 */
public class Player implements Runnable{
private long _id;
private int _keys=0;
public int direction=0;
private boolean go;
private boolean killed=false;
private boolean winner=false;
protected MapField _MapPos;

	/**
	 * @return number of keys which player carries
	 */
	public int keys_count(){
		return _keys;
	}
	
	public void stop(){
		go = false;
	}
	public boolean isWinner(){
		return winner;
	}
	/**
	 * Class constructor
	 * @param l player identifier
	 * @param f field where player is placed to
	 */
	public Player(long l, MapField f){
		this._id = l;
		_MapPos = f;
	}
	
	/**
	 * @return player position inside map
	 */
	public MapField seizedField(){
		return _MapPos;
	}
	public synchronized boolean isKilled(){
		return killed;
	}
	public void KillPlayer(){
		_MapPos._map.PlayerArr.remove(_MapPos.leave());
		killed=true;
	}
	public MapField destField(){
		switch(direction)
		{
		case 1: return _MapPos.RightPos();
		case 2: return _MapPos.LeftPos();
		case 3: return _MapPos.UpPos();
		case 4: return _MapPos.DownPos();
		default: return _MapPos;
		}
	}
	/**
	 * @info opens gate in selected direction
	 * @param direction
	 * @return action success/failure
	 */
	public boolean open(){
		switch(direction)
		{
		case 1:
			if (_MapPos.RightPos()!=null && _MapPos.RightPos().canBeOpen() && keys_count() >0){
				_MapPos.RightPos().open();
				_keys--;
				_MapPos._map.openedgates.add(_MapPos.RightPos().position);
				return true;
			}
			else return false;
		case 2:
			if (_MapPos.LeftPos()!=null && _MapPos.LeftPos().canBeOpen() && keys_count() >0){
				_MapPos.LeftPos().open();
				_keys--;
				_MapPos._map.openedgates.add(_MapPos.LeftPos().position);
				return true;
			}
			else return false;
		case 3:
			if (_MapPos.UpPos()!=null && _MapPos.UpPos().canBeOpen() && keys_count() >0){
				_MapPos.UpPos().open();
				_keys--;
				_MapPos._map.openedgates.add(_MapPos.UpPos().position);
				return true;
			}
			else return false;
		case 4:
			if (_MapPos.DownPos()!=null && _MapPos.DownPos().canBeOpen() && keys_count() >0){
				_MapPos.DownPos().open();
				_keys--;
				_MapPos._map.openedgates.add(_MapPos.DownPos().position);
				return true;
			}
			else return false;
		default:
			return false;
		}
	}
	
	/**
	 * @info takes key from the field in selected direction
	 * @param direction
	 * @return action success/failure
	 */
	public boolean take(){
		switch(direction)
		{
		case 1:
			if (_MapPos.RightPos()!=null && _MapPos.RightPos().take_key()){
				_keys++;
				return true;
			}
			else return false;
		case 2:
			if (_MapPos.LeftPos()!=null && _MapPos.LeftPos().take_key()){
				_keys++;
				return true;
			}
			else return false;
		case 3:
			if (_MapPos.UpPos()!=null && _MapPos.UpPos().take_key()){
				_keys++;
				return true;
			}
			else return false;
		case 4:
			if (_MapPos.DownPos()!=null && _MapPos.DownPos().take_key()){
				_keys++;
				return true;
			}
			else return false;
		default:
			return false;
		}
	}
	
	public void go(){
		if (direction==0)
			return;
		go = true;
		Runnable r = this;
		Thread thread = new Thread(r);
		thread.start();
	}
	
	public void run(){
		while (go && Move() && !isInFinish()){
			try {
				Thread.sleep(_MapPos._map.SleepDelay);
			} catch (InterruptedException e) {
			}
		}
	}
	public boolean Move(){
		switch(direction)
		{
		case 1:
			if (_MapPos.RightPos() != null)
			{
				if (_MapPos.RightPos().canSeize()){
					_MapPos.RightPos().seize(_MapPos.leave());
					_MapPos = _MapPos.RightPos();
					return true;
				}
			}
			return false;
		case 2:
			if (_MapPos.LeftPos() != null)
			{
				if (_MapPos.LeftPos().canSeize()){
					_MapPos.LeftPos().seize(_MapPos.leave());
					_MapPos = _MapPos.LeftPos();
					return true;
				}
			}	
			return false;
		case 3:
			if (_MapPos.UpPos() != null)
			{
				if (_MapPos.UpPos().canSeize()){
					_MapPos.UpPos().seize(_MapPos.leave());
					_MapPos = _MapPos.UpPos();
					return true;
				}
			}
			return false;
		case 4:
			if (_MapPos.DownPos() != null)
			{
				if (_MapPos.DownPos().canSeize()){
					_MapPos.DownPos().seize(_MapPos.leave());
					_MapPos = _MapPos.DownPos();
					return true;
				}
			}
			return false;
		default: return false;
		}
	}
	
	/**
	 * @return player identifier
	 */
	public long id(){
		return _id;
	}
	
	/**
	 * @return objects are/aren't equal by type
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Player))
			return false;
		Player other = (Player) o;
		return _id==other._id;
	}
	
	/**
	 * @return object hash code
	 */
	@Override
	public int hashCode (){
		return (int)_id+ Player.class.hashCode(); // prevent from just returning ID
	}
	
	/**
	 * @return true if player stands inside finish field
	 */
	public boolean isInFinish() {
		if (_MapPos.is_final()){
			this._MapPos._map.IsWon=true;
			this.winner=true;
			System.out.println("DBG: Game has a WINNER");
			return true;
		}
		return false;
	}
}

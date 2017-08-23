package ija.logic.map;

import java.util.Random;


public class Watcher extends Player implements Runnable{
	public Watcher(long l, MapField f) {
		super(l, f);
	}
	@Override
	public void run() {

		Random generator = new Random( 19580427 );
		while (!_MapPos._map.IsWon && !_MapPos._map.deleted){
			for (int i=0;i<this._MapPos._map.WatcherCount;i++){
				int watchdirection = generator.nextInt(3)+1;
				this._MapPos._map.WatcherArr[i].direction=watchdirection;
				if (this._MapPos._map.WatcherArr[i].destField()!=null 
						&& (this._MapPos._map.WatcherArr[i].destField().seizedPlayer() instanceof Player)
						&& !(this._MapPos._map.WatcherArr[i].destField().seizedPlayer() instanceof Watcher)) {
					
					this._MapPos._map.WatcherArr[i].destField().seizedPlayer().KillPlayer();
				}
				if (!this._MapPos._map.WatcherArr[i].Move()){
					for (int k=0;k<3;k++){
						this._MapPos._map.WatcherArr[i].direction = ((watchdirection+k)%4)+1;
						if (this._MapPos._map.WatcherArr[i].destField()!=null 
								&& (this._MapPos._map.WatcherArr[i].destField().seizedPlayer() instanceof Player)
								&& !(this._MapPos._map.WatcherArr[i].destField().seizedPlayer() instanceof Watcher)) {
							System.out.println("DBG :Killing Player "+this._MapPos._map.WatcherArr[i].destField().seizedPlayer());
							
							this._MapPos._map.WatcherArr[i].destField().seizedPlayer().KillPlayer();
						}
						if (this._MapPos._map.WatcherArr[i].Move())
							break;
					}
				}
			}
			/** DBG map printing */
			this._MapPos._map.PrintMap();
			System.out.println();
			try {
				Thread.sleep(this._MapPos._map.SleepDelay);
			} catch (InterruptedException e) {
			}
		}
	}
}

package net.minekingdom.snaipe.Thieves.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator; 

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.Thieves;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.events.ThiefDetectEvent;

public class PlayerMovedChecker implements Runnable{
	
	private Thieves plugin;
	private Collection<Player> thiefTargets = new HashSet<Player>();
	private HashMap<Player, Player> thieves = new HashMap<Player, Player>();
	private HashMap<Player, double[]> playerCoords = new HashMap<Player, double[]>();
	private Heroes heroes;
	
	public PlayerMovedChecker(Thieves thieves) {
		plugin = thieves;
		this.heroes = (Heroes)plugin.getServer().getPluginManager().getPlugin("Heroes");
	}

	@Override
	public void run() {
		Iterator<Player> iterator = thiefTargets.iterator();
		while(iterator.hasNext()) {
			Player player = iterator.next();
			if(checkPlayer(player)){onPlayerMove(thieves.get(player), player); playerCanSee(thieves.get(player), player);}
			else playerCanSee(thieves.get(player), player);
			
		}
	}
	
	public boolean checkPlayer(Player player) {
		Location playerLoc = player.getLocation();
		double[] oldCoords = playerCoords.get(player);
		double newX = playerLoc.getX();
		double newY = playerLoc.getY();
		double newZ = playerLoc.getZ();
		if(oldCoords == null)return false;
		return(oldCoords[0] != newX || oldCoords[1] != newY || oldCoords[2] != newZ);
	}
	
	public void playerCanSee(Player thiever, Player thieved) {
		
		final ThievesPlayer player = plugin.getPlayerManager().getPlayer(thieved);
        
            
            if (plugin.getPlayerManager().isStealed(player))
            {
            	ThievesPlayer thief = plugin.getPlayerManager().getPlayer(thiever);
                
                if (thief == null)
                    return;
                
            	if (player.canSeePlayer(thief))
                {
                    ThiefDetectEvent detectEvent = new ThiefDetectEvent(thief, player, player);
                    plugin.getServer().getPluginManager().callEvent(detectEvent);
                    
                    thief.closeWindow();
                    plugin.getPlayerManager().removeThief(thief);
                    thief.stun(plugin.getSettingManager().getStunTime());
                    if(heroes != null) {
                    	Hero hero = heroes.getCharacterManager().getHero(thief.getPlayer());
                    	hero.setHealth(hero.getHealth() - plugin.getSettingManager().getDamage());
                    	hero.syncHealth();
                    }
                    
                    thief.sendMessage(ChatColor.RED + Language.youHaveBeenDiscovered);
                }
            }
        
	}
	
	public void onPlayerMove(Player thiever, Player thieved) {
		final ThievesPlayer player = plugin.getPlayerManager().getPlayer(thieved);
        
            if (plugin.getPlayerManager().isStealed(player))
            {
                ThievesPlayer thief = plugin.getPlayerManager().getPlayer(thiever);
                
                if (thief == null)
                    return;
                
                if (!thief.isTargetWithinRange(player))
                {
                    thief.closeWindow();
                    removeThief(thiever, thieved);
                }
                
                if (player.canSeePlayer(thief))
                {
                    ThiefDetectEvent detectEvent = new ThiefDetectEvent(thief, player, player);
                    plugin.getServer().getPluginManager().callEvent(detectEvent);
                    
                    thief.closeWindow();
                    plugin.getPlayerManager().removeThief(thief);
                    thief.stun(plugin.getSettingManager().getStunTime());
                    if(heroes != null) {
                    	Hero hero = heroes.getCharacterManager().getHero(thief.getPlayer());
                    	hero.setHealth(hero.getHealth() - plugin.getSettingManager().getDamage());
                    	hero.syncHealth();
                    }
                    thief.sendMessage(ChatColor.RED + Language.youHaveBeenDiscovered);
                }
            }
        }
	
	public void addThief(Player thief, Player thieved) {
		Location playerLoc = thieved.getLocation();
		thiefTargets.add(thieved);
		playerCoords.put(thieved, new double[]{playerLoc.getX(), playerLoc.getY(), playerLoc.getZ()});
		thieves.put(thieved, thief);
	}
	
	public void removeThief(Player thief, Player thieved) {
		thiefTargets.remove(thieved);
		playerCoords.remove(thieved);
		thieves.remove(thieved);
	}

}

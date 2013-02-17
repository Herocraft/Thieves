package net.minekingdom.snaipe.Thieves.listeners;


import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.Thieves;
import net.minekingdom.snaipe.Thieves.events.PlayerStealEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener
{
    
    private final Thieves plugin;
    
    public PlayerListener()
    {
        plugin = Thieves.getInstance();
    }
    
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        if (event.isCancelled())
            return;
        
        if (event.getPlayer() == null)
            return;
        
        final ThievesPlayer thief = plugin.getPlayerManager().getPlayer(event.getPlayer());
        final Entity entity = event.getRightClicked();
        
        if (entity == null)
            return;
        
        if (plugin.getSettingManager().isActiveWorld(thief.getWorld()) && thief.getEnabled())
        {
            if (Thieves.isTheftEnabled && thief.hasPermission("thieves.steal"))
            {
                if (!thief.isSneaking())
                    return;
                
                if (entity instanceof Player)
                {
                    ThievesPlayer target = plugin.getPlayerManager().getPlayer((Player) entity);
                    if(target == null)return;
                    if (target.hasPermission("thieves.protected"))
                    {
                        thief.sendMessage(ChatColor.RED + Language.cannotRobThisPlayer);
                        return;
                    }
                    
                    if (thief.getCooldown() > 0)
                    {
                        thief.sendMessage(ChatColor.RED + Language.cooldownNotFinished);
                        return;
                    }
                    
                    if (!target.canSeePlayer(thief))
                    {
                        if (!plugin.getSettingManager().canStealInNonPvPArea())
                        {
                            EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(thief.getPlayer(), entity, DamageCause.ENTITY_ATTACK, 1);
                            plugin.getServer().getPluginManager().callEvent(damageEvent);
                            
                            if (damageEvent.isCancelled())
                            {
                                thief.sendMessage(ChatColor.RED + Language.youCannotStealInNonPvPArea);
                                return;
                            }
                        }
                        
                        PlayerStealEvent stealEvent = new PlayerStealEvent(thief, target);
                        plugin.getServer().getPluginManager().callEvent(stealEvent);
                        
                        if (stealEvent.isCancelled())
                            return;
                        
                        thief.startStealing(target);
                        plugin.getPlayerManager().addThief(thief, target);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        
        if (player != null)
            plugin.getPlayerManager().addPlayer(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        
        if (plugin.getPlayerManager().getThiefFromTarget(player) != null)
            plugin.getPlayerManager().getThiefFromTarget(player).closeInventory();
        
        if (player != null)
            plugin.getPlayerManager().removePlayer(player);        
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        final Player player = event.getPlayer();
        
        if (player != null)
            plugin.getPlayerManager().removePlayer(player);
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        final Player player = event.getPlayer();
        
        if (plugin.getPlayerManager().getThiefFromTarget(player) != null) {
            plugin.getPlayerManager().getThiefFromTarget(player).closeInventory();
        }
    }
}

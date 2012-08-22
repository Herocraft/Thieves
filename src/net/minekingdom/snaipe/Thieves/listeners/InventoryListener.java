package net.minekingdom.snaipe.Thieves.listeners;

import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import net.minekingdom.snaipe.Thieves.ItemValues;
import net.minekingdom.snaipe.Thieves.Language;
import net.minekingdom.snaipe.Thieves.ThievesPlayer;
import net.minekingdom.snaipe.Thieves.Thieves;
import net.minekingdom.snaipe.Thieves.events.ItemStealEvent;

public class InventoryListener implements Listener
{
    
    private final Thieves plugin;
    private static final Random random = new Random();
    
    public InventoryListener()
    {
        plugin = Thieves.getInstance();
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (event.isCancelled())
            return;
        
        if (event.getWhoClicked() == null)
            return;
        
        final ThievesPlayer thief = plugin.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        final ThievesPlayer target = plugin.getPlayerManager().getTarget(thief);
                
        if (target != null && event.getRawSlot() < 36)
        {
            if (event.isShiftClick())
            {
                event.setCancelled(true);
                return;
            }
            
            if(event.getCurrentItem() == null)return;
            if (event.getCurrentItem().getAmount() == 0)
            {
                return;
            }
            
            if (event.getRawSlot() < 9 && !plugin.getSettingManager().canStealHotBar())
            {
                event.setCancelled(true);
                return;
            }
            
            ItemStack item = event.getCurrentItem();
            
            if (thief.getMaxItemWealth() < thief.getItemWealth() + ItemValues.valueOf(item.getType()))
            {
                thief.sendMessage(ChatColor.RED + Language.cannotStealMore);
                event.setCancelled(true);
                return;
            }
            
            Map<Enchantment, Integer> enchantments = item.getEnchantments();
            double enchantmentMultiplier = 1;
            
            for (Enchantment enchantment : enchantments.keySet())
            {
                enchantmentMultiplier += plugin.getSettingManager().getEnchantmentUnitMultiplier() * item.getEnchantmentLevel(enchantment);
            }
            boolean successful = false;
            int rand = random.nextInt(100) + 1;
            if (rand <= ((double) 100 * ((double) 1 - ((double) ItemValues.valueOf(item.getType()) * enchantmentMultiplier) / ((double) thief.getThiefLevel() + (double) 9))) * plugin.getSettingManager().getSuccessMultiplier())
                successful = true;
                        
            ItemStealEvent stealEvent = new ItemStealEvent(thief, target, event.getCurrentItem(), successful);
            plugin.getServer().getPluginManager().callEvent(stealEvent);
            
            if (stealEvent.isCancelled())
            {
                event.setCancelled(true);
                return;
            }
            else
            {
                thief.addItemToWealth(item);
                
                if (!successful)
                {
                    target.sendMessage(ChatColor.RED + Language.thiefSpotted);
                    event.setCancelled(true);
                    return;
                }
                
                thief.addThiefExperience(ItemValues.valueOf(item.getType()));
                
                if (thief.getThiefExperience() > thief.getExperienceToNextLevel())
                {
                    thief.sendMessage(ChatColor.RED + "You're thieving level has increased to " + (thief.getThiefLevel() + 1) +"!");
                    thief.incrementThiefLevel();
                }
                                
                event.setCancelled(true);
                ItemStack cursor = new ItemStack(item.getType(), 1, item.getDurability(), item.getData().getData());
                cursor.addEnchantments(item.getEnchantments());
                cursor.setDurability(item.getDurability());
                thief.getInventory().addItem(cursor);
                target.getInventory().removeItem(cursor);
            }
            target.updateInventory();
            thief.updateInventory();
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        final Player player = (Player) event.getPlayer();
        if (player != null)
        {
            ThievesPlayer thief = plugin.getPlayerManager().getPlayer(player);
            
            if (plugin.getPlayerManager().isThief(thief))
            {
                plugin.getPlayerManager().removeThief(thief);
                thief.setCooldown(plugin.getSettingManager().getCooldown());
                thief.setItemWealth(0);
            }
        }
        
    }
}

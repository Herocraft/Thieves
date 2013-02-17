package net.minekingdom.snaipe.Thieves;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ThievesInventory implements Inventory
{
    Player player;
    private ItemStack[] items = new ItemStack[36];
    PlayerInventory inventory;
    
    public ThievesInventory(PlayerInventory inventory, Player player)
    {
        this.player = player;
        this.items = inventory.getContents();
        this.inventory = inventory;
    }

    public ItemStack[] getContents()
    {
        ItemStack[] C = new ItemStack[getSize()];
        System.arraycopy(items, 0, C, 0, items.length);
        return C;
    }

    public int getSize()
    {
        return Thieves.getInstance().getSettingManager().canStealHotBar() ? 36 : 27 ;
    }

    public ItemStack getItem(int i)
    {
        ItemStack[] is = this.items;

        if (i >= is.length)
        {
            i -= is.length;
        }
        else
        {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length)
        {
            i -= is.length;
        }

        return is[i];
    }

    public void setItem(int i, ItemStack itemstack)
    {
        ItemStack[] is = this.items;

        if (i >= is.length)
        {
            i -= is.length;
        }
        else
        {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length)
        {
            i -= is.length;
        }

        is[i] = itemstack;
    }

    private int getReversedItemSlotNum(int i)
    {
        if (i >= 27) return i - 27;
        else return i + 9;
    }

    public String getName()
    {
        if (player.getName().length() > 16) return player.getName().substring(0, 16);
        return player.getName();
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... arg0)
    {
        return inventory.addItem(arg0);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int arg0)
    {
        return inventory.all(arg0);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material arg0)
    {
        return inventory.all(arg0);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0)
    {
        return inventory.all(arg0);
    }

    @Override
    public void clear()
    {        
        inventory.clear();
    }

    @Override
    public void clear(int arg0)
    {inventory.clear(arg0);
    }

    @Override
    public boolean contains(int arg0)
    {
        return inventory.contains(arg0);
    }

    @Override
    public boolean contains(Material arg0)
    {
        return inventory.contains(arg0);
    }

    @Override
    public boolean contains(ItemStack arg0)
    {
        return inventory.contains(arg0);
    }

    @Override
    public boolean contains(int arg0, int arg1)
    {
        return inventory.contains(arg0, arg1);
    }

    @Override
    public boolean contains(Material arg0, int arg1)
    {
        return inventory.contains(arg0, arg1);
    }

    @Override
    public boolean contains(ItemStack arg0, int arg1)
    {
        return inventory.contains(arg0, arg1);
    }

    @Override
    public int first(int arg0)
    {
        return inventory.first(arg0);
    }

    @Override
    public int first(Material arg0)
    {
        return inventory.first(arg0);
    }

    @Override
    public int first(ItemStack arg0)
    {
        return inventory.first(arg0);
    }

    @Override
    public int firstEmpty()
    {
        return inventory.firstEmpty();
    }

    @Override
    public InventoryHolder getHolder()
    {
        return inventory.getHolder();
    }

    @Override
    public String getTitle()
    {
        return inventory.getTitle();
    }

    @Override
    public InventoryType getType()
    {
        return inventory.getType();
    }

    @Override
    public List<HumanEntity> getViewers()
    {
        return inventory.getViewers();
    }

    @Override
    public ListIterator<ItemStack> iterator()
    {
        return inventory.iterator();
    }

    @Override
    public void remove(int arg0)
    {
        inventory.remove(arg0);
    }

    @Override
    public void remove(Material arg0)
    {
        inventory.remove(arg0);
    }

    @Override
    public void remove(ItemStack arg0)
    {
        inventory.remove(arg0);
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0)
    {        
        return inventory.removeItem(arg0);
    }

    @Override
    public void setContents(ItemStack[] arg0)
    {        
        inventory.setContents(arg0);
    }

	@Override
	public int getMaxStackSize() {
		return inventory.getMaxStackSize();
	}

	@Override
	public ListIterator<ItemStack> iterator(int arg0) {
		return inventory.iterator(arg0);
	}

	@Override
	public void setMaxStackSize(int arg0) {
		inventory.setMaxStackSize(arg0);
	}

    @Override
    public boolean containsAtLeast(ItemStack arg0, int arg1)
    {
        return inventory.containsAtLeast(arg0, arg1);
    }
}
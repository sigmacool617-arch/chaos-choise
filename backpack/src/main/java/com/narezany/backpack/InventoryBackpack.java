package com.narezany.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Содержимое рюкзака. Живёт в NBT самого предмета, поэтому вещи остаются
 * внутри, даже если рюкзак переложить в сундук или выбросить.
 */
public class InventoryBackpack implements IInventory {

    private final ItemStack stack;
    private final ItemStack[] items;

    public InventoryBackpack(ItemStack stack) {
        this.stack = stack;
        int slots = 9;
        if (stack.getItem() instanceof ItemBackpack) {
            slots = ((ItemBackpack) stack.getItem()).getSlots();
        }
        this.items = new ItemStack[slots];
        read();
    }

    private void read() {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return;
        }
        NBTTagList list = tag.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < items.length) {
                items[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    private void write() {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                items[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setTag("Items", list);
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return items[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (items[slot] == null) {
            return null;
        }
        ItemStack stack;
        if (items[slot].stackSize <= count) {
            stack = items[slot];
            items[slot] = null;
        } else {
            stack = items[slot].splitStack(count);
            if (items[slot].stackSize == 0) {
                items[slot] = null;
            }
        }
        markDirty();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        items[slot] = stack;
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.backpack";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        write();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
        write();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return !(stack.getItem() instanceof ItemBackpack);
    }
}

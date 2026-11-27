package com.narezany.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Содержимое рюкзака. Живёт в NBT самого предмета, поэтому вещи остаются
 * внутри, даже если рюкзак переложить в сундук или выбросить.
 *
 * Важно: сервер после правого клика заменяет предмет в руке копией
 * (NetHandlerPlayServer.processPlayerBlockPlacement -> ItemStack.copyItemStack),
 * поэтому ссылку, взятую при открытии окна, хранить нельзя. Каждый раз
 * находим настоящий рюкзак в инвентаре игрока заново.
 */
public class InventoryBackpack implements IInventory {

    private final EntityPlayer owner;
    private final boolean readOnly;
    private int slot;
    private ItemStack stack;
    private final ItemStack[] items;

    /** Копия для клиента: только показывает содержимое, NBT не трогает. */
    public InventoryBackpack(ItemStack stack) {
        this(null, -1, stack, true);
    }

    /** Серверная часть: пишет прямо в рюкзак из инвентаря игрока. */
    public InventoryBackpack(EntityPlayer owner, int slot, ItemStack stack) {
        this(owner, slot, stack, false);
    }

    private InventoryBackpack(EntityPlayer owner, int slot, ItemStack stack, boolean readOnly) {
        this.owner = owner;
        this.slot = slot;
        this.stack = stack;
        this.readOnly = readOnly;
        int slots = 9;
        if (stack != null && stack.getItem() instanceof ItemBackpack) {
            slots = ((ItemBackpack) stack.getItem()).getSlots();
        }
        this.items = new ItemStack[slots];
        read();
    }

    /** Настоящий предмет в инвентаре игрока. */
    private ItemStack target() {
        if (readOnly || owner == null) {
            return stack;
        }
        ItemStack[] main = owner.inventory.mainInventory;
        if (slot >= 0 && slot < main.length && isBackpack(main[slot])) {
            stack = main[slot];
            return stack;
        }
        // рюкзак переложили в другой слот — ищем по содержимому
        for (int i = 0; i < main.length; i++) {
            if (isBackpack(main[i]) && sameStack(main[i], stack)) {
                slot = i;
                stack = main[i];
                return stack;
            }
        }
        return stack;
    }

    private static boolean isBackpack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBackpack;
    }

    private static boolean sameStack(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.getItem() != b.getItem()) {
            return false;
        }
        NBTTagCompound ta = a.getTagCompound();
        NBTTagCompound tb = b.getTagCompound();
        return ta == null ? tb == null : ta.equals(tb);
    }

    private void read() {
        NBTTagCompound tag = target().getTagCompound();
        if (tag == null) {
            return;
        }
        NBTTagList list = tag.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int index = itemTag.getByte("Slot") & 255;
            if (index >= 0 && index < items.length) {
                items[index] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    private void write() {
        if (readOnly) {
            return;
        }
        ItemStack live = target();
        if (live == null) {
            return;
        }
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                items[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        NBTTagCompound tag = live.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            live.setTagCompound(tag);
        }
        tag.setTag("Items", list);
    }

    @Override
    public int getSizeInventory() {
        return items.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (items[index] == null) {
            return null;
        }
        ItemStack result;
        if (items[index].stackSize <= count) {
            result = items[index];
            items[index] = null;
        } else {
            result = items[index].splitStack(count);
            if (items[index].stackSize == 0) {
                items[index] = null;
            }
        }
        markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items[index] = stack;
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return !(stack.getItem() instanceof ItemBackpack);
    }
}

package com.narezany.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Окно рюкзака: слоты рюкзака сверху, инвентарь игрока снизу. */
public class ContainerBackpack extends Container {

    private final InventoryBackpack inventory;
    private final ItemStack stack;

    /** Клиентская копия окна: только показывает содержимое. */
    public ContainerBackpack(EntityPlayer player, ItemStack stack) {
        this.stack = stack;
        this.inventory = new InventoryBackpack(stack);
        addSlots(player);
    }

    /** Серверное окно: пишет прямо в рюкзак из слота инвентаря. */
    public ContainerBackpack(EntityPlayer player, ItemStack stack, int slot) {
        this.stack = stack;
        this.inventory = new InventoryBackpack(player, slot, stack);
        addSlots(player);
    }

    private void addSlots(EntityPlayer player) {
        int rows = inventory.getSizeInventory() / 9;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new SlotBackpack(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        int invY = 30 + 18 * rows;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, invY + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(player.inventory, col, 8 + col * 18, invY + 58));
        }
    }

    public int getRows() {
        return inventory.getSizeInventory() / 9;
    }

    public String getTitle() {
        return stack.getDisplayName();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    /** Пока окно открыто, держим NBT рюкзака свежим на сервере. */
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        inventory.markDirty();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack copy = null;
        Slot slot = (Slot) inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();

            int backpackSlots = inventory.getSizeInventory();
            if (index < backpackSlots) {
                // из рюкзака в инвентарь игрока
                if (!mergeItemStack(stack, backpackSlots, backpackSlots + 36, true)) {
                    return null;
                }
            } else {
                // из инвентаря игрока в рюкзак
                if (stack.getItem() instanceof ItemBackpack) {
                    return null;
                }
                if (!mergeItemStack(stack, 0, backpackSlots, false)) {
                    return null;
                }
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (stack.stackSize == copy.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, stack);
        }
        return copy;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        inventory.closeInventory();
        super.onContainerClosed(player);
    }
}

package wayoftime.bloodmagic.tile.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import wayoftime.bloodmagic.common.block.BloodMagicBlocks;
import wayoftime.bloodmagic.common.item.ItemTelepositionFocus;
import wayoftime.bloodmagic.tile.TileTeleposer;

public class ContainerTeleposer extends Container
{
	public final IInventory tileTeleposer;

	public ContainerTeleposer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
	{
		this((TileTeleposer) playerInventory.player.world.getTileEntity(extraData.readBlockPos()), windowID, playerInventory);
	}

	public ContainerTeleposer(TileTeleposer tile, int windowID, PlayerInventory playerInventory)
	{
		super(BloodMagicBlocks.TELEPOSER_CONTAINER.get(), windowID);
		this.tileTeleposer = tile;
		this.setup(playerInventory, tile);
	}

	public void setup(PlayerInventory inventory, IInventory tileTeleposer)
	{
		this.addSlot(new SlotTeleposer(tileTeleposer, 0, 80, 15));

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 39 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++)
		{
			addSlot(new Slot(inventory, i, 8 + i * 18, 97));
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotstack = slot.getStack();
			itemstack = slotstack.copy();

			if (index == 0)
			{
				if (!this.mergeItemStack(slotstack, 1, 37, true))
				{
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(slotstack, itemstack);
			} else if (index > 0)
			{
				if (slotstack.getItem() instanceof ItemTelepositionFocus)
				{
					if (!this.mergeItemStack(slotstack, 0, 1, false))
					{
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.mergeItemStack(slotstack, 1, 37, false))
			{
				return ItemStack.EMPTY;
			}

			if (slotstack.getCount() == 0)
			{
				slot.putStack(ItemStack.EMPTY);
			} else
			{
				slot.onSlotChanged();
			}

			if (slotstack.getCount() == itemstack.getCount())
			{
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, slotstack);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return this.tileTeleposer.isUsableByPlayer(playerIn);
	}

	private class SlotTeleposer extends Slot
	{

		public SlotTeleposer(IInventory inventory, int index, int x, int y)
		{
			super(inventory, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack)
		{
			return stack.getItem() instanceof ItemTelepositionFocus;
		}
	}
}

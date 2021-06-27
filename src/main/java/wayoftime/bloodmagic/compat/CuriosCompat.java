package wayoftime.bloodmagic.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import wayoftime.bloodmagic.api.compat.IBloodMagicContainer;

public class CuriosCompat implements IBloodMagicContainer
{
	static
	{
		IBloodMagicContainer.CONTAINERS.add(new CuriosCompat());
	}

	public void setupSlots(InterModEnqueueEvent evt)
	{
		InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
	}

	@Override
	public List<ItemStack> getContainerStacks(PlayerEntity player)
	{
		IItemHandler itemHandler = CuriosApi.getCuriosHelper().getEquippedCurios(player).resolve().get();
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			items.add(itemHandler.getStackInSlot(i));
		}
		return items;
	}
}

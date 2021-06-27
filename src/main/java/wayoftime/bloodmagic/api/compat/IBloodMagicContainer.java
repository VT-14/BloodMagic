package wayoftime.bloodmagic.api.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IBloodMagicContainer
{
	List<IBloodMagicContainer> CONTAINERS = new ArrayList<IBloodMagicContainer>();

	List<ItemStack> getContainerStacks(PlayerEntity player);
}

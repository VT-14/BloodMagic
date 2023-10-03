package wayoftime.bloodmagic.api.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Interface for Items that allow players to see their Soul Network HUD.
 */
public interface ISoulNetworkViewer
{
    boolean canSeeDemonSoulNetwork(Level level, ItemStack stack, Player player);

    int getSoulNetworkResolution(Level level, ItemStack stack, Player player);
}

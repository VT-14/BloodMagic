package wayoftime.bloodmagic.common.item;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Base Teleposer Focus class for Teleposition Foci.
 */
public class TeleposerFocus extends ForgeRegistryEntry<TeleposerFocus>
{
	private final ResourceLocation name;
	private final int range;

	/**
	 * A base object for Teleposition Foci.
	 * 
	 * @param name  - Name of the Foci. Gets put into an unlocalized name.
	 * @param range - Range of area for the focus.
	 */
	public TeleposerFocus(ResourceLocation name, int range)
	{
		this.name = name;
		this.range = range;
	}

	public ResourceLocation getResourceLocation()
	{
		return name;
	}

	public int getRange()
	{
		return range;
	}

	@Override
	public String toString()
	{
		return "TelepositionFocus{" + "name='" + name + '\'' + ", range=" + range + '}';
	}
}

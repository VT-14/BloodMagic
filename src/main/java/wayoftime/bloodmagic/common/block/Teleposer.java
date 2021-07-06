package wayoftime.bloodmagic.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class Teleposer extends Block
{

	public Teleposer()
	{
		super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(2.0F, 5.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2));
	}

}

package wayoftime.bloodmagic.compat.patchouli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.common.block.BloodMagicBlocks;
import wayoftime.bloodmagic.ritual.EnumRuneType;
import wayoftime.bloodmagic.ritual.Ritual;
import wayoftime.bloodmagic.ritual.RitualComponent;

public class RegisterPatchouliMultiblocks
{
	public RegisterPatchouliMultiblocks()
	{
		IPatchouliAPI patAPI = PatchouliAPI.get();

		// Rituals
		List<Ritual> rituals = BloodMagic.RITUAL_MANAGER.getSortedRituals();
		for (Ritual ritual : rituals)
		{
			Map<BlockPos, EnumRuneType> ritualMap = new HashMap();
			List<RitualComponent> components = Lists.newArrayList();
			ritual.gatherComponents(components::add);

			int maxX = 0;
			int minX = 0;
			int maxY = 0;
			int minY = 0;
			int maxZ = 0;
			int minZ = 0;
			for (RitualComponent component : components) // Get Structure Dimensions.
			{
				ritualMap.put(component.getOffset(), component.getRuneType());
				int x = component.getX(Direction.NORTH);
				int y = component.getY();
				int z = component.getZ(Direction.NORTH);

				if (x > maxX)
				{
					maxX = x;
				}
				if (x < minX)
				{
					minX = x;
				}
				if (y > maxY)
				{
					maxY = y;
				}
				if (y < minY)
				{
					minY = y;
				}
				if (z > maxZ)
				{
					maxZ = z;
				}
				if (z < minZ)
				{
					minZ = z;
				}
			}
			int xSize = 1 + maxX - minX;
			int ySize = 1 + maxY - minY;

			String[][] pattern = new String[ySize][xSize];
			for (int y = maxY; y >= minY; y--) // Top to Bottom
			{
				for (int x = minX; x <= maxX; x++) // West to East
				{
					StringBuilder row = new StringBuilder();
					for (int z = minZ; z <= maxZ; z++) // North to South
					{
						BlockPos pos = new BlockPos(x, y, z);
						EnumRuneType rune = ritualMap.get(pos);
						if (rune != null)
						{
							FindKey: for (EnumKeys key : EnumKeys.values())
							{
								if (rune == key.getRune())
								{
									row.append(key.getChar());
									break FindKey;
								}
							}
						} else
						{
							if (y == 0 && x == 0 && z == 0)
							{
								row.append('0'); // Center of Multiblock
							} else
							{
								row.append('_'); // Patchouli's "Anything" Character.
							}
						}
					}
					pattern[maxY - y][x - minX] = row.toString();
				}
			}
			IMultiblock multiblock = patAPI.makeMultiblock(pattern, 'B', BloodMagicBlocks.BLANK_RITUAL_STONE.get(), 'W', BloodMagicBlocks.WATER_RITUAL_STONE.get(), 'F', BloodMagicBlocks.FIRE_RITUAL_STONE.get(), 'E', BloodMagicBlocks.EARTH_RITUAL_STONE.get(), 'A', BloodMagicBlocks.AIR_RITUAL_STONE.get(), 'D', BloodMagicBlocks.DUSK_RITUAL_STONE.get(), 'd', BloodMagicBlocks.DAWN_RITUAL_STONE.get(), '0', BloodMagicBlocks.MASTER_RITUAL_STONE.get());
			patAPI.registerMultiblock(new ResourceLocation(BloodMagic.MODID, BloodMagic.RITUAL_MANAGER.getId(ritual)), multiblock);
		}

		// TODO: Blood Altars.
	}

	private enum EnumKeys
	{
		BLANK_RITUAL_STONE(EnumRuneType.BLANK, 'B', BloodMagicBlocks.BLANK_RITUAL_STONE.get()),
		WATER_RITUAL_STONE(EnumRuneType.WATER, 'W', BloodMagicBlocks.WATER_RITUAL_STONE.get()),
		FIRE_RITUAL_STONE(EnumRuneType.FIRE, 'F', BloodMagicBlocks.FIRE_RITUAL_STONE.get()),
		EARTH_RITUAL_STONE(EnumRuneType.EARTH, 'E', BloodMagicBlocks.EARTH_RITUAL_STONE.get()),
		AIR_RITUAL_STONE(EnumRuneType.AIR, 'A', BloodMagicBlocks.AIR_RITUAL_STONE.get()),
		DUSK_RITUAL_STONE(EnumRuneType.DUSK, 'D', BloodMagicBlocks.DUSK_RITUAL_STONE.get()),
		DAWN_RITUAL_STONE(EnumRuneType.DAWN, 'd', BloodMagicBlocks.DAWN_RITUAL_STONE.get()),
		MASTER_RITUAL_STONE("ritual", BloodMagicBlocks.MASTER_RITUAL_STONE.get());

		String type;
		EnumRuneType rune;
		Character character;
		Block render;

		EnumKeys(String type, Block block)
		{
			this.type = type;
			this.character = '0';
			this.render = block;
		}

		EnumKeys(EnumRuneType rune, Character character, Block block)
		{
			this.type = "ritual";
			this.rune = rune;
			this.character = character;
			this.render = block;
		}

		public EnumRuneType getRune()
		{
			return rune;
		}

		public char getChar()
		{
			return character;
		}
	}
}

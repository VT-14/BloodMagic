package wayoftime.bloodmagic.compat.patchouli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.altar.AltarComponent;
import wayoftime.bloodmagic.altar.AltarTier;
import wayoftime.bloodmagic.altar.ComponentType;
import wayoftime.bloodmagic.common.block.BloodMagicBlocks;
import wayoftime.bloodmagic.impl.BloodMagicAPI;
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
							String name = rune.name();
							PickRune: switch (name)
							{
							case "BLANK":
								row.append('B');
								break PickRune;
							case "WATER":
								row.append('W');
								break PickRune;
							case "FIRE":
								row.append('F');
								break PickRune;
							case "EARTH":
								row.append('E');
								break PickRune;
							case "AIR":
								row.append('A');
								break PickRune;
							case "DUSK":
								row.append('D');
								break PickRune;
							case "DAWN":
								row.append('d');
								break PickRune;
							}
						} else
						{
							if (y == 0 && x == 0 && z == 0)
							{
								row.append('0'); // Master Ritual Stone
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

		// Blood Altars
		for (AltarTier tier : AltarTier.values())
		{
			String[][] pattern;
			int shiftMultiblock = 1;

			if (tier.equals(AltarTier.ONE)) // Special Case Tier 1
			{
				pattern = new String[][] { { "0" }, { "_" } };
				shiftMultiblock = 0;
			} else if (tier.equals(AltarTier.TWO))
			{ // Special Case Tier 2. Non-upgraded Runes.
				pattern = new String[][] { { "___", "_0_", "___" }, { "rRr", "r_r", "rRr" } };
			} else
			{
				Map<BlockPos, ComponentType> altarMap = Maps.newHashMap();
				List<AltarComponent> components = tier.getAltarComponents();
				int maxX = 0;
				int minX = 0;
				int maxY = 0;
				int minY = 0;
				int maxZ = 0;
				int minZ = 0;
				for (AltarComponent component : components) // Get Structure Dimensions.
				{
					BlockPos offset = component.getOffset();
					altarMap.put(offset, component.getComponent());
					int x = offset.getX();
					int y = offset.getY();
					int z = offset.getZ();

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

				pattern = new String[ySize][xSize];
				for (int y = maxY; y >= minY; y--) // Top to Bottom
				{
					for (int x = minX; x <= maxX; x++) // West to East
					{
						StringBuilder row = new StringBuilder();
						for (int z = minZ; z <= maxZ; z++) // North to South
						{
							BlockPos pos = new BlockPos(x, y, z);
							ComponentType component = altarMap.get(pos);
							if (component != null)
							{
								String name = component.name();
								PickComponent: switch (name)
								{
								case "BLOODRUNE":
									row.append('R');
									break PickComponent;
								case "NOTAIR":
									row.append('P');
									break PickComponent;
								case "GLOWSTONE":
									row.append('G');
									break PickComponent;
								case "BLOODSTONE":
									row.append('S');
									break PickComponent;
								case "BEACON":
									row.append('B');
									break PickComponent;
								case "CRYSTAL":
									row.append('C');
									break PickComponent;
								}
							} else
							{
								if (y == 0 && x == 0 && z == 0)
								{
									row.append('0'); // Blood Altar.
								} else
								{
									row.append('_'); // Patchouli's "Anything" Character.
								}
							}
						}
						pattern[maxY - y][x - minX] = row.toString();
					}
				}
			}

			// TODO: try to make the "display" block cycle between usable blocks).

			IStateMatcher bloodRuneSM = patAPI.predicateMatcher(BloodMagicBlocks.BLANK_RUNE.get(), state -> BloodMagicAPI.INSTANCE.getComponentStates(ComponentType.BLOODRUNE).contains(state));
			IStateMatcher notAirSM = patAPI.predicateMatcher(Blocks.STONE_BRICKS, state -> state.getMaterial() != Material.AIR && !state.getMaterial().isLiquid());
			IStateMatcher glowstoneSM = patAPI.predicateMatcher(Blocks.GLOWSTONE, state -> BloodMagicAPI.INSTANCE.getComponentStates(ComponentType.GLOWSTONE).contains(state));
			IStateMatcher bloodStoneSM = patAPI.predicateMatcher(BloodMagicBlocks.BLOODSTONE.get(), state -> BloodMagicAPI.INSTANCE.getComponentStates(ComponentType.BLOODSTONE).contains(state));
			IStateMatcher beaconSM = patAPI.predicateMatcher(Blocks.BEACON, state -> BloodMagicAPI.INSTANCE.getComponentStates(ComponentType.BEACON).contains(state));
			IStateMatcher crystalSM = patAPI.predicateMatcher(Blocks.AIR, state -> BloodMagicAPI.INSTANCE.getComponentStates(ComponentType.CRYSTAL).contains(state));

			IMultiblock multiblock = patAPI.makeMultiblock(pattern, '0', BloodMagicBlocks.BLOOD_ALTAR.get(), 'R', bloodRuneSM, 'P', notAirSM, 'G', glowstoneSM, 'S', bloodStoneSM, 'B', beaconSM, 'C', crystalSM, 'r', BloodMagicBlocks.BLANK_RUNE.get());
			multiblock.offset(0, shiftMultiblock, 0);
			patAPI.registerMultiblock(new ResourceLocation(BloodMagic.MODID, "altar_" + tier.name().toLowerCase()), multiblock);
		}
	}
}

package wayoftime.bloodmagic.compat.patchouli.processors;

import org.apache.logging.log4j.LogManager;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.common.item.ItemRitualDiviner;
import wayoftime.bloodmagic.ritual.Ritual;
import wayoftime.bloodmagic.util.helper.RitualHelper;
import wayoftime.bloodmagic.util.helper.TextHelper;

public class RitualInfoProcessor implements IComponentProcessor
{
	private Ritual ritual;

	@Override
	public void setup(IVariableProvider variables)
	{
		String id = variables.get("ritual").asString();
		ritual = BloodMagic.RITUAL_MANAGER.getRitual(id);
		if (ritual == null)
		{
			LogManager.getLogger().warn("Guidebook given invalid Ritual ID {}", id);
		}
	}

	@Override
	public IVariable process(String key)
	{
		if (ritual == null)
		{
			return null;
		}
		if (key.equals("auto_text"))
		{
			final String TEXT_BASE = ItemRitualDiviner.tooltipBase; // Use the Ritual Diviner's text.
			int counts[] = RitualHelper.getRuneCounts(ritual); // blank, air, water, fire, earth, dusk, dawn, total
			String output = TextHelper.localize(ritual.getTranslationKey() + ".info") + "$(br2)";

			for (enumRuneTextFormatter rune : enumRuneTextFormatter.values())
			{
				if (counts[rune.i] > 0)
				{
					output += rune.colorCode + TextHelper.localize(TEXT_BASE + rune.runeType, counts[rune.i]) + "$()$(br)";
				}
			}

			output += "$(br2)" + TextHelper.localize(TEXT_BASE + "totalRune", counts[7]) + "$(br)";
			// In Patchouli, "$(br)$(br)" apparently does not make a double line break, so
			// we start this with a double line break.

			switch (ritual.getCrystalLevel())
			{
			case 0:
				output += TextHelper.localize("patchouli.bloodmagic.ritual_info.weak_activation_crystal_link", TextHelper.localize("item.bloodmagic.activationcrystalweak"));
				break;
			case 1:
				output += TextHelper.localize("patchouli.bloodmagic.ritual_info.awakened_activation_crystal_link", TextHelper.localize("item.bloodmagic.activationcrystalawakened"));
				break;
			default:
				output += TextHelper.localize("item.bloodmagic.activationcrystalcreative") + "$(br)";
			}
			output += TextHelper.localize("patchouli.bloodmagic.ritual_info.activation_cost", ritual.getActivationCost());
			if (ritual.getRefreshCost() != 0)
			{
				output += TextHelper.localize("patchouli.bloodmagic.ritual_info.upkeep_cost", ritual.getRefreshCost(), ritual.getRefreshTime());
			}

			return IVariable.wrap(output);
		}
		return null;
	}

	private enum enumRuneTextFormatter
	{
		BLANK(0, "$(blank)", "blankRune"),
		AIR(1, "$(air)", "airRune"),
		WATER(2, "$(water)", "waterRune"),
		FIRE(3, "$(fire)", "fireRune"),
		EARTH(4, "$(earth)", "earthRune"),
		DUSK(5, "$(dusk)", "duskRune"),
		DAWN(6, "$(dawn)", "dawnRune");

		private final int i; // index value from RitualHelper.getRuneCounts array.
		private final String colorCode; // Patchouli color codes (set in Book.json)
		private final String runeType; // rune's suffix for translation.

		enumRuneTextFormatter(int i, String colorCode, String runeType)
		{
			this.i = i;
			this.colorCode = colorCode;
			this.runeType = runeType;
		}
	}
}

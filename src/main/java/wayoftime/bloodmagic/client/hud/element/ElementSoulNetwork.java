package wayoftime.bloodmagic.client.hud.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.core.data.SoulNetwork;
import wayoftime.bloodmagic.util.Utils;
import wayoftime.bloodmagic.util.helper.NetworkHelper;

public class ElementSoulNetwork extends HUDElement
{

    private static final ResourceLocation BAR_LOCATION = new ResourceLocation(BloodMagic.MODID, "textures/hud/bars.png");
    public ElementSoulNetwork() {super(36, 76);}

    @Override
    public void draw(GuiGraphics guiGraphics, float partialTicks, int drawX, int drawY)
    {
        drawX += 4; // Offset X to account for wider HUD Box.

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        guiGraphics.blit(BAR_LOCATION, drawX, drawY, 0, 78, 28, 70);

        SoulNetwork soulNetwork = NetworkHelper.getSoulNetwork(player);
        int maxOrb = NetworkHelper.getCurrentMaxOrb(soulNetwork);
        int level = soulNetwork.getCurrentEssence();
        int maxCapacity = NetworkHelper.getMaximumForTier(maxOrb);
        int fillHeight = 61 * level / maxCapacity; // 60 would have the bar full at exactly 100%.  Set to 61 so the bar
        // is full slightly before the Soul Network is completely topped, so rituals don't flash the HUD.

        guiGraphics.blit(BAR_LOCATION, drawX + 8, drawY + 66 - fillHeight, 32, 86 + (58 - fillHeight), 12, fillHeight);

        if (player.isShiftKeyDown())
        {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(drawX + 14, drawY + 70, 0);
            poseStack.scale(0.5f, 0.5f, 1f);
            guiGraphics.drawCenteredString(minecraft.font, String.format("%,d LP", level), 0, 2, 0xffffffff);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRender(Minecraft minecraft)
    {
        return NetworkHelper.canPlayerSeeSoulNetwork(Minecraft.getInstance().player);
    }
}

package de.guntram.mcmod.advancementinfo.mixin;

import de.guntram.mcmod.advancementinfo.AdvancementInfo;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * NeoForge 1.21.1 port of the original Fabric AdvancementTabTypeMixin.
 * Original: https://github.com/wafarm/AdvancementInfo (author: Giselbaer / gbl)
 *
 * AdvancementTabType is a package-private enum in mojmap, so the mixin must be
 * registered via its string target name.
 */
@Mixin(targets = "net.minecraft.client.gui.screens.advancements.AdvancementTabType")
public class AdvancementTabTypeMixin {
    @ModifyConstant(method = "getX", constant = @Constant(intValue = 248), require = 1)
    public int getAdjustedTabX(int orig) {
        //noinspection DataFlowIssue
        return Minecraft.getInstance().screen.width - AdvancementInfo.config.marginX * 2 - 4;
    }

    @ModifyConstant(method = "getY", constant = @Constant(intValue = 136), require = 1)
    public int getAdjustedTabY(int orig) {
        //noinspection DataFlowIssue
        return Minecraft.getInstance().screen.height - AdvancementInfo.config.marginY * 2 - 4;
    }
}

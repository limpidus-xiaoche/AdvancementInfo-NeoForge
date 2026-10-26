package de.guntram.mcmod.advancementinfo.mixin;

import de.guntram.mcmod.advancementinfo.AdvancementInfo;
import de.guntram.mcmod.advancementinfo.accessors.AdvancementWidgetAccessor;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge 1.21.1 port of the original Fabric AdvancementWidgetMixin.
 * Original: https://github.com/wafarm/AdvancementInfo (author: Giselbaer / gbl)
 */
@Mixin(AdvancementWidget.class)
public class AdvancementWidgetMixin implements AdvancementWidgetAccessor {

    @Shadow
    private AdvancementProgress progress;

    @Inject(method = "drawHover", at = @At("HEAD"))
    public void rememberTooltip(GuiGraphics context, int originX, int originY, float alpha, int x, int y, CallbackInfo ci) {
        AdvancementInfo.mouseOver = (AdvancementWidget) (Object) this;
    }

    @Override
    public AdvancementProgress advancementInfo$getProgress() {
        return this.progress;
    }
}

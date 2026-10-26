package de.guntram.mcmod.advancementinfo.accessors;

import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.multiplayer.ClientAdvancements;

public interface AdvancementScreenAccessor {
    ClientAdvancements advancementInfo$getAdvancementHandler();

    AdvancementTab advancementInfo$myGetTab(AdvancementNode advancement);
}

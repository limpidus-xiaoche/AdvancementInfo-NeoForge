package de.guntram.mcmod.advancementinfo;

import de.guntram.mcmod.advancementinfo.accessors.AdvancementScreenAccessor;
import de.guntram.mcmod.advancementinfo.accessors.AdvancementWidgetAccessor;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * NeoForge 1.21.1 port of the Fabric mod AdvancementInfo.
 * Original: https://github.com/wafarm/AdvancementInfo (author: Giselbaer / gbl)
 */
@Mod(AdvancementInfo.MODID)
public class AdvancementInfo {
    static final String MODID = "advancementinfo";
    static final Logger LOGGER = LoggerFactory.getLogger("AdvancementInfo");

    static public AdvancementWidget mouseOver, mouseClicked;
    static public List<AdvancementStep> cachedClickList;
    static public int cachedClickListLineCount;
    public static boolean showAll;
    public static ModConfig config;

    public AdvancementInfo(IEventBus modBus) {
        modBus.addListener(this::onInitializeClient);
    }

    private void onInitializeClient(FMLClientSetupEvent event) {
        showAll = false;
        config = ModConfig.load();
        LOGGER.info("AdvancementInfo initialized");
    }

    public static List<AdvancementStep> getSteps(AdvancementWidgetAccessor widget) {
        List<AdvancementStep> result = new ArrayList<>();
        addStep(result, widget.advancementInfo$getProgress(), widget.advancementInfo$getProgress().getRemainingCriteria(), false);
        addStep(result, widget.advancementInfo$getProgress(), widget.advancementInfo$getProgress().getCompletedCriteria(), true);
        return result;
    }

    private static void addStep(List<AdvancementStep> result, AdvancementProgress progress, Iterable<String> criteria, boolean obtained) {
        final String[] prefixes = new String[]{"item.minecraft", "block.minecraft", "entity.minecraft", "container", "effect.minecraft", "biome.minecraft"};
        // criteria is actually a List<> .. but play nice
        ArrayList<String> sorted = new ArrayList<>();
        for (String s : criteria) {
            sorted.add(s);
        }
        ArrayList<String> details = null;
        Collections.sort(sorted);
        for (String s : sorted) {
            String translation = null;
            String key = s;
            if (key.startsWith("minecraft:")) {
                key = key.substring(10);
            }
            if (key.startsWith("armor_trimmed_minecraft:")) {
                key = key.substring(24);
            }
            if (key.startsWith("textures/entity/")) {
                String entityAppearance = key.substring(16);
                int dotPos;
                if ((dotPos = entityAppearance.indexOf(".")) > 0) {
                    entityAppearance = entityAppearance.substring(0, dotPos);
                }
                translation = entityAppearance;
            }
            if (translation == null) {
                for (String prefix : prefixes) {
                    if (I18n.exists(prefix + "." + key)) {
                        translation = I18n.get(prefix + "." + key);
                        break;
                    }
                }
            }
            if (translation == null) {
            /*
                As this helps only very few advancements which use potion effects, and
                we can't get the conditions the way we did any more, just ignore this
                and use the key, which we do with everything else anyway.

                CriterionConditions conditions = ((AdvancementProgressAccessor)(progress)).getCriterion(s).conditions();
                if (conditions != null) {
                    JsonObject o = conditions.toJson();
                    JsonElement maybeEffects = o.get("effects");
                    if (maybeEffects != null && maybeEffects instanceof JsonObject) {
                        JsonObject effects = (JsonObject) maybeEffects;
                        details = new ArrayList<>(effects.entrySet().size());
                        for (Map.Entry<String, JsonElement> entry: effects.entrySet()) {
                            details.add(I18n.get("effect."+entry.getKey().replace(':', '.')));
                        }
                    }
                }
            */
                translation = key;
            }
            result.add(new AdvancementStep(translation, obtained, details));
        }
    }

    public static void setMatchingFrom(AdvancementsScreen screen, String text) {
        List<AdvancementStep> result = new ArrayList<>();
        ClientAdvancements advancementHandler = ((AdvancementScreenAccessor) screen).advancementInfo$getAdvancementHandler();
        Collection<AdvancementNode> all = advancementHandler.getTree().nodes();
        int lineCount = 0;

        text = text.toLowerCase();
        for (AdvancementNode adv : all) {
            ResourceLocation id = adv.holder().id();
            if (id.getPath().startsWith("recipes/")) {
                continue;
            }
            Optional<DisplayInfo> display = adv.advancement().display();
            if (display.isEmpty()) {
                LOGGER.debug("! {} Has no display", id);
                continue;
            }
            if (display.get().getTitle() == null) {
                LOGGER.debug("! {} Has no title", id);
                continue;
            }
            if (display.get().getDescription() == null) {
                LOGGER.debug("! {} Has no description", id);
                continue;
            }
            String title = display.get().getTitle().getString();
            String desc = display.get().getDescription().getString();
            LOGGER.debug("- {} {}: {} ", id, title, desc);
            if (title.toLowerCase().contains(text)
                || desc.toLowerCase().contains(text)) {
                ArrayList<String> details = new ArrayList<>();
                details.add(desc);
                AdvancementTab tab = ((AdvancementScreenAccessor) screen).advancementInfo$myGetTab(adv);
                if (tab == null) {
                    LOGGER.info("no tab found for advancement {} title {} description {}", id, title, desc);
                    continue;
                }
                details.add(tab.getTitle().getString());
                boolean done = ((AdvancementWidgetAccessor) (screen.getAdvancementWidget(adv))).advancementInfo$getProgress().isDone();
                result.add(new AdvancementStep(title, done, details));
                lineCount += 3;
            }
        }
        cachedClickList = result;
        cachedClickListLineCount = lineCount;
        mouseOver = null;
    }
}

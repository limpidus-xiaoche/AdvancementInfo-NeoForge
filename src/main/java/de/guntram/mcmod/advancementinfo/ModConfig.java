package de.guntram.mcmod.advancementinfo;

import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Simple properties-file based config, replacing the cloth-config dependency
 * of the original Fabric mod (kept lightweight for the NeoForge port).
 */
public class ModConfig {
    public int colorHave = 0x00aa00;
    public int colorHaveNot = 0xaa0000;
    public int marginX = 30;
    public int marginY = 30;
    public InfoWidth infoWidth = new InfoWidth();

    public void validate() {
        infoWidth.min = Math.max(100, infoWidth.min);
        if (infoWidth.min > infoWidth.max) {
            infoWidth.max = infoWidth.min;
        }
    }

    public static ModConfig load() {
        ModConfig config = new ModConfig();
        Path path = FMLPaths.CONFIGDIR.get().resolve(AdvancementInfo.MODID + ".properties");
        Properties props = new Properties();
        if (Files.exists(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            } catch (IOException e) {
                AdvancementInfo.LOGGER.warn("Could not read config {}, using defaults: {}", path, e.toString());
            }
        }
        config.colorHave = readInt(props, "colorHave", config.colorHave);
        config.colorHaveNot = readInt(props, "colorHaveNot", config.colorHaveNot);
        config.marginX = readInt(props, "marginX", config.marginX);
        config.marginY = readInt(props, "marginY", config.marginY);
        config.infoWidth.min = readInt(props, "infoWidth.min", config.infoWidth.min);
        config.infoWidth.max = readInt(props, "infoWidth.max", config.infoWidth.max);
        config.infoWidth.percent = readInt(props, "infoWidth.percent", config.infoWidth.percent);
        config.validate();
        if (!Files.exists(path)) {
            config.save(path);
        }
        return config;
    }

    private static int readInt(Properties props, String key, int def) {
        String value = props.getProperty(key);
        if (value == null) {
            return def;
        }
        try {
            return Integer.decode(value.trim());
        } catch (NumberFormatException e) {
            AdvancementInfo.LOGGER.warn("Invalid value for config key {}: {}", key, value);
            return def;
        }
    }

    private void save(Path path) {
        Properties props = new Properties();
        props.setProperty("colorHave", "0x" + Integer.toHexString(colorHave));
        props.setProperty("colorHaveNot", "0x" + Integer.toHexString(colorHaveNot));
        props.setProperty("marginX", Integer.toString(marginX));
        props.setProperty("marginY", Integer.toString(marginY));
        props.setProperty("infoWidth.min", Integer.toString(infoWidth.min));
        props.setProperty("infoWidth.max", Integer.toString(infoWidth.max));
        props.setProperty("infoWidth.percent", Integer.toString(infoWidth.percent));
        try (OutputStream out = Files.newOutputStream(path)) {
            props.store(out, "AdvancementInfo configuration (NeoForge port)");
        } catch (IOException e) {
            AdvancementInfo.LOGGER.warn("Could not write default config {}: {}", path, e.toString());
        }
    }

    public static class InfoWidth {
        public int min = 120;
        public int max = 300;
        public int percent = 30;

        private int clamp(int totalWidth) {
            int wantWidth = (int) (totalWidth * percent / 100f);
            return Math.max(Math.min(wantWidth, max), min);
        }

        public int calculate(int screenWidth) {
            if (percent == 0 || max == 0) {
                return 0;
            }
            return clamp(screenWidth - AdvancementInfo.config.marginX * 2);
        }
    }
}

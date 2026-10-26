package de.guntram.mcmod.advancementinfo;

import java.util.List;

public class AdvancementStep {

    // As we need to run trimToWidth over all entries anyway, which wants
    // Strings, not Texts, we can save a few µs by using all Strings here
    // and not converting to and fro.

    private final String name;
    private final boolean obtained;
    private final List<String> details;

    AdvancementStep(String name, boolean obtained, List<String> details) {
        this.name = name;
        this.obtained = obtained;
        this.details = details;
    }

    public boolean getObtained() {
        return obtained;
    }

    public String getName() {
        return name;
    }

    public List<String> getDetails() {
        return details;
    }
}

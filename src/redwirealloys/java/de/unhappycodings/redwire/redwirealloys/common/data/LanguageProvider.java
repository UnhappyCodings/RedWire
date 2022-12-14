package de.unhappycodings.redwire.redwirealloys.common.data;

import de.unhappycodings.redwire.redwirealloys.RedwireAlloys;
import de.unhappycodings.redwire.redwirealloys.common.block.ModBlocks;
import net.minecraft.data.DataGenerator;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {

    public LanguageProvider(DataGenerator gen, String locale) {
        super(gen, RedwireAlloys.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {

        add(ModBlocks.RED_ALLOY_WIRE.get(), "Red Alloy Wire");

        add("itemGroup.redwirealloys.items", "Redwire: Alloys");

    }
}

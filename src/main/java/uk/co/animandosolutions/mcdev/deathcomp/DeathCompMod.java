package uk.co.animandosolutions.mcdev.deathcomp;

import static uk.co.animandosolutions.mcdev.deathcomp.utils.Logger.LOGGER;

import net.fabricmc.api.ModInitializer;
import uk.co.animandosolutions.mcdev.deathcomp.command.CommandHandler;

public class DeathCompMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Loaded DeathComp mod");

        CommandHandler.INSTANCE.registerCommands();
    }
} 
package uk.co.animandosolutions.mcdev.deathcomp;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import uk.co.animandosolutions.mcdev.deathcomp.command.CommandHandler;
import uk.co.animandosolutions.mcdev.deathcomp.utils.Logger;

public class DeathCompMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        CommandHandler.INSTANCE.registerCommands();
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register( this::listener);
    }

    public void listener(MinecraftServer minecraftserver1) {
        
        Logger.LOGGER.info("Server started");
    }
} 
package uk.co.animandosolutions.mcdev.deathcomp.command;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.Arrays;
import java.util.List;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandHandler {

	public static final CommandHandler INSTANCE = new CommandHandler();

	List<CommandDefinition> commands = Arrays.asList(new ListScores());

	private CommandHandler() {
	}

	public void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

			commands.forEach(commandDefinition -> {

				String subCommand = commandDefinition.getCommand();
				var subCommandBuilder = literal(subCommand).requires(scope -> scope.hasPermissionLevel(0));

				subCommandBuilder.executes(commandDefinition::execute);
				dispatcher.register(subCommandBuilder);
			});

		});
	}
}

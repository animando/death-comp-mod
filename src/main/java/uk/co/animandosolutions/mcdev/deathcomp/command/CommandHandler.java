package uk.co.animandosolutions.mcdev.deathcomp.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class CommandHandler {

	public static final CommandHandler INSTANCE = new CommandHandler();

	List<CommandDefinition> commands = Arrays.asList(new ListScores());

	private CommandHandler() {
	}

	public void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

			commands.forEach(commandDefinition -> {

				String subCommand = commandDefinition.getCommand();
				var subCommandBuilder = literal(subCommand)
						.requires(scope -> scope.hasPermissionLevel(0));

				var args = commandDefinition.getArguments();
				RequiredArgumentBuilder<ServerCommandSource, ?> argBuilder = null;
				for (int i = args.length - 1; i >= 0; i--) {
					var arg = args[i];
					var finalArg = i == args.length - 1;

					RequiredArgumentBuilder<ServerCommandSource, ?> localArgBuilder = argument(arg.name(),
							arg.argumentType());
					if (arg.suggestionProvider().isPresent()) {
						localArgBuilder.suggests(arg.suggestionProvider().get());
					}
					localArgBuilder.requires(Permissions.require(arg.permission()));
					if (finalArg || args[i + 1].optional()) {
						localArgBuilder = localArgBuilder.executes(commandDefinition::execute);
					}
					if (argBuilder != null) {
						argBuilder = localArgBuilder.then(argBuilder);
					} else {
						argBuilder = localArgBuilder;
					}
				}
				if (argBuilder != null) {
					subCommandBuilder.then(argBuilder);
				}
				if (args.length == 0 || args[0].optional()) {
					subCommandBuilder.executes(commandDefinition::execute);
				}

				dispatcher.register(subCommandBuilder);
			});

		});
	}
}

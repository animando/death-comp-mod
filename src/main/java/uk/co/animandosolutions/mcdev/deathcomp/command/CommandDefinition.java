package uk.co.animandosolutions.mcdev.deathcomp.command;

import static uk.co.animandosolutions.mcdev.deathcomp.command.CommandHelper.getOptionalArgument;

import java.util.Optional;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.server.command.ServerCommandSource;

public interface CommandDefinition {
	public record Argument<T>(String name, ArgumentType<T> argumentType, boolean optional, String permission, Optional<SuggestionProvider<ServerCommandSource>> suggestionProvider) {
	}

	String getCommand();

	int execute(CommandContext<ServerCommandSource> context);

	default Argument<?>[] getArguments() {
		return new Argument<?>[] {};
	}

	default void sendMessage(ServerCommandSource source, String message) {
		CommandHelper.sendFeedback(source, message);
	}

	default Optional<String> getArgument(CommandContext<ServerCommandSource> context, String argumentName) {
		return this.getArgument(context, argumentName, String.class);
	}

	default String getArgument(CommandContext<ServerCommandSource> context, String argumentName, String fallbackValue) {
		return this.getArgument(context, argumentName, String.class).orElse(fallbackValue);
	}
	
	default <V> V getArgument(CommandContext<ServerCommandSource> context, String argumentName, final Class<V> clazz, V fallbackValue) {
		return this.getArgument(context, argumentName, clazz).orElse(fallbackValue);
	}
	
	default <V> Optional<V> getArgument(CommandContext<ServerCommandSource> context, String argumentName, final Class<V> clazz) {
		return getOptionalArgument(context, argumentName, clazz);
	}
	
	default String getPermission() {
		return null;
	};
}

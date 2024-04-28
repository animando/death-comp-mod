package uk.co.animandosolutions.mcdev.deathcomp.command;

import java.util.Optional;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandHelper {

	public static void sendFeedback(ServerCommandSource source, String message) {
		source.sendFeedback(() -> Text.literal(message), false);
	}

	public static <T> Optional<T> getOptionalArgument(CommandContext<ServerCommandSource> context, String argumentName,
			Class<T> clazz) {
		try {
			return Optional.of(context.getArgument(argumentName, clazz));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}

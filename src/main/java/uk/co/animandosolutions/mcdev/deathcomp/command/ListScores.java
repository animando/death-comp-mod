package uk.co.animandosolutions.mcdev.deathcomp.command;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import uk.co.animandosolutions.mcdev.deathcomp.utils.Logger;

public class ListScores implements CommandDefinition {
	static final BigDecimal TICKS_PER_HOUR = new BigDecimal(72000);

	static record Scores(String playerName, int deaths, double playTime, Double quotient) {
		public String toString() {
			return String.format("[name=%s, deaths=%s, playTime=%sh]", playerName, deaths, playTime);
		}
	}

	@Override
	public int execute(CommandContext<ServerCommandSource> context) {
		try {

			MinecraftServer server = context.getSource().getServer();
			ServerScoreboard scoreboard = server.getScoreboard();
			ScoreboardObjective deathCountObjective = scoreboard.getObjectives().stream()
					.filter(t -> t.getName().equals("ts_Deaths")).findFirst().orElseThrow();
			ScoreboardObjective playTimeObjective = scoreboard.getObjectives().stream()
					.filter(t -> t.getName().equals("ts_PlayTime")).findFirst().orElseThrow();

			var whitelist = new HashSet<>(Arrays.asList(server.getPlayerManager().getWhitelistedNames()));
			var scores = scoreboard.getKnownScoreHolders().stream()
					.filter(it -> whitelist.contains(it.getNameForScoreboard()))
					.map(getPlayerScores(scoreboard, deathCountObjective, playTimeObjective))
					.filter(it -> it.playTime() > 0).sorted(this::sortScores).toList();

			sendMessage(context.getSource(), "Death Competition Standings");
			scores.forEach(it -> {
				if (it.quotient() == null) {
					sendMessage(context.getSource(), format("%s: Error", it.playerName));
				} else {
					sendMessage(context.getSource(),
							format("%s [deaths=%s, playtime=%.2f hours]", it.playerName, it.deaths, it.playTime));
				}
			});

			return 1;
		} catch (Exception e) {
			Logger.LOGGER.error("Error", e);
			return -1;
		}
	}

	private Function<ScoreHolder, Scores> getPlayerScores(final ServerScoreboard scoreboard,
			final ScoreboardObjective deathCountObjective, final ScoreboardObjective playTimeObjective) {
		return scoreholder -> {
			double playTime = Optional.ofNullable(scoreboard.getScore(scoreholder, playTimeObjective))
					.map(it -> it.getScore()).map(this::ticksToHours).orElse(0d);
			int deathCount = Optional.ofNullable(scoreboard.getScore(scoreholder, deathCountObjective))
					.map(it -> it.getScore()).orElse(0);
			try {
				double quotient = this.deathsPerPlayTimeQuotient(deathCount, playTime);

				return new Scores(scoreholder.getNameForScoreboard(), deathCount, playTime, quotient);
			} catch (Exception e) {
				Logger.LOGGER
						.error(format("Error calculating quotient for player %s with playTime %.2f and deathCount %s",
								scoreholder.getNameForScoreboard(), playTime, deathCount), e);
				return new Scores(scoreholder.getNameForScoreboard(), deathCount, playTime, null);
			}
		};
	}

	@Override
	public String getCommand() {
		return "deathcomp";
	}

	@Override
	public String getPermission() {
		return "deathcomp.list";
	}

	private int sortScores(Scores score1, Scores score2) {
		return this.compareByDeathsPerPlayTime(score1, score2);
	}

	private int compareByDeathsPerPlayTime(Scores score1, Scores score2) {
		if (score1.quotient() == null) {
			return -1;
		}
		if (score2.quotient() == null) {
			return 1;
		}

		return new BigDecimal(score2.quotient()).compareTo(new BigDecimal(score1.quotient()));
	}

	private double deathsPerPlayTimeQuotient(int deaths, double playTime) {
		var playTimeDecimal = new BigDecimal(playTime);
		var deathExponent = Math.sqrt(deaths);
		var eRaisedToRootDeaths = new BigDecimal(Math.pow(Math.E, deathExponent));

		return eRaisedToRootDeaths.divide(playTimeDecimal, MathContext.DECIMAL128).doubleValue();
	}

	private Double ticksToHours(Integer integer1) {
		return new BigDecimal(integer1).divide(TICKS_PER_HOUR, MathContext.DECIMAL64).doubleValue();
	};
}

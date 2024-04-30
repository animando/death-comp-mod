package uk.co.animandosolutions.mcdev.deathcomp.command;

import static java.lang.String.format;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import uk.co.animandosolutions.mcdev.deathcomp.Scores;
import uk.co.animandosolutions.mcdev.deathcomp.utils.Logger;
import uk.co.animandosolutions.mcdev.deathcomp.utils.StatsUtils;

public class ListScores implements CommandDefinition {
    private static final String TS_DEATHS = "ts_Deaths";
    private static final String TS_PLAY_TIME = "ts_PlayTime";
    private static final BigDecimal TICKS_PER_HOUR = new BigDecimal(72000);

    static final class DeltaDeathRateComparator implements Comparator<Scores> {
        private double expectedDeathRate;

        public DeltaDeathRateComparator(final double expectedDeathRate) {
            this.expectedDeathRate = expectedDeathRate;
        }

        @Override
        public int compare(Scores o1, Scores o2) {
            var delta1 = createDelta(o1);
            var delta2 = createDelta(o2);

            return Double.valueOf(delta2).compareTo(Double.valueOf(delta1));

        }

        private double createDelta(Scores scores) {
            var expectedDeaths = this.expectedDeathRate * scores.playTime();
            return scores.deaths() - expectedDeaths;
        }

    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        try {
            MinecraftServer server = context.getSource().getServer();
            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardObjective deathCountObjective = scoreboard.getObjectives().stream()
                    .filter(t -> t.getName().equals(TS_DEATHS)).findFirst().orElse(null);
            ScoreboardObjective playTimeObjective = scoreboard.getObjectives().stream()
                    .filter(t -> t.getName().equals(TS_PLAY_TIME)).findFirst().orElse(null);

            if (deathCountObjective == null || playTimeObjective == null) {
                sendMessage(context.getSource(), "Track Raw Statistics datapack is required by deathcomp");
                return -1;
            }

            var whitelist = new HashSet<>(Arrays.asList(server.getPlayerManager().getWhitelistedNames()));
            var scores = scoreboard.getKnownScoreHolders().stream()
                    .filter(it -> whitelist.contains(it.getNameForScoreboard()))
                    .map(getPlayerScores(scoreboard, deathCountObjective, playTimeObjective))
                    .filter(it -> it.playTime() > 0).toList();
            if (scores.size() == 0) {
                sendMessage(context.getSource(), "<empty>");
                return 0;
            } else {

                var medianDeaths = scores.stream().map(Scores::deaths).map(Integer::doubleValue)
                        .collect(collectingAndThen(toList(), StatsUtils::computeMedian));
                var medianPlayTime = scores.stream().map(Scores::playTime)
                        .collect(collectingAndThen(toList(), StatsUtils::computeMedian));
                var averageDeathsPerPlayTime = new BigDecimal(medianDeaths).divide(new BigDecimal(medianPlayTime),
                        MathContext.DECIMAL128); 

                var sortedScores = scores.stream()
                        .sorted(new DeltaDeathRateComparator(averageDeathsPerPlayTime.doubleValue())).toList();
                
                publishScores(context, sortedScores);

                return 1;
            }
        } catch (Exception e) {
            Logger.LOGGER.error("Error", e);
            return -1;
        }
    }

    private void publishScores(CommandContext<ServerCommandSource> context, List<Scores> sortedScores) {
        sendMessage(context.getSource(), "Death Competition Standings");
        sortedScores.forEach(it -> {
            sendMessage(context.getSource(),
                    format("%s [deaths=%s, playtime=%.2f hours]", it.playerName(), it.deaths(), it.playTime()));
        });
    }

    private Function<ScoreHolder, Scores> getPlayerScores(final ServerScoreboard scoreboard,
            final ScoreboardObjective deathCountObjective, final ScoreboardObjective playTimeObjective) {
        return scoreholder -> {
            double playTime = Optional.ofNullable(scoreboard.getScore(scoreholder, playTimeObjective))
                    .map(it -> it.getScore()).map(this::ticksToHours).orElse(0d);
            int deathCount = Optional.ofNullable(scoreboard.getScore(scoreholder, deathCountObjective))
                    .map(it -> it.getScore()).orElse(0);
            try {

                return new Scores(scoreholder.getNameForScoreboard(), deathCount, playTime);
            } catch (Exception e) {
                Logger.LOGGER
                        .error(format("Error calculating quotient for player %s with playTime %.2f and deathCount %s",
                                scoreholder.getNameForScoreboard(), playTime, deathCount), e);
                return new Scores(scoreholder.getNameForScoreboard(), deathCount, playTime);
            }
        };
    }

    @Override
    public String getCommand() {
        return CommandConstants.Commands.DEATHCOMP;
    }

    private Double ticksToHours(Integer integer1) {
        return new BigDecimal(integer1).divide(TICKS_PER_HOUR, MathContext.DECIMAL64).doubleValue();
    };
}

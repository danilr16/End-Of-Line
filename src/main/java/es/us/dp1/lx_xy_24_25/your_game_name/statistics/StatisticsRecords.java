package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import java.util.Map;

import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;

public class StatisticsRecords {
    
    public record Ranking(Map<String, Integer> victories, Map<String, Integer> points) {

        public static Ranking of(Map<String, Integer> victories, Map<String, Integer> points) {
            return new Ranking(victories, points);
        }
    }

    public record BasicStatistics (Integer total, Double average, Integer min, Integer max) {

        public static BasicStatistics of(Integer total, Double average, Integer min, Integer max) {
            return new BasicStatistics(total, average, min, max);
        }
    }

    public record MyGamesStatistics (BasicStatistics victories, BasicStatistics defeats, Map<PowerType, Integer> powersMostUsed) {

        public static MyGamesStatistics of(BasicStatistics victories, BasicStatistics defeats, Map<PowerType, Integer> powersMostUsed) {
            return new MyGamesStatistics(victories, defeats, powersMostUsed);
        }
    }

    public record NumGames (BasicStatistics global, BasicStatistics user) {

        public static NumGames of(BasicStatistics global, BasicStatistics user) {
            return new NumGames(global, user);
        }
    }

    public record NumPlayers (BasicStatistics global, BasicStatistics user, GameMode globalMostPlayed, GameMode userMostPlayed) {
    
        public static NumPlayers of(BasicStatistics global, BasicStatistics user, GameMode globalMostPlayed, GameMode userMostPlayed) {
            return new NumPlayers(global, user, globalMostPlayed, userMostPlayed);
        }
    }

    public record DurationGames (BasicStatistics global, BasicStatistics user) {

        public static DurationGames of(BasicStatistics global, BasicStatistics user) {
            return new DurationGames(global, user);
        }
    }
}

package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;

public class StatisticsClasses {
    
    public record Ranking(List<Victories> victories, List<Points> points) {

        public static Ranking of(List<Victories> victories, List<Points> points) {
            return new Ranking(victories, points);
        }
    }

    public interface Victories {
    
        String getUserName();
        void setUserName(String username);
        
        Integer getVictories();
        void setVictories(Integer victories);
    }

    public interface Points {
    
        String getUserName();
        void setUserName(String username);
        
        Integer getPoints();
        void setVictories(Integer points);
    }

    public interface BasicStatistics{

        Integer getTotal();
        void setTotal(Integer total);

        Double getAverage();
        void setAverage(Double average);

        Integer getMin();
        void setMin(Integer min);
        
        Integer getMax();
        void setMax(Integer max);
    }

    public record MyGamesStatistics (BasicStatistics victories, BasicStatistics defeats, List<PowerMostUsed> powersMostUsed, Integer maxStreak) {

        public static MyGamesStatistics of(BasicStatistics victories, BasicStatistics defeats, List<PowerMostUsed> powersMostUsed, Integer maxStreak) {
            return new MyGamesStatistics(victories, defeats, powersMostUsed, maxStreak);
        }
    }

    public interface PowerMostUsed {
    
        PowerType getPowerType();
        void setPowetType(PowerType powerType);

        Integer getTimesUsed();
        void setTimesUsed(Integer timesUsed);
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

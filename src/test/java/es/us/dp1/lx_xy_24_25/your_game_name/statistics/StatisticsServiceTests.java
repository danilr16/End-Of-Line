package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.BasicStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.DurationGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.MyGamesStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.NumGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.NumPlayers;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.Points;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.PowerMostUsed;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.Ranking;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.Victories;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@SpringBootTest
@AutoConfigureTestDatabase
public class StatisticsServiceTests {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = userService.findUser(4);
    }

    @Test
    void shouldReturnGamesStatistics() {
        NumGames numGames = statisticsService.findNumGames(user);
        BasicStatistics global = numGames.global();
        BasicStatistics currentUser = numGames.user();
        assertTrue(global.getTotal() >= 1);
        assertTrue(global.getMax() >= 7 && global.getMin() <= 7);
        assertTrue(currentUser.getTotal() >= 1);
        assertTrue(currentUser.getMax() >= 7 && currentUser.getMin() <= 7);
    }

    @Test
    void shouldReturnMyGamesStatistics() {
        MyGamesStatistics res = statisticsService.findMyGamesStatistics(user);
        BasicStatistics victories = res.victories();
        BasicStatistics defeats = res.defeats();
        List<PowerMostUsed> powers = res.powersMostUsed();
        assertTrue(victories.getTotal() >= 1);
        assertTrue(defeats.getTotal() >= 0);
        assertNull(victories.getMax());
        assertNull(victories.getMin());
        assertNull(defeats.getMax());
        assertNull(defeats.getMin());
        assertTrue(res.maxStreak() >= 3);
        assertTrue(powers.size() >= 1);
        assertTrue(powers.stream().findFirst().get().getPowerType().equals(PowerType.ACCELERATE));
    }

    @Test
    void shouldReturnPlayersStatistics() {
        NumPlayers res = statisticsService.findNumPlayers(user);
        BasicStatistics global = res.global();
        BasicStatistics currentUser = res.user();
        GameMode globalMode = res.globalMostPlayed();
        GameMode currentUserMode = res.userMostPlayed();
        assertTrue(global.getTotal() >= 1);
        assertTrue(global.getMax() >= 1 && global.getMin() <= 1);
        assertTrue(currentUser.getTotal() >= 1);
        assertTrue(currentUser.getMax() >= 1 && currentUser.getMin() <= 1);
        assertTrue(globalMode.equals(GameMode.PUZZLE_SINGLE));
        assertTrue(currentUserMode.equals(GameMode.PUZZLE_SINGLE));
    }

    @Test
    void shouldReturnRankingStatistics() {
        Ranking res = statisticsService.findRanking();
        List<Victories> victories = res.victories();
        List<Points> points = res.points();
        assertFalse(victories.isEmpty());
        assertTrue(victories.stream().map(v -> v.getUserName()).toList().contains("player1"));
        assertTrue(points.stream().map(p -> p.getUserName()).toList().contains("player1"));
    }

    @Test
    void shouldReturnTimeStatistics() {
        DurationGames res = statisticsService.findTime(user);
        BasicStatistics global = res.global();
        BasicStatistics currentUser = res.user();
        assertTrue(global.getTotal() >= 5);
        assertTrue(global.getMax() >= 5 && global.getMin() <= 5);
        assertTrue(currentUser.getTotal() >= 5);
        assertTrue(currentUser.getMax() >= 5 && currentUser.getMin() <= 5);
    }
}

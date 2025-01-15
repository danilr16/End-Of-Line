package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameRepository;
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
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;

@Service
public class StatisticsService {

    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Autowired
    public StatisticsService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }
    
    @Transactional(readOnly = true)
    private BasicStatistics findGlobalNumGames() {
        return gameRepository.findStatisticsOfGlobalNumGames();
    }

    @Transactional(readOnly = true)
    private BasicStatistics findUserNumGames(User user) {
        return userRepository.findStatisticsOfUserNumGames(user);
    }

    @Transactional(readOnly = true)
    public NumGames findNumGames(User currentUser) {
        BasicStatistics global = this.findGlobalNumGames();
        BasicStatistics user = this.findUserNumGames(currentUser);
        return NumGames.of(global, user);
    }

    @Transactional(readOnly = true)
    private BasicStatistics findGlobalDuration() {
        return gameRepository.findStatisticsOfGlobalDuration();
    }

    @Transactional(readOnly = true)
    private BasicStatistics findUserDuration(User user) {
        return userRepository.findStatisticsOfUserDuration(user);
    }

    @Transactional(readOnly = true)
    public DurationGames findTime(User currentUser) {
        BasicStatistics global = this.findGlobalDuration();
        BasicStatistics user = this.findUserDuration(currentUser);
        return DurationGames.of(global, user);
    }

    @Transactional(readOnly = true)
    private BasicStatistics findGlobalNumPlayers() {
        return gameRepository.findStatisticsOfGlobalNumPlayers();
    }

    @Transactional(readOnly = true)
    private BasicStatistics findUserNumPlayers(User user) {
        return userRepository.findStatisticsOfUserNumPlayers(user);
    }

    @Transactional(readOnly = true)
    private GameMode findMostPlayedGameMode() {
        List<GameMode> gameModes = this.gameRepository.findMostPlayedGameMode();
        return gameModes.stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    private GameMode findMostPlayedGameModeByUser(User user) {
        List<GameMode> gameModes = this.userRepository.findMostPlayedGameModeByUser(user);
        return gameModes.stream().findFirst().orElse(null);
    }

    @Transactional(readOnly = true)
    public NumPlayers findNumPlayers(User currentUser) {
        BasicStatistics global = this.findGlobalNumPlayers();
        BasicStatistics user = this.findUserNumPlayers(currentUser);
        GameMode gameModeGlobal = this.findMostPlayedGameMode();
        GameMode gameModeUser = this.findMostPlayedGameModeByUser(currentUser);
        return NumPlayers.of(global, user, gameModeGlobal, gameModeUser);
    }

    @Transactional(readOnly = true)
    private List<Victories> findRankingVictories() {
        return gameRepository.findRankingVictories().stream().limit(10).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private List<Points> findRankingPoints() {
        return gameRepository.findRankingPoints().stream().limit(10).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Ranking findRanking() {
        List<Victories> victories = this.findRankingVictories();
        List<Points> points = this.findRankingPoints();
        return Ranking.of(victories, points);
    }

    @Transactional(readOnly = true)
    private List<PowerMostUsed> findPowersMostUsed(User user) {
        return userRepository.findMostPlayedPowerType(user).stream().filter(p -> p.getPowerType() != null).limit(2).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    private BasicStatistics findUserVictories(User user) {
        return userRepository.findUserVictories(user);
    }

    @Transactional(readOnly = true)
    private BasicStatistics findUserDefeats(User user) {
        return userRepository.findUserDefeats(user);
    }

    @Transactional(readOnly = true)
    public MyGamesStatistics findMyGamesStatistics(User user) {
        List<PowerMostUsed> powersMostUsed = this.findPowersMostUsed(user);
        BasicStatistics victories = this.findUserVictories(user);
        BasicStatistics defeats = this.findUserDefeats(user);
        return MyGamesStatistics.of(victories, defeats, powersMostUsed, user.getMaxStreak());
    }

}

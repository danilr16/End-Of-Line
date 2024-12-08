package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.BasicStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.Points;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.Victories;

import java.util.Optional;
import java.util.List;

public interface GameRepository extends CrudRepository<Game,Integer>{

    Optional<Game> findById(Integer id);
    
    List<Game> findByGameStateIn(List<GameState> gameStates);

    @Query("SELECT g FROM Game g WHERE g.gameCode = :gameCode")
    Optional<Game> findGameByGameCode(String gameCode);

    @Query("SELECT g.chat FROM Game g WHERE g.gameCode = ?1")
    Optional<List<ChatMessage>> findGameChat( String gameCode);

    @Query("SELECT count(g) AS total, avg(p.score) AS average, min(p.score) AS min, max(p.score) AS max " + 
    "FROM Game g INNER JOIN g.players p WHERE (g.gameState = IN_PROCESS OR g.gameState = END) AND (g.gameMode = PUZZLE_SINGLE OR g.gameMode = PUZZLE_COOP)")
    BasicStatistics findStatisticsOfGlobalNumGames();

    @Query("SELECT sum(g.duration) AS total, avg(g.duration) AS average, min(g.duration) AS min, max(g.duration) AS max " + 
    "FROM Game g WHERE (g.gameState = IN_PROCESS OR g.gameState = END)")
    BasicStatistics findStatisticsOfGlobalDuration();

    @Query("SELECT sum(size(g.players)) AS total, avg(size(g.players)) AS average, min(size(g.players)) AS min, max(size(g.players)) AS max " + 
    "FROM Game g WHERE (g.gameState = IN_PROCESS OR g.gameState = END)")
    BasicStatistics findStatisticsOfGlobalNumPlayers();

    @Query("SELECT g.gameMode FROM Game g WHERE (g.gameState = IN_PROCESS OR g.gameState = END) GROUP BY g.gameMode ORDER BY count(g) DESC")
    List<GameMode> findMostPlayedGameMode();

    @Query("SELECT p.user.username AS userName, count(p) AS victories FROM Game g INNER JOIN g.players p WHERE p.state = WON AND (g.gameState = IN_PROCESS OR g.gameState = END) " + 
    "GROUP BY p.user.username ORDER BY count(p) DESC")
    List<Victories> findRankingVictories();

    @Query("SELECT p.user.username AS userName, sum(p.score) AS points FROM Game g INNER JOIN g.players p WHERE p.state = WON AND (g.gameState = IN_PROCESS OR g.gameState = END) " +
    "AND (g.gameMode = PUZZLE_SINGLE OR g.gameMode = PUZZLE_COOP) " + 
    "GROUP BY p.user.username ORDER BY sum(p.score) DESC")
    List<Points> findRankingPoints();
}

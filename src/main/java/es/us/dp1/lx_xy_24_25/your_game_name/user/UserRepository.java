package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.BasicStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.PowerMostUsed;

public interface UserRepository extends CrudRepository<User, Integer>{			


	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findById(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);
	
	//@Query("DELETE FROM Player o WHERE o.user.id = :userId")
	//@Modifying
	//void deletePlayerRelation(int userId);
	
	@Query("SELECT p FROM Player p WHERE p.user = :user")
	Iterable<Player> findAllPlayerByUser(User user);

	@Query("SELECT g FROM Game g WHERE g.host = :user")
	Iterable<Game> findAllGamesHostingByUser(User user);

	@Query("SELECT g FROM Game g INNER JOIN g.players p WHERE p.user = :user")
	Iterable<Game> findAllGamesByUser(User user);

	@Query("SELECT count(DISTINCT g) AS total, avg(CASE WHEN g.gameMode = PUZZLE_SINGLE OR g.gameMode = PUZZLE_COOP THEN p.score END) AS average, " + 
	"min(CASE WHEN g.gameMode = PUZZLE_SINGLE OR g.gameMode = PUZZLE_COOP THEN p.score END) AS min, max(CASE WHEN g.gameMode = PUZZLE_SINGLE OR " + 
	"g.gameMode = PUZZLE_COOP THEN p.score END) AS max " + 
	"FROM Game g INNER JOIN g.players p WHERE (g.gameState = IN_PROCESS OR g.gameState = END) "+ 
	"AND (p.user = :user)")
    BasicStatistics findStatisticsOfUserNumGames(User user);

	@Query("SELECT sum(g.duration) AS total, avg(g.duration) AS average, min(g.duration) AS min, max(g.duration) AS max " + 
	"FROM Game g INNER JOIN g.players gp WHERE (g.gameState = IN_PROCESS OR g.gameState = END) "+ 
	"AND (gp.user = :user)")
    BasicStatistics findStatisticsOfUserDuration(User user);
	@Query("SELECT sum(size(g.players)) AS total, avg(size(g.players)) AS average, min(size(g.players)) AS min, max(size(g.players)) AS max " + 
    "FROM Game g INNER JOIN g.players gp WHERE (g.gameState = IN_PROCESS OR g.gameState = END) " + 
	"AND (gp.user = :user)")
    BasicStatistics findStatisticsOfUserNumPlayers(User user);

	@Query("SELECT g.gameMode FROM Game g INNER JOIN g.players gp WHERE gp.user = :user AND (g.gameState = IN_PROCESS OR g.gameState = END) " + 
	"GROUP BY g.gameMode ORDER BY count(g) DESC")
    List<GameMode> findMostPlayedGameModeByUser(User user);

	@Query("SELECT p.usedPowers AS powerType, count(p.usedPowers) AS timesUsed FROM Game g INNER JOIN g.players p "+ 
	"WHERE p.user = :user AND g.gameState = END AND p.usedPowers IS NOT NULL GROUP BY p.usedPowers ORDER BY count(p.usedPowers) DESC")
	List<PowerMostUsed> findMostPlayedPowerType(User user);

	@Query("SELECT count(CASE WHEN p.state = WON THEN 1 END) AS total, avg(CASE WHEN p.state = WON THEN 1 ELSE 0 END) AS average " + 
	"FROM Game g INNER JOIN g.players p WHERE p.user = :user AND g.gameState = END")
	BasicStatistics findUserVictories(User user);

	@Query("SELECT count(CASE WHEN p.state = LOST THEN 1 END) AS total, avg(CASE WHEN p.state = LOST THEN 1 ELSE 0 END) AS average FROM Game g " + 
	"INNER JOIN g.players p WHERE p.user = :user AND g.gameState = END")
	BasicStatistics findUserDefeats(User user);
	
}

package es.us.dp1.lx_xy_24_25.your_game_name.player;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;

public interface PlayerRepository extends CrudRepository<Player, Integer>{

    @Query("SELECT g FROM Game g, IN (g.players) AS p WHERE p = :player")
    Iterable<Game> findAllGameByPlayer(Player player);
    
}

package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.List;

public interface GameRepository extends CrudRepository<Game,Integer>{

    Optional<Game> findById(Integer id);
    
    List<Game> findByGameStateIn(List<GameState> gameStates);

    @Query("SELECT g FROM Game g WHERE g.gameCode = :gameCode")
    Optional<Game> findGameByGameCode(String gameCode);
}

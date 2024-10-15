package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface GameRepository extends CrudRepository<Game,Integer>{

    Optional<Game> findById(Integer id); 

}

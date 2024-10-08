package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import java.util.List;

public interface GameRepository extends CrudRepository<Game,Integer>{

    Optional<Game> findById(Integer id); 

    @Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<Game> findAllByAuthority(String auth);
    //List<Game> findByUser(User user);



    



}

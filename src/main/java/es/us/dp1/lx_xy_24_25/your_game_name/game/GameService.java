package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;



@Service
public class GameService {

    private GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository){
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true)
    public Iterable<Game> findAllForAdmin() {
        return gameRepository.findAll();

    }

    @Transactional
    public Game saveGame(Game game) throws DataAccessException {
        gameRepository.save(game);
        return game;
    }

    /* 
    @Transactional(readOnly = true)
    public Iterable<Game> findByUser(User user){
        return gameRepository.findByUser(user);
    }
    */

    @Transactional(readOnly = true)
    public Game findGame(Integer id){
        return gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game","id",id));
    }

}

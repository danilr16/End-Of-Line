package es.us.dp1.lx_xy_24_25.your_game_name.player;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.validation.Valid;

@Service
public class PlayerService {

    private PlayerRepository playerRepository;	

	@Autowired
	public PlayerService(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;

	}

    @Transactional
	public Player savePlayer(Player player) throws DataAccessException {
		playerRepository.save(player);
		return player;
	}

    @Transactional(readOnly = true)
    public Iterable<Game> findAllGameByPlayer(Player player) {
        return playerRepository.findAllGameByPlayer(player);
    }

    @Transactional(readOnly = true)
	public Player findPlayer(Integer id) {
		return playerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
	}

	@Transactional
	public Player saveUserPlayerbyUser(User u,Hand h) {
		Player p = new Player();
		p.setUser(u);
		p.setHand(h);
		playerRepository.save(p);
		return p;
	}

	@Transactional
	public Player updatePlayer(@Valid Player player, Integer idToUpdate) {
		Player toUpdate = findPlayer(idToUpdate);
		BeanUtils.copyProperties(player, toUpdate, "id");
		playerRepository.save(toUpdate);
		return toUpdate;
	}
	@Transactional
	public void deletePlayer(@Valid Player player){
		playerRepository.delete(player);
	}
}

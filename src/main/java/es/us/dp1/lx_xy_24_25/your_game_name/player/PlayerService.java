package es.us.dp1.lx_xy_24_25.your_game_name.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
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
	public Player findPlayer(Integer id) {
		return playerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Player", "id", id));
	}

	@Transactional
	public Iterable<Player> findAll() {
		return this.playerRepository.findAll();
	}

	@Transactional
	public Player saveUserPlayerbyUser(User u,Hand h) {
		Player p = new Player();
		p.setUser(u);
		p.setHand(h);
		this.savePlayer(p);
		return p;
	}

	@Transactional
	public Player updatePlayer(@Valid Player player) {
		playerRepository.save(player);
		return player;
	}
	@Transactional
	public void deletePlayer(@Valid Player player){
		playerRepository.delete(player);
	}
}

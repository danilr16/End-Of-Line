/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;

@Service
public class UserService {

	private UserRepository userRepository;	
	private PlayerService playerService;
	private GameService gameService;

	@Autowired
	public UserService(UserRepository userRepository, PlayerService playerService, GameService gameService) {
		this.userRepository = userRepository;
		this.playerService = playerService;
		this.gameService = gameService;
	}

	@Transactional
	public User saveUser(User user) throws DataAccessException {
		userRepository.save(user);
		return user;
	}

	@Transactional(readOnly = true)
	public User findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}	

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
	}

	public Boolean existsUser(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	public Iterable<User> findAllByAuthority(String auth) {
		return userRepository.findAllByAuthority(auth);
	}

	@Transactional(readOnly = true)
	public Iterable<Player> findAllPlayerByUser(User user) {
		return userRepository.findAllPlayerByUser(user);
	}

	@Transactional
	public User updateUser(@Valid User user, Integer idToUpdate) {
		User toUpdate = findUser(idToUpdate);
		BeanUtils.copyProperties(user, toUpdate, "id");
		userRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public List<Game> findAllGamesByUserHost(User user) {
		List<Game> games = (List<Game>) userRepository.findAllGamesHostingByUser(user);
		return games;
	}

	@Transactional
	public void deleteUser(Integer id) {
		User toDelete = findUser(id);
		List<Game> games = this.findAllGamesByUserHost(toDelete);
		for (Game game: games) {
			this.gameService.deleteGame(game.getId());
		}
		List<Player> players = (List<Player>) findAllPlayerByUser(toDelete);
		for (Player player: players) {
			this.playerService.deletePlayer(player);
		}
		List<User> friends = toDelete.getFriends();
		for (User friend: friends) {
			friend.getFriends().remove(toDelete);
			this.updateUser(friend, friend.getId());
		}
		toDelete.setFriends(new ArrayList<>());
		this.updateUser(toDelete, id);
		this.userRepository.delete(toDelete);
	}
	

}

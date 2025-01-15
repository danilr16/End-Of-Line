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
import java.util.stream.Collectors;

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
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.Notification;
import es.us.dp1.lx_xy_24_25.your_game_name.notification.NotificationService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Cell;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.CellService;

@Service
public class UserService {

	private UserRepository userRepository;	
	private GameService gameService;
	private NotificationService notificationService;
	private CellService cellService;
	private PlayerService playerService;

	@Autowired
	public UserService(UserRepository userRepository, GameService gameService, 
	NotificationService notificationService, CellService cellService, PlayerService playerService) {
		this.userRepository = userRepository;
		this.gameService = gameService;
		this.notificationService = notificationService;
		this.cellService = cellService;
		this.playerService = playerService;
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
	public List<Game> findAllGamesWithUser(User user) {
		List<Game> games = (List<Game>) userRepository.findAllGamesByUser(user);
		return games;
	}

	@Transactional
	public void deleteUser(Integer id) {
		User toDelete = findUser(id);
		List<Game> games = this.findAllGamesByUserHost(toDelete);
		List<Cell> cells = this.findAllGamesWithUser(toDelete).stream().filter(g -> !g.getGameState().equals(GameState.WAITING))//Antes de eliminar todas las cartas hay que quitar la referencia en las celdas
			.map(g -> g.getTable().getRows()).flatMap(List::stream).map(r -> r.getCells()).flatMap(List::stream)//Para evitar problemas de consistencia
			.filter(c -> c.getCard() != null).filter(c -> c.getCard().getPlayer().getUser().equals(toDelete))
			.collect(Collectors.toList());
		for (Cell cell: cells) {
			cell.setCard(null);
			cellService.updateCell(cell, cell.getId());
		}
		for (Game game: games) {//Se eliminan los juegos hosteados por el usuario
			this.gameService.deleteGame(game.getId());
		}
		this.findAllGamesWithUser(toDelete).stream().map(g -> g.getPlayers()).flatMap(List::stream)//Se eliminan los jugadores con referencia al usuario 
			.filter(p -> p.getUser().equals(toDelete)).forEach(p -> playerService.deletePlayer(p));//del resto de partidas
		
		this.findAllGamesWithUser(toDelete).stream().map(g -> g.getSpectators()).flatMap(List::stream)//Se eliminan los espectadores con referencia al usuario
			.filter(p -> p.getUser().equals(toDelete)).forEach(p -> playerService.deletePlayer(p));//del resto de partidas
		
			List<User> friends = toDelete.getFriends();
		for (User friend: friends) {//Eliminar referencia del usuario en la lista de amigos de los demas usuarios
			friend.getFriends().remove(toDelete);
			this.updateUser(friend, friend.getId());
		}
		List<Notification> notifications = notificationService.findAllByUser(toDelete).get();
		for (Notification notification: notifications) {//Eliminar notificaciones con referencia al usuario
			this.notificationService.deleteNotification(notification.getId());
		}
		toDelete.setFriends(new ArrayList<>());
		this.updateUser(toDelete, id);
		this.userRepository.delete(toDelete);
	}
}

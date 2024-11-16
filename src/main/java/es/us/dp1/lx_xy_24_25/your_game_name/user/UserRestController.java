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

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.UserProfileUpdateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "The User API based on JWT")
class UserRestController {

	private final UserService userService;
	private final AuthoritiesService authService;
	private final PlayerService playerService;
	private final PasswordEncoder encoder;

	@Autowired
	public UserRestController(UserService userService, AuthoritiesService authService, PlayerService playerService,
			PasswordEncoder encoder) {
		this.userService = userService;
		this.authService = authService;
		this.playerService = playerService;
		this.encoder = encoder;
	}

	@GetMapping
	public ResponseEntity<List<User>> findAll(@RequestParam(required = false) String auth) {
		List<User> res;
		if (auth != null) {
			res = (List<User>) userService.findAllByAuthority(auth);
		} else
			res = (List<User>) userService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("authorities")
	public ResponseEntity<List<Authorities>> findAllAuths() {
		List<Authorities> res = (List<Authorities>) authService.findAll();
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping(value = "{id}")
	public ResponseEntity<User> findById(@PathVariable("id") Integer id) {
		return new ResponseEntity<>(userService.findUser(id), HttpStatus.OK);
	}

	@GetMapping("/games")
	public ResponseEntity<List<Game>> findAllGames() {
		User user = userService.findCurrentUser();
		List<Player> players = (List<Player>) userService.findAllPlayerByUser(user);
		List<Game> games = players.stream().map(p -> (List<Game>) playerService.findAllGameByPlayer(p))
				.flatMap(List::stream).collect(Collectors.toList());
		return new ResponseEntity<>(games, HttpStatus.OK);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<User> create(@RequestBody @Valid User user) {
		User savedUser = userService.saveUser(user);
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

	@PutMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<User> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		return new ResponseEntity<>(this.userService.updateUser(user, id), HttpStatus.OK);
	}

	@DeleteMapping(value = "{userId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
		RestPreconditions.checkNotNull(userService.findUser(id), "User", "ID", id);
		if (userService.findCurrentUser().getId() != id) {
			userService.deleteUser(id);
			return new ResponseEntity<>(new MessageResponse("User deleted!"), HttpStatus.OK);
		} else
			throw new AccessDeniedException("You can't delete yourself!");
	}

	@PatchMapping("/myProfile")
	public ResponseEntity<User> updateMyProfile(@RequestBody UserProfileUpdateDTO userUpdateDTO) {
		User toUpdate = userService.findCurrentUser();
		int id = toUpdate.getId();
		String newUsername = userUpdateDTO.getNewUsername();
		String newImage = userUpdateDTO.getNewImage();

		if (newUsername != null && !newUsername.strip().isEmpty()) {
			if (!userService.existsUser(newUsername)) {
				toUpdate.setUsername(newUsername);
			} else {
				throw new AccessDeniedException("There already is a user with that username!");
			}
		}
		if (newImage != null && !newImage.strip().isEmpty()) {
			toUpdate.setImage(newImage);
		}
		userService.updateUser(toUpdate, id);
		return new ResponseEntity<>(toUpdate, HttpStatus.OK);

	}

	@GetMapping("/currentUser")
	public ResponseEntity<User> findCurrentUser() {
		User user = userService.findCurrentUser();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
  @PatchMapping("/myProfile/update-password")
     public ResponseEntity<User> updateMyPassword(@RequestBody UserProfileUpdateDTO userUpdateDTO) { {
         User toUpdate = userService.findCurrentUser();
		 if (toUpdate == null) {
			throw new AccessDeniedException("User not authenticated");
		}
         int id = toUpdate.getId();
		 String oldPassword = userUpdateDTO.getOldPasswordDTO();
		 String newPassword = userUpdateDTO.getNewPasswordDTO();
		 //Comprobamos que la antigua contraseña sea correcta
         if (oldPassword != null && !oldPassword.strip().isEmpty()) {
			 if (!encoder.matches(oldPassword, toUpdate.getPassword())) {
                 throw new AccessDeniedException("The old password is incorrect!");
             }
         }
		 //Comprobamos que la nueva contraseña sea diferente a la antigua
		 if (!newPassword.equals(oldPassword)) {
			//Comprobamos que la nueva contraseña sea válida
			if(newPassword!= null &&!newPassword.strip().isEmpty()){
				String encodePassword = encoder.encode(newPassword);
				toUpdate.setPassword(encodePassword);
			}else{
				throw new AccessDeniedException("The new password is not valid");
			}
             
         } else {
             throw new AccessDeniedException("The new password can't be the same as the old password!");
         }
         userService.updateUser(toUpdate, id);
         return new ResponseEntity<>(toUpdate, HttpStatus.OK);

     }
	}
	 

}

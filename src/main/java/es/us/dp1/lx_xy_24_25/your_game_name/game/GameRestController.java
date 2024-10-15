package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties.Servlet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import es.us.dp1.lx_xy_24_25.your_game_name.user.AuthoritiesService;
@RestController
@RequestMapping("api/v1/games")
@SecurityRequirement(name = "bearerAuth")
class GameRestController {

    private final GameService gameService;
    private final AuthoritiesService authService; 
    
    

    @Autowired
    public GameRestController(GameService gameService , AuthoritiesService authService){
        this.gameService = gameService;
        this.authService = authService;
    }


    @GetMapping
    public ResponseEntity<List<Game>> findAll(@RequestParam(required = false) String auth){
        List<Game>  res = (List<Game>) gameService.findAll();
        
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> create(@RequestBody @Valid Game game){
        Game savedGame = gameService.saveGame(game);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                    .path("/{id}")
                                    .buildAndExpand(savedGame.getId())
                                    .toUri();
        return new ResponseEntity<>(savedGame,HttpStatus.CREATED);
    }





    


    



}

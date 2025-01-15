package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/achievements")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Achievement", description = "The Achievement API based on JWT")
public class AchievementRestController {

    private final AchievementService service;
	private final UserService userService;

    @Autowired
	public AchievementRestController(AchievementService achievementService,UserService userService) {
		this.service = achievementService;
		this.userService = userService;
	}
    
    @GetMapping
	public ResponseEntity<List<Achievement>> findAll() {
		List<Achievement> res = (List<Achievement>) service.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Achievement> create(@RequestBody @Valid Achievement achievement){
        Achievement savedAchievement = service.saveAchievement(achievement);
        return new ResponseEntity<>(savedAchievement,HttpStatus.CREATED);
    }
	

    @PutMapping(value = "{achievementId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Achievement> update(@PathVariable("achievementId") Integer id, @RequestBody @Valid Achievement achievement) {
		RestPreconditions.checkNotNull(service.findAchievement(id), "Achievement", "ID", id);
		return new ResponseEntity<>(this.service.updateAchievement(achievement, id), HttpStatus.OK);
	}

    @DeleteMapping(value = "{achievementId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("achievementId") int id) {
		RestPreconditions.checkNotNull(service.findAchievement(id), "Achievement", "ID", id);
		service.deleteAchievement(id);
		return new ResponseEntity<>(new MessageResponse("Achievement deleted!"), HttpStatus.OK);
	}

	@GetMapping("/myAchievement")
	public ResponseEntity<List<Achievement>> getAchievementsByUserId() {
		int userId = userService.findCurrentUser().getId();
		List<Achievement> achievements = service.findAchievementByUserId(userId);
		return new ResponseEntity<>(achievements, HttpStatus.OK);
	}

 	@GetMapping("/check") 
	 public ResponseEntity<List<String>> checkAchievements() {
		User user = userService.findCurrentUser();
        List<String> achievedNames = service.checkAchievement(user);
        if (!achievedNames.isEmpty()) {
            return ResponseEntity.ok(achievedNames); 
        }
        return ResponseEntity.noContent().build(); 
    }

	
}

package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@Service
public class AchievementService {

    private AchievementRepository repository;
    private UserService userService;

    @Autowired
    public AchievementService(AchievementRepository repository, UserService userService){
        this.repository = repository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Iterable<Achievement> findAll() {
        return repository.findAll();
    }

    @Transactional
    //No puede existir dos achievement con el mismo nombre/descripcion
    public Achievement saveAchievement(Achievement achievement) throws DataAccessException {
        repository.save(achievement);
        return achievement;
    }

    @Transactional(readOnly = true)
	public Achievement findAchievement(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Achievement", "id", id));
	}	

    @Transactional
    public Achievement updateAchievement(@Valid Achievement achievement, Integer idToUpdate) {
        Achievement toUpdate = findAchievement(idToUpdate);

        //No puede existir dos achievement con el mismo nombre/descripcion
        List<Achievement> existing = repository.findByNameOrDescription(achievement.getName(), achievement.getDescription());
        existing.removeIf(a -> a.getId().equals(idToUpdate));
    
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("There is already an achievement with the same name or description.");
        }
    
        toUpdate.setName(achievement.getName());
        toUpdate.setDescription(achievement.getDescription());
        
        repository.save(toUpdate);
        return toUpdate;
    }
    
    

    @Transactional
	public void deleteAchievement(Integer id) {
		Achievement toDelete = this.findAchievement(id);
        List<User> users = ((List<User>)this.userService.findAll()).stream().filter(u -> u.getAchievements().contains(toDelete))
            .collect(Collectors.toList());
        for (User user:users) {
            user.getAchievements().remove(toDelete);
            this.userService.updateUser(user, user.getId());
        }
		this.repository.delete(toDelete);
	}

    @Transactional(readOnly = true)
    public List<Achievement> findAchievementByUserId(Integer userId) {
    User user = this.userService.findUser(userId);
    return user.getAchievements();
}



    
}

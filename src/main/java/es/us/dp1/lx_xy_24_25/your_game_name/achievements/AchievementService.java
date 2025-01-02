package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.achievements.Achievement.Metric;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.BasicStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.validation.Valid;

@Service
public class AchievementService {

    private AchievementRepository repository;
    private UserService userService;
    private UserRepository  userRepository;

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
    
    @Transactional
    public void checkAchievement(User user) {
        BasicStatistics gamesPlayedStats = userRepository.findStatisticsOfUserNumGames(user);
        BasicStatistics victoriesStats = userRepository.findUserVictories(user);
        BasicStatistics defeatsStats = userRepository.findUserDefeats(user);
    
        List<Achievement> unachievedGamesPlayed = repository.findUnachievedAchievements("GAMES_PLAYED", gamesPlayedStats.getTotal());
        List<Achievement> unachievedVictories = repository.findUnachievedAchievements("VICTORIES", victoriesStats.getTotal());
        List<Achievement> unachievedDefeats = repository.findUnachievedAchievements("DEFEATS", defeatsStats.getTotal());
    
        for (Achievement achievement : unachievedGamesPlayed) {
            if (gamesPlayedStats.getTotal() >= achievement.getThreshold()) {
                markAchievement(user, achievement);
            }
        }
        for (Achievement achievement : unachievedVictories) {
            if (victoriesStats.getTotal() >= achievement.getThreshold()) {
                markAchievement(user, achievement);
            }
        }
        for (Achievement achievement : unachievedDefeats) {
            if (defeatsStats.getTotal() >= achievement.getThreshold()) {
                markAchievement(user, achievement);
            }
        }
    }
    
    private void markAchievement(User user, Achievement achievement) {
        if (!user.getAchievements().contains(achievement)) {
            user.getAchievements().add(achievement);
            this.userService.updateUser(user, user.getId());
        }
    }

    

 

    
}

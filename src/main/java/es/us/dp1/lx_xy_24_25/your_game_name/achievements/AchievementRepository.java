package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AchievementRepository extends CrudRepository<Achievement,Integer>{
    @Query("SELECT a FROM Achievement a WHERE a.name = ?1 OR a.description = ?2")
    List<Achievement> findByNameOrDescription(String name, String description);

    @Query("SELECT a FROM Achievement a WHERE a.metric = ?1 AND a.threshold >= ?2")
    List<Achievement> findUnachievedAchievements(Achievement.Metric metric, Integer userProgress);
    
    
    
    

    
}

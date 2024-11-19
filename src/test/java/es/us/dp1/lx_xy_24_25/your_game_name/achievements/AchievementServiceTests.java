package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
public class AchievementServiceTests {

    @Autowired
    private AchievementService achievementService;

    @Test
    void shouldFindAll() {
        List<Achievement> achievements = (List<Achievement>)achievementService.findAll();
        assertTrue(!achievements.isEmpty());
        assertTrue(achievements.size() >= 3);
    }

    @Test
    void shouldFindById() {
        Achievement achievement = achievementService.findAchievement(1);
        assertEquals("achievement1", achievement.getName());;
    }

    @Test
    void shouldNotFindById() {
        assertThrows(ResourceNotFoundException.class, () -> achievementService.findAchievement(13456));
    }
    
    @Test
    @Transactional
    void shouldFindAllAchievementsFromUser() {
        List<Achievement> achievements = achievementService.findAchievementByUserId(4);
        assertTrue(!achievements.isEmpty());
        assertTrue(achievements.size() >= 2);
    }

    @Test
    void shouldCreateAchievement() {
        int count = ((Collection<Achievement>) this.achievementService.findAll()).size();

		Achievement achievement = new Achievement();
		achievement.setName("prueba");
        achievement.setDescription("fake_description");
        achievement.setThreshold(2);
        achievement.setImage("fake_image");

		this.achievementService.saveAchievement(achievement);
		assertNotEquals(0, achievement.getId().longValue());
		assertNotNull(achievement.getId());

		int finalCount = ((Collection<Achievement>) this.achievementService.findAll()).size();
		assertEquals(count + 1, finalCount);
    }

    @Test
    @Transactional
    void shouldUpdateAchievement() {
        int idToUpdate = 1;
		String newName="Changed";
        Integer newThreshold = 25;
		Achievement achievement = this.achievementService.findAchievement(idToUpdate);
		achievement.setName(newName);
        achievement.setThreshold(newThreshold);
		achievementService.updateAchievement(achievement, idToUpdate);
        achievement = this.achievementService.findAchievement(idToUpdate);
		assertEquals(newName, achievement.getName());
        assertEquals(newThreshold, achievement.getThreshold());
    }

    @Test
	@Transactional
	void shouldDeleteAchivement() {
		int count = ((Collection<Achievement>) this.achievementService.findAll()).size();
		int idToDelete = 1;
		this.achievementService.deleteAchievement(idToDelete);
		int finalCount = ((Collection<Achievement>) this.achievementService.findAll()).size();
		assertEquals(count - 1, finalCount);
		assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findAchievement(idToDelete));
	}
}

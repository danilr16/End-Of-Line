package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
public class AchievementServiceTests {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserService userService;

    @Test
    void shouldFindAll() {
        List<Achievement> achievements = (List<Achievement>)achievementService.findAll();
        assertTrue(!achievements.isEmpty());
        assertTrue(achievements.size() >= 3);
    }

    @Test
    void shouldFindById() {
        Achievement achievement = achievementService.findAchievement(1);
        assertEquals("First", achievement.getName());;
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
        assertTrue(achievements.size() >= 1);
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
    void shouldNotUpdateAchievementDuplicated() {
        int idToUpdate = 1;
		String newName="End of the Beginning";
		Achievement achievement = this.achievementService.findAchievement(idToUpdate);
		achievement.setName(newName);
        assertThrows(IllegalArgumentException.class, () -> achievementService.updateAchievement(achievement, idToUpdate));
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
    List<Card> simCreate25Cards(Player player) { 
        List<Card> cards = new ArrayList<>();
        for(int i=1;i<=3;i++) {
            Card c1 = Card.createByType(TypeCard.TYPE_1, player);
            cards.add(c1);
            Card c2 = Card.createByType(TypeCard.TYPE_2_IZQ, player);
            cards.add(c2);
            Card c3 = Card.createByType(TypeCard.TYPE_2_DER, player);
            cards.add(c3);
            Card c4 = Card.createByType(TypeCard.TYPE_3_IZQ, player);
            cards.add(c4);
            Card c5 = Card.createByType(TypeCard.TYPE_3_DER, player);
            cards.add(c5);
            Card c6 = Card.createByType(TypeCard.TYPE_4, player);
            cards.add(c6);
            Card c7 = Card.createByType(TypeCard.TYPE_5, player);
            cards.add(c7);
            Card c8 = Card.createByType(TypeCard.TYPE_0, player);
            cards.add(c8);
        }
        Card c9 = Card.createByType(TypeCard.TYPE_1, player);
        cards.add(c9);
        return cards;
    }

    @Test
    @Transactional
    void shouldCheckAchievement() {
        User user = userService.findUser(4);
        Integer cont = user.getAchievements().size();
        List<String> achievements = achievementService.checkAchievement(user);
        Integer newCont = user.getAchievements().size();
        assertEquals(cont + 1, newCont);
        assertTrue(achievements.contains("End of the Beginning"));
    }
}

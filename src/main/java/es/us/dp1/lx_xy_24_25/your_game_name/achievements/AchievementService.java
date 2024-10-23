package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class AchievementService {

    private AchievementRepository repository;

    @Autowired
    public AchievementService(AchievementRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Iterable<Achievement> findAll() {
        return repository.findAll();
    }

    @Transactional
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
		BeanUtils.copyProperties(achievement, toUpdate, "id");
		repository.save(toUpdate);
		return toUpdate;
	}

    @Transactional
	public void deleteAchievement(Integer id) {
		Achievement toDelete = findAchievement(id);
		this.repository.delete(toDelete);
	}
    
}

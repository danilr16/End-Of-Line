package es.us.dp1.lx_xy_24_25.your_game_name.hand;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class HandService {

    private HandRepository repository;

    @Autowired
    public HandService(HandRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Iterable<Hand> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Hand saveHand(Hand hand) throws DataAccessException {
        repository.save(hand);
        return hand;
    }

    @Transactional(readOnly = true)
	public Hand findHand(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hand", "id", id));
	}	

    @Transactional
	public Hand updateHand(@Valid Hand hand, Integer idToUpdate) {
		Hand toUpdate = findHand(idToUpdate);
		BeanUtils.copyProperties(hand, toUpdate, "id");
		repository.save(toUpdate);
		return toUpdate;
	}

    @Transactional
	public void deleteHand(Integer id) {
		Hand toDelete = findHand(id);
		this.repository.delete(toDelete);
	}
    
}

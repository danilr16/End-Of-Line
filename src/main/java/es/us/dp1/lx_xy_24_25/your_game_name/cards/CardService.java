package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class CardService {

    private CardRepository repository;

    @Autowired
    public CardService(CardRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Iterable<Card> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Card saveCard(Card card) throws DataAccessException {
        repository.save(card);
        return card;
    }

    @Transactional(readOnly = true)
	public Card findCard(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card", "id", id));
	}	

    @Transactional
	public Card updateCard(@Valid Card card, Integer idToUpdate) {
		Card toUpdate = findCard(idToUpdate);
		BeanUtils.copyProperties(card, toUpdate, "id");
		repository.save(toUpdate);
		return toUpdate;
	}

    @Transactional
	public void deleteCard(Integer id) {
		Card toDelete = findCard(id);
		this.repository.delete(toDelete);
	}
    
}

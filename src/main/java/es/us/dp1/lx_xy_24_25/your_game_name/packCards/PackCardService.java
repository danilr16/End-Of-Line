package es.us.dp1.lx_xy_24_25.your_game_name.packCards;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.validation.Valid;

@Service
public class PackCardService {

    private PackCardRepository repository;
    private CardService cardService;

    @Autowired
    public PackCardService(PackCardRepository repository, CardService cardservice){
        this.repository = repository;
        this.cardService = cardservice;
    }

    @Transactional(readOnly = true)
    public Iterable<PackCard> findAll() {
        return repository.findAll();
    }

    @Transactional
    public PackCard savePackCard(PackCard packCard) throws DataAccessException {
        repository.save(packCard);
        return packCard;
    }

    @Transactional(readOnly = true)
	public PackCard findPackCard(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PackCard", "id", id));
	}	

    @Transactional
	public PackCard updatePackCard(@Valid PackCard packCard, Integer idToUpdate) {
		PackCard toUpdate = findPackCard(idToUpdate);
		BeanUtils.copyProperties(packCard, toUpdate, "id");
		repository.save(toUpdate);
		return toUpdate;
	}

    @Transactional
	public void deletePackCard(Integer id) {
		PackCard toDelete = findPackCard(id);
		this.repository.delete(toDelete);
	}

    @Transactional
    public PackCard creaPackCard(Player player) {
        PackCard packCard = new PackCard();
        List<Card> cards = cardService.create25Cards(player);
        packCard.setPlayer(player);
        packCard.setCards(cards);
        savePackCard(packCard);
        return packCard;
    }
    
}

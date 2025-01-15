package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
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

    public Card getLastPlaced(Player player) {
        return findCard(player.getPlayedCards().get(player.getPlayedCards().size()-1));
    }

    @Transactional
    public  List<Card> create25Cards(Player player) {
        List<Card> cards = new ArrayList<>();
        for(int i=1;i<=3;i++) {
            Card c1 = Card.createByType(TypeCard.TYPE_1, player);
            saveCard(c1);
            cards.add(c1);
            Card c2 = Card.createByType(TypeCard.TYPE_2_IZQ, player);
            saveCard(c2);
            cards.add(c2);
            Card c3 = Card.createByType(TypeCard.TYPE_2_DER, player);
            saveCard(c3);
            cards.add(c3);
            Card c4 = Card.createByType(TypeCard.TYPE_3_IZQ, player);
            saveCard(c4);
            cards.add(c4);
            Card c5 = Card.createByType(TypeCard.TYPE_3_DER, player);
            saveCard(c5);
            cards.add(c5);
            Card c6 = Card.createByType(TypeCard.TYPE_4, player);
            saveCard(c6);
            cards.add(c6);
            Card c7 = Card.createByType(TypeCard.TYPE_5, player);
            saveCard(c7);
            cards.add(c7);
            Card c8 = Card.createByType(TypeCard.TYPE_0, player);
            saveCard(c8);
            cards.add(c8);
        }
        Card c9 = Card.createByType(TypeCard.TYPE_1, player);
        saveCard(c9);
        cards.add(c9);
        return cards;
    }
}

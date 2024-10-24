package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class TableCardService {

    private TableCardRepository repository;

    @Autowired
    public TableCardService(TableCardRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Iterable<TableCard> findAll() {
        return repository.findAll();
    }

    @Transactional
    public TableCard saveTableCard(TableCard tableCard) throws DataAccessException {
        repository.save(tableCard);
        return tableCard;
    }

    @Transactional(readOnly = true)
	public TableCard findTableCard(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TableCard", "id", id));
	}	

    @Transactional
	public TableCard updateTableCard(@Valid TableCard tableCard, Integer idToUpdate) {
		TableCard toUpdate = findTableCard(idToUpdate);
		BeanUtils.copyProperties(tableCard, toUpdate, "id");
		repository.save(toUpdate);
		return toUpdate;
	}

    @Transactional
	public void deleteTableCard(Integer id) {
		TableCard toDelete = findTableCard(id);
		this.repository.delete(toDelete);
	}
    
}

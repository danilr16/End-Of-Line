package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class CellService {
    
    private CellRepository repository;

    @Autowired
    public CellService(CellRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Iterable<Cell> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Cell saveCell(Cell cell) throws DataAccessException {
        cell.setIsFull(cell.getCard() != null);
        repository.save(cell);
        return cell;
    }

    @Transactional(readOnly = true)
	public Cell findCell(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cell", "id", id));
	}	

    @Transactional
    public Cell updateCell(@Valid Cell cell, Integer idToUpdate) {
        Cell toUpdate = findCell(idToUpdate);
        if (toUpdate.getCard() != null && cell.getCard() != null && 
            !toUpdate.getCard().getId().equals(cell.getCard().getId())) {
            throw new IllegalArgumentException("Cannot update cell with a different card.");
        }
        BeanUtils.copyProperties(cell, toUpdate, "id");
        toUpdate.setIsFull(toUpdate.getCard() != null);
    
        repository.save(toUpdate);
        return toUpdate;
    }
    
    
    @Transactional
	public void deleteCell(Integer id) {
		Cell toDelete = findCell(id);
		this.repository.delete(toDelete);
        
	}

}

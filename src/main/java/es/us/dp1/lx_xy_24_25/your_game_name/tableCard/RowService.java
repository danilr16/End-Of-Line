package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
public class RowService {

    private RowRepository repository;

    @Autowired
    public RowService(RowRepository repository){
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Iterable<Row> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Row saveRow(Row row) throws DataAccessException {
        repository.save(row);
        return row;
    }

    @Transactional(readOnly = true)
	public Row findRow(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Row", "id", id));
	}

    @Transactional
	public void deleteRow(Integer id) {
		Row toDelete = findRow(id);
		this.repository.delete(toDelete);
	}
    
}

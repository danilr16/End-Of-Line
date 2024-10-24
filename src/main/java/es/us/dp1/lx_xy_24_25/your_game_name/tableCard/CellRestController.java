package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.util.RestPreconditions;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cells")
@SecurityRequirement(name = "bearerAuth")
public class CellRestController {

    private final CellService service;

    @Autowired
	public CellRestController(CellService cellService) {
		this.service = cellService;
	}
    
    @GetMapping
	public ResponseEntity<List<Cell>> findAll() {
		List<Cell> res = (List<Cell>) service.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Cell> create(@RequestBody @Valid Cell cell){
        Cell savedCell = service.saveCell(cell);
        return new ResponseEntity<>(savedCell,HttpStatus.CREATED);
    }

    @PutMapping(value = "{cellId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Cell> update(@PathVariable("cellId") Integer id, @RequestBody @Valid Cell cell) {
		RestPreconditions.checkNotNull(service.findCell(id), "Cell", "ID", id);
		return new ResponseEntity<>(this.service.updateCell(cell, id), HttpStatus.OK);
	}

    @DeleteMapping(value = "{cellId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("cellId") int id) {
		RestPreconditions.checkNotNull(service.findCell(id), "Cell", "ID", id);
		service.deleteCell(id);
		return new ResponseEntity<>(new MessageResponse("Cell deleted!"), HttpStatus.OK);
	}
    
}

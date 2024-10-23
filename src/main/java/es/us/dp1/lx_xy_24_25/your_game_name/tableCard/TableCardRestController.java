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
@RequestMapping("/api/v1/tableCards")
@SecurityRequirement(name = "bearerAuth")
public class TableCardRestController {

    private final TableCardService service;

    @Autowired
	public TableCardRestController(TableCardService tableCardService) {
		this.service = tableCardService;
	}
    
    @GetMapping
	public ResponseEntity<List<TableCard>> findAll() {
		List<TableCard> res = (List<TableCard>) service.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TableCard> create(@RequestBody @Valid TableCard tableCard){
        TableCard savedTableCard = service.saveTableCard(tableCard);
        return new ResponseEntity<>(savedTableCard,HttpStatus.CREATED);
    }

    @PutMapping(value = "{tableCardId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<TableCard> update(@PathVariable("tableCardId") Integer id, @RequestBody @Valid TableCard tableCard) {
		RestPreconditions.checkNotNull(service.findTableCard(id), "TableCard", "ID", id);
		return new ResponseEntity<>(this.service.updateTableCard(tableCard, id), HttpStatus.OK);
	}

    @DeleteMapping(value = "{tableCardId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("tableCardId") int id) {
		RestPreconditions.checkNotNull(service.findTableCard(id), "TableCard", "ID", id);
		service.deleteTableCard(id);
		return new ResponseEntity<>(new MessageResponse("TableCard deleted!"), HttpStatus.OK);
	}
    
}

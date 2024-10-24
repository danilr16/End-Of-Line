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
@RequestMapping("/api/v1/rows")
@SecurityRequirement(name = "bearerAuth")
public class RowRestController {

    private final RowService service;

    @Autowired
	public RowRestController(RowService rowService) {
		this.service = rowService;
	}
    
    @GetMapping
	public ResponseEntity<List<Row>> findAll() {
		List<Row> res = (List<Row>) service.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Row> create(@RequestBody @Valid Row row){
        Row savedRow = service.saveRow(row);
        return new ResponseEntity<>(savedRow,HttpStatus.CREATED);
    }

    @PutMapping(value = "{rowId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Row> update(@PathVariable("rowId") Integer id, @RequestBody @Valid Row row) {
		RestPreconditions.checkNotNull(service.findRow(id), "Row", "ID", id);
		return new ResponseEntity<>(this.service.updateRow(row, id), HttpStatus.OK);
	}

    @DeleteMapping(value = "{rowId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("rowId") int id) {
		RestPreconditions.checkNotNull(service.findRow(id), "Row", "ID", id);
		service.deleteRow(id);
		return new ResponseEntity<>(new MessageResponse("Row deleted!"), HttpStatus.OK);
	}
    
}

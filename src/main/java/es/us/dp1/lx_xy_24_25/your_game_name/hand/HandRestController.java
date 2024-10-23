package es.us.dp1.lx_xy_24_25.your_game_name.hand;

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
@RequestMapping("/api/v1/hands")
@SecurityRequirement(name = "bearerAuth")
public class HandRestController {

    private final HandService service;

    @Autowired
	public HandRestController(HandService handService) {
		this.service = handService;
	}
    
    @GetMapping
	public ResponseEntity<List<Hand>> findAll() {
		List<Hand> res = (List<Hand>) service.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Hand> create(@RequestBody @Valid Hand hand){
        Hand savedHand = service.saveHand(hand);
        return new ResponseEntity<>(savedHand,HttpStatus.CREATED);
    }

    @PutMapping(value = "{handId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Hand> update(@PathVariable("handId") Integer id, @RequestBody @Valid Hand hand) {
		RestPreconditions.checkNotNull(service.findHand(id), "Hand", "ID", id);
		return new ResponseEntity<>(this.service.updateHand(hand, id), HttpStatus.OK);
	}

    @DeleteMapping(value = "{handId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("handId") int id) {
		RestPreconditions.checkNotNull(service.findHand(id), "Hand", "ID", id);
		service.deleteHand(id);
		return new ResponseEntity<>(new MessageResponse("Achievement deleted!"), HttpStatus.OK);
	}
    
}

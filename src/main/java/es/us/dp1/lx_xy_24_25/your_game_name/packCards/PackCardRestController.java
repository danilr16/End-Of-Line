package es.us.dp1.lx_xy_24_25.your_game_name.packCards;

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
@RequestMapping("/api/v1/packCards")
@SecurityRequirement(name = "bearerAuth")
public class PackCardRestController {

    private final PackCardService service;

    @Autowired
	public PackCardRestController(PackCardService packCardService) {
		this.service = packCardService;
	}
    
    @GetMapping
	public ResponseEntity<List<PackCard>> findAll() {
		List<PackCard> res = (List<PackCard>) service.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
	}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PackCard> create(@RequestBody @Valid PackCard packCard){
        PackCard savedPackCard = service.savePackCard(packCard);
        return new ResponseEntity<>(savedPackCard,HttpStatus.CREATED);
    }

    @PutMapping(value = "{packCardId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PackCard> update(@PathVariable("packCardId") Integer id, @RequestBody @Valid PackCard packCard) {
		RestPreconditions.checkNotNull(service.findPackCard(id), "PackCard", "ID", id);
		return new ResponseEntity<>(this.service.updatePackCard(packCard, id), HttpStatus.OK);
	}

    @DeleteMapping(value = "{packCardId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("packCardId") int id) {
		RestPreconditions.checkNotNull(service.findPackCard(id), "PackCard", "ID", id);
		service.deletePackCard(id);
		return new ResponseEntity<>(new MessageResponse("PackCard deleted!"), HttpStatus.OK);
	}
    
}

package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureTestDatabase
public class CellServiceTests {
    @Autowired
    private CellService cellService;

    @Test
    void shouldFindAll(){
        this.cellService.findAll();
        



    }
    
}

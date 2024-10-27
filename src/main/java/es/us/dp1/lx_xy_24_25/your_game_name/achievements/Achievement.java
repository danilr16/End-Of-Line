package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Achievement extends BaseEntity{

    String name;

    String description;

    String image;

    Integer threshold;

    public enum Metric {
        GAMES_PLAYED,VICTORIES
    }

    @Enumerated(EnumType.STRING)
    Metric metric;
    
}

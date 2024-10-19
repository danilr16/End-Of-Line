package es.us.dp1.lx_xy_24_25.your_game_name.achivements;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="appAchivements")
public class Achivement extends BaseEntity{

    String name;

    String description;

    String image;

    String threshold;

    public enum Metric {
        GAMES_PLAYED,VICTORIES
    }

    Metric metric;
    
}

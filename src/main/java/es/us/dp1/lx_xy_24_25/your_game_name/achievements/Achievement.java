package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Appachievements")
public class Achievement extends BaseEntity{

    public enum Metric {
        GAMES_PLAYED,VICTORIES
    }

    @NotNull
    @NotBlank
    String name;

    String description;

    String image;

    @Min(1)
    Integer threshold;

    @Enumerated(EnumType.STRING)
    Metric metric;
    
}

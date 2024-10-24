package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="appCards")
public class Card extends BaseEntity{

    public enum TypeCard {
        TYPE_1,TYPE_2_IZQ,TYPE_2_DER,TYPE_3_IZQ,TYPE_3_DER,TYPE_4,TYPE_5,TYPE_0,INICIO
    }

    TypeCard type;

    Integer iniciative;

    Integer rotation;

    public static record Output(List<Integer> outputs, List<Integer> inputs) {

        public static Output of(List<Integer> outputs, List<Integer> inputs) {
            return new Output(outputs, inputs);
        }

    }

    @Transient
    Output output;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (rotation == null) {
            rotation = 0;
        }
    }
    
}

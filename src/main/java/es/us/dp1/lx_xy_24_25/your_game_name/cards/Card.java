package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name="appCards")
public class Card extends BaseEntity{

    public enum TypeCard {
        TYPE_1,TYPE_2_IZQ,TYPE_2_DER,TYPE_3_IZQ,TYPE_3_DER,TYPE_4,TYPE_5,TYPE_0,INICIO
    }

    public static record Output(List<Integer> outputs, Integer input) {

        public static Output of(List<Integer> outputs, Integer input) {
            return new Output(outputs, input);
        }

    }

    @NotNull
    @Enumerated(EnumType.STRING)
    TypeCard type;

    @Range(min = 0, max = 5)
    Integer iniciative;

    @Range(min = 0, max = 3)
    @NotNull
    Integer rotation;

    @NotNull
    @ManyToOne(optional = false)
    Player player;

    @Transient
    Output output;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (rotation == null) {
            rotation = 0;
        }
    }

    public static Card createByType(TypeCard type, Player player) {
        Card card = new Card();
        card.setPlayer(player);
        card.setRotation(0);
        card.setType(type);
        if(type.equals(TypeCard.TYPE_1)) {
            card.setIniciative(1);
            List<Integer> outputs = new ArrayList<>(List.of(2));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_2_IZQ)) {
            card.setIniciative(2);
            List<Integer> outputs = new ArrayList<>(List.of(1));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_2_DER)) {
            card.setIniciative(2);
            List<Integer> outputs = new ArrayList<>(List.of(3));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_3_IZQ)) {
            card.setIniciative(3);
            List<Integer> outputs = new ArrayList<>(List.of(1,2));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_3_DER)) {
            card.setIniciative(3);
            List<Integer> outputs = new ArrayList<>(List.of(2,3));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_4)) {
            card.setIniciative(4);
            List<Integer> outputs = new ArrayList<>(List.of(1,3));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_5)) {
            card.setIniciative(5);
            List<Integer> outputs = new ArrayList<>(List.of(1,2,3));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.TYPE_0)) {
            card.setIniciative(0);
            List<Integer> outputs = new ArrayList<>(List.of(1,2,3));
            card.setOutput(Output.of(outputs, 0));
        } else if(type.equals(TypeCard.INICIO)) {
            List<Integer> outputs = new ArrayList<>(List.of(2));
            card.setOutput(Output.of(outputs, null));
        } else {
            card = null;
        }
        return card;
    }
    
}

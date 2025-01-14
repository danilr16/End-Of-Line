package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {
    
    private String userName1;
    private String userName2;

    public TeamDTO() {}

    public TeamDTO(String userName1, String userName2) {
        this.userName1 = userName1;
        this.userName2 = userName2;
    }

    public static TeamDTO convertToDTO(Team team) {
        if (team.getPlayer2() == null) {
            return new TeamDTO(team.getPlayer1().getUser().getUsername(), null);
        } else {
            return new TeamDTO(team.getPlayer1().getUser().getUsername(), team.getPlayer2().getUser().getUsername());
        }
    }
}

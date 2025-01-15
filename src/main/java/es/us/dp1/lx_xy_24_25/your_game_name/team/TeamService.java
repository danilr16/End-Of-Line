package es.us.dp1.lx_xy_24_25.your_game_name.team;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@Service
public class TeamService {

    private TeamRepository repository;

    @Autowired
    public TeamService(TeamRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Team saveTeam(Team team) throws DataAccessException {
        this.repository.save(team);
        return team;
    }

    @Transactional 
    public Team saveTeamByPlayers(Player p1, Player p2) throws DataAccessException {
        Team team = new Team();
        team.setPlayer1(p1);
        team.setPlayer2(p2);
        return this.saveTeam(team);
    }

    @Transactional
    public Team updateTeam(Team team) {
        this.repository.save(team);
        return team;
    }

    @Transactional
    public void deleteTeam(Team team) {
        this.repository.delete(team);
    }
    
    @Transactional
    public Game joinATeam(Game game, Player player) {
        Optional<Team> teamToJoin = game.getTeams().stream().filter(t -> t.getPlayer2() == null).findFirst();
        if (teamToJoin.isPresent()) {
            Team team = teamToJoin.get();
            team.setPlayer2(player);
            this.updateTeam(team);
        } else {
            Team newTeam = this.saveTeamByPlayers(player, null);
            game.getTeams().add(newTeam);
        }
        return game;
    }

    @Transactional
    public void leaveTeam(Game game, Player player) {
        for (Team team: game.getTeams()) {
            if (team.getPlayer1().equals(player)) {
                if (team.getPlayer2() == null) {
                    this.deleteTeam(team);
                } else {
                    team.setPlayer1(team.getPlayer2());
                    team.setPlayer2(null);
                    this.updateTeam(team);
                }
            } else if (team.getPlayer2() != null) {
                if (team.getPlayer2().equals(player)) {
                    team.setPlayer2(null);
                    this.updateTeam(team);
                }
            }
        }
    }
}

package es.us.dp1.lx_xy_24_25.your_game_name.team;

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
        Team lastTeam = game.getTeams().get(game.getTeams().size()-1);
        if (lastTeam.getPlayer2() == null) {
            lastTeam.setPlayer2(player);
            this.updateTeam(lastTeam);
        } else {
            Team newTeam = this.saveTeamByPlayers(player, null);
            game.getTeams().add(newTeam);
        }
        return game;
    }
}

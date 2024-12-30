package es.us.dp1.lx_xy_24_25.your_game_name.team;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTests {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    private Team team;

    private Player player1, player2, player3;

    @BeforeEach
    void setUp() {
        player1 = new Player();
        player1.setId(1);
        player2 = new Player();
        player2.setId(2);
        player3 = new Player();
        player3.setId(3);
        //Creamos dos equipos, uno con dos jugadores y otro con uno
        team = new Team();
        team.setId(1);
        team.setPlayer1(player1);
    }

    @Test
    void shouldSaveATeam() {
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        Team res = teamService.saveTeam(team);
        assertEquals(1, res.getId());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void shouldSaveATeamByPlayers() {
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        Team res = teamService.saveTeamByPlayers(player1, player2);
        assertEquals(player1, res.getPlayer1());
        assertEquals(player2, res.getPlayer2());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void shouldUpdateTeam() {
        team.setId(3);
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        Team res = teamService.updateTeam(team);
        assertEquals(3, res.getId());
        verify(teamRepository).save(any(Team.class));
        team.setId(1);
    }

    @Test
    void shouldDeleteTeam() {
        teamService.deleteTeam(team);
        verify(teamRepository).delete(any(Team.class));
    }

    @Test
    void shouldJoinInATeamWithAnotherPlayer() {
        Game game = new Game();
        game.setId(1);
        game.setTeams(new ArrayList<Team>(List.of(team)));
        Game res = teamService.joinATeam(game, player2);
        assertEquals(1, res.getId());
        assertEquals(1, res.getTeams().size());
        assertEquals(player1, res.getTeams().get(0).getPlayer1());
        assertEquals(player2, res.getTeams().get(0).getPlayer2());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void shouldJoinInATeamWithoutAnotherPlayer() {
        Game game = new Game();
        game.setId(1);
        team.setPlayer2(player2);
        game.setTeams(new ArrayList<Team>(List.of(team)));
        Game res = teamService.joinATeam(game, player3);
        assertEquals(1, res.getId());
        assertEquals(2, res.getTeams().size());
        assertEquals(player1, res.getTeams().get(0).getPlayer1());
        assertEquals(player2, res.getTeams().get(0).getPlayer2());
        assertEquals(player3, res.getTeams().get(1).getPlayer1());
        assertNull(res.getTeams().get(1).getPlayer2());
    }
}

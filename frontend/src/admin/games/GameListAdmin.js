import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup, Table } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function GameListAdmin(){
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [games, setGames] = useFetchState(
        [],
        `/api/v1/games`,
        jwt,
        setMessage,
        setVisible
      );
    
    const [alerts,setAlerts] = useState([]);
    
    const gamesList = games.map((game) => {
      console.log("si")
      console.log(game.gameCode)
        return (
          <tr key={game.id}>
            <td>{game.gameCode}</td>
            
            <td>
              <ButtonGroup>
                <Button
                  size="sm"
                  color="danger"
                  aria-label={"delete-" + game.id}
                  onClick={() =>
                    deleteFromList(
                      `/api/v1/games/${game.id}`,
                      game.id,
                      [games, setGames],
                      [alerts, setAlerts],
                      setMessage,
                      setVisible
                    )
                  }
                >
                  Delete
                </Button>
              </ButtonGroup>
            </td>
          </tr>
        );
      });

    const modal = getErrorModal(setVisible, visible, message);
    return (
        <div className="admin-page-container">
          <h1 className="text-center">Games Ended</h1>
          {alerts.map((a) => a.alert)}
          {modal}
          <Button color="success" tag={Link} to="/users/new">
            Add Game
          </Button>
          <div>
            <Table aria-label="games" className="mt-4">
              <thead>
                <tr>
                  <th>GameCode</th>
                  
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>{gamesList}</tbody>
            </Table>
          </div>
        </div>
      );

}
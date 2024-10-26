import useFetchState from "../util/useFetchState";
import { useDebugValue, useEffect, useState } from "react";
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import jwt_decode from "jwt-decode";



export default function MyGames(){

    const jwt = tokenService.getLocalAccessToken();

    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false);
    const [currentUser,setcurrentUser] = useFetchState(
        null,
        `/api/v1/users/currentUser`,
        jwt,
        setMessage,
        setVisible
    );
    
    const [games, setGames] = useState([]);
    const [loadingGames, setLoadingGames] = useState(true);  // Estado para controlar si las partidas están cargando
    const [gamesError, setGamesError] = useState(null); // Estado para controlar errores en la carga de partidas

    useEffect(() => {
        // Solo hacemos la llamada si currentUser no es null y tiene un id válido
        if (currentUser && currentUser.id) {
            // Función asíncrona para hacer fetch de las partidas
            const fetchGames = async () => {
                setLoadingGames(true); // Establecemos el estado de carga
                try {
                    const response = await fetch(`/api/v1/users/${currentUser.id}/games`, {
                        headers: {
                            Authorization: `Bearer ${jwt}`
                        }
                    });
                    const data = await response.json();
                    setGames(data); // Guardamos las partidas en el estado
                } catch (error) {
                    setGamesError('Error al cargar las partidas.');
                } finally {
                    setLoadingGames(false); // Terminamos el estado de carga
                }
            };

            fetchGames(); // Llamamos a la función para obtener las partidas
        }
    }, [currentUser, jwt]);
    
    if (loadingGames) {
      return <p>Cargando partidas...</p>;
  }
  console.log(games)
  return (
    <div className="myGames-container">
        <h1 className="myGames-title">Mis Partidas</h1>
        {games.length === 0 ? (
            <p className="myGames-title">No hay partidas disponibles.</p>
        ) : (
            <ul className="myGames-table">
                {games.map((item, index) => (
            <div className="myGames-td" key={index}>
                <h4 className="myGames-tr">GameCode: {item.gameCode}</h4>
                <p className="myGames-tr">Host: {item.host.username}</p>
        </div>
      ))}
            </ul>
        )}
    </div>
);

   
}
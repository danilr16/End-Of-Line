import { useEffect, useRef, useState } from "react";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import "../static/css/screens/Stats.css";
import { useNavigate } from "react-router-dom";


export default function Stats() {
    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const canvasRef = useRef(null);
    const navigate = useNavigate();

    const [user, setUser] = useFetchState(
        [],
        '/api/v1/users/currentUser',
        jwt,
        setMessage,
        setVisible
    );

    const drawDiagram = (diagram) => {

        const border = diagram.getContext("2d");
        const centerX = diagram.width / 2;
        const centerY = diagram.height / 2;
        const radius = 100;
        const wins = 80;
        const loses = 35;
    
        // Función para dibujar un segmento
        const drawSegment = (startAngle, endAngle, color) => {
          border.beginPath();
          border.arc(centerX, centerY, radius, startAngle, endAngle);
          border.lineWidth = 10; // Ancho del borde
          border.strokeStyle = color;
          border.stroke();
        };
    
        // Aquí se calcula qué parte del diagrama corresponderá a victorias y cual a derrotas
        const total = wins + loses;
        const startAngle1 = -Math.PI / 2;
        const endAngle1 = startAngle1 + (wins / total) * 2 * Math.PI;
    
        const startAngle2 = endAngle1;
        const endAngle2 = 2 * Math.PI;
    

        const winColor = '#7AEB6b';
        const loseColor = '#f05557';
        let drawPorgress = 0;

        //Dibuja los segmentos progresivamente
        const animate = () => {

            border.clearRect(0, 0, diagram.width, diagram.height); //Reinicia el diagrama
            const currentAngle = drawPorgress * 2 * Math.PI;

            if(currentAngle <= endAngle1 - startAngle1){ //Se dibuja la parte de victorias
                drawSegment(startAngle1, startAngle1 + currentAngle, winColor);
            } else {
                drawSegment(startAngle1, endAngle1, winColor); //Esto permite que el color verde persista en el diagrama
                
                const currentAngle2 = (drawPorgress - (wins/total)) * 2 * Math.PI; //Se calcula el angulo cuando se termina de dibujar la parte de victorias
                if(currentAngle2 <= endAngle2-startAngle2){
                    drawSegment(startAngle2, startAngle2 + currentAngle2, loseColor);//Se dibuja la parte de derrotas
                }
                
            }

            const animationConstant = 0.007; //Esta variable es necesaria, si no el diagrama no se completa
            drawPorgress += animationConstant;

            if(drawPorgress<=1 + animationConstant){
                requestAnimationFrame(animate);
            }
        }

        animate(); //Se llama a la función de animación
        
    }

    useEffect(() => {
        const canvas = canvasRef.current;
        if(canvas) { //Este if es para asegurarnos de que el diagrama está disponible (si no se pone puede dar error el useRef)
            drawDiagram(canvas);
        }
    }, []);




    return (
        
        <div>
            <div className="stats-container">
                <h1 className="stats-text">Stats</h1>
                <div className="stats-data">
                    <h2 className="username">
                        {user.username}
                    </h2>
                    <h3 className="games">
                        Games
                    </h3>
                    <div className="win-lose-ratio">
                        <canvas ref={canvasRef} width={250} height={250}/>
                        <div className="user-games">
                            <h3 className="won-games">Won: 80</h3>
                            <h3 className="lost-games">Lost: 35</h3>
                            <h3 className="total-games">Total: 115</h3>
                        </div>
                    </div>
                    
                </div>
                <div className="buttons-container">
                    <button className="sub-screen-button" onClick={()=>navigate("/individualStats")}>Individual stats</button>
                    {/*cuando estén hechas las rutas esa seguramente sea /:currentUser/individualStats*/}
                    <button className="sub-screen-button">Global stats</button>
                    <button className="sub-screen-button">Ranking</button>
                </div>
            </div>
        </div>

    );

}
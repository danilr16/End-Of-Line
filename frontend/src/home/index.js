import React from 'react';
import '../App.css';
import '../static/css/home/home.css';
import '../static/css/screens/MyGames.css'

import { useNavigate } from 'react-router-dom';

export default function Home() {
    const navigate = useNavigate()
    return (
        <div>
            <div className={"home-page-container"}>
                <div className="hero-div">
                    <h1>End of Line</h1>
                    <div className="button-container">
                        <button className="big-button" onClick={()=>navigate("/games/current")}>Play</button>
                    </div>
                </div>
            </div>

        </div>
    );
}
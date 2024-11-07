import React from 'react';
import '../App.css';
import '../static/css/home/home.css';
import '../static/css/screens/MyGames.css'

import { useNavigate } from 'react-router-dom';
import tokenService from '../services/token.service';

export default function Home() {
    const user = tokenService.getUser()
    const navigate = useNavigate()

    const handleClick = () => {
        if (user) {
            navigate("/games/current")
        } else {
            navigate("/login")
        }
    }

    return (
        <div>
            <div className={"home-page-container"}>
                <div className="hero-div">
                    <h1>End of Line</h1>
                    <div className="button-container">
                        <button className="big-button" onClick={handleClick}>Play</button>
                    </div>
                </div>
            </div>

        </div>
    );
}
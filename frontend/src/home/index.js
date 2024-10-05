import React from 'react';
import '../App.css';
import '../static/css/home/home.css';
import logo from '../static/images/palanca-de-mando.png' 

export default function Home(){
    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>END OF LINE</h1>
                <div className="button-container">
                    <button className="big-button">Create</button>
                    <button className="big-button">Join</button>
                </div>               
            </div>
        </div>
    );
}
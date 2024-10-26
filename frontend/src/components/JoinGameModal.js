import React, { useState } from 'react';
import "../static/css/components/components.css";
import tokenService from "../services/token.service"

export default function JoinGameModal({gameCode, setGameCode}){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
}
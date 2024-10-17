import React, { useEffect, useState } from 'react';
import userService from '../services/userContext'; 
import jwt_decode from "jwt-decode";
import tokenService from '../services/token.service';


export default function Profile() {
    const [user, setUser] = useState(null);
    const jwt = tokenService.getLocalAccessToken();


    useEffect(() => {
        if (jwt) {
            setUser(jwt_decode(jwt).sub);
        }
    }, [jwt])

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const currentUser = await userService.getCurrentUser();
                setUser(currentUser); 
            } catch (error) {
                console.error("Error fetching user data", error);
            }
        };

        fetchUser();
    }, []);

    if (!user) {
        return <div>Cargando...</div>;
    }

    return (
        <div className="profile-container">
            <h2>Perfil</h2>
            <img src={user.image} alt="Profile" className="profile-image" />
            <p>Usuario: {user.username}</p>
            <p>Contraseña: {user.password}</p>
        </div>
    );
}

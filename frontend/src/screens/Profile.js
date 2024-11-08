import React, { useEffect, useState } from 'react';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import '../static/css/components/components.css'; 

export default function Profile() {
    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [newImage, setNewImage] = useState(null);
    const [newUsername, setNewUsername] = useState(null);
    const [newPassword, setNewPassword] = useState(null);

//CAMBIAR update/${user.id} Y QUE SE MUESTREN TODOS LOS LOGROS (LOS CONSEGUIDOS EN VERDE)
    const [user, setUser] = useFetchState(
        [],
        '/api/v1/users/currentUser',
        jwt,
        setMessage,
        setVisible
    );

    const [achievements,setAchievements] = useFetchState(
        [],
        `/api/v1/achievements/myAchievement`,
        jwt,
        setMessage,
        setVisible
    );

    const [allAchievements, setAllAchievements] = useFetchState(
        [],
        `/api/v1/achievements`,
        jwt,
        setMessage,
        setVisible
    );


    const handleImageChange = (e) => {
        
        const file = e.target.files[0];
        if (file && file.type === 'image/png') {
            const imageUrl = URL.createObjectURL(file);
            setNewImage(imageUrl);
        }
    };
    const handleEditClick = () => {
        setIsEditing(!isEditing);
        if (isEditing) {
            setNewUsername(''); 
        } else {
            setNewUsername(user.username); 
        }
    };

    const handleSave = async () => {
        console.log("Saving user profile...");
        console.log("New Username:", newUsername);
        try {
            const updatedUser = {
                ...(newUsername && newUsername !== user.username ? { newUsername: newUsername } : {}),
                ...(newImage && newImage !== user.image  ? { newImage: newImage } : {}),
            };
            console.log("Updated User Data:", updatedUser);
            
            const response = await fetch(`/api/v1/users/myProfile`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`,
                },
                body: JSON.stringify(updatedUser),
            });
            
            console.log("Response", response);
            if (response.ok) {
                const updatedUserData = await response.json();
                //const newJwt = response.headers.get("Authorization").replace("Bearer ", "");
                //localStorage.setItem("jwt", newJwt);
                //tokenService.updateLocalAccessToken(newJwt);
                setUser(updatedUserData);
                window.location.reload(); 
                setMessage('Perfil actualizado con éxito');
                setIsEditing(false);
            }
             else {
                const errorData = await response.json();
                setMessage(`Error al actualizar el perfil: ${errorData.message || 'Desconocido'}`);
                console.error("Error Response:", errorData);
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
            console.error("Fetch Error:", error);
        }
    };
    
    
    const renderAchievement = (achievement) => {
        const isAchieved = achievements.some(userAchievements => userAchievements.id === achievement.id);
        return (
            <div key={achievement.id} className={`achievement-item ${isAchieved ? '-achieved' : ''}`} 
            >
                <img src={achievement.image} className="achievement-image" />
                <div className="achievement-details">
                    <h4>{achievement.name}</h4>
                    <p>{achievement.description}</p>
                </div>
            </div>
        );
    };
    
    // Pantalla de carga (cambiar)

    if (!user || !achievements || !allAchievements) {
        return <h3 className="myGames-title">loading...</h3>
    }

    return (
        <div>
            <div className="profile-container">
                <div className="profile-text">
                    <h3>Profile</h3>
                </div>
                <div className="profile-data">
                <div 
                        className={`image-wrapper ${isEditing ? 'editing' : ''}`} 
                        onClick={isEditing ? () => document.getElementById('profileImageInput').click() : null} 
                    >
                        <img src={newImage || user.image} className="profile-image" alt="Profile" />
                        {isEditing && (
                            <input
                                type="file"
                                id="profileImageInput"
                                accept="image/png"
                                style={{ display: 'none' }}
                                onChange={handleImageChange}
                            />
                        )}
                    </div>

    
                    <div className="profile-details">
                        {isEditing ? (
                            <>
                                <input 
                                    type="text" 
                                    value={newUsername}
                                    placeholder="new username"
                                    onChange={(e) => setNewUsername(e.target.value)} 
                                    className="editable-input" 
                                />
                                <p>Contraseña: {'*'.repeat(8)}</p>
                                
                                <button className='button-save' onClick={handleSave}>Guardar</button>
                            </>
                        ) : (
                            <>
                                <p>Usuario: {user.username}</p>
                                <p>Contraseña: {'*'.repeat(8)}</p>
                                
                            </>
                        )}
                    </div>

                    <button className='button-edit' onClick={handleEditClick}> {isEditing ? 'Cancelar' : 'Editar'} </button>
                    
                </div>
            </div>
            <div className="achievements-container">
        <div className="profile-text">
            <h3>Logros</h3>
        </div>
        {allAchievements && allAchievements.length > 0 ? (
            allAchievements.map(renderAchievement) 
        ) : (
            <p>No hay logros disponibles.</p>
        )}
    </div>
        </div>
    );
}    

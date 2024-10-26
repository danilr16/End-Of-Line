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

    const [user, setUser] = useFetchState(
        null,
        '/api/v1/users/currentUser',
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
        setNewPassword('');
    };

    const handleSave = async () => {
        console.log("Saving user profile...");
        console.log("Username:", newUsername);
        console.log("New Password:", newPassword || user.password);
        try {
            const updatedUser = {
                ...user,
                username: newUsername,
                password: newPassword || user.password, 
            };
            console.log("Updated User Data:", updatedUser);

            const response = await fetch(`/api/v1/users/update/${user.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`,
                },
                body: JSON.stringify(updatedUser),
            });

            if (response.ok) {
                const updatedUserData = await response.json();
                setUser(updatedUserData);
                setMessage('Perfil actualizado con éxito');
                setIsEditing(false);
            } else {
                setMessage('Error al actualizar el perfil');
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
        }
    };

    if (!user) {
        return <div>Loading...</div>;
    }

    return (
        <div className="profile-container">
            <div className="profile-text">
                <h3>Profile</h3>
            </div>
            <div className="profile-data">
            
                <div className="image-wrapper" onClick={() => document.getElementById('profileImageInput').click()}>
                    <img src={newImage ||user.image}  className="profile-image" />
                    <input
                        type="file"
                        id="profileImageInput"
                        accept="image/png"
                        style={{ display: 'none' }}
                        onChange={handleImageChange}
                    />
                </div>

                <div className="profile-details">
                    {isEditing ? (
                        <>
                            <input type="text" 
                            defaultValue={user.username}
                            placeholder="new username"
                            onChange={(e) => setNewUsername(e.target.value)} 
                            className="editable-input" />

                            <input 
                                type="password" 
                                value={newPassword} 
                                placeholder="new password"
                                onChange={(e) => setNewPassword(e.target.value)} 
                                className="editable-input" 
                            />
                            
                            <button className='button-profile' onClick={handleSave}>Guardar</button>
                        </>
                    ) : (
                        <>
                            <p>Usuario: {user.username}</p>
                            <p>Contraseña: {'*'.repeat(8)}</p>
                            <button className='button-profile' onClick={handleEditClick}>Editar</button>
                        </>
                    )}
                    </div>
            </div>
        </div>
    );
}
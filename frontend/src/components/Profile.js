import React, { useEffect, useState } from 'react';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import '../static/css/components/components.css'; // Asegúrate de importar tu archivo CSS

export default function Profile() {
    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [newImage, setNewImage] = useState(null);
    const [newPassword, setNewPassword] = useState('');

    const [user, setUser] = useFetchState(
        null,
        '/api/v1/users/current',
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
        try {
            const updatedUser = {
                ...user,
                password: newPassword || user.password, 
            };

            const response = await fetch('/api/v1/users/update', {
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

            <div className="image-wrapper" onClick={() => document.getElementById('profileImageInput').click()}>
                <img src={newImage || user.image} alt="Profile" className="profile-image" />
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
                        <input type="text" defaultValue={user.username} className="editable-input" />
                        
                        {/* Campo de contraseña editable */}
                        <input 
                            type="password" 
                            value={newPassword} 
                            placeholder="Nueva contraseña"
                            onChange={(e) => setNewPassword(e.target.value)} 
                            className="editable-input" 
                        />
                        
                        <button onClick={handleSave}>Guardar</button>
                    </>
                ) : (
                    <>
                        <p>Usuario: {user.username}</p>
                        
                        {/* Mostrar contraseña como **** */}
                        <p>Contraseña: {'*'.repeat(user.password.length)}</p>
                        
                        <button onClick={handleEditClick}>Editar</button>
                    </>
                )}
            </div>
        </div>
    );
}
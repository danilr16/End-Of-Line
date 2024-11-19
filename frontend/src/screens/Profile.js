import React, { useEffect, useState  } from 'react';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import "../static/css/screens/Profile.css";
import ReactDOM from 'react-dom';




export default function Profile() {
    const jwt = tokenService.getLocalAccessToken();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [newImage, setNewImage] = useState(null);
    const [newUsername, setNewUsername] = useState(null);
    const [newPassword, setNewPassword] = useState(null);
    const [oldPassword, setOldPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState(''); 
    const [showPasswordModal, setShowPasswordModal] = useState(false); // Cambiar contraseña
    const [showImageModal, setShowImageModal] = useState(false); //Cambiar imagen de perfil
    const [showConfirmationModal, setShowConfirmationModal] = useState(false); //Se confima el cambio de datos/usuario y se cierra sesión
    


    const predefinedImages = [
        "https://cdn-icons-png.flaticon.com/512/9368/9368199.png",
        "https://cdn-icons-png.flaticon.com/512/9368/9368199.png",
        "https://cdn-icons-png.flaticon.com/512/3135/3135768.png",
        "https://cdn-icons-png.flaticon.com/512/2920/2920072.png",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSM9o9d6uNkVAJUTyjn3nIX7ff6fABWy3SOCGU-J6WELR-0d7zcovc0_nxn_ahdERI54_I&usqp=CAU"
    ];
    


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

    const handleImageClick = () => {
        setShowImageModal(true); 
    };
    

    const handleEditClick = () => {
        setIsEditing(!isEditing);
        if (isEditing) {
            setNewUsername(''); 
        } else {
            setNewUsername(user.username); 
        }
    };

    const handleImageSelect = (image) => {
        setNewImage(image);  
        setShowImageModal(false); 
    };

    const handleSave = async () => {
        console.log("Saving user profile...");
        console.log("New Username:", newUsername);
        try {
            const updatedUser = {
                ...(newUsername && newUsername !== user.username ? { newUsername: newUsername } : {}),
                ...(newImage && newImage !== user.image ? { newImage: newImage } : {}),
                ...(oldPassword ? { oldPasswordDTO: oldPassword } : {}),
                ...(newPassword ? { newPasswordDTO: newPassword } : {}),
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
                setUser(updatedUserData);
                setMessage('Perfil actualizado con éxito');
                setIsEditing(false);
                if (newUsername && newUsername !== user.username) {
                    setShowConfirmationModal(true);

                    setTimeout(() => {
                        tokenService.updateLocalAccessToken(null);
                        localStorage.removeItem('jwt');
                        window.location.href = "/login";
                    }, 5000); // Se cierra sesión en 5 segundos

                }
            }
             else {
                const errorData = await response.json();
                setVisible(true); // Mostrar modal de error
                setMessage(errorData.message || 'Error desconocido al actualizar el perfil.');
                console.error("Error Response Data:", errorData);
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
            console.error("Fetch Error:", error);
        }
    };

    const handlePasswordChange = async () => {
        console.log("Old Password:", oldPassword);
        console.log("New Password:", newPassword);
        console.log("Confirm Password:", confirmPassword);
        if (newPassword !== confirmPassword) {
            setMessage("Passwords do not match");
            setVisible(true);
            return;
        }
        try {
            const updatedUser = {
                ...(oldPassword ? { oldPasswordDTO: oldPassword } : {}),
                ...(newPassword ? { newPasswordDTO: newPassword } : {}),
            };
            
            console.log("Updated User Data:", updatedUser);
            console.log("Bearer Token:", jwt);
            const response = await fetch(`/api/v1/users/myProfile/update-password`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`,
                },
                body: JSON.stringify(updatedUser),
            });
            
            console.log("Response", response);
            
            if (response.ok) {
                setShowPasswordModal(false);
                setShowConfirmationModal(true);
                setTimeout(() => {
                    tokenService.updateLocalAccessToken(null);
                    localStorage.removeItem('jwt');
                    window.location.href = "/login";
                }, 5000);
            } else {
                const errorData = await response.json();
                setVisible(true); // Mostrar modal de error
                setMessage(errorData.message || 'Error desconocido al actualizar el perfil.');
                console.error("Error Response Data:", errorData);
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
            console.error(error);
        }
    };
    
    
    
    
    const renderAchievement = (achievement) => {
        const isAchieved = achievements.some(userAchievements => userAchievements.id === achievement.id);
        return (
            <div key={achievement.id} className={`achievement-item${isAchieved ? '-achieved' : ''}`} 
            >
                <img src={achievement.image} className={`achievement-image${isAchieved ? '-achieved' : ''}`}  />
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
                    onClick={handleImageClick}
                >
                <img src={newImage || user.image} className="profile-image" />

                </div>
                {isEditing && showImageModal && ReactDOM.createPortal(
                    <div className="modal-profile-overlay">
                        <div className="modal-profile-container">
                            <h2>Select a Profile Image</h2>
                            <div className="image-selector">
                                {predefinedImages.map((image, index) => (
                                    <img 
                                        key={index} 
                                        src={image} 
                                        className="image-selector-item" 
                                        onClick={() => handleImageSelect(image)} 
                                    />
                                ))}
                            </div>
                            <button className="button-edit" onClick={() => setShowImageModal(false)}>Close</button> 
                        </div>
                    </div>,
                    document.body
                )}

                {showConfirmationModal && ReactDOM.createPortal(
                    <div className="modal-profile-overlay">
                        <div className="modal-confirmation-container">
                            <p>Name/Password changed successfully. The session will be closed in 5 seconds.</p>
                        </div>
                    </div>,
                    document.body
                )}
                {visible && ReactDOM.createPortal(
                    <div className="modal-profile-overlay">
                        <div className="message-box">
                        <p className="message-text">{message}</p>
                        <button className="button-edit" onClick={() => setVisible(false)}>X</button>
                        </div>
                    </div>,
                    document.body
                    
                )}
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
                                <button className="button-change-password" onClick={() => setShowPasswordModal(true)}>
                                    Cambiar Contraseña
                                </button>
                                {showPasswordModal && ReactDOM.createPortal(
                                    <div className="modal-profile-overlay">
                                        <div className="modal-profile-container">
                                            <h2>Cambiar Contraseña</h2>
                                            <input 
                                                type="password" 
                                                placeholder="Contraseña Antigua"
                                                value={oldPassword}
                                                onChange={(e) => setOldPassword(e.target.value)}
                                                className="editable-input"
                                            />
                                            <input 
                                                type="password" 
                                                placeholder="Nueva Contraseña"
                                                value={newPassword}
                                                onChange={(e) => setNewPassword(e.target.value)}
                                                className="editable-input"
                                            />
                                            <input 
                                                type="password" 
                                                placeholder="Confirmar Nueva Contraseña"
                                                value={confirmPassword}
                                                onChange={(e) => setConfirmPassword(e.target.value)}
                                                className="editable-input"
                                            />
                                            <div className="modal-buttons">
                                                <button className="button-save" onClick={handlePasswordChange}>Guardar</button>
                                                <button className="button-edit" onClick={() => setShowPasswordModal(false)}>Cancelar</button>
                                            </div>
                                        </div>
                                    </div>,
                                    document.body
                                )}
                
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

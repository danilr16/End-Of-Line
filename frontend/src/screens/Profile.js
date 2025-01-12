import React, { useEffect, useState  } from 'react';
import tokenService from '../services/token.service';
import useFetchState from "../util/useFetchState";
import "../static/css/screens/Profile.css";
import Friends from "../components/Friends";
import ReactDOM from 'react-dom';
import jwt_decode from "jwt-decode";




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

    const [editingAchievementID, setEditingAchievementID] = useState(null); // id del logro a edicitar
    const [newNameAchievement, setNewNameAchievement] = useState(''); 
    const [newDescriptionAchievement, setNewDescriptionAchievement] = useState(''); 
    const [newImageAchievement, setNewImageAchievement] = useState(''); 
    //Añadir nuevos logros
    const [newAchievementName, setNewAchievementName] = useState('');
    const [newAchievementDescription, setNewAchievementDescription] = useState('');
    const [newAchievementImage, setNewAchievementImage] = useState('');
    const [newAchievementThreshold, setNewAchievementThreshold] = useState(1);  
    const [newAchievementMetric, setNewAchievementMetric] = useState('GAMES_PLAYED');


    const [showPasswordModal, setShowPasswordModal] = useState(false); // Cambiar contraseña
    const [showImageModal, setShowImageModal] = useState(false); //Cambiar imagen de perfil
    const [showAddAchievement, setShowAddAchievement] = useState(false); //Añadir nuevo logro
    const [showConfirmationModal, setShowConfirmationModal] = useState(false); //Se confima el cambio de datos/usuario y se cierra sesión
    //Comprobamos si es administrador
    const roles = jwt_decode(jwt).authorities;
    const isAdmin = roles.includes("ADMIN");
    const [showEditAchievementModal, setEditAchievementModal] = useState(false); //Editar logros 
    const [showDeleteAchievementModal, setDeleteAchievementModal] = useState(false); //Eliminar logros 


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

    const handleEditAchievement = async (achievementID) => {
        try {
            if (!newNameAchievement?.trim()) {
                setMessage('The name cannot be empty.');
                setVisible(true);
                return;        
            }
            
            const updatedAchievement = {
                ...(newNameAchievement ? { name: newNameAchievement } : {}),
                ...(newDescriptionAchievement ? { description: newDescriptionAchievement } : {}),
            };

            const response = await fetch(`/api/v1/achievements/${achievementID}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`,
                },
                body: JSON.stringify(updatedAchievement),
            });
    
            if (response.ok) {
                const updatedData = await response.json(); 
                setAllAchievements((prev) =>
                    prev.map((achievement) =>
                        achievement.id === achievementID ? { ...achievement, ...updatedData } : achievement
                    )
                );
                setEditAchievementModal(false); 
            } else {
                const errorData = await response.json();
                setMessage(errorData.message || 'Error desconocido al actualizar el logro.');
                setVisible(true);
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
            console.error(error);
        }
    };

    const handleDeleteAchievement = async (achievementID) => {
        try {
            const response = await fetch(`/api/v1/achievements/${achievementID}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`,
                },
            });
            if (response.ok) {
                setAllAchievements((prev) =>
                    prev.filter((achievement) => achievement.id !== achievementID)
                );
                setEditAchievementModal(false);
            } else {
                const errorData = await response.json();
                setMessage(errorData.message || 'Error desconocido al eliminar el logro.');
                setVisible(true);
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
            console.error(error);
        }
    };

    const handleAddAchievement = async () =>  {
        try {
            
            const newAchievement = {
                ...(newAchievementName ? { name: newAchievementName } : {}),
                ...(newAchievementDescription ? { description: newAchievementDescription } : {}),
                ...(newAchievementImage ? { image: newAchievementImage } : {}),
                ...(newAchievementThreshold ? { threshold: newAchievementThreshold } : {}),
                ...(newAchievementMetric ? { metric: newAchievementMetric } : {}),
            };
            console.log(newAchievement);

            const response = await fetch(`/api/v1/achievements`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`,
                },
                body: JSON.stringify(newAchievement),
            });
            console.log(response);
            if (response.ok) {
                const addedAchievement = await response.json(); 
                setAllAchievements((prev) => [...prev, addedAchievement]); 
                setShowAddAchievement(false); 
            } else {
                const errorData = await response.json();
                setMessage(errorData.message || 'Error desconocido al actualizar el logro.');
                setVisible(true);
            }
        } catch (error) {
            setMessage('Error al conectar con el servidor');
            console.error(error);
        }
    };
    
        
    const renderAchievement = (achievement) => {
        const isAchieved = achievements.some(userAchievements => userAchievements.id === achievement.id);
        if (isAdmin){
            return (
                <div key={achievement.id} className={`achievement-item${isAchieved ? '-achieved' : ''}`}>
                    <div className="achievement-one">
                        <img src={achievement.image} className={`achievement-image${isAchieved ? '-achieved' : ''}`}  />
                        <div className="achievement-details">
                            <h4>{achievement.name}</h4>
                            <p>{achievement.description}</p>
                        </div>
                    </div>
                    <div className="achievement-buttons">
                        <button className="button-edit"
                         onClick={() => {
                            setEditingAchievementID(achievement.id);
                            setNewNameAchievement(achievement.name); 
                            setNewDescriptionAchievement(achievement.description);
                            setNewImageAchievement(achievement.image);
                            setEditAchievementModal(true); 
                        }}>Edit</button> 
                        <button className="button-delete" 
                            onClick={() => {
                                setEditingAchievementID(achievement.id); 
                                setDeleteAchievementModal(true);       
                            }}
                        >Delete </button>
                    </div>
                    {showEditAchievementModal && editingAchievementID === achievement.id && ReactDOM.createPortal(
                        <div className="modal-profile-overlay">
                            <div className="modal-input-container">
                                <h2>Edit achievement</h2>
                                <input
                                    type="text"
                                    value={newNameAchievement || ''}
                                    placeholder="New name"
                                    onChange={(e) => setNewNameAchievement(e.target.value)}
                                    className="editable-input"
                                />
                                <input
                                    type="text"
                                    value={newDescriptionAchievement || ''}
                                    placeholder="New description"
                                    onChange={(e) => setNewDescriptionAchievement(e.target.value)}
                                    className="editable-input"
                                />
                                <input
                                    type="text"
                                    value={newImageAchievement || ''}
                                    placeholder="Image URL"
                                    onChange={(e) => setNewImageAchievement(e.target.value)}
                                    className="editable-input"
                                />
                                <div className="modal-buttons">
                                    <button className="button-save" onClick={() => handleEditAchievement(editingAchievementID)}>Save</button>
                                    <button className="button-edit" onClick={() => setEditAchievementModal(false)}>Cancel</button>
                                    
                                </div>
                            </div>
                        </div>,
                        document.body
                    )}
                 
                    {showDeleteAchievementModal && editingAchievementID === achievement.id && ReactDOM.createPortal(
                        <div className="modal-profile-overlay">
                            <div className="modal-confirmation-achievement-container">
                              <p>Are you sure you want to eliminate this achievement?</p>
                                <div className="modal-buttons">
                                    <button className="button-save" onClick={() => handleDeleteAchievement(editingAchievementID)}>Save</button>
                                    <button className="button-edit" onClick={() => setDeleteAchievementModal(false)}>Cancel</button>
                                </div>
                            </div>
                        </div>,
                        document.body
                    )}
                </div>


                
            );

        }else{
   
            return (
                <div key={achievement.id} className={`achievement-item${isAchieved ? '-achieved' : ''}`}>
                    <img src={achievement.image} className={`achievement-image${isAchieved ? '-achieved' : ''}`}  />
                    <div className="achievement-details">
                        <h4>{achievement.name}</h4>
                        <p>{achievement.description}</p>
                    </div>
                    
                </div>
                
            );

        }
        
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
                                    className="editable-input-username" 
                                />

                                
                                <button className='button-save' onClick={handleSave}>Save</button>
                            </>
                        ) : (
                            <>
                                <p>User: {user.username}</p>
                                <p>Password: {'*'.repeat(8)}</p>
                                <button className="button-change-password" onClick={() => setShowPasswordModal(true)}>
                                    Change Password
                                </button>
                                {showPasswordModal && ReactDOM.createPortal(
                                    <div className="modal-profile-overlay">
                                        <div className="modal-input-container">
                                            <h2>Change Password</h2>
                                            <input 
                                                type="password" 
                                                placeholder="Old Password"
                                                value={oldPassword}
                                                onChange={(e) => setOldPassword(e.target.value)}
                                                className="editable-input"
                                            />
                                            <input 
                                                type="password" 
                                                placeholder="New Password"
                                                value={newPassword}
                                                onChange={(e) => setNewPassword(e.target.value)}
                                                className="editable-input"
                                            />
                                            <input 
                                                type="password" 
                                                placeholder="Confirm new password"
                                                value={confirmPassword}
                                                onChange={(e) => setConfirmPassword(e.target.value)}
                                                className="editable-input"
                                            />
                                            <div className="modal-buttons">
                                                <button className="button-save" onClick={handlePasswordChange}>Save</button>
                                                <button className="button-edit" onClick={() => setShowPasswordModal(false)}>Cancel</button>
                                            </div>
                                        </div>
                                    </div>,
                                    document.body
                                )}
                
                            </>
                            
                        )}

                    </div>

                    <button className='button-edit' onClick={handleEditClick}> {isEditing ? 'Cancel' : 'Edit'} </button>
                    
                </div>
            </div>
            <Friends/>
            <div className="achievements-container">
                <div className="profile-text">
                    <h3>Achievements</h3>
                </div>
                {allAchievements && allAchievements.length > 0 ? (
                    allAchievements.map(renderAchievement)
                ) : (
                    <p>No achievements available.</p>
                )}

                {isAdmin && ( 
                    <button className="button-change-password" onClick={() => setShowAddAchievement(true)}>
                        Add new achievement
                    </button>
                )}
                {showAddAchievement && ReactDOM.createPortal(
                    <div className="modal-profile-overlay">
                        <div className="modal-input-container">
                            <h2>Add achievement</h2>
                            <input
                                type="text"
                                value={newAchievementName}
                                placeholder="Name"
                                onChange={(e) => setNewAchievementName(e.target.value)}
                                className="editable-input"
                            />
                            <input
                                type="text"
                                value={newAchievementDescription}
                                placeholder="Description"
                                onChange={(e) => setNewAchievementDescription(e.target.value)}
                                className="editable-input"
                            />
                            <input
                                type="text"
                                value={newAchievementImage}
                                placeholder="Image URL"
                                onChange={(e) => setNewAchievementImage(e.target.value)}
                                className="editable-input"
                            />
                            <input
                                type="number"
                                value={newAchievementThreshold}
                                placeholder="Umbral (Threshold)"
                                onChange={(e) => setNewAchievementThreshold(Number(e.target.value))}
                                min="1"
                                className="editable-input"
                            />
                            <select
                                value={newAchievementMetric}
                                onChange={(e) => setNewAchievementMetric(e.target.value)}
                                className="editable-input"
                            >
                                <option value="GAMES_PLAYED">Games played</option>
                                <option value="VICTORIES">Victories</option>
                                <option value="FRIEND_ADDED">Friend Added</option>
                                <option value="TOTAL_POINTS">Total points</option>
                            </select>

                            <div className="modal-buttons">
                                <button className="button-save" onClick={handleAddAchievement}>Save</button>
                                <button className="button-edit" onClick={() => setShowAddAchievement(false)}>Cancel</button>
                            </div>
                        </div>
                    </div>,
                    document.body
                )}
             </div>  
            </div>


            );
}    

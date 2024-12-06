import { useEffect, useState } from 'react';
import tokenService from '../services/token.service';
import request from '../util/request';
import "../static/css/components/friends.css"
import NewFriendModal from './NewFriendModal';


export default function Friends(){
    const jwt = tokenService.getLocalAccessToken();
    const [isModalOpen,setIsModalOpen] = useState(false);
    const [friendName,setFriendName] = useState("");
    const [user,setUser] = useState(null);
    useEffect(() => {
        const fetchUser = async () => {
            const {resContent } = await request(`/api/v1/users/currentUser`,"GET",null,jwt,null);
            setUser(resContent);
        }
        fetchUser();
    },[jwt])
    
    const handleAddFriend =  () => {
        setIsModalOpen(true)
    }
    const parseToFriend = (f) => {
        return (
        <li className='friend-container' key = {f.username}>
            <img alt ="Profile img" src ={f.image} className='friend-image'/>
            <p className='friend-name'>{f.username}</p>
        </li>
        );
    }
    
    const friends = user?.friends && user.friends.length>0 ? user.friends.map((f)=>parseToFriend(f)) : null;
    
    console.log(user)

    return(
    <>
        {isModalOpen &&<NewFriendModal friendName={friendName} setFriendName={setFriendName} jwt={jwt} closeModal={() => setIsModalOpen(false)}/>}
        <div className='friend-list-container'>
            <button onClick= {handleAddFriend} className='add-friend-button'>Add friend</button>
            {friends?
            <ul className='friend-list'>
                {friends}
            </ul>
            :<p style= {{color:'white'}}>You don't have any friends yet</p>}    
        </div>
    </>
    );


}
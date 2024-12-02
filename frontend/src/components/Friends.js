import { useEffect, useState } from 'react';
import tokenService from '../services/token.service';
import request from '../util/request';
import "../static/css/components/friends.css"


export default function Friends(){
    const jwt = tokenService.getLocalAccessToken();
    const [user,setUser] = useState(null);
    useEffect(() => {
        const fetchUser = async () => {
            const {resContent } = await request(`/api/v1/users/currentUser`,"GET",null,jwt,null);
            setUser(resContent);
        }
        fetchUser();
    },[jwt])
    
    const handleAddFriend =  () => {
        alert("To be implemented");
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
        
        <div className='friend-list-container'>
            <button className='add-friend-button'>Add friend</button>
            {friends?
            <ul className='friend-list'>
                {friends}
            </ul>
            :<p style= {{color:'white'}}>You don't have any friends yet</p>}    
        </div>
    </>
    );


}
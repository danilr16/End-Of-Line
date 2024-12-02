import tokenService from '../services/token.service';
import request from '../util/request';


export default function Friends(){
    const jwt = tokenService.getLocalAccessToken();
    const user = request("/api/v1/users/currentUser","GET",null,jwt);
    
    const parseToFriend = (f) => {
        return (
        <li className='friend-container'>
            <img alt ="Profile img" src ={f.profileImg} className='friend-image'/>
            <p className='friend-name'>{f.username}</p>
        </li>
        );
    }

    const friends = user.friends? user.friends.map((f)=>parseToFriend(f)) : null;

    return(
       <>
        {friends?friends:<p style= {{color:'white'}}>You don't have any friends yet</p>}


       </>
    );


}
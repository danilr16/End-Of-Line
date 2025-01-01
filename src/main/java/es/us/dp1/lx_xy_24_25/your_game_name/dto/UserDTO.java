package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private String username;

   
    private Authorities authorities;

    private List<FriendDTO> friends;

    private String image;

    public UserDTO(){}

    public UserDTO(String username,Authorities authorities, List<FriendDTO> friends, String image){
        this.username = username;
        this.authorities = authorities;
        this.friends = friends;
        this.image = image;
    }

    public static UserDTO convertUserToDTO(User u){
        List<FriendDTO> friends = new ArrayList<>();
        if(!u.getFriends().isEmpty()){
        for(User f:u.getFriends()){
            friends.add(FriendDTO.userToFriendDTO(f));
            }
        }
        UserDTO  userDTO = new UserDTO(u.getUsername(), u.getAuthority(), friends, u.getImage());
        return userDTO;
    }
}

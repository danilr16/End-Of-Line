package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private String username;

   
    private Authorities authorities;

    private List<String> friendsName;

    public UserDTO(){}

    public UserDTO(String username,Authorities authorities, List<String> friendsName){
        this.username = username;
        this.authorities = authorities;
        this.friendsName = friendsName;
    }

    public static UserDTO convertUserToDTO(User u){
        List<String> friends = new ArrayList<>();
        if(!u.getFriends().isEmpty()){
        for(User f:u.getFriends()){
            friends.add(f.getUsername());
            }
        }
        UserDTO  userDTO = new UserDTO(u.getUsername(), u.getAuthority(), friends);
        return userDTO;
    }
}

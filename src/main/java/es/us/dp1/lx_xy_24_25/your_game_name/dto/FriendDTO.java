package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendDTO {
    private String username;
    private String profileImg;

    public FriendDTO(){}
    public FriendDTO(String username,String img){
        this.username = username;
        this.profileImg = img;
    }

    public static FriendDTO userToFriendDTO(User u){
        return new FriendDTO(u.getUsername(),u.getImage());
    }
}

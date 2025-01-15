package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDTO {

    private String newUsername;
    private String newImage;
    private String oldPasswordDTO;
    private String newPasswordDTO;

     public UserProfileUpdateDTO(){}

    public UserProfileUpdateDTO(String newUsername,String newImage, String oldPasswordDTO, String newPasswordDTO){
        this.newUsername = newUsername;
        this.newImage = newImage;
        this.oldPasswordDTO = oldPasswordDTO;
        this.newPasswordDTO = newPasswordDTO;
    }


}

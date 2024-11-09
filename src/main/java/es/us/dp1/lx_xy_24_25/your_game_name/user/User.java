package es.us.dp1.lx_xy_24_25.your_game_name.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appusers")
public class User extends BaseEntity {

	@Column(unique = true)
	String username;

	String password;

	String image;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "authority")
	Authorities authority;

	@ManyToMany
	@NotNull
	List<Achievement> achievements;

	@ManyToMany
	@NotNull
	List<User> friends;

	@PrePersist
    @PreUpdate
    private void prePersist() {
        if (image == null ) {
            this.image = "https://cdn-icons-png.flaticon.com/512/3135/3135768.png";
        }
    }

	public void setImage(String image) {
        if (image == null || image.isEmpty()) {
            this.image = "https://cdn-icons-png.flaticon.com/512/3135/3135768.png"; 
        } else {
            this.image = image;
        }
    }

	public Boolean hasAuthority(String auth) {
		return authority.getAuthority().equals(auth);
	}

	public Boolean hasAnyAuthority(String... authorities) {
		Boolean cond = false;
		for (String auth : authorities) {
			if (auth.equals(authority.getAuthority()))
				cond = true;
		}
		return cond;
	}
}

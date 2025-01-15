package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.us.dp1.lx_xy_24_25.your_game_name.achievements.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appusers")
public class User extends BaseEntity {
	
	private static final List<String> IMAGE_POOL = List.of(
        "https://cdn-icons-png.flaticon.com/128/414/414681.png",
        "https://cdn-icons-png.flaticon.com/128/414/414686.png",
        "https://cdn-icons-png.flaticon.com/128/414/414682.png",
        "https://cdn-icons-png.flaticon.com/128/414/414683.png",
        "https://cdn-icons-png.flaticon.com/128/414/414679.png",
        "https://cdn-icons-png.flaticon.com/128/414/414696.png",
        "https://cdn-icons-png.flaticon.com/128/414/414710.png",
        "https://cdn-icons-png.flaticon.com/128/414/414691.png",
        "https://cdn-icons-png.flaticon.com/128/414/414692.png",
        "https://cdn-icons-png.flaticon.com/128/414/414695.png",
        "https://cdn-icons-png.flaticon.com/128/414/414684.png"
    );

	@Column(unique = true)
	String username;

	String password;

	String image;

	Integer winningStreak;

    Integer maxStreak;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "authority")
	Authorities authority;

	@ManyToMany
	List<Achievement> achievements;

	@ManyToMany
	@JsonIgnore
	List<User> friends;

	@PrePersist
    @PreUpdate
    private void prePersist() {
        if (image == null ) {
            this.image = getRandomImage();
        }
		if (achievements == null) {
			this.achievements = new ArrayList<>();
		}
		if (friends == null) {
			this.friends = new ArrayList<>();
		}
		if (winningStreak == null) {
            winningStreak = 0;
        }
        if (maxStreak == null) {
            maxStreak = 0;
        }
    }

	public void setImage(String image) {
        if (image == null || image.isEmpty()) {
            this.image = getRandomImage(); 
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

	private String getRandomImage() {
        Random random = new Random();
        return IMAGE_POOL.get(random.nextInt(IMAGE_POOL.size()));
    }
}

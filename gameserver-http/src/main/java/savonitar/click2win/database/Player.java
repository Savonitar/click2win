package savonitar.click2win.database;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "playername")
    private String playerName;

    @Column(name = "rating")
    private Integer rating;
}
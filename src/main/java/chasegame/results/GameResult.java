package chasegame.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;

/**
 * Class representing the result of a game played by a specific player.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GameResult {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the player.
     */
    @Column
    private String player;

    /**
     * The number of rounds played.
     */
    private int rounds;

    /**
     * The duration of the game.
     */
    @Column(nullable = false)
    private Duration duration;
}

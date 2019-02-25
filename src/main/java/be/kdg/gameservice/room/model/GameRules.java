package be.kdg.gameservice.room.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * The rules of a game are represented in the a game format.
 */
@Getter
@Entity
@Setter
@Table(name = "game_rules")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public final class GameRules {
    /**
     * The id of the gamerule. Used for persistence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * An obligated small bet that needs to be made.
     */
    private int smallBlind;

    /**
     * An obligated 'big' bet that needs to be made.
     */
    private int bigBlind;

    /**
     * The time each player has to make an act during a round.
     */
    private int playDelay;

    /**
     * The staring chips for each player at the beginning of the round.
     */
    private int startingChips;

    /**
     * The max number of players that are permitted in a room.
     */
    private int maxPlayerCount;

    public GameRules(int smallBlind, int bigBlind, int playDelay, int startingChips, int maxPlayerCount) {
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.playDelay = playDelay;
        this.startingChips = startingChips;
        this.maxPlayerCount = maxPlayerCount;
    }

    public GameRules() {
        this.smallBlind = 10;
        this.bigBlind = 20;
        this.playDelay = 30;
        this.startingChips = 2000;
        this.maxPlayerCount = 6;
    }
}
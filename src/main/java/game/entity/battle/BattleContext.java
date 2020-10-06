package game.entity.battle;

import com.sun.istack.internal.Nullable;
import game.entity.Ship;
import game.enums.Level;
import game.enums.Turn;
import game.moveStrategy.AdvancedServerMoveStrategy;
import game.moveStrategy.LaboratoryMoveStrategy;
import game.moveStrategy.MoveStrategy;
import game.moveStrategy.SimpleServerMoveStrategy;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

//Can't implement pattern Builder, because all parameters are required :/
public class BattleContext implements Serializable {

    private Collection<Ship> firstPlayerShips = new HashSet<>();
    private Collection<Ship> secondPlayerShips = new HashSet<>();
    private MoveStrategy moveStrategy = null;
    private Turn turn = Turn.PLAYER_1;
    private boolean isClientServerContext;
    private static final long serialVersionUID = -1311025019735470420L;

    public BattleContext() {
    }

    private BattleContext(Collection<Ship> firstPlayerShips, Collection<Ship> secondPlayerShips,
                          MoveStrategy moveStrategy, boolean isClientServerContext) {
        this.firstPlayerShips = firstPlayerShips;
        this.secondPlayerShips = secondPlayerShips;
        this.moveStrategy = moveStrategy;
        this.isClientServerContext = isClientServerContext;
    }

    public static BattleContext clientClientContext(Collection<Ship> firstPlayerShips,
                                                    Collection<Ship> secondPlayerShips) {
        return new BattleContext(firstPlayerShips, secondPlayerShips,
                null, false);
    }

    public static BattleContext clientServerContext(Collection<Ship> clientShips,
                                                    Collection<Ship> serverShips,
                                                    Level level) {
        return new BattleContext(clientShips, serverShips,
                createStrategy(level, clientShips), true);
    }

    private static MoveStrategy createStrategy(Level level, Collection<Ship> clientShips) {
        if (level == Level.SIMPLE) {
            return new SimpleServerMoveStrategy();
        } else if (level == Level.LABORATORY) {
            return new LaboratoryMoveStrategy();
        }
        return new AdvancedServerMoveStrategy(clientShips);
    }

    public Collection<Ship> getFirstPlayerShips() {
        return firstPlayerShips;
    }

    public Collection<Ship> getSecondPlayerShips() {
        return secondPlayerShips;
    }

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    @Nullable
    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }

    public boolean isClientServerContext() {
        return isClientServerContext;
    }

    public boolean gameIsOver() {
        return firstPlayerShips.isEmpty() || secondPlayerShips.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleContext that = (BattleContext) o;
        return isClientServerContext == that.isClientServerContext &&
                Objects.equals(firstPlayerShips, that.firstPlayerShips) &&
                Objects.equals(secondPlayerShips, that.secondPlayerShips) &&
                turn == that.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstPlayerShips, secondPlayerShips, turn, isClientServerContext);
    }

    @Override
    public String toString() {
        return "BattleContext{" +
                "firstPlayerShips=" + firstPlayerShips +
                ", secondPlayerShips=" + secondPlayerShips +
                ", moveStrategy=" + moveStrategy +
                ", turn=" + turn +
                ", isClientServerContext=" + isClientServerContext +
                '}';
    }
}

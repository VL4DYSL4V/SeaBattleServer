package game.entity.battle;

import com.sun.istack.internal.Nullable;
import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FiringResult;
import game.enums.Level;
import game.enums.Turn;
import game.exception.GameOverException;
import game.moveStrategy.MoveStrategy;
import game.service.FieldGenerator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

//TODO: может, сделать 1 метод для стрельбы, и LinkedList запросов на выстрел в каждой
// битве. Тогда битва сама будет решать, кто и в кого сейчас стреляет, и будет просто результат
// возвращать. Так а как решить, кому этот запрос отправлять?

public class Battle implements Serializable {
    private BattleContext battleContext = new BattleContext();
    private BattleStatistics battleStatistics = new BattleStatistics();

    private static final long serialVersionUID = -2257822420997975557L;

    public Battle() {
    }

    private Battle(BattleContext battleContext) {
        this.battleContext = battleContext;
    }

    public static Battle generateClientClientBattle() {
        FieldGenerator fieldGenerator = new FieldGenerator();
        Collection<Ship> firstPlayerShips = fieldGenerator.createShips();
        Collection<Ship> secondPlayerShip = fieldGenerator.createShips();
        return Battle.clientClientBattle(firstPlayerShips, secondPlayerShip);
    }

    public static Battle generateClientServerBattle(Level level) {
        FieldGenerator fieldGenerator = new FieldGenerator();
        Collection<Ship> clientShips = fieldGenerator.createShips();
        Collection<Ship> serverShips = fieldGenerator.createShips();
        return Battle.clientServerBattle(clientShips, serverShips, level);
    }

    public static Battle clientClientBattle(Collection<Ship> firstClientShip, Collection<Ship> secondClientShips) {
        return new Battle(BattleContext.clientClientContext(firstClientShip, secondClientShips));
    }

    public static Battle clientServerBattle(Collection<Ship> clientShips, Collection<Ship> serverShips,
                                            Level level) {
        return new Battle(BattleContext.clientServerContext(clientShips, serverShips, level));
    }

    public FiringResult shootAtFirstPlayer(Coordinates coordinates) throws GameOverException {
        if (!battleContext.gameIsOver()) {
            FiringResult firingResult;
            if (coordinates != null) {
                firingResult = fireAtFirst(coordinates);
            } else {
                firingResult = FiringResult.MISSED;
            }
            battleStatistics.updateStatisticsSecond(firingResult);
            changeTurn(firingResult);
            return firingResult;
        } else {
            throw new GameOverException("Game over!");
        }
    }

    public FiringResult shootAtSecondPlayer(Coordinates coordinates) throws GameOverException {
        if (!battleContext.gameIsOver()) {
            FiringResult firingResult;
            if (coordinates != null) {
                firingResult = fireAtSecond(coordinates);
            } else {
                firingResult = FiringResult.MISSED;
            }
            battleStatistics.updateStatisticsFirst(firingResult);
            changeTurn(firingResult);
            return firingResult;
        } else {
            throw new GameOverException("Game over!");
        }
    }

    private void changeTurn(FiringResult firingResult) {
        if (firingResult == FiringResult.MISSED) {
            if (battleContext.getTurn() == Turn.PLAYER_1) {
                battleContext.setTurn(Turn.PLAYER_2);
            } else if (battleContext.getTurn() == Turn.PLAYER_2){
                battleContext.setTurn(Turn.PLAYER_1);
            }
        }
    }

    private FiringResult fireAtFirst(Coordinates whereTo) {
        return fireAt(whereTo, battleContext.getFirstPlayerShips());
    }

    private FiringResult fireAtSecond(Coordinates whereTo) {
        return fireAt(whereTo, battleContext.getSecondPlayerShips());
    }

    private FiringResult fireAt(Coordinates whereTo, Collection<Ship> opponentShips) {
        for (Ship ship : opponentShips) {
            Collection<Coordinates> occupied = ship.getOccupiedCoordinates();
            if (occupied.contains(whereTo)) {
                ship.getDamagedCoordinates().add(whereTo);
                if (ship.isDestroyed()) {
                    opponentShips.remove(ship);
                    return FiringResult.DESTROYED;
                }
                return FiringResult.AIMED;
            }
        }
        return FiringResult.MISSED;
    }

    @Nullable
    public MoveStrategy getServerMoveStrategy() {
        return battleContext.getMoveStrategy();
    }

    public boolean isServerClientBattle() {
        return battleContext.isClientServerContext();
    }

    public boolean isClientClientBattle() {
        return !isServerClientBattle();
    }

    public Map<FiringResult, Integer> getStatisticsFirst() {
        return battleStatistics.getStatisticsFirst();
    }

    public Map<FiringResult, Integer> getStatisticsSecond() {
        return battleStatistics.getStatisticsSecond();
    }

    public Turn getTurn() {
        return battleContext.getTurn();
    }

    public Collection<Ship> getFirstPlayerShips() {
        return battleContext.getFirstPlayerShips();
    }

    public Collection<Ship> getSecondPlayerShips() {
        return battleContext.getSecondPlayerShips();
    }

    public boolean gameOver(){
        return battleContext.gameIsOver();
    }
}

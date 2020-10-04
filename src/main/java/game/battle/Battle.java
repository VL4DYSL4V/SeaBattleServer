package game.battle;

import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FiringResult;
import game.exception.GameOverException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Battle implements Serializable {

    private Collection<Ship> firstPlayerShips;
    private Collection<Ship> secondPlayerShips;

    private Map<String, Integer> statisticsSecond;
    private Map<String, Integer> statisticsFirst;

    private static final long serialVersionUID = 4236456693791369822L;

    {
        this.statisticsSecond = new HashMap<>();
        statisticsSecond.put("aimed", 0);
        statisticsSecond.put("missed", 0);
        statisticsSecond.put("destroyed", 0);
    }

    public Battle() {

    }

    public FiringResult playFirst(Coordinates coordinates) throws GameOverException {
//        if(!isEndOfGame()){
//            FiringResult firingResult = firstBehaviour.makeMove(coordinates);
//            updateStatistics(statisticsFirst, firingResult);
//            return firingResult;
//        }
        throw new GameOverException("Game over!");
    }

    public FiringResult playSecond(Coordinates coordinates) throws GameOverException {
//        if(!isEndOfGame()){
//            FiringResult firingResult = secondBehaviour.makeMove(coordinates);
//            updateStatistics(statisticsSecond, firingResult);
//            return firingResult;
//        }
        throw new GameOverException("Game over!");
    }


    public FiringResult makeMove(Coordinates fireAt) {
        return fireAtSecond(fireAt);
    }

    protected final FiringResult fireAtFirst(Coordinates whereTo) {
        return fireAt(whereTo, secondPlayerShips);
    }

    protected final FiringResult fireAtSecond(Coordinates whereTo) {
        return fireAt(whereTo, secondPlayerShips);
    }

    protected final FiringResult fireAt(Coordinates whereTo, Collection<Ship> opponentShips) {
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

    private boolean isEndOfGame() {
        return firstPlayerShips.isEmpty() || secondPlayerShips.isEmpty();
    }

    private void updateStatistics(Map<String, Integer> statistics, FiringResult firingResult){
        if(firingResult == FiringResult.AIMED){
            incrementAiming(statistics);
        }else if(firingResult == FiringResult.DESTROYED){
            incrementDestroyed(statistics);
        }else{
            incrementMissed(statistics);
        }
    }

    private void incrementAiming(Map<String, Integer> statistics){
        statistics.put("aimed", statisticsSecond.get("aimed") + 1);
    }

    private void incrementDestroyed(Map<String, Integer> statistics){
        statistics.put("destroyed", statisticsSecond.get("destroyed") + 1);
    }

    private void incrementMissed(Map<String, Integer> statistics){
        statistics.put("missed", statisticsSecond.get("missed") + 1);
    }

    public Map<String, Integer> getStatisticsFirst(){
        return statisticsFirst;
    }

    public Map<String, Integer> getStatisticsSecond(){
        return statisticsSecond;
    }

    public final Collection<Ship> getFirstPlayerShips() {
        return Collections.unmodifiableCollection(firstPlayerShips);
    }

    public final Collection<Ship> getSecondPlayerShips() {
        return Collections.unmodifiableCollection(secondPlayerShips);
    }
}

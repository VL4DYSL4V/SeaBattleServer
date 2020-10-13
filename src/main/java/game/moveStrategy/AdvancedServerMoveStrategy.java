package game.moveStrategy;

import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FieldConstraints;
import game.util.CoordinateCalculator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class AdvancedServerMoveStrategy implements MoveStrategy, Serializable {

    private Collection<Ship> clientShips;
    private Coordinates firstDamaged;
    private HashSet<Coordinates> unprofitableCoordinates = new HashSet<>(50);
    private ArrayList<Coordinates> currUsefulCoords;

    private static final long serialVersionUID = -6518916842455821829L;

    private AdvancedServerMoveStrategy() {
    }

    public AdvancedServerMoveStrategy(Collection<Ship> clientShips){
        this.clientShips = Collections.unmodifiableCollection(clientShips);
    }

    @Override
    public Coordinates getCoordinates() {
        if (firstDamaged == null) {
            return noOneIsDamagedAlgorithm();
        }
        if (currUsefulCoords == null) {
            currUsefulCoords = CoordinateCalculator.generateSWENneighbours(firstDamaged);
        }
        return oneDeckIsDamagedAlgorithm();
    }

    private Coordinates noOneIsDamagedAlgorithm() {
        Coordinates coordinates;
        for (int i = 0; i < 10; i++) {
            coordinates = CoordinateCalculator.simpleCoordinatesCalculation(FieldConstraints.MAX_X.getValue(),
                    FieldConstraints.MAX_Y.getValue());
            if (!unprofitableCoordinates.contains(coordinates)) {
                initialAimingAttempt(coordinates);
                return coordinates;
            }
        }
        coordinates = CoordinateCalculator.advancedCoordinatesCalculation(unprofitableCoordinates);
        initialAimingAttempt(coordinates);
        return coordinates;
    }

    private Coordinates oneDeckIsDamagedAlgorithm() {
        Coordinates possibleUseful = currUsefulCoords.get(0);
        unprofitableCoordinates.add(possibleUseful);
        currUsefulCoords.remove(possibleUseful);
        for (Ship clientShip : clientShips) {
            if (clientShip.getOccupiedCoordinates().contains(possibleUseful)) {
                Integer damagedDeckAmount = clientShip.getDamagedCoordinates().size();
                if (damagedDeckAmount.equals(clientShip.getDeckAmount() - 1)) {
                    Collection<Coordinates> unavailable = CoordinateCalculator.calculateCoordsAround(clientShip);
                    unprofitableCoordinates.addAll(unavailable);
                    currUsefulCoords = null;
                    firstDamaged = null;
                } else {
                    currUsefulCoords = CoordinateCalculator.calculateNextPossibleUseful(firstDamaged, possibleUseful);
                }
                break;
            }
        }
        return possibleUseful;
    }

    private void initialAimingAttempt(Coordinates whereTo) {
        unprofitableCoordinates.add(whereTo);
        for (Ship clientShip : clientShips) {
            if (clientShip.getOccupiedCoordinates().contains(whereTo)) {
                if (clientShip.getDeckAmount().equals(1)) {
                    Collection<Coordinates> unavailable = CoordinateCalculator.calculateCoordsAround(clientShip);
                    unprofitableCoordinates.addAll(unavailable);
                } else {
                    firstDamaged = whereTo;
                }
                break;
            }
        }
    }
}

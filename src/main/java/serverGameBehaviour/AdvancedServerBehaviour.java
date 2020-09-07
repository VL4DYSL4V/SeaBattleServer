package serverGameBehaviour;

import data.Coordinates;
import data.Ship;
import enums.FieldConstraints;
import enums.FiringResult;
import enums.PlayerType;
import util.CoordinateCalculator;
import util.CoordinatesPosition;

import java.util.*;

public class AdvancedServerBehaviour extends ServerBehaviour {

    private final Collection<Ship> clientShips;
    private Coordinates firstDamaged;
    private Coordinates lastDamaged;
    private final HashSet<Coordinates> unprofitableCoordinates = new HashSet<>();
    private ArrayList<Coordinates> currUsefulCoords = null;

    public AdvancedServerBehaviour(Collection<Ship> serverShips, Collection<Ship> clientShips) {
        super(serverShips, clientShips);
        this.clientShips = super.getClientShips();
    }

    @Override
    public Coordinates calculateCoordinates() {
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
            if (!this.unprofitableCoordinates.contains(coordinates)) {
                initialAimingAttempt(coordinates);
                return coordinates;
            }
        }
        coordinates = CoordinateCalculator.advancedCoordinatesCalculation(unprofitableCoordinates);
        if (CoordinatesPosition.inInvalidPosition(coordinates)) {
            throw new IllegalArgumentException(coordinates.toString());
        }
        initialAimingAttempt(coordinates);
        return coordinates;
    }

    private Coordinates oneDeckIsDamagedAlgorithm() {
        Coordinates possibleUseful = currUsefulCoords.get(0);
        this.unprofitableCoordinates.add(possibleUseful);
        currUsefulCoords.remove(possibleUseful);
        for (Ship clientShip : clientShips) {
            if (clientShip.getAllOccupiedCoordinates().contains(possibleUseful)) {
                Integer damagedDeckAmount = clientShip.getDamagedCoordinates().size();
                if (damagedDeckAmount.equals(clientShip.getDeckAmount() - 1)) {
                    Collection<Coordinates> unavailable = CoordinateCalculator.calculateCoordsAround(clientShip);
                    unprofitableCoordinates.addAll(unavailable);
                    currUsefulCoords = null;
                    this.firstDamaged = null;
                    this.lastDamaged = null;
                } else {
                    lastDamaged = possibleUseful;
                    this.currUsefulCoords = CoordinateCalculator.calculateNextPossibleUseful(firstDamaged, lastDamaged);
                }
                break;
            }
        }
        return possibleUseful;
    }


    private void initialAimingAttempt(Coordinates whereTo) {
        this.unprofitableCoordinates.add(whereTo);
        for (Ship clientShip : clientShips) {
            if (clientShip.getAllOccupiedCoordinates().contains(whereTo)) {
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

    @Override
    public PlayerType calculateTurn(FiringResult firingResult) {
        return (firingResult == FiringResult.AIMED) ? PlayerType.SERVER : PlayerType.PLAYER_1;
    }

    @Override
    public Collection<Coordinates> getUsedCoords() {
        return Collections.unmodifiableCollection(unprofitableCoordinates);
    }
}

package serverGameBehaviour;

import data.Coordinates;
import data.Ship;
import enums.FieldConstraints;
import enums.FiringResult;
import enums.PlayerType;
import util.CoordinateCalculator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


public class SimpleServerBehaviour extends ServerBehaviour {

    private final Collection<Coordinates> usedCoords;
    private Coordinates lastCalculated = new Coordinates(0, 0);

    public SimpleServerBehaviour(Collection<Ship> serverShips, Collection<Ship> clientShips) {
        super(serverShips, clientShips);
        this.usedCoords = new HashSet<>();
    }

    @Override
    public Coordinates calculateCoordinates() {
        int loadFactor = 40;
        if (usedCoords.size() < loadFactor) {
            for (int i = 0; i < 5; i++) {
                Coordinates coordinates = CoordinateCalculator.simpleCoordinatesCalculation(FieldConstraints.MAX_X.getValue(),
                        FieldConstraints.MAX_Y.getValue());
                if (!this.usedCoords.contains(coordinates)) {
                    usedCoords.add(coordinates);
                    return coordinates;
                }
            }
        }
        while (this.usedCoords.contains(lastCalculated)) {
            lastCalculated = CoordinateCalculator.walkRightAndDown(
                    new Coordinates(lastCalculated.getX(), lastCalculated.getY()), 0);
        }
        usedCoords.add(lastCalculated);
        return lastCalculated;
    }

    @Override
    public PlayerType calculateTurn(FiringResult firingResult) {
        return PlayerType.PLAYER_1;
    }

    @Override
    public Collection<Coordinates> getUsedCoords() {
        return Collections.unmodifiableCollection(this.usedCoords);
    }
}

package game.moveStrategy;

import game.entity.Coordinates;
import game.enums.FieldConstraints;
import game.util.CoordinateCalculator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class SimpleServerMoveStrategy implements MoveStrategy, Serializable {

    private final Collection<Coordinates> usedCoords = new HashSet<>(50);
    private Coordinates lastCalculated = new Coordinates(0, 0);

    private static final long serialVersionUID = 3003460519162862780L;

    public SimpleServerMoveStrategy() {

    }

    @Override
    public Coordinates getCoordinates() {
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
    public Collection<Coordinates> getUsedCoords() {
        return Collections.unmodifiableCollection(this.usedCoords);
    }
}

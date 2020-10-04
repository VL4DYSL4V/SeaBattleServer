package game.moveStrategy;

import game.entity.Coordinates;

import java.util.Collection;

public interface MoveStrategy {

    Coordinates getCoordinates();

    Collection<Coordinates> getUsedCoords();
}

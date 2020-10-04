package game.service;

import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.Placement;
import game.util.CoordinateCalculator;

import java.util.*;

public class FieldGenerator implements FieldGeneratorService {

    private Collection<Ship> occupants = new HashSet<>();
    private final Collection<Coordinates> unavailableCoordinates = new HashSet<>();

    @Override
    public Collection<Ship> createShips() {
        occupants = new HashSet<>();
        createShips(4, 1);
        createShips(3, 2);
        createShips(2, 3);
        createShips(1, 4);
        unavailableCoordinates.clear();
        return occupants;
    }

    private void createShips(int deckAmount, int shipAmount) {
        for (int i = 0; i < shipAmount; i++) {
            Placement placement = calculatePlacement();
            int maxX = CoordinateCalculator.maxLeftCornerX(placement, deckAmount);
            int maxY = CoordinateCalculator.maxLeftCornerY(placement, deckAmount);
            Coordinates coordinates = CoordinateCalculator.simpleCoordinatesCalculation(maxX, maxY);
            Ship ship = new Ship(deckAmount, placement, coordinates);
            makeShipCoordsCorrect(ship);
            occupants.add(ship);
            Collection<Coordinates> occupied = CoordinateCalculator.calculateCoordsAround(ship);
            unavailableCoordinates.addAll(occupied);
            unavailableCoordinates.addAll(ship.getOccupiedCoordinates());
        }
    }

    private void makeShipCoordsCorrect(Ship ship) {
        if (unavailableCoordinates.isEmpty()) {
            return;
        }
        if (crossesUnavailableArea(ship)) {
            Ship proxy = ship.clone();
            for (int i = 0; i < 5; i++) {
                Coordinates coordinates = CoordinateCalculator.simpleCoordinatesCalculation(
                        CoordinateCalculator.maxLeftCornerX(proxy.getPlacement(), proxy.getDeckAmount()),
                                CoordinateCalculator.maxLeftCornerY(proxy.getPlacement(), proxy.getDeckAmount()));
                proxy.setLeftUpperCornerCoordinates(coordinates);
                if (!crossesUnavailableArea(proxy)) {
                    ship.setLeftUpperCornerCoordinates(coordinates);
                    return;
                }
            }

            Coordinates coordinates = CoordinateCalculator.calculateLeftCornerCoordinates(
                    Collections.unmodifiableCollection(this.unavailableCoordinates), ship);
            ship.setLeftUpperCornerCoordinates(coordinates);
        }
        if (crossesUnavailableArea(ship)) {
            throw new IllegalStateException("Can't place the boat");
        }
    }

    private boolean crossesUnavailableArea(Ship ship) {
        Collection<Coordinates> occupiedCoords = ship.getOccupiedCoordinates();
        return occupiedCoords.stream().anyMatch(unavailableCoordinates::contains);
    }

    private static Placement calculatePlacement() {
        return new Random().nextInt(100) >= 50 ? Placement.VERTICAL : Placement.HORIZONTAL;
    }
}

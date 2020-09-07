package service;

import data.Coordinates;
import data.Ship;
import enums.Placement;
import util.CoordinateCalculator;
import util.CoordinatesPosition;

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
            int maxX = Ship.calculateMaxLeftCornerX(placement, deckAmount);
            int maxY = Ship.calculateMaxLeftCornerY(placement, deckAmount);
            Coordinates coordinates = CoordinateCalculator.simpleCoordinatesCalculation(maxX, maxY);
            Ship ship = new Ship(deckAmount, placement, coordinates);
            makeShipCoordsCorrect(ship);
            occupants.add(ship);
            Collection<Coordinates> occupied = CoordinateCalculator.calculateCoordsAround(ship);
            unavailableCoordinates.addAll(occupied);
            unavailableCoordinates.addAll(ship.getAllOccupiedCoordinates());
        }
    }

    private void makeShipCoordsCorrect(Ship ship) {
        if (unavailableCoordinates.isEmpty()) {
            return;
        }
        if (crossesUnavailableArea(ship)) {
            for (int i = 0; i < 5; i++) {
                Ship proxy = ship.clone();
                Coordinates coordinates = CoordinateCalculator
                        .simpleCoordinatesCalculation(proxy.getMaxLeftCornerX(), proxy.getMaxLeftCornerY());
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
        Collection<Coordinates> occupiedCoords = ship.getAllOccupiedCoordinates();
        for (Coordinates c : occupiedCoords) {
            if (unavailableCoordinates.contains(c)) {
                return true;
            }
        }
        return false;
    }

    private static Placement calculatePlacement() {
        return new Random().nextInt(100) >= 50 ? Placement.VERTICAL : Placement.HORIZONTAL;
    }
}

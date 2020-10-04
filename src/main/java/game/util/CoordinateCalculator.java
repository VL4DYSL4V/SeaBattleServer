package game.util;

import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FieldConstraints;
import game.enums.Placement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class CoordinateCalculator {

    public static Coordinates simpleCoordinatesCalculation(Integer maxDesiredX, Integer maxDesiredY) {
        Random random = new Random();
        return new Coordinates(random.nextInt(maxDesiredX + 1),
                random.nextInt(maxDesiredY + 1));
    }

    public static Coordinates advancedCoordinatesCalculation(Collection<Coordinates> redundantCoords) {
        Coordinates coordinates;
        int chance = new Random().nextInt(4);
        switch (chance) {
            case 1:
                coordinates = generateLeftBottomCorner();
                while (redundantCoords.contains(coordinates)) {
                    coordinates = walkRightAndUp(coordinates, FieldConstraints.MIN_X.getValue());
                    if (CoordinatesPosition.isRightUpperCorner(coordinates)) {
                        return coordinates;
                    }
                }
                break;
            case 2:
                coordinates = generateRightUpperCorner();
                while (redundantCoords.contains(coordinates)) {
                    coordinates = walkLeftAndDown(coordinates, FieldConstraints.MAX_X.getValue());
                    if (CoordinatesPosition.isLeftBottomCorner(coordinates)) {
                        return coordinates;
                    }
                }
                break;
            case 3:
                coordinates = generateRightBottomCorner();
                while (redundantCoords.contains(coordinates)) {
                    coordinates = walkLeftAndUp(coordinates, FieldConstraints.MAX_X.getValue());
                    if (CoordinatesPosition.isLeftUpperCorner(coordinates)) {
                        return coordinates;
                    }
                }
                break;
            default:
                coordinates = generateLeftUpperCorner();
                while (redundantCoords.contains(coordinates)) {
                    coordinates = walkRightAndDown(coordinates, FieldConstraints.MIN_X.getValue());
                    if (CoordinatesPosition.isRightBottomCorner(coordinates)) {
                        return coordinates;
                    }
                }
                break;
        }
        return coordinates;
    }

    public static Coordinates walkRightAndDown(Coordinates from, Integer where2StartNewRowX) {
        if (from.getX() < FieldConstraints.MAX_X.getValue()) {
            return walkRight(from);
        } else if (from.getY() < FieldConstraints.MAX_Y.getValue()) {
            return new Coordinates(where2StartNewRowX, from.getY() + 1);
        } else {
            throw new IllegalStateException(from.toString());
        }
    }

    public static Coordinates walkLeftAndDown(Coordinates from, Integer where2StartNewRowX) {
        if (from.getX() > FieldConstraints.MIN_X.getValue()) {
            return walkLeft(from);
        } else if (from.getY() < FieldConstraints.MAX_Y.getValue()) {
            return new Coordinates(where2StartNewRowX, from.getY() + 1);
        } else {
            throw new IllegalStateException(from.toString());
        }
    }

    public static Coordinates walkLeftAndUp(Coordinates from, Integer where2StartNewRowX) {
        if (from.getX() > FieldConstraints.MIN_X.getValue()) {
            return walkLeft(from);
        } else if (from.getY() > FieldConstraints.MIN_Y.getValue()) {
            return new Coordinates(where2StartNewRowX, from.getY() - 1);
        } else {
            throw new IllegalStateException(from.toString());
        }
    }

    public static Coordinates walkRightAndUp(Coordinates from, Integer where2StartNewRowX) {
        if (from.getX() < FieldConstraints.MAX_X.getValue()) {
            return walkRight(from);
        } else if (from.getY() > FieldConstraints.MIN_Y.getValue()) {
            return new Coordinates(where2StartNewRowX, from.getY() - 1);
        } else {
            throw new IllegalStateException(from.toString());
        }
    }

    public static Collection<Coordinates> calculateCoordsAround(Ship ship) {
        Collection<Coordinates> out;
        if (ship.getPlacement() == Placement.HORIZONTAL) {
            out = calculateCoordsAroundHorizontal(ship.getLeftUpperCornerCoordinates(), ship.getDeckAmount());
        } else {
            out = calculateCoordsAroundVertical(ship.getLeftUpperCornerCoordinates(), ship.getDeckAmount());
        }
        out.removeIf(CoordinatesPosition::inInvalidPosition);
        return out;
    }

    private static Collection<Coordinates> calculateCoordsAroundHorizontal(Coordinates leftCornerCoordinates, int deckAmount) {
        Collection<Coordinates> out = new HashSet<>();
        int count = deckAmount + 2;
        for (int i = 0; i < count; i++) {
            out.add(new Coordinates(leftCornerCoordinates.getX() + i - 1,
                    leftCornerCoordinates.getY() - 1));
            out.add(new Coordinates(leftCornerCoordinates.getX() + i - 1,
                    leftCornerCoordinates.getY() + 1));
        }
        out.add(new Coordinates(leftCornerCoordinates.getX() - 1, leftCornerCoordinates.getY()));
        out.add(new Coordinates(leftCornerCoordinates.getX() + deckAmount, leftCornerCoordinates.getY()));
        return out;
    }

    private static Collection<Coordinates> calculateCoordsAroundVertical(Coordinates leftUpperCornerCoordinates, int deckAmount) {
        Collection<Coordinates> out = new HashSet<>();
        int count = deckAmount + 2;
        for (int j = 0; j < count; j++) {
            out.add(new Coordinates(leftUpperCornerCoordinates.getX() - 1,
                    leftUpperCornerCoordinates.getY() + j - 1));
            out.add(new Coordinates(leftUpperCornerCoordinates.getX() + 1,
                    leftUpperCornerCoordinates.getY() + j - 1));
        }
        out.add(new Coordinates(leftUpperCornerCoordinates.getX(), leftUpperCornerCoordinates.getY() - 1));
        out.add(new Coordinates(leftUpperCornerCoordinates.getX(), leftUpperCornerCoordinates.getY() + deckAmount));
        return out;
    }

    public static Coordinates calculateLeftCornerCoordinates(Collection<Coordinates> unavailableCoordinates,
                                                             Ship ship) {
        Coordinates coordinates = null;
        Ship proxy = ship.clone();
        for (int x = FieldConstraints.MIN_X.getValue(); x <= maxLeftCornerX(proxy.getPlacement(), proxy.getDeckAmount()); x++) {
            for (int y = FieldConstraints.MIN_Y.getValue(); y <= maxLeftCornerY(proxy.getPlacement(), proxy.getDeckAmount()); y++) {
                coordinates = new Coordinates(x, y);
                proxy.setLeftUpperCornerCoordinates(coordinates);
                Collection<Coordinates> proxyAllOccupied = proxy.getOccupiedCoordinates();
                boolean coordsAreOk = true;
                for (Coordinates c : proxyAllOccupied) {
                    if (unavailableCoordinates.contains(c)) {
                        coordsAreOk = false;
                        break;
                    }
                }
                if (coordsAreOk) {
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    public static ArrayList<Coordinates> calculateNextPossibleUseful(Coordinates firstDamaged, Coordinates lastDamaged) {
        Coordinates nextUsefulFirst = null;
        Coordinates nextUsefulSecond = null;
        if (lastDamaged.getX().equals(firstDamaged.getX())) {
            if (lastDamaged.getY() < firstDamaged.getY()) {
                if (!CoordinatesPosition.isOnUpperEdge(lastDamaged)) {
                    nextUsefulFirst = CoordinateCalculator.walkUp(lastDamaged);
                }
                if (!CoordinatesPosition.isOnBottomEdge(firstDamaged)) {
                    nextUsefulSecond = CoordinateCalculator.walkDown(firstDamaged);
                }
            } else {
                if (!CoordinatesPosition.isOnBottomEdge(lastDamaged)) {
                    nextUsefulFirst = CoordinateCalculator.walkDown(lastDamaged);
                }
                if (!CoordinatesPosition.isOnUpperEdge(firstDamaged)) {
                    nextUsefulSecond = CoordinateCalculator.walkUp(firstDamaged);
                }
            }
        } else if (lastDamaged.getY().equals(firstDamaged.getY())) {
            if (lastDamaged.getX() < firstDamaged.getX()) {
                if (!CoordinatesPosition.isOnLeftEdge(lastDamaged)) {
                    nextUsefulFirst = CoordinateCalculator.walkLeft(lastDamaged);
                }
                if (!CoordinatesPosition.isOnRightEdge(firstDamaged)) {
                    nextUsefulSecond = CoordinateCalculator.walkRight(firstDamaged);
                }
            } else {
                if (!CoordinatesPosition.isOnRightEdge(lastDamaged)) {
                    nextUsefulFirst = CoordinateCalculator.walkRight(lastDamaged);
                }
                if (!CoordinatesPosition.isOnLeftEdge(firstDamaged)) {
                    nextUsefulSecond = CoordinateCalculator.walkLeft(firstDamaged);
                }
            }
        } else {
            throw new IllegalArgumentException("Arguments must be in the same column or row");
        }
        ArrayList<Coordinates> out = new ArrayList<>(2);
        if (nextUsefulFirst != null) {
            out.add(nextUsefulFirst);
        }
        if (nextUsefulSecond != null) {
            out.add(nextUsefulSecond);
        }
        return out;
    }

    public static Coordinates walkRight(Coordinates from) {
        return new Coordinates(from.getX() + 1, from.getY());
    }

    public static Coordinates walkLeft(Coordinates from) {
        return new Coordinates(from.getX() - 1, from.getY());
    }

    public static Coordinates walkUp(Coordinates from) {
        return new Coordinates(from.getX(), from.getY() - 1);
    }

    public static Coordinates walkDown(Coordinates from) {
        return new Coordinates(from.getX(), from.getY() + 1);
    }

    public static ArrayList<Coordinates> generateSWENneighbours(Coordinates coordinates) {
        ArrayList<Coordinates> out = new ArrayList<>(6);
        out.add(new Coordinates(coordinates.getX(), coordinates.getY() - 1));
        out.add(new Coordinates(coordinates.getX() + 1, coordinates.getY()));
        out.add(new Coordinates(coordinates.getX(), coordinates.getY() + 1));
        out.add(new Coordinates(coordinates.getX() - 1, coordinates.getY()));
        out.removeIf(CoordinatesPosition::inInvalidPosition);
        return out;
    }

    public static Coordinates generateLeftUpperCorner() {
        return new Coordinates(FieldConstraints.MIN_X.getValue(), FieldConstraints.MIN_Y.getValue());
    }

    public static Coordinates generateRightBottomCorner() {
        return new Coordinates(FieldConstraints.MAX_X.getValue(), FieldConstraints.MAX_Y.getValue());
    }

    public static Coordinates generateRightUpperCorner() {
        return new Coordinates(FieldConstraints.MAX_X.getValue(), FieldConstraints.MIN_Y.getValue());
    }

    public static Coordinates generateLeftBottomCorner() {
        return new Coordinates(FieldConstraints.MIN_X.getValue(), FieldConstraints.MAX_Y.getValue());
    }

    public static Integer maxLeftCornerX(Placement placement, Integer deckAmount) {
        return (placement == Placement.HORIZONTAL)
                ? FieldConstraints.MAX_X.getValue() - deckAmount + 1
                : FieldConstraints.MAX_X.getValue();
    }

    public static Integer maxLeftCornerY(Placement placement, Integer deckAmount) {
        return (placement == Placement.HORIZONTAL)
                ? FieldConstraints.MAX_Y.getValue()
                : FieldConstraints.MAX_Y.getValue() - deckAmount + 1;
    }
}


package util;

import data.Coordinates;
import enums.FieldConstraints;

import java.util.Objects;

public class CoordinatesPosition {

    private static final Coordinates leftUpperCorner = CoordinateCalculator.generateLeftUpperCorner();
    private static final Coordinates rightUpperCorner = CoordinateCalculator.generateRightUpperCorner();
    private static final Coordinates leftBottomCorner = CoordinateCalculator.generateLeftBottomCorner();
    private static final Coordinates rightBottomCorner = CoordinateCalculator.generateRightBottomCorner();

    public static boolean inInvalidPosition(Coordinates coords) {
        return coords.getX() < FieldConstraints.MIN_X.getValue() || coords.getX() > FieldConstraints.MAX_X.getValue()
                || coords.getY() < FieldConstraints.MIN_Y.getValue() || coords.getY() > FieldConstraints.MAX_Y.getValue();
    }

    public static boolean isOnLeftEdge(Coordinates coordinates) {
        return coordinates.getX().equals(FieldConstraints.MIN_X.getValue());
    }

    public static boolean isOnRightEdge(Coordinates coordinates) {
        return coordinates.getX().equals(FieldConstraints.MAX_X.getValue());
    }

    public static boolean isOnUpperEdge(Coordinates coordinates) {
        return coordinates.getY().equals(FieldConstraints.MIN_Y.getValue());
    }

    public static boolean isOnBottomEdge(Coordinates coordinates) {
        return coordinates.getY().equals(FieldConstraints.MAX_Y.getValue());
    }

    public static boolean isLeftUpperCorner(Coordinates coordinates) {
        return Objects.equals(coordinates, leftUpperCorner);
    }

    public static boolean isRightUpperCorner(Coordinates coordinates) {
        return Objects.equals(coordinates, rightUpperCorner);
    }

    public static boolean isLeftBottomCorner(Coordinates coordinates) {
        return Objects.equals(coordinates, leftBottomCorner);
    }

    public static boolean isRightBottomCorner(Coordinates coordinates) {
        return Objects.equals(coordinates, rightBottomCorner);
    }

    public static boolean onSomeEdge(Coordinates coordinates) {
        return isOnLeftEdge(coordinates) || isOnUpperEdge(coordinates)
                || isOnRightEdge(coordinates) || isOnBottomEdge(coordinates);
    }

    public static boolean onSomeCorner(Coordinates coordinates) {
        return isLeftUpperCorner(coordinates) || isLeftBottomCorner(coordinates)
                || isRightUpperCorner(coordinates) || isRightBottomCorner(coordinates);
    }
}

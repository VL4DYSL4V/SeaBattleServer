package printer;

import data.Coordinates;
import data.Ship;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Printer {
    public static void prettyFieldPrinter(Collection<Ship> occupants) {
        String[][] field = new String[10][10];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = "-";
            }
        }
        occupants.forEach((ship) -> {
            Collection<Coordinates> coordinates = ship.getAllOccupiedCoordinates();
            coordinates.forEach(currCoords -> field[currCoords.getY()][currCoords.getX()] = "+");
        });
        StringBuilder sb = new StringBuilder(110);
        for (String[] strings : field) {
            sb.append("\n");
            sb.append(Arrays.toString(strings));
        }
        System.out.println(sb);
    }

    public static void prettyFieldPrinter(Collection<Ship> player1ships, Collection<Ship> player2ships, Collection<Coordinates> usedCoordinates) {
        String[][] field1 = new String[10][10];
        String[][] field2 = new String[10][10];
        for (int i = 0; i < field1.length; i++) {
            for (int j = 0; j < field1[0].length; j++) {
                field1[i][j] = "-";
                field2[i][j] = "-";
            }
        }
        usedCoordinates.forEach(coordinates -> field2[coordinates.getY()][coordinates.getX()] = " ");
        player1ships.forEach((ship) -> {
            Collection<Coordinates> coordinates = ship.getAllOccupiedCoordinates();
            coordinates.forEach(currCoords ->
                    field1[currCoords.getY()][currCoords.getX()] = "+");
        });
        player2ships.forEach((ship) -> {
            Collection<Coordinates> coordinates = ship.getAllOccupiedCoordinates();
            coordinates.forEach(currCoords -> field2[currCoords.getY()][currCoords.getX()] = "+");
        });
        StringBuilder sb = new StringBuilder(210);
        for (int i = 0; i < 10; i++) {
            sb.append("\n");
            sb.append(Arrays.toString(field1[i]));
            sb.append("\t\t|\t\t");
            sb.append(Arrays.toString(field2[i]));
        }
        System.out.println(sb);
    }
}

package game.moveStrategy;

import game.entity.Coordinates;
import game.enums.FieldConstraints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class ConsoleStrategy implements MoveStrategy, Serializable {

    private Collection<Coordinates> usedCoordinates = new HashSet<>(50);
    private Scanner sc = new Scanner(System.in);

    private static final long serialVersionUID = -5519681931623805705L;

    public ConsoleStrategy() {
    }

    @Override
    public Coordinates getCoordinates() {
        Coordinates out;
        boolean isUsed;
        do {
            String input = askCoordinates();
            out = Coordinates.parse(input);
            isUsed = usedCoordinates.contains(out);
            if (isUsed) {
                System.out.println("Coordinate is used. Please, try again");
            }
        } while (isUsed);
        usedCoordinates.add(out);
        return out;
    }

    private String askCoordinates() {
        String input;
        do {
            System.out.println("Input coordinates in 'x - y' format, where\n x is "
                    + FieldConstraints.MIN_X.getValue() + " - " + FieldConstraints.MAX_X.getValue() + " and\n y is "
                    + FieldConstraints.MIN_Y.getValue() + " - " + FieldConstraints.MAX_Y.getValue() + " including.");
            input = sc.nextLine().trim().toLowerCase();
        } while (!input.matches("[" + FieldConstraints.MIN_X.getValue() +
                "-" + FieldConstraints.MAX_X.getValue() + "] - [" + FieldConstraints.MIN_Y.getValue() +
                "-" + FieldConstraints.MAX_Y.getValue() + "]"));
        return input;
    }

    @Override
    public Collection<Coordinates> getUsedCoords() {
        return Collections.unmodifiableCollection(usedCoordinates);
    }
}

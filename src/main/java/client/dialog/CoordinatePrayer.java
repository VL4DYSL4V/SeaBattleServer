package client.dialog;

import client.exception.SessionInterruption;
import game.entity.Coordinates;
import game.enums.FieldConstraints;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;

public class CoordinatePrayer implements CoordinateSupplier, ExitAware, Serializable {

    private Collection<Coordinates> usedCoordinates = new HashSet<>(50);
    private Scanner sc = new Scanner(System.in);

    private static final long serialVersionUID = -5519681931623805705L;

    public CoordinatePrayer() {
    }

    @Override
    public Coordinates getCoordinates() throws SessionInterruption {
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

    private String askCoordinates() throws SessionInterruption {
        String input;
        do {
            System.out.println("Input coordinates in 'x - y' format, where x is "
                    + FieldConstraints.MIN_X.getValue() + " - " + FieldConstraints.MAX_X.getValue() + " and y is "
                    + FieldConstraints.MIN_Y.getValue() + " - " + FieldConstraints.MAX_Y.getValue() + " including. " +
                    "To exit, input 'exit'.");
            input = sc.nextLine().trim().toLowerCase();
            if(Objects.equals("exit", input)){
                handleExit();
            }
        } while (!input.matches("[" + FieldConstraints.MIN_X.getValue() +
                "-" + FieldConstraints.MAX_X.getValue() + "] - [" + FieldConstraints.MIN_Y.getValue() +
                "-" + FieldConstraints.MAX_Y.getValue() + "]"));
        return input;
    }

}

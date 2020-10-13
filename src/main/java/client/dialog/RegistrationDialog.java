package client.dialog;

import client.exception.SessionInterruption;
import game.enums.Level;
import game.enums.Opponent;

import java.util.Objects;
import java.util.Scanner;

public class RegistrationDialog implements ExitAware {

    private final Scanner sc = new Scanner(System.in);

    public String getName() throws SessionInterruption {
        System.out.println("Input name. Input 'exit' to exit");
        String input = sc.nextLine();
        if (Objects.equals("exit", input.trim().toLowerCase())) {
            handleExit();
        }
        return input;
    }

    public Level getLevel() throws SessionInterruption {
        String input;
        while (true) {
            System.out.println("Input level: laboratory, simple or advanced. Input 'exit' to exit");
            input = sc.nextLine().trim().toLowerCase();
            if (Objects.equals("exit", input)) {
                handleExit();
            } else if (Objects.equals("laboratory", input)) {
                return Level.LABORATORY;
            } else if (Objects.equals("simple", input)) {
                return Level.SIMPLE;
            } else if (Objects.equals("advanced", input)) {
                return Level.ADVANCED;
            }
        }
    }

    public Opponent getOpponent() throws SessionInterruption {
        String input;
        while (true) {
            System.out.println("Input opponent: server or human. Input 'exit' to exit");
            input = sc.nextLine().trim().toLowerCase();
            if (Objects.equals("exit", input)) {
                handleExit();
            } else if (Objects.equals("server", input)) {
                return Opponent.SERVER;
            } else if (Objects.equals("human", input)) {
                return Opponent.ANOTHER_CLIENT;
            }
        }
    }
}

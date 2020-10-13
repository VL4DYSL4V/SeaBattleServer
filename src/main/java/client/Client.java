package client;

import client.dialog.CoordinateSupplier;
import client.dialog.RegistrationDialog;
import client.exception.SessionInterruption;
import client.model.Model;
import game.enums.FiringResult;
import game.enums.Opponent;
import enums.ResponseType;
import exception.UnknownResponseException;
import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.Level;
import client.dialog.CoordinatePrayer;
import protocol.Request;
import protocol.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Client implements Runnable {

    private static final int SERVERS_SERVER_PORT = 7788;
    private static final int SERVERS_CLIENT_PORT = 7765;
    private volatile boolean gameOver = false;
    private volatile boolean registrationSucceeded = false;
    private final Object registration_lock = new Object();
    private Model model = new Model();

    @Override
    public void run() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SERVERS_CLIENT_PORT)) {
                while (true) {
                    runGettingResponses(serverSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        RegistrationDialog registrationDialog = new RegistrationDialog();
        String name = null;
        Level level;
        Opponent opponent;
        try {
            do {
                name = registrationDialog.getName();
                level = registrationDialog.getLevel();
                opponent = registrationDialog.getOpponent();
                Request request = Request.registrationRequest(name, level, opponent);
                synchronized (registration_lock) {
                    sendRequest(request);
                    registration_lock.wait();
                }
            } while (!registrationSucceeded);
            CoordinateSupplier coordinatePrayer = new CoordinatePrayer();
            while (!gameOver) {
                Coordinates coordinates = coordinatePrayer.getCoordinates();
                sendRequest(Request.moveRequest(name, coordinates));
            }
        } catch (Exception e) {
            if (!(e instanceof SessionInterruption)) {
                e.printStackTrace();
            }
        } finally {
            if (registrationSucceeded) {
                Request request = Request.exitMessage(name);
                sendRequest(request);
            }
        }
    }

    private void runGettingResponses(ServerSocket serverSocket) {
        try (Socket socket = serverSocket.accept()) {
            try {
                Response response = getMessage(socket);
                handleResponse(response);
            } catch (IOException | ClassNotFoundException |
                    UnknownResponseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response getMessage(Socket socket) throws IOException, ClassNotFoundException, UnknownResponseException {
        Response response;
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(
                             new BufferedInputStream(
                                     socket.getInputStream()))) {
            Object input = objectInputStream.readObject();
            if (!(input instanceof Response)) {
                throw new UnknownResponseException("Unknown response");
            }
            response = (Response) input;
        }
        return response;
    }

    private void sendRequest(Request request) {
        try (Socket socket = new Socket("127.0.0.1", SERVERS_SERVER_PORT);
             ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new BufferedOutputStream(
                                     socket.getOutputStream()))) {
            oos.writeObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(Response response) {
        if (response.getResponseType() == ResponseType.SUCCESSFUL_REGISTRATION) {
            handleSuccessfulRegistration(response);
        } else if (response.getResponseType() == ResponseType.FAILED_REGISTRATION) {
            handleFailedRegistration(response);
        } else if (response.getResponseType() == ResponseType.MOVE_RESPONSE) {
            handleGettingDamage(response);
        } else if (response.getResponseType() == ResponseType.ATTACK_RESULT) {
            handleAttackResult(response);
        } else if (response.getResponseType() == ResponseType.GAME_OVER) {
            this.gameOver = true;
        } else if (response.getResponseType() == ResponseType.STATISTICS) {
            System.out.println(response.getAttribute("statistics"));
        } else {
            System.out.println(response);
        }
    }

    private void handleAttackResult(Response response) {
        model.handleAttackResult((FiringResult) response.getAttribute("firingResult"));
    }

    private void handleGettingDamage(Response response) {
        Coordinates coordinates = (Coordinates) response.getAttribute("coordinates");
        if (coordinates == null) return;
        model.getDamage(coordinates);
    }

    @SuppressWarnings("unchecked")
    private void handleSuccessfulRegistration(Response response) {
        synchronized (registration_lock) {
            model.setMyShips((Collection<Ship>) response.getAttribute("ships"));
            this.registrationSucceeded = true;
            registration_lock.notifyAll();
        }
    }

    private void handleFailedRegistration(Response response) {
        synchronized (registration_lock) {
            System.out.println(response);
            registration_lock.notifyAll();
        }
    }
}

package server;

import dao.DAO;
import dao.HardCodeDAO;
import entity.Pair;
import game.enums.Opponent;
import enums.ResponseType;
import exception.NoSuchPlayerException;
import exception.UnknownRequestException;
import game.entity.Ship;
import game.entity.battle.Battle;
import game.entity.Coordinates;
import game.enums.FiringResult;
import game.enums.Level;
import game.exception.GameOverException;
import game.util.Printer;
import protocol.Request;
import enums.RequestType;
import protocol.Response;
import util.RegistrationUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private static final int SERVER_PORT = 7788;
    private static final int CLIENT_PORT = 7765;
    private static final DAO GAME_DAO = new HardCodeDAO();
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(100);

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                final Socket socket = serverSocket.accept();
//                Runnable task = new Runnable() {
//                    @Override
//                    public void run() {
                Request request = new Request();
                try {
                    request = getMessage(socket);
//                    System.out.println("Handling" + request + Thread.currentThread().getName());
                    handleRequest(request);
                } catch (IOException | ClassNotFoundException | UnknownRequestException e) {
                    e.printStackTrace();
                } catch (NoSuchPlayerException e) {
                    sendMessage(Response.dialogResponse(request.getFromWhom(), e.getMessage()));
                }
//                    }
//                };
//                EXECUTOR.execute(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Request getMessage(Socket socket) throws IOException, ClassNotFoundException, UnknownRequestException {
        Request request;
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(
                             new BufferedInputStream(
                                     socket.getInputStream()))) {
            Object input = objectInputStream.readObject();
            if (!(input instanceof Request)) {
                throw new UnknownRequestException("Unknown request");
            }
            request = (Request) input;
        }
        return request;
    }

    private void sendMessage(Response response) {
        try (Socket socket = new Socket("127.0.0.1", CLIENT_PORT);
             ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new BufferedOutputStream(
                                     socket.getOutputStream()))) {
            oos.writeObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void handleRequest(Request request) throws NoSuchPlayerException {
        if (request.getRequestType() == RequestType.MAKE_MOVE) {
            try {
                handleMove(request);
            } catch (GameOverException e) {
                handleEndOfSession(request.getFromWhom());
            }
        } else if (request.getRequestType() == RequestType.REGISTRATION) {
            register(request);
        } else {
            handleOtherRequests(request);
        }
    }

    private void register(Request request) {
        String name = request.getFromWhom();
        if (GAME_DAO.userExists(name)) {
            sendMessage(new Response(request.getFromWhom(), ResponseType.FAILED_REGISTRATION));
        }
        Opponent opponent = (Opponent) request.getAttribute("opponent");
        Collection<Ship> clientShips;
        if (opponent == Opponent.SERVER) {
            clientShips = RegistrationUtil.hookUpWithServer(name, (Level) request.getAttribute("level"), GAME_DAO);
        } else {
            if (GAME_DAO.someoneIsWaiting()) {
                clientShips = RegistrationUtil.hookUpWithPlayer(name, GAME_DAO);
            } else {
                clientShips = RegistrationUtil.putInQueue(name, GAME_DAO);
            }
        }
        sendMessage(Response.successfulRegistrationResponse(name, clientShips));
    }

    private void handleMove(Request moveRequest) throws NoSuchPlayerException, GameOverException {
        String name = moveRequest.getFromWhom();
        Battle battle = GAME_DAO.getBattle(GAME_DAO.getRegisteredPair(name));
        Coordinates receivedCoordinates = (Coordinates) moveRequest.getAttribute("coordinates");

        if (battle.isServerClientBattle()) {
            handleAttackOnServer(battle, receivedCoordinates, name);
        } else if (battle.isClientClientBattle()) {
            handleAttackOnAnotherClient(battle, receivedCoordinates, name);
        }
        Printer.printPretty(battle);
    }

    //TODO: it is not even tested yet
    private void handleAttackOnAnotherClient(Battle battle, Coordinates coordinates, String name)
            throws GameOverException, NoSuchPlayerException {
        Pair<String> users = GAME_DAO.getRegisteredPair(name);
        FiringResult firingResult;
        if (Objects.equals(users.getFirst(), name)) {
            firingResult = battle.shootAtSecondPlayer(coordinates);
            sendMessage(Response.attackResultResponse(users.getFirst(), firingResult));
            sendMessage(Response.attackResultResponse(users.getSecond(), firingResult));
        } else if (Objects.equals(users.getSecond(), name)) {
            firingResult = battle.shootAtFirstPlayer(coordinates);
            sendMessage(Response.attackResultResponse(users.getFirst(), firingResult));
            sendMessage(Response.attackResultResponse(users.getSecond(), firingResult));
        }

    }

    private void handleAttackOnServer(Battle battle, Coordinates coordinates, String name)
            throws GameOverException, NoSuchPlayerException {
        FiringResult clientsFiringResult = battle.shootAtSecondPlayer(coordinates);
        sendMessage(Response.attackResultResponse(name, clientsFiringResult));
        if (clientsFiringResult == FiringResult.MISSED) {
            FiringResult serversFiringResult;
            do {
                Coordinates serverFiringCoordinates = null;
                if (battle.getServerMoveStrategy() != null) {
                    serverFiringCoordinates = battle.getServerMoveStrategy().getCoordinates();
                }
                serversFiringResult = battle.shootAtFirstPlayer(serverFiringCoordinates);
                sendMessage(Response.moveResponse(name, serverFiringCoordinates));
            } while (serversFiringResult != FiringResult.MISSED);
        }
        if (battle.gameOver()) {
            handleEndOfSession(name);
        }
    }

    private void handleOtherRequests(Request request) throws NoSuchPlayerException {
        if (request.getRequestType() == RequestType.EXIT) {
            handleEndOfSession(request.getFromWhom());
        } else if (request.getRequestType() == RequestType.DIALOG_REQUEST) {
            sendMessage(new Response(request.getFromWhom(), ResponseType.DIALOG_RESPONSE));
        }
    }

    private void handleEndOfSession(String clientName) throws NoSuchPlayerException {
        Pair<String> users = GAME_DAO.getRegisteredPair(clientName);
        Battle battle = GAME_DAO.getBattle(users);
        sendGoodBye(users.getFirst(), battle.getStatisticsFirst());
        if (battle.isClientClientBattle()) {
            sendGoodBye(users.getSecond(), battle.getStatisticsSecond());
        }
        GAME_DAO.removeBattle(users);
    }

    private void sendGoodBye(String toWhom, Map<FiringResult, Integer> statistics) {
        sendMessage(new Response(toWhom, ResponseType.GAME_OVER));
        sendMessage(Response.statisticsResponse(toWhom, statistics));
    }

}

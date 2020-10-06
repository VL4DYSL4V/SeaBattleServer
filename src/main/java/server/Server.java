package server;

import entity.Pair;
import enums.Opponent;
import exception.NoSuchPlayerException;
import exception.SuchUserAlreadyExists;
import exception.UnknownRequestException;
import game.entity.battle.Battle;
import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FiringResult;
import game.enums.Level;
import game.enums.Turn;
import game.exception.GameOverException;
import game.service.FieldGenerator;
import protocol.Request;
import enums.RequestType;
import protocol.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

//TODO: create class ServerContext. Непраивльная регистрация: клентов-то двое в битве
public class Server {

    private static final int SERVER_PORT = 7788;
    private static final int CLIENT_PORT = 7765;
    private static final String SERVER_NAME = "SERVER";
    private static final Map<Pair<String>, Battle> DATA_MAP = new ConcurrentHashMap<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                try {
                    runServer(serverSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runServer(ServerSocket serverSocket) throws IOException {
        try (Socket socket = serverSocket.accept()) {
            new Thread(() -> {
                try {
                    Request request = getMessage(socket);
                    handleRequest(request);
                } catch (IOException | ClassNotFoundException |
                        SuchUserAlreadyExists | NoSuchPlayerException |
                        UnknownRequestException e) {
                    e.printStackTrace();
                }
            }).start();
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

    private void handleRequest(Request request) throws SuchUserAlreadyExists, NoSuchPlayerException {
        if (request.getRequestType() == RequestType.MAKE_MOVE) {
            handleMove(request);
        } else if (request.getRequestType() == RequestType.REGISTRATION) {
            register(request);
        } else {
            handleOtherRequests(request);
        }
    }

    //TODO: create client-client battle. I suppose, if client is the only one who is connected to
    // server, we send him into queue, and then the next one trying to play vs human should be
    // paired with the first one. Queue must be synchronized.
    private void register(Request request) throws SuchUserAlreadyExists {
        String name = request.getFromWhom();
        if (DATA_MAP.keySet().stream().anyMatch((key) -> Objects.equals(key.getFirst(), name) ||
                Objects.equals(key.getSecond(), name))) {
            throw new SuchUserAlreadyExists("Such user already exists!!");
        }
        Level level = (Level) request.getAttribute("level");
        Opponent opponent = (Opponent) request.getAttribute("opponent");
        if(opponent == Opponent.SERVER) {
            DATA_MAP.put(new Pair<>(name, SERVER_NAME), createClientServerBattle(level));
        }else{

        }
    }

    private Battle createClientClientBattle(){
        FieldGenerator fieldGenerator = new FieldGenerator();
        Collection<Ship> firstPlayerShips = fieldGenerator.createShips();
        Collection<Ship> secondPlayerShip = fieldGenerator.createShips();
        return Battle.clientClientBattle(firstPlayerShips, secondPlayerShip);
    }

    private Battle createClientServerBattle(Level level) {
        FieldGenerator fieldGenerator = new FieldGenerator();
        Collection<Ship> clientShips = fieldGenerator.createShips();
        Collection<Ship> serverShips = fieldGenerator.createShips();
        return Battle.clientServerBattle(clientShips, serverShips, level);
    }

    private void handleMove(Request moveRequest) throws NoSuchPlayerException {
        String name = moveRequest.getFromWhom();
        Pair<String> users = getRegisteredPair(name);
        Battle battle = DATA_MAP.get(users);
        Coordinates receivedCoordinates = (Coordinates) moveRequest.getAttribute("coordinates");

        if (battle.isServerClientBattle()) {
            try {
                handleAttackOnServer(battle, receivedCoordinates, name);
            } catch (GameOverException e) {
                handleEndOfSession(name);
            }
        } else if (battle.isClientClientBattle()) {
            try {
                handleAttackOnAnotherClient(battle, receivedCoordinates, name);
            } catch (GameOverException e) {
                handleEndOfSession(users.getFirst());
                handleEndOfSession(users.getSecond());
            }
        }

    }

    public void handleAttackOnAnotherClient(Battle battle, Coordinates coordinates, String name)
            throws GameOverException, NoSuchPlayerException {
        Pair<String> users = getRegisteredPair(name);
        FiringResult firingResult;
        if (Objects.equals(users.getFirst(), name) && battle.getTurn() == Turn.PLAYER_1) {
             firingResult = battle.shootAtSecondPlayer(coordinates);
            sendMessage(Response.attackResultResponse(users.getFirst(), firingResult));
            sendMessage(Response.attackResultResponse(users.getSecond(), firingResult));
        } else if (Objects.equals(users.getSecond(), name) && battle.getTurn() == Turn.PLAYER_2) {
            firingResult = battle.shootAtFirstPlayer(coordinates);
            sendMessage(Response.attackResultResponse(users.getFirst(), firingResult));
            sendMessage(Response.attackResultResponse(users.getSecond(), firingResult));
        }

    }

    public void handleAttackOnServer(Battle battle, Coordinates coordinates, String name) throws GameOverException {
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
    }

    private void handleOtherRequests(Request request) throws NoSuchPlayerException {
        if (request.getRequestType() == RequestType.EXIT) {
            handleEndOfSession(request.getFromWhom());
        }
    }

    private void handleEndOfSession(String clientName) throws NoSuchPlayerException {
        Battle battle = getBattle(clientName);
        if (battle.isServerClientBattle()) {
            sendMessage(Response.statisticsResponse(clientName, battle.getStatisticsSecond()));
            return;
        }
        Pair<String> users = getRegisteredPair(clientName);
        sendMessage(Response.statisticsResponse(users.getFirst(), battle.getStatisticsFirst()));
        sendMessage(Response.statisticsResponse(users.getSecond(), battle.getStatisticsSecond()));
        removeBattle(clientName);
    }


    private Battle getBattle(String name) throws NoSuchPlayerException {
        return DATA_MAP.get(getRegisteredPair(name));
    }

    private void removeBattle(String name) throws NoSuchPlayerException {
        DATA_MAP.remove(getRegisteredPair(name));
    }

    private Pair<String> getRegisteredPair(String oneOfPair) throws NoSuchPlayerException {
        for (Pair<String> key : DATA_MAP.keySet()) {
            if (Objects.equals(key.getFirst(), oneOfPair) || Objects.equals(key.getSecond(), oneOfPair)) {
                return key;
            }
        }
        throw new NoSuchPlayerException("Player " + oneOfPair + " is not registered");
    }
}

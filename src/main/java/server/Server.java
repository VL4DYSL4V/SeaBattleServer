package server;

import data.UserData;
import exception.NoSuchUserException;
import exception.SuchUserAlreadyExists;
import exception.UnknownRequestException;
import game.battle.Battle;
import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FiringResult;
import game.enums.Level;
import game.service.FieldGenerator;
import protocol.Request;
import enums.RequestType;
import protocol.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final int SERVER_PORT = 7788;
    private static final Map<String, UserData> DATA_MAP = new ConcurrentHashMap<>();

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
                        SuchUserAlreadyExists | NoSuchUserException |
                        UnknownRequestException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private Request getMessage(Socket socket) throws IOException, ClassNotFoundException, UnknownRequestException{
        Request request;
        try (ObjectInputStream objectInputStream =
                     new ObjectInputStream(
                             new BufferedInputStream(
                                     socket.getInputStream()))) {
            Object input = objectInputStream.readObject();
            if(! (input instanceof Request)){
                throw new UnknownRequestException("Unknown request");
            }
            request = (Request) input;
        }
        return request;
    }

    private void sendMessage(String toWhom, Response response) {
        try (Socket socket = new Socket("127.0.0.1", DATA_MAP.get(toWhom).getPort());
             ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new BufferedOutputStream(
                                     socket.getOutputStream()))) {
            oos.writeObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Request request) throws SuchUserAlreadyExists, NoSuchUserException {
        if (request.getRequestType() == RequestType.MAKE_MOVE) {
            handleMove(request);
        } else if (request.getRequestType() == RequestType.REGISTRATION) {
            register(request);
        } else {
            handleOtherRequests(request);
        }
    }

    private void register(Request request) throws SuchUserAlreadyExists {
        if (DATA_MAP.containsKey(request.getFromWhom())) {
            throw new SuchUserAlreadyExists("Such user already exists!!");
        }
        FieldGenerator fieldGenerator = new FieldGenerator();
        UserData userData = new UserData((Integer) request.getAttribute("port"),
                createBattle((Level) request.getAttribute("level"), fieldGenerator));
        DATA_MAP.put(request.getFromWhom(), userData);
    }

    private Battle createBattle(Level level, FieldGenerator fieldGenerator) {
        Collection<Ship> clientShips = fieldGenerator.createShips();
        Collection<Ship> serverShips = fieldGenerator.createShips();

        return null;
    }

    private FiringResult handleMove(Request moveRequest) throws NoSuchUserException {
        if (!DATA_MAP.containsKey(moveRequest.getFromWhom())) {
            throw new NoSuchUserException("Such user doesn't exist!!");
        }
        Battle battle = DATA_MAP.get(moveRequest.getFromWhom()).getBattle();
        Coordinates clientCoordinates = (Coordinates) moveRequest.getAttribute("coordinates");


        return null;
    }

    private void handleOtherRequests(Request request) {
        String name = request.getFromWhom();
        if (request.getRequestType() == RequestType.EXIT) {
            Battle battle = DATA_MAP.get(name).getBattle();
            Map<String, Integer> statistics = battle.getStatisticsSecond();
            sendMessage(name, Response.statisticsResponse(name, statistics));
            DATA_MAP.remove(name);
        }
    }
}

package main;

import client.Client;
import game.entity.Coordinates;
import game.entity.Ship;
import game.entity.battle.Battle;
import game.enums.FiringResult;
import game.exception.GameOverException;
import game.moveStrategy.AdvancedServerMoveStrategy;
import game.moveStrategy.MoveStrategy;
import game.moveStrategy.SimpleServerMoveStrategy;
import game.util.Printer;
import server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.xml.bind.JAXBException;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;

//Current lowest amount of moves = 23 :D
public class Main {
    public static void main(String[] args) throws JAXBException{
        new Thread(new Server()).start();
        new Client().run();
    }

    private static void testStandardAdvancedBattle(){
        Battle battle = Battle.generateClientClientBattle();
        Collection<Ship> ships1 = battle.getFirstPlayerShips();
        Collection<Ship> ships2 = battle.getSecondPlayerShips();
        MoveStrategy moveStrategy1 = new AdvancedServerMoveStrategy(ships2);
//        MoveStrategy moveStrategy2 = new AdvancedServerMoveStrategy(ships1);
        MoveStrategy moveStrategy2 = new SimpleServerMoveStrategy();
        try {
            while (!battle.gameOver()) {
                FiringResult firingResult1;
                do {
                    Coordinates coordinates1 = moveStrategy1.getCoordinates();
                    firingResult1 = battle.shootAtSecondPlayer(coordinates1);
                } while (firingResult1 != FiringResult.MISSED);
                FiringResult firingResult2;
                do {
                    Coordinates coordinates2 = moveStrategy2.getCoordinates();
                    firingResult2 = battle.shootAtFirstPlayer(coordinates2);
                } while (firingResult2 != FiringResult.MISSED);
                Printer.printPretty(ships1, ships2, new ArrayList<>(), new ArrayList<>());
            }
        }catch (GameOverException e) {
            System.out.println("Winner: " + battle.getTurn());
            Printer.printPretty(ships1, ships2, new ArrayList<>(), new ArrayList<>());
            System.out.println("Statistics first: " + battle.getStatisticsFirst());
            System.out.println("Statistics second: " + battle.getStatisticsSecond());
        }
    }

    private static void server() throws IOException {
        // Selector: multiplexor of SelectableChannel objects
        Selector selector = Selector.open(); // selector is open here

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        ServerSocketChannel crunchifySocket = ServerSocketChannel.open();
        InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 1111);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        crunchifySocket.bind(crunchifyAddr);

        // Adjusts this channel's blocking mode.
        crunchifySocket.configureBlocking(false);

        int ops = crunchifySocket.validOps();
        SelectionKey selectKy = crunchifySocket.register(selector, ops, null);

        // Infinite loop..
        // Keep server running
        while (true) {

            log("i'm a server and i'm waiting for new connection and buffer select...");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> crunchifyKeys = selector.selectedKeys();
            Iterator<SelectionKey> crunchifyIterator = crunchifyKeys.iterator();

            while (crunchifyIterator.hasNext()) {
                SelectionKey myKey = crunchifyIterator.next();

                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                    SocketChannel crunchifyClient = crunchifySocket.accept();

                    // Adjusts this channel's blocking mode to false
                    crunchifyClient.configureBlocking(false);

                    // Operation-set bit for read operations
                    crunchifyClient.register(selector, SelectionKey.OP_READ);
                    log("Connection Accepted: " + crunchifyClient.getLocalAddress() + "\n");

                    // Tests whether this key's channel is ready for reading
                } else if (myKey.isReadable()) {

                    SocketChannel crunchifyClient = (SocketChannel) myKey.channel();
                    ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
                    crunchifyClient.read(crunchifyBuffer);
                    String result = new String(crunchifyBuffer.array()).trim();

                    log("Message received: " + result);

                    if (result.equals("Crunchify")) {
                        crunchifyClient.close();
                        log("\nIt's time to close connection as we got last company name 'Crunchify'");
                        log("\nServer will keep running. Try running client again to establish new connection");
                    }
                }
                crunchifyIterator.remove();
            }
        }
    }

    private static void client() throws IOException, InterruptedException {
        InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 1111);
        SocketChannel crunchifyClient = SocketChannel.open(crunchifyAddr);

        log("Connecting to Server on port 1111...");

        ArrayList<String> companyDetails = new ArrayList<>();

        // create a ArrayList with companyName list
        companyDetails.add("Facebook");
        companyDetails.add("Twitter");
        companyDetails.add("IBM");
        companyDetails.add("Google");
        companyDetails.add("Crunchify");

        for (String companyName : companyDetails) {

            byte[] message = companyName.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(message);
            crunchifyClient.write(buffer);

            log("sending: " + companyName);
            buffer.clear();

            // wait for 2 seconds before sending next message
            Thread.sleep(2000);
        }
        crunchifyClient.close();
    }

    private static void log(String str) {
        System.out.println(str);
    }

    //        File file = new File("battle.xml");
//        JAXBContext jaxbContext = JAXBContext.newInstance(battle.getClass(), Coordinates.class, Ship.class,
//                AdvancedServerBehaviour.class, ConsoleClientBehaviour.class, GameBehaviour.class, SimpleServerBehaviour.class);
//        Marshaller marshaller = jaxbContext.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//        marshaller.marshal(battle, file);
//        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//        battle = (Battle) unmarshaller.unmarshal(file);
}
 
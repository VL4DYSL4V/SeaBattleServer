package data;

import enums.PlayerType;
import printer.Printer;
import serverGameBehaviour.ServerBehaviour;

import java.util.Collection;

public class BattleWithServer {

    private final ServerBehaviour serverBehaviour;
    private PlayerType playerType = PlayerType.PLAYER_1;
    private final Collection<Ship> clientShips;
    private final Collection<Ship> serverShips;

    public BattleWithServer(ServerBehaviour serverBehaviour) {
        this.serverBehaviour = serverBehaviour;
        this.clientShips = serverBehaviour.getClientShips();
        this.serverShips = serverBehaviour.getServerShips();
    }

    public void play() {
        int count = 0;
        while(! isEndOfGame()) {
            serverBehaviour.makeMove();
            Printer.prettyFieldPrinter(serverShips, clientShips, serverBehaviour.getUsedCoords());
            count++;
        }
        System.out.println(count);
    }

    public boolean isEndOfGame() {
        return serverShips.isEmpty() || clientShips.isEmpty();
    }

}

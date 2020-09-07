package main;

import data.BattleWithServer;
import data.Ship;
import serverGameBehaviour.AdvancedServerBehaviour;
import serverGameBehaviour.ServerBehaviour;
import serverGameBehaviour.SimpleServerBehaviour;
import service.FieldGenerator;

import java.util.Collection;


public class Main {
    public static void main(String[] args) {
        FieldGenerator fieldGenerator = new FieldGenerator();
        Collection<Ship> clientShips = fieldGenerator.createShips();
        Collection<Ship> serverShips = fieldGenerator.createShips();
        ServerBehaviour serverBehaviour = new AdvancedServerBehaviour(serverShips, clientShips);
        BattleWithServer battleWithServer = new BattleWithServer(serverBehaviour);
        battleWithServer.play();
    }
}

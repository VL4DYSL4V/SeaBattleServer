package util;

import dao.DAO;
import entity.Pair;
import game.entity.Ship;
import game.entity.battle.Battle;
import game.enums.Level;

import java.util.Collection;

public class RegistrationUtil {

    public static Collection<Ship> hookUpWithServer(String name, Level level, DAO dao) {
        Battle battle = Battle.generateClientServerBattle(level);
        dao.addClientServerBattle(name, battle);
        return battle.getFirstPlayerShips();
    }

    public static Collection<Ship> putInQueue(String name, DAO dao){
        Battle battle = Battle.generateClientClientBattle();
        dao.putInQueue(name, battle);
        return battle.getFirstPlayerShips();
    }

    public static Collection<Ship> hookUpWithPlayer(String name, DAO dao) {
        String registered = dao.getWaiting();
        Battle battle = dao.getWaitingBattle(registered);
        dao.addClientClientBattle(new Pair<>(registered, name), battle);
        dao.removeFromQueue(name);
        return battle.getSecondPlayerShips();
    }
}

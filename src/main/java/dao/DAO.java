package dao;

import entity.Pair;
import exception.NoSuchPlayerException;
import game.entity.battle.Battle;

public interface DAO {

    boolean someoneIsWaiting();

    boolean userExists(String name);

    Pair<String> getRegisteredPair(String oneOfPair) throws NoSuchPlayerException;

    void addClientServerBattle(String name, Battle battle);

    void addClientClientBattle(Pair<String> users, Battle battle);

    void putInQueue(String name, Battle battle);

    void removeFromQueue(String name);

    String getWaiting();

    Battle getBattle(Pair<String> users);

    Battle getWaitingBattle(String name);

    void removeBattle(Pair<String> users);

}

package dao;

import entity.Pair;
import exception.NoSuchPlayerException;
import game.entity.battle.Battle;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class HardCodeDAO implements DAO {

    private final Map<Pair<String>, Battle> DATA_MAP = new ConcurrentHashMap<>();
    private final Map<String, Battle> WAITING_USERS = new ConcurrentHashMap<>();
    public static final String SERVER_NAME = "SERVER";

    @Override
    public boolean userExists(String name) {
        return DATA_MAP.keySet().stream().anyMatch((pair) -> Objects.equals(pair.getFirst(), name)
                || Objects.equals(pair.getSecond(), name))
                || WAITING_USERS.keySet().stream().anyMatch((user) ->Objects.equals(user, name));
    }

    @Override
    public boolean someoneIsWaiting() {
        return !WAITING_USERS.isEmpty();
    }

    @Override
    public Pair<String> getRegisteredPair(String oneOfPair) throws NoSuchPlayerException {
        for (Pair<String> pair : DATA_MAP.keySet()) {
            if (Objects.equals(pair.getFirst(), oneOfPair) || Objects.equals(pair.getSecond(), oneOfPair)) {
                return pair;
            }
        }
        throw new NoSuchPlayerException("Player " + oneOfPair + " is not registered");
    }

    @Override
    public void addClientServerBattle(String name, Battle battle) {
        DATA_MAP.put(new Pair<>(name, SERVER_NAME), battle);
    }

    @Override
    public void addClientClientBattle(Pair<String>users, Battle battle) {
        DATA_MAP.put(users, battle);
    }

    @Override
    public void putInQueue(String name, Battle battle){
        WAITING_USERS.put(name, battle);
    }

    @Override
    public void removeFromQueue(String name) {
        WAITING_USERS.remove(name);
    }

    @Override
    public String getWaiting() {
        return WAITING_USERS.keySet().iterator().next();
    }

    @Override
    public Battle getBattle(Pair<String> users){
        return DATA_MAP.get(users);
    }

    @Override
    public void removeBattle(Pair<String> users){
        DATA_MAP.remove(users);
    }

    @Override
    public Battle getWaitingBattle(String name){
        return WAITING_USERS.get(name);
    }

}

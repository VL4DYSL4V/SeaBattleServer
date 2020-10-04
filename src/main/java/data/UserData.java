package data;

import game.battle.Battle;
import java.util.Objects;

public class UserData {

    private int port;
    private Battle battle;

    public UserData(int port, Battle battle) {
        this.port = port;
        this.battle = battle;
    }

    public int getPort() {
        return port;
    }

    public Battle getBattle() {
        return battle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return port == userData.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port);
    }
}

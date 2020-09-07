package enums;

import java.io.Serializable;

public enum RequestType implements Serializable {

    REGISTER_ME("Register me"), EXIT("Exit"), END_THE_GAME("End the game");
//    , MATCH_WITH_PLAYER, MAKE_ANOTHER_MATCH, AGAIN_WITH_THIS_PLAYER;

    private String message;

    RequestType(String message) {
        this.message = message;
    }

    private static final long serialVersionUID = -6432063504437012866L;


}

package server;

import enums.ResponseType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResponseFromServer implements Serializable {

    private static final long serialVersionUID = -8147542540893653758L;
    private String toWhom;
    private Map<String, Object> requestAttributes = new HashMap<>();

    public ResponseFromServer(String toWhom) {
        this.toWhom = toWhom;
    }

    public String getToWhom() {
        return toWhom;
    }

    public void addAttributes(String paramName, Object o){
        
    }
}

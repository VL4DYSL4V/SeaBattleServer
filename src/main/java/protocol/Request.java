package protocol;

import game.enums.Opponent;
import game.entity.Coordinates;
import game.enums.Level;
import enums.RequestType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request implements Externalizable {

    private String fromWhom = "";
    private RequestType requestType = RequestType.DIALOG_REQUEST;
    private Map<String, Object> attributes = new HashMap<>();

    public Request() {
    }

    public Request(String fromWhom, RequestType requestType) {
        this.fromWhom = fromWhom;
        this.requestType = requestType;
    }

    public void addAttribute(String key, Object value){
        attributes.put(key, value);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public static Request exitMessage(String fromWhom) {
        return new Request(fromWhom, RequestType.EXIT);
    }

    public static Request registrationRequest(String whom, Level level, Opponent opponent) {
        Request out = new Request(whom, RequestType.REGISTRATION);
        out.addAttribute("level", level);
        out.addAttribute("opponent", opponent);
        return out;
    }

    public static Request moveRequest(String fromWhom, Coordinates coordinates){
        Request out = new Request(fromWhom, RequestType.MAKE_MOVE);
        out.addAttribute("coordinates", coordinates);
        return out;
    }

    public String getFromWhom() {
        return fromWhom;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(fromWhom);
        out.writeObject(requestType);
        out.writeObject(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fromWhom = (String) in.readObject();
        requestType = (RequestType) in.readObject();
        attributes = (Map<String, Object>) in.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(fromWhom, request.fromWhom) &&
                requestType == request.requestType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromWhom, requestType);
    }

    @Override
    public String toString() {
        return "Message{" +
                "fromWhom='" + fromWhom + '\'' +
                ", requestType=" + requestType +
                '}';
    }
}

package protocol;

import enums.ResponseType;
import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FiringResult;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Response implements Externalizable {

    private String toWhom = "";
    private ResponseType responseType = ResponseType.DIALOG_RESPONSE;
    private Map<String, Object> attributes = new HashMap<>();

    public Response(){}

    public Response(String toWhom, ResponseType responseType){
        this.toWhom = toWhom;
        this.responseType = responseType;
    }

    public void addAttribute(String key, Object value){
        attributes.put(key, value);
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public static Response statisticsResponse(String toWhom, Map<FiringResult, Integer> statistics){
        Response response = new Response(toWhom, ResponseType.STATISTICS);
        response.addAttribute("statistics", statistics);
        return response;
    }

    public static Response moveResponse(String toWhom, Coordinates coordinates){
        Response response = new Response(toWhom, ResponseType.MOVE_RESPONSE);
        response.addAttribute("coordinates", coordinates);
        return response;
    }

    public static Response attackResultResponse(String toWhom, FiringResult firingResult){
        Response response = new Response(toWhom, ResponseType.ATTACK_RESULT);
        response.addAttribute("firingResult", firingResult);
        return response;
    }

    public static Response dialogResponse(String toWhom, String message){
        Response response = new Response(toWhom, ResponseType.DIALOG_RESPONSE);
        response.addAttribute("message", message);
        return response;
    }

    public static Response successfulRegistrationResponse(String toWhom, Collection<Ship> ships){
        Response response = new Response(toWhom, ResponseType.SUCCESSFUL_REGISTRATION);
        response.addAttribute("ships", ships);
        return response;
    }

    public String getToWhom() {
        return toWhom;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(toWhom);
        out.writeObject(responseType);
        out.writeObject(attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        toWhom = (String) in.readObject();
        responseType = (ResponseType) in.readObject();
        attributes = (Map<String, Object>) in.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return Objects.equals(toWhom, response.toWhom) &&
                responseType == response.responseType &&
                Objects.equals(attributes, response.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toWhom, responseType, attributes);
    }

    @Override
    public String toString() {
        return "Response{" +
                "toWhom='" + toWhom + '\'' +
                ", responseType=" + responseType +
                ", attributes=" + attributes +
                '}';
    }
}

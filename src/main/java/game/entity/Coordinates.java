package game.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

//@XmlRootElement(name = "Coordinates")
public class Coordinates implements Serializable {
//    @XmlElement
    private Integer x = 0;
//    @XmlElement
    private Integer y = 0;

    private static final long serialVersionUID = -858568786997L;


    private Coordinates(){

    }

    public Coordinates(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public static Coordinates parse(String s){
        if(s == null || !s.matches("-?\\d+ - -?\\d+")){
            throw new IllegalArgumentException();
        }
        String[] coords = s.split(" - ");
        return new Coordinates(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x.equals(that.x) &&
                y.equals(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "ShipCoordinates {\n" +
                "\tleftCornerX = " + x +
                ", leftCornerY = " + y +
                "\n}";
    }
}

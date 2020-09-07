package data;

import enums.FieldConstraints;
import enums.Placement;

import java.util.*;

public class Ship implements Cloneable{

    private Integer deckAmount;
    private Placement placement;
    private Coordinates leftUpperCornerCoordinates;
    private final Collection<Coordinates> damagedCoordinates = new HashSet<>(4);
    private Collection<Coordinates> allOccupiedCoordinates;
    private final Integer maxLeftCornerX;
    private final Integer maxLeftCornerY;

    private Ship(Ship ship){
        this.deckAmount = ship.deckAmount;
        this.placement = ship.placement;
        this.leftUpperCornerCoordinates = ship.leftUpperCornerCoordinates;
        this.allOccupiedCoordinates = new HashSet<>(4);
        for(Coordinates c: ship.allOccupiedCoordinates){
            this.allOccupiedCoordinates.add(new Coordinates(c.getX(), c.getY()));
        }
        for(Coordinates c: ship.damagedCoordinates){
            this.damagedCoordinates.add(new Coordinates(c.getX(), c.getY()));
        }
        this.maxLeftCornerX = ship.maxLeftCornerX;
        this.maxLeftCornerY = ship.maxLeftCornerY;
    }

    public Ship(Integer deckAmount, Placement placement, Coordinates leftUpperCornerCoordinates) {
        this.deckAmount = deckAmount;
        this.placement = placement;
        this.leftUpperCornerCoordinates = leftUpperCornerCoordinates;
        this.allOccupiedCoordinates = calculateAllOccupiedCoords();
        this.maxLeftCornerX = calculateMaxLeftCornerX(placement, deckAmount);
        this.maxLeftCornerY = calculateMaxLeftCornerY(placement, deckAmount);
    }

    private Collection<Coordinates> calculateAllOccupiedCoords(){
        Collection<Coordinates> coordinates = new HashSet<>(6);
        coordinates.add(this.leftUpperCornerCoordinates);
        for (int i = 1; i < this.deckAmount; i++) {
            Coordinates occupiedCoordinates;
            if (this.placement == Placement.VERTICAL) {
                occupiedCoordinates = new Coordinates(this.leftUpperCornerCoordinates.getX(), this.leftUpperCornerCoordinates.getY() + i);
            } else {
                occupiedCoordinates = new Coordinates(this.leftUpperCornerCoordinates.getX() + i, this.leftUpperCornerCoordinates.getY());
            }
            coordinates.add(occupiedCoordinates);
        }
        return coordinates;
    }

    public static Integer calculateMaxLeftCornerX(Placement placement, Integer deckAmount) {
        return (placement == Placement.HORIZONTAL)
                ? FieldConstraints.MAX_X.getValue() - deckAmount + 1
                : FieldConstraints.MAX_X.getValue();
    }

    public static Integer calculateMaxLeftCornerY(Placement placement, Integer deckAmount) {
        return (placement == Placement.HORIZONTAL)
                ? FieldConstraints.MAX_Y.getValue()
                : FieldConstraints.MAX_Y.getValue() - deckAmount + 1;
    }

    public Integer getDeckAmount() {
        return deckAmount;
    }

    public void setDeckAmount(Integer deckAmount) {
        this.deckAmount = deckAmount;
    }

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public Coordinates getLeftUpperCornerCoordinates() {
        return leftUpperCornerCoordinates;
    }

    public void setLeftUpperCornerCoordinates(Coordinates leftUpperCornerCoordinates) {
        if(! Objects.equals(leftUpperCornerCoordinates, this.leftUpperCornerCoordinates)) {
            this.leftUpperCornerCoordinates = leftUpperCornerCoordinates;
            this.allOccupiedCoordinates = calculateAllOccupiedCoords();
        }
    }

    public Collection<Coordinates> getAllOccupiedCoordinates() {
        return allOccupiedCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return deckAmount.equals(ship.deckAmount) &&
                placement == ship.placement &&
                leftUpperCornerCoordinates.equals(ship.leftUpperCornerCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deckAmount, placement, leftUpperCornerCoordinates);
    }

    @Override
    public String toString() {
        return "Ship{\n" +
                "\t deckAmount = " + deckAmount +
                ", placement = " + placement +
                ", coordinates = " + leftUpperCornerCoordinates +
                "\n}";
    }

    @Override
    public Ship clone(){
        return new Ship(this);
    }

    public Collection<Coordinates> getDamagedCoordinates() {
        return damagedCoordinates;
    }

    public boolean isDestroyed(){
        return damagedCoordinates.equals(getAllOccupiedCoordinates());
    }

    public Integer getMaxLeftCornerX() {
        return maxLeftCornerX;
    }

    public Integer getMaxLeftCornerY() {
        return maxLeftCornerY;
    }
}

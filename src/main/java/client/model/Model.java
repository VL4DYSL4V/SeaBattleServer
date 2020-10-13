package client.model;


import game.entity.Coordinates;
import game.entity.Ship;
import game.enums.FiringResult;

import java.util.*;

public class Model {

    private Collection<Ship> myShips = new ArrayList<>();
    private Map<Coordinates, FiringResult> myUsedCoordinates = new HashMap<>();
    private Collection<Ship> opponentShips = new ArrayList<>();
    private Coordinates lastEntered = new Coordinates(0,0);
    private Collection<Coordinates> opponentUsedCoordinates = new HashSet<>();

    public void handleAttackResult(FiringResult firingResult){
        myUsedCoordinates.put(lastEntered, firingResult);
    }

    public void getDamage(Coordinates coordinates){
        opponentUsedCoordinates.add(coordinates);
        myShips.forEach((ship) -> {
            if (ship.getOccupiedCoordinates().contains(coordinates)) {
                ship.getDamagedCoordinates().add(coordinates);
            }
        });
    }

    public void fire(){
        for (Ship ship : opponentShips) {
            Collection<Coordinates> occupied = ship.getOccupiedCoordinates();
            if (occupied.contains(lastEntered)) {
                ship.getDamagedCoordinates().add(lastEntered);
                if (ship.isDestroyed()) {
                    opponentShips.remove(ship);
                }
            }
        }
    }

    public Collection<Ship> getMyShips() {
        return myShips;
    }

    public void setMyShips(Collection<Ship> myShips) {
        this.myShips = myShips;
    }

    public Map<Coordinates, FiringResult> getMyUsedCoordinates() {
        return myUsedCoordinates;
    }

    public Coordinates getLastEntered() {
        return lastEntered;
    }

    public void setLastEntered(Coordinates lastEntered) {
        this.lastEntered = lastEntered;
    }

    public Collection<Coordinates> getOpponentUsedCoordinates() {
        return opponentUsedCoordinates;
    }
}

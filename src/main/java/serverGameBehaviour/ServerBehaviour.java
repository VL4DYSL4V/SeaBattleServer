package serverGameBehaviour;

import data.Coordinates;
import data.Ship;
import enums.FiringResult;
import enums.PlayerType;

import java.util.*;

public abstract class ServerBehaviour {

    private final Collection<Ship> serverShips;
    private final Collection<Ship> clientShips;

    public ServerBehaviour(Collection<Ship> serverShips, Collection<Ship> clientShips) {
        this.serverShips = serverShips;
        this.clientShips = clientShips;
    }

    public final void makeMove() {
        FiringResult firingResult;
        do {
            Coordinates fireAt;
                fireAt = calculateCoordinates();
            firingResult = performFiring(fireAt);
        } while (calculateTurn(firingResult) != PlayerType.PLAYER_1);
    }

    public abstract Coordinates calculateCoordinates();

    public FiringResult performFiring(Coordinates whereTo) {
        for (Ship ship : clientShips) {
            Collection<Coordinates> existingUserCoords = ship.getAllOccupiedCoordinates();
            if (existingUserCoords.contains(whereTo)) {
                ship.getDamagedCoordinates().add(whereTo);
                clientShips.removeIf(Ship::isDestroyed);
                return FiringResult.AIMED;
            }
        }
        return FiringResult.MISSED;
    }

    public abstract PlayerType calculateTurn(FiringResult firingResult);

    public final void performGettingDamage(Coordinates coordinates) {
        for (Ship s : serverShips) {
            Collection<Coordinates> existingServerCoords = s.getAllOccupiedCoordinates();
            if (existingServerCoords.contains(coordinates)) {
                s.getDamagedCoordinates().add(coordinates);
                serverShips.removeIf(Ship::isDestroyed);
                return;
            }
        }
    }

    public abstract Collection<Coordinates> getUsedCoords();

    public Collection<Ship> getServerShips() {
        return Collections.unmodifiableCollection(serverShips);
    }

    public Collection<Ship> getClientShips() {
        return Collections.unmodifiableCollection(clientShips);
    }
}

package game.service;

import game.entity.Ship;

import java.util.Collection;

public interface FieldGeneratorService {
    Collection<Ship> createShips();
}

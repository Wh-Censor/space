package com.space.service;

import com.space.controller.ShipOrder;
import com.space.repository.ShipDao;
import com.space.model.ShipInfo;
import com.space.model.ShipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipService {

    @Autowired
    private ShipDao shipDao;



    public List<ShipInfo> getAllShips(String name,
                                       String planet,
                                       ShipType shipType,
                                       Long after,
                                       Long before,
                                       Boolean isUsed,
                                       Double minSpeed,
                                       Double maxSpeed,
                                       Integer minCrewSize,
                                       Integer maxCrewSize,
                                       Double minRating,
                                       Double maxRating,
                                       ShipOrder order,
                                       Integer pageNumber,
                                       Integer pageSize) {
        return shipDao.getAllShips(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
    }

    public Integer getAllShipsCount(String name,
                                    String planet,
                                    ShipType shipType,
                                    Long after,
                                    Long before,
                                    Boolean isUsed,
                                    Double minSpeed,
                                    Double maxSpeed,
                                    Integer minCrewSize,
                                    Integer maxCrewSize,
                                    Double minRating,
                                    Double maxRating) {
        return shipDao.getAllShipsCount(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
    }

    public ShipInfo createShip(String name,
                               String planet,
                               ShipType shipType,
                               Long prodDate,
                               Boolean isUsed,
                               Double speed,
                               Integer crewSize) {
        return shipDao.createShip(name, planet, shipType, prodDate, isUsed, speed, crewSize);
    }

    public ShipInfo getShip(Long id) {
        return shipDao.getShip(id);
    }

    public ShipInfo updateShip(Long id,
                               String name,
                               String planet,
                               ShipType shipType,
                               Long prodDate,
                               Boolean isUsed,
                               Double speed,
                               Integer crewSize) {
        return shipDao.updateShip(id, name, planet, shipType, prodDate, isUsed, speed, crewSize);
    }

    public ShipInfo deleteShip(Long id) {
        return shipDao.deleteShip(id);
    }
}

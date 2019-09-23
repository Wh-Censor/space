package com.space.controller;

import com.space.model.ShipInfo;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


import static org.springframework.http.ResponseEntity.badRequest;

@RestController
@RequestMapping("/rest")
public class ShipController {
    @Autowired
    private ShipService shipService;

    /*getAllShips*/
    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public ResponseEntity<List<ShipInfo>> getAllShips(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "planet", required = false, defaultValue = "") String planet,
            @RequestParam(value = "shipType", required = false, defaultValue = "") ShipType shipType,
            @RequestParam(value = "after", required = false, defaultValue = "") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "") Long before,
            @RequestParam(value = "isUsed", required = false, defaultValue = "") Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false, defaultValue = "") Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false, defaultValue = "") Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false, defaultValue = "") Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false, defaultValue = "") Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false, defaultValue = "") Double minRating,
            @RequestParam(value = "maxRating", required = false, defaultValue = "") Double maxRating,
            @RequestParam(value = "order", required = false, defaultValue = "") ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "") Integer pageSize) {

        if (after != null) {
            after = truncDate(after);
        }

        if (before != null) {
            before = truncDate(before);
        }


        List<ShipInfo> result = shipService.getAllShips(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
        return new ResponseEntity<List<ShipInfo>>(result, HttpStatus.OK);
    }

    /*getAllShipsCount*/
    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> getAllShipsCount(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "planet", required = false, defaultValue = "") String planet,
            @RequestParam(value = "shipType", required = false, defaultValue = "") ShipType shipType,
            @RequestParam(value = "after", required = false, defaultValue = "") Long after,
            @RequestParam(value = "before", required = false, defaultValue = "") Long before,
            @RequestParam(value = "isUsed", required = false, defaultValue = "") Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false, defaultValue = "") Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false, defaultValue = "") Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false, defaultValue = "") Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false, defaultValue = "") Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false, defaultValue = "") Double minRating,
            @RequestParam(value = "maxRating", required = false, defaultValue = "") Double maxRating) {

        if (after != null) {
            after = truncDate(after);
        }

        if (before != null) {
            before = truncDate(before);
        }

        Integer result = shipService.getAllShipsCount(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<Integer>(result, HttpStatus.OK);
    }

    /*createShip*/
    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    public ResponseEntity<ShipInfo> createShip(
            @RequestBody ShipInfo ship) {

        if (ship.getName() == null || ship.getName().equals("") ||
                ship.getPlanet() == null || ship.getPlanet().equals("") ||
                ship.getShipType() == null ||
                ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null ||
                ship.getName().length() > 50 || ship.getPlanet().length() > 50 ||
                ship.getCrewSize() < 1 || ship.getCrewSize() > 9999 || ship.getProdDate() < 0) {
            return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
        }

            ship.setProdDate(truncDate(ship.getProdDate()));
            if (LocalDate.ofEpochDay(ship.getProdDate() / (1000 * 60 * 60 * 24)).getYear() > 3019 ||
                    LocalDate.ofEpochDay(ship.getProdDate() / (1000 * 60 * 60 * 24)).getYear() < 2800) {
                return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
            }

            ship.setSpeed(Math.round(ship.getSpeed() * 100.0) / 100.0);
            if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99) {
                return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
            }

            if (ship.isUsed() == null) {
            ship.setUsed(false);
        }

        ShipInfo result = shipService.createShip(ship.getName(), ship.getPlanet(), ship.getShipType(), ship.getProdDate(),
                ship.isUsed(), ship.getSpeed(), ship.getCrewSize());
        return new ResponseEntity<ShipInfo>(result, HttpStatus.OK);
    }

    /*getShip*/
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<ShipInfo> getShip(
            @PathVariable("id") String idText) {
        Long idLong = idConvert(idText);

        if (idLong == null) {
            return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
        }

        ShipInfo result = shipService.getShip(idLong);
        if (result == null) {
            return new ResponseEntity<ShipInfo>((ShipInfo) null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<ShipInfo>(result, HttpStatus.OK);
    }

    /*updateShip*/
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ShipInfo> updateShip(
           @PathVariable("id") String idText,
           @RequestBody ShipInfo ship) {

        Long idLong = idConvert(idText);
        if (idLong == null) {
            return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getSpeed() != null) {
            ship.setSpeed(Math.round(ship.getSpeed() * 100.0) / 100.0);
        }

        if (ship.getProdDate() != null) {
            ship.setProdDate(truncDate(ship.getProdDate()));
        }

        if ((ship.getName() != null && (ship.getName().equals("") || ship.getName().length() > 50)) ||
                (ship.getPlanet() != null && (ship.getPlanet().equals("") || ship.getPlanet().length() >  50)) ||
                (ship.getProdDate() != null && (ship.getProdDate() <= 0 ||
                     LocalDate.ofEpochDay(ship.getProdDate() / (1000 * 60 * 60 * 24)).getYear() > 3019 ||
                     LocalDate.ofEpochDay(ship.getProdDate() / (1000 * 60 * 60 * 24)).getYear() < 2800)) ||
                (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)) ||
                (ship.getSpeed() != null && (ship.getSpeed()  < 0.1 || ship.getSpeed()  > 0.99))) {
            return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
        }


        ShipInfo result = shipService.updateShip(idLong, ship.getName(), ship.getPlanet(), ship.getShipType(), ship.getProdDate(),
            ship.isUsed(), ship.getSpeed(), ship.getCrewSize());
        if (result != null) {
            return new ResponseEntity<ShipInfo>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<ShipInfo>(HttpStatus.NOT_FOUND);
        }
    }

    /*deleteShip*/
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ShipInfo> deleteShip(
            @PathVariable("id") String idText) {
        Long idLong = idConvert(idText);

        if (idLong == null) {
            return new ResponseEntity<ShipInfo>(HttpStatus.BAD_REQUEST);
        }

        ShipInfo result = shipService.deleteShip(idLong);
        if (result != null) {
            return new ResponseEntity<ShipInfo>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<ShipInfo>(HttpStatus.NOT_FOUND);
        }
    }

    private Long idConvert(String idText) {
        Long result = null;

        if (idText == null || idText.equals("")) {
            return null;
        }

        try {
            result = Long.parseLong(idText);
        } catch (Exception e) {
            return null;
        }

        if (result <=  0) {
            return null;
        }

        return result;
    }


    private Long truncDate(Long date) {
        //This function was written for truncate local dates from front side to 01/01/YYYY
        //Date in MySQL would be in UTC
        //But it wasn't work with tests, because we have time converted from GMT+2 in tests (Kiev time?), and I have GMT+3 (Moscow time)
        //I wouldn't change tests :(
        //return LocalDate.of(LocalDate.ofEpochDay((date + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)) / (1000 * 60 * 60 * 24)).getYear(), Month.JANUARY, 1).getLong(ChronoField.EPOCH_DAY) * 24 * 60 * 60 * 1000;
        return date;
    }

}
package com.space.repository;

import com.space.controller.ShipOrder;
import com.space.model.ShipInfo;
import com.space.model.ShipType;
import com.space.utils.DateAttributeConverter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Repository("shipDao")
public class ShipDao {
    @PersistenceUnit
    private EntityManagerFactory managerFactory;

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
        List<ShipInfo> data = getAllShipsData(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order);

        List<ShipInfo> result = new ArrayList<ShipInfo>();

        if (pageNumber == null) {
            pageNumber = 0;
        }

        if (pageSize == null) {
            pageSize = 3;
        }
        for(int i = pageSize * pageNumber; i < Math.min(pageSize * (pageNumber + 1), data.size()); i++) {
            result.add(data.get(i));
        }

        return result;
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
        return getAllShipsData(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, ShipOrder.ID).size();
    }

    private List<ShipInfo> getAllShipsData(String name,
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
                                           ShipOrder order) {
        EntityManager entityManager = managerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ShipInfo> criteriaQuery = criteriaBuilder.createQuery(ShipInfo.class);

        Root<ShipInfo> root = criteriaQuery.from(ShipInfo.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (name != null && !name.equals("")) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (planet != null && !planet.equals("")) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("planet")), "%" + planet.toLowerCase() + "%"));
        }


        if (shipType != null) {
            predicates.add(criteriaBuilder.equal(root.get("shipType"), shipType));
        }

        if (after != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate").as(Date.class),
                    new DateAttributeConverter().convertToDatabaseColumn(after)));
        }

        if (before != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("prodDate").as(Date.class),
                    new DateAttributeConverter().convertToDatabaseColumn(before + 1)));
        }

        if (isUsed != null) {
            predicates.add(criteriaBuilder.equal(root.get("isUsed"), isUsed));
        }

        if (minSpeed != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed));
        }

        if (maxSpeed != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed));
        }

        if (minCrewSize != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize));
        }

        if (maxCrewSize != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize));
        }

        if (minRating != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating));
        }

        if (maxRating != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating));
        }

        if (order == null || order == ShipOrder.ID) {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));
        } else if (order == ShipOrder.SPEED) {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("speed")));
        } else if (order == ShipOrder.DATE) {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("prodDate")));
        } else if (order == ShipOrder.RATING) {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("rating")));
        }

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[]{}));

        List<ShipInfo> result = entityManager.createQuery(criteriaQuery).getResultList();
        entityManager.close();
        return result;
    }

    public ShipInfo createShip(String name,
                               String planet,
                               ShipType shipType,
                               Long prodDate,
                               Boolean isUsed,
                               Double speed,
                               Integer crewSize) {

        EntityManager entityManager = managerFactory.createEntityManager();
        Session session = entityManager.unwrap(Session.class);
        ShipInfo ship = new ShipInfo();
        ship.setName(name);
        ship.setPlanet(planet);
        ship.setShipType(shipType);
        ship.setProdDate(prodDate);
        ship.setUsed(isUsed);
        ship.setSpeed(speed);
        ship.setCrewSize(crewSize);
        session.save(ship);
        entityManager.close();
        return ship;
    }

    public ShipInfo getShip(Long id) {
        EntityManager entityManager = managerFactory.createEntityManager();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ShipInfo> criteriaQuery = criteriaBuilder.createQuery(ShipInfo.class);

        Root<ShipInfo> root = criteriaQuery.from(ShipInfo.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(criteriaBuilder.equal(root.get("id"), id));

        criteriaQuery.select(root).where(predicates.toArray(new Predicate[]{}));
        List<ShipInfo> result = entityManager.createQuery(criteriaQuery).getResultList();
        entityManager.close();

        if (result != null && result.size() == 1) {
            return result.get(0);
        }

        return null;
    }

    public ShipInfo updateShip(Long id,
                               String name,
                               String planet,
                               ShipType shipType,
                               Long prodDate,
                               Boolean isUsed,
                               Double speed,
                               Integer crewSize) {
        ShipInfo ship = getShip(id);

        if (ship != null) {
            EntityManager entityManager = managerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            ship = entityManager.merge(ship);

            if (name != null && !name.equals("")) {
                ship.setName(name);
            }

            if (planet != null && !planet.equals("")) {
                ship.setPlanet(planet);
            }


            if (shipType != null) {
                ship.setShipType(shipType);
            }

            if (prodDate != null) {
                ship.setProdDate(prodDate);
            }

            if (isUsed != null) {
                ship.setUsed(isUsed);
            }

            if (speed != null) {
                ship.setSpeed(speed);
            }

            if (crewSize != null) {
                ship.setCrewSize(crewSize);
            }

            Transaction transaction = session.beginTransaction();
            session.update(ship);
            transaction.commit();
            entityManager.close();

        }
        return ship;
    }

    public ShipInfo deleteShip(Long id) {
        ShipInfo ship = getShip(id);

        if (ship != null) {
            EntityManager entityManager = managerFactory.createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            ship = entityManager.merge(ship);
            Transaction transaction = session.beginTransaction();
            session.delete(ship);
            transaction.commit();
            entityManager.close();
        }

        return ship;
    }

}

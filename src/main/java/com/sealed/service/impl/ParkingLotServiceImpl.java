package com.sealed.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.sealed.entity.ParkingLot;
import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.enums.SpotType;
import com.sealed.enums.VehicleType;
import com.sealed.exception.ServiceException;
import com.sealed.repository.ParkingLotRepository;
import com.sealed.service.ParkingLotService;
import com.sealed.service.SpotService;
import com.sealed.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ParkingLotServiceImpl implements ParkingLotService {

  private final ParkingLotRepository parkingLotRepository;
  private final SpotService spotService;
  private final VehicleService vehicleService;

  @Autowired
  public ParkingLotServiceImpl(final ParkingLotRepository parkingLotRepository,
                               final SpotServiceImpl spotService,
                               final VehicleServiceImpl vehicleService) {
    this.parkingLotRepository = parkingLotRepository;
    this.spotService = spotService;
    this.vehicleService = vehicleService;
  }

  /**
   * Creates a new parking lot and persists it to the database.
   * @param parkingLot the parking lot object to be created and persisted
   * @return the newly created parking lot object
   * @throws ServiceException if there is an error persisting the parking lot object
   */
  @Override
  public ParkingLot createParkingLot(final ParkingLot parkingLot) throws ServiceException {
    try {
      return parkingLotRepository.save(parkingLot);
    } catch (final Exception e) {
      log.error("Error persisting a new ParkLot: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting a new ParkLot", e.getCause());
    }
  }

  /**
   * This method parks a vehicle in the first available spot of the correct type, if none are available it will try to park in the adjacent spots if available
   * @param vehicle Vehicle to park
   * @return The spot in which the vehicle was parked
   * @throws ServiceException if there is an error while parking the vehicle
   */
  @Override
  public Spot parkVehicle(final Vehicle vehicle) throws ServiceException {
    final List<Spot> availableSpots = spotService.findAllSpots().stream().filter(s -> !s.isOccupied()).toList();

    if (vehicle.getType() == VehicleType.MOTORCYCLE) {
      return parkMotorcycle(availableSpots, vehicle);
    } else if (vehicle.getType() == VehicleType.CAR) {
      return parkCar(availableSpots, vehicle);
    } else {
      return parkVan(availableSpots, vehicle);
    }
  }

  /**
   * This method unparks a vehicle from a parking spot.
   * @param licensePlate the vehicle license plate to unpark.
   * @return the spot that the vehicle was parked in
   * @throws ServiceException        if there is an error while unparking the vehicle or if the vehicle is not found in the repository
   * @throws EntityNotFoundException if the vehicle with the specified license plate number is not found in the repository
   */
  public Spot unparkVehicle(final String licensePlate) throws ServiceException {
    final Vehicle vehicle = Optional.of(vehicleService.getVehicleByLicensePlate(licensePlate))
                           .orElseThrow(() -> new EntityNotFoundException("Could not find vehicle with license plate " + licensePlate));
    final Spot spot = vehicle.getSpot();

    if (vehicle.getType() == VehicleType.MOTORCYCLE) {
      return unparkMotorcycle(spot, vehicle);
    } else if (vehicle.getType() == VehicleType.CAR) {
      return unparkCar(spot, vehicle);
    } else {
      return unparkVan(spot, vehicle);
    }
  }

  /**
   * Parks a motorcycle in the first available spot from a list of spots.
   * @param availableSpots A List of available parking spots
   * @param vehicle        The motorcycle vehicle to be parked
   * @return The Spot where the motorcycle has been parked
   * @throws ServiceException If there are no available spots, or an error occurs while parking the motorcycle
   */
  private Spot parkMotorcycle(final List<Spot> availableSpots, final Vehicle vehicle) throws ServiceException {
    if (availableSpots.isEmpty()) {
      throw new ServiceException("No available spots to park");
    }

    final Spot spot = availableSpots.get(0);
    spot.setOccupied(true);
    spot.setVehicle(vehicle);
    vehicle.setSpot(spot);
    vehicleService.createVehicle(vehicle);

    try {
      spotService.createSpot(spot);
    } catch (final Exception e) {
      log.error("Error parking a motorcycle in a spot: {}", e.getMessage(), e);
      throw new ServiceException("Error parking a motorcycle in a spot!", e);
    }
    return spot;
  }

  /**
   * Searches for an available parking spot for a car or van, assigns it to the given vehicle,
   * and updates the status of the spot and the vehicle. The updated vehicle and spot objects
   * are persisted to the database. If no available spot is found, a ServiceException is thrown.
   * @param availableSpots a List of Spot objects representing the available parking spots
   * @param vehicle        a Vehicle object representing the vehicle that needs to be parked
   * @return a Spot object representing the parking spot assigned to the vehicle
   * @throws ServiceException if no available spot is found or if there's an error creating
   *                          the updated vehicle and spot objects in the database
   */
  private Spot parkCar(final List<Spot> availableSpots, final Vehicle vehicle) throws ServiceException {
    for (final Spot spot : availableSpots) {
      if (spot.getType() == SpotType.CAR || spot.getType() == SpotType.VAN) {
        spot.setOccupied(true);
        spot.setVehicle(vehicle);
        vehicle.setSpot(spot);
        vehicleService.createVehicle(vehicle);
        try {
          spotService.createSpot(spot);
        } catch (final Exception e) {
          log.error("Error parking a car in a spot: {}", e.getMessage(), e);
          throw new ServiceException("Error parking a car in a spot!", e);
        }
        return spot;
      }
    }
    throw new ServiceException("There's no available spot for a car");
  }

  /**
   * Parks a given {@link Vehicle} of type van in the first available {@link Spot} of type van.
   * If no van spots are available, the method will attempt to park the van in adjacent empty car spots,
   * but only if at least two consecutive adjacent spots are unoccupied.
   * If no suitable spots are found, a {@link ServiceException} is thrown.
   * @param availableSpots a list of all available spots in the parking lot
   * @param vehicle        the van to be parked
   * @return the spot where the van was parked
   * @throws ServiceException if no available spots for a van or car are found
   */
  private Spot parkVan(final List<Spot> availableSpots, final Vehicle vehicle) throws ServiceException {
    for (Spot spot : availableSpots) {
      if (spot.getType() == SpotType.VAN && !spot.isOccupied()) {
        occupySpot(vehicle, spot);
        return spot;
      }
    }
    // A van can park a van spot or car spot, but it will take up 3 adjacent car spots
    for (int i = 0; i < availableSpots.size() - 2; i++) {
      Spot spot = availableSpots.get(i);
      if ((spot.getType() == SpotType.VAN && !spot.isOccupied()) ||
          (spot.getType() == SpotType.CAR && !spot.isOccupied() &&
              !availableSpots.get(i + 1).isOccupied() && !availableSpots.get(i + 2).isOccupied())) {

        occupySpot(vehicle, spot);
        occupySpot(vehicle, availableSpots.get(i + 1));
        occupySpot(vehicle, availableSpots.get(i + 2));
        return spot;
      }
    }
    throw new ServiceException("There are no available spots for a van or car.");
  }

  /**
   * Occupies a parking spot with a given vehicle.
   * @param vehicle the vehicle to occupy the spot
   * @param spot    the parking spot to be occupied
   * @throws ServiceException if there is an error creating the vehicle or updating the spot
   */
  private void occupySpot(final Vehicle vehicle, final Spot spot) throws ServiceException {
    spot.setOccupied(true);
    spot.setVehicle(vehicle);
    vehicle.setSpot(spot);
    try {
      vehicleService.createVehicle(vehicle);
      spotService.updateSpot(spot.getId(), spot);
    } catch (final Exception e) {
      log.error("Error occupying a spot: {}", e.getMessage(), e);
      throw new ServiceException("Error occupying a spot", e);
    }
  }

  /**
   * Removes a motorcycle from a parking spot.
   * @param spot    the parking spot from which to remove the motorcycle
   * @param vehicle the motorcycle to remove
   * @return the updated parking spot that was previously occupied by the motorcycle
   * @throws ServiceException if there is an error updating the spot or removing the vehicle
   */
  private Spot unparkMotorcycle(final Spot spot, final Vehicle vehicle) throws ServiceException {
    spot.setOccupied(false);
    spot.setVehicle(null);
    try {
      final Spot freeSpot = spotService.updateSpot(spot.getId(), spot);
      vehicleService.removeVehicle(vehicle.getId());
      return freeSpot;
    } catch (final Exception e) {
      log.error("Error unparking a motorcycle from spot: {}", e.getMessage(), e);
      throw new ServiceException("Error unparking a motorcycle from spot", e);
    }
  }

  /**
   * Unparks a car from the specified spot, freeing up the spot and removing the car from the vehicle service.
   * @param spot    The spot from which the car is being unparked
   * @param vehicle The car being unparked
   * @return The freed up spot
   * @throws ServiceException If there is an error unparking the car or updating the spot
   */
  private Spot unparkCar(final Spot spot, final Vehicle vehicle) throws ServiceException {
    spot.setOccupied(false);
    spot.setVehicle(null);
    try {
      final Spot freeSpot = spotService.updateSpot(spot.getId(), spot);
      vehicleService.removeVehicle(vehicle.getId());
      return freeSpot;
    } catch (final Exception e) {
      log.error("Error unparking a car from spot: {}", e.getMessage(), e);
      throw new ServiceException("Error unparking a car from spot", e);
    }
  }

  /**
   * Unparks the given van vehicle from the given spot.
   * @param spot the spot where the van vehicle is parked
   * @param vehicle the van vehicle to unpark
   * @return the unparked spot
   * @throws ServiceException if an error occurs while unparking the vehicle
   */
  private Spot unparkVan(final Spot spot, final Vehicle vehicle) throws ServiceException {
    var adjacentSpots = new ArrayList<Spot>();

    if (spot.getType() == SpotType.VAN) {
      adjacentSpots.add(spot);
    } else {
      adjacentSpots.add(spotService.findById(spot.getId() - 1));
      adjacentSpots.add(spotService.findById(spot.getId() - 2));
    }

    for (final Spot adjacentSpot : adjacentSpots) {
      adjacentSpot.setOccupied(false);
      adjacentSpot.setVehicle(null);
      spotService.updateSpot(adjacentSpot.getId(), adjacentSpot);
    }

    spot.setOccupied(false);
    spot.setVehicle(null);
    vehicleService.removeVehicle(vehicle.getId());
    spotService.updateSpot(spot.getId(), spot);

    return spot;
  }

  /**
   * Finds a ParkingLot by id.
   * @param id the id of the ParkingLot to be found.
   * @return the ParkingLot with the given id.
   * @throws EntityNotFoundException if the ParkingLot with the given id is not found.
   */
  @Override
  public ParkingLot findParkingLotById(final Long id) {
    return parkingLotRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No parking lot found for id " + id));
  }

  /**
   * Returns a boolean value indicating whether the parking lot is full or not.
   * This is determined by checking the number of available spots using the spotService.
   * If the count of available spots is zero, then the parking lot is considered full.
   * @return boolean value indicating whether the parking lot is full or not
   * @throws ServiceException if an error occurs while retrieving the available spots count
   */
  @Override
  public boolean isParkingLotFull() throws ServiceException {
    try {
      return spotService.countAvailableSpots() == 0;
    } catch (final Exception e) {
      log.error("Error retrieving available spots count: {}", e.getMessage(), e);
      throw new ServiceException("Error retrieving available spots count", e);
    }
  }

  /**
   * Returns the number of available parking spots by calling the countAvailableSpots method of the spotService.
   * @return An integer representing the number of available parking spots
   * @throws ServiceException If an error occurs while retrieving the available spots count from the spotService
   */
  @Override
  public int countAvailableSpots() throws ServiceException {
    try {
      return spotService.countAvailableSpots();
    } catch (final Exception e) {
      log.error("Error retrieving available spots count: {}", e.getMessage(), e);
      throw new ServiceException("Error retrieving available spots count", e);
    }
  }

  /**
   * Returns the count of occupied parking spots in the parking lot.
   * @return the count of occupied parking spots
   * @throws ServiceException if there is an error retrieving the count of occupied parking spots
   */
  @Override
  public int countOccupiedSpots() throws ServiceException {
    try {
      return spotService.countOccupiedSpots();
    } catch (final Exception e) {
      log.error("Error retrieving occupied spots count: {}", e.getMessage(), e);
      throw new ServiceException("Error occupied spots count", e);
    }
  }

  /**
   * Returns the number of parking spots currently occupied by vans.
   * It does this by retrieving all parking spots using the spotService.findAllSpots() method,
   * filtering the spots to include only those occupied by a van, and returning the count of those spots as an integer.
   * @return an integer representing the number of parking spots occupied by vans
   */
  public int countVanSpots() throws ServiceException {
    try {
      return (int) spotService.findAllSpots().stream()
                              .filter(s -> s.getVehicle() != null && s.getVehicle().getType() == VehicleType.VAN)
                              .count();
    } catch (final Exception e) {
      log.error("Error counting van spots: {}", e.getMessage(), e);
      throw new ServiceException("Error counting van spots", e);
    }
  }
}

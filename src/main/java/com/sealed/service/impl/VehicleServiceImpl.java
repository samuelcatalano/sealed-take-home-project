package com.sealed.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.exception.ServiceException;
import com.sealed.repository.SpotRepository;
import com.sealed.repository.VehicleRepository;
import com.sealed.service.VehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VehicleServiceImpl implements VehicleService {

  private final VehicleRepository vehicleRepository;
  private final SpotRepository spotRepository;

  @Autowired
  public VehicleServiceImpl(final VehicleRepository vehicleRepository, final SpotRepository spotRepository) {
    this.vehicleRepository = vehicleRepository;
    this.spotRepository = spotRepository;
  }

  /**
   * Persists a new vehicle instance.
   * @param vehicle the vehicle to be persisted
   * @return the persisted vehicle instance
   * @throws ServiceException if an error occurs during the persistence operation
   */
  @Override
  public Vehicle createVehicle(final Vehicle vehicle) throws ServiceException {
    try {
      return vehicleRepository.save(vehicle);
    } catch (final Exception e) {
      log.error("Error persisting a new vehicle: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting a new vehicle", e);
    }
  }

  /**
   * Deletes a vehicle instance from the database and frees up the spot it was occupying.
   * @param vehicleId the id of the vehicle to be deleted
   * @throws ServiceException if an error occurs during the deletion operation
   */
  @Override
  public void removeVehicle(final Long vehicleId) throws ServiceException {
    final Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new EntityNotFoundException("Vehicle not found!"));
    try {
      final Spot spot = vehicle.getSpot();
      spot.setOccupied(false);
      spotRepository.save(spot);
      vehicleRepository.delete(vehicle);
    } catch (final Exception e) {
      log.error("Error deleting a vehicle: {}", e.getMessage(), e);
      throw new ServiceException("Error deleting a vehicle", e);
    }
  }

  /**
   * This method retrieves a vehicle by its license plate number.
   * @param licensePlate the license plate number of the vehicle to retrieve
   * @return the vehicle with the specified license plate number
   * @throws EntityNotFoundException if no vehicle with the specified license plate number is found in the repository
   */
  @Override
  public Vehicle getVehicleByLicensePlate(final String licensePlate) {
    return Optional.ofNullable(vehicleRepository.findByLicensePlate(licensePlate))
          .orElseThrow(() -> new EntityNotFoundException("Could not find vehicle with license plate " + licensePlate));
  }

  /**
   * Fetches all persisted vehicles from the database.
   * @return a list of all persisted vehicles
   */
  @Override
  public List<Vehicle> getAllVehicles() {
    var vehicleIterable = vehicleRepository.findAll();
    return Streamable.of(vehicleIterable).toList();
  }
}

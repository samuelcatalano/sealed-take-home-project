package com.sealed.service;

import jakarta.persistence.EntityNotFoundException;

import com.sealed.entity.Vehicle;
import com.sealed.exception.ServiceException;

import java.util.List;

public interface VehicleService {

  Vehicle createVehicle(Vehicle vehicle) throws ServiceException;
  void removeVehicle(Long vehicleId) throws ServiceException;
  List<Vehicle> getAllVehicles() throws ServiceException;
  Vehicle getVehicleByLicensePlate(String licensePlate) throws EntityNotFoundException;

}

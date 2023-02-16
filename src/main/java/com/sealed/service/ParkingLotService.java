package com.sealed.service;

import jakarta.persistence.EntityNotFoundException;

import com.sealed.entity.ParkingLot;
import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.exception.ServiceException;

public interface ParkingLotService {

  ParkingLot createParkingLot(ParkingLot parkingLot) throws ServiceException;
  Spot parkVehicle(Vehicle vehicle) throws ServiceException;
  Spot unparkVehicle(String licensePlate) throws ServiceException;
  ParkingLot findParkingLotById(Long id) throws EntityNotFoundException;
  boolean isParkingLotFull() throws ServiceException;
  int countAvailableSpots() throws ServiceException;
  int countOccupiedSpots() throws ServiceException;
  int countVanSpots() throws ServiceException;

}

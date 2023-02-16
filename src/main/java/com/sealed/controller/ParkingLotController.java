package com.sealed.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.sealed.entity.ParkingLot;
import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.exception.ServiceException;
import com.sealed.service.ParkingLotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/parking-lot")
@RestController
public class ParkingLotController {

  private final ParkingLotService parkingLotService;

  @Autowired
  public ParkingLotController(final ParkingLotService parkingLotService) {
    this.parkingLotService = parkingLotService;
  }

  /**
   * Create a new parking lot with the specified parameters.
   * @param parkingLot The new parking lot to be created
   * @return ResponseEntity<ParkingLot> with the newly created parking lot
   * @throws ServiceException if an error occurs while persisting the parking lot
   */
  @PostMapping
  public ResponseEntity<ParkingLot> createParkingLot(@RequestBody final ParkingLot parkingLot) throws ServiceException {
    try {
      var response = parkingLotService.createParkingLot(parkingLot);
      return ResponseEntity.status(HttpStatus.CREATED)
                           .contentType(MediaType.APPLICATION_JSON)
                           .body(response);
    } catch (final Exception e) {
      log.error("Error persisting a new ParkLot: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }

  /**
   * Retrieve a parking lot by its id.
   * @param id The id of the parking lot to be retrieved
   * @return ResponseEntity<ParkingLot> with the parking lot matching the specified id
   * @throws EntityNotFoundException if no parking lot is found with the specified id
   */
  @GetMapping("/{id}")
  public ResponseEntity<ParkingLot> getParkingLot(@PathVariable final Long id) throws EntityNotFoundException {
    try {
      var result = parkingLotService.findParkingLotById(id);
      return ResponseEntity.ok(result);
    } catch (final Exception e) {
      log.error("No parking lot found", e);
      throw new EntityNotFoundException(e.getMessage());
    }
  }

  /**
   * Parks a vehicle in the parking lot.
   * @param vehicle The vehicle to park
   * @return A ResponseEntity containing the spot where the vehicle was parked
   * @throws ServiceException if an error occurs while parking the vehicle
   */
  @PostMapping("/park-vehicle")
  public ResponseEntity<Spot> parkVehicle(@RequestBody final Vehicle vehicle) throws ServiceException {
    try {
      var spot = parkingLotService.parkVehicle(vehicle);
      return ResponseEntity.ok(spot);
    } catch (final Exception e) {
      log.error("Error parking a vehicle: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }

  /**
   * Unparks a vehicle in the parking lot.
   * @param licensePlate The vehicle license plate to unpark
   * @return A ResponseEntity containing the spot where the vehicle was parked
   * @throws ServiceException if an error occurs while parking the vehicle
   */
  @PostMapping("/unpark-vehicle/{licensePlate}")
  public ResponseEntity<Spot> unparkVehicle(@PathVariable final String licensePlate) throws ServiceException {
    try {
      var spot = parkingLotService.unparkVehicle(licensePlate);
      return ResponseEntity.ok(spot);
    } catch (final Exception e) {
      log.error("Error unparking a vehicle: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }

  /**
   * Checks if the parking lot is full.
   * @return A ResponseEntity containing a string indicating whether the parking lot is full or not
   * @throws ServiceException if an error occurs while checking if the parking lot is full
   */
  @GetMapping("/is-full")
  public ResponseEntity<String> isParkingLotFull() throws ServiceException {
    try {
      var result = parkingLotService.isParkingLotFull();
      String response = result ? "Full Parking Lot" : "Available Spots";
      return ResponseEntity.ok(response);
    } catch (final Exception e) {
      log.error("Error checking if the parking lot is full: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }

  /**
   * Counts the number of available spots in the parking lot.
   * @return A ResponseEntity containing the number of available spots in the parking lot
   * @throws ServiceException if an error occurs while counting the available spots
   */
  @GetMapping("/available-spots")
  public ResponseEntity<Integer> countAvailableSpots() throws ServiceException {
    try {
      var result = parkingLotService.countAvailableSpots();
      return ResponseEntity.ok(result);
    } catch (final Exception e) {
      log.error("Error counting the available spots: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }

  /**
   * GET endpoint to count the number of occupied spots in the parking lot.
   * @return a ResponseEntity containing the number of occupied spots as an integer value
   * @throws ServiceException if there is an error while counting the occupied spots
   */
  @GetMapping("/occupied-spots")
  public ResponseEntity<Integer> countOccupiedSpots() throws ServiceException {
    try {
      var result = parkingLotService.countOccupiedSpots();
      return ResponseEntity.ok(result);
    } catch (final Exception e) {
      log.error("Error counting the occupied spots: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }

  /**
   * GET endpoint to count the number of van spots in the parking lot.
   * @return a ResponseEntity containing the number of van spots as an integer value
   * @throws ServiceException if there is an error while counting the van spots
   */
  @GetMapping("/van-spots")
  public ResponseEntity<Integer> countVanSpots() throws ServiceException {
    try {
      var result = parkingLotService.countVanSpots();
      return ResponseEntity.ok(result);
    } catch (final Exception e) {
      log.error("Error counting the van spots: {}", e.getMessage(), e);
      throw new ServiceException(e.getMessage(), e);
    }
  }
}

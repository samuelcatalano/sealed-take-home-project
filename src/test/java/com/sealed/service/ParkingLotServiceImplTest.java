package com.sealed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;

import com.sealed.entity.ParkingLot;
import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.enums.SpotType;
import com.sealed.enums.VehicleType;
import com.sealed.exception.ServiceException;
import com.sealed.repository.ParkingLotRepository;
import com.sealed.service.impl.ParkingLotServiceImpl;
import com.sealed.service.impl.SpotServiceImpl;
import com.sealed.service.impl.VehicleServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceImplTest {

  @Mock
  private ParkingLotRepository parkingLotRepository;

  @Mock
  private SpotServiceImpl spotService;

  @Mock
  private VehicleServiceImpl vehicleService;

  @InjectMocks
  private ParkingLotServiceImpl parkingLotService;

  @Test
  void testCreateParkingLot() throws ServiceException {
    // given
    final List<Spot> spots = Arrays.asList(new Spot(), new Spot(), new Spot());
    final ParkingLot parkingLot = new ParkingLot("Test Parking Lot", spots, 100, 5, 10, 20);

    // when
    Mockito.when(parkingLotRepository.save(Mockito.any(ParkingLot.class))).thenReturn(parkingLot);
    final ParkingLot savedParkingLot = parkingLotService.createParkingLot(parkingLot);

    // then
    assertNotNull(savedParkingLot);
    assertNotNull(parkingLot.getSpots());
    assertEquals(parkingLot.getName(), savedParkingLot.getName());
    assertEquals(parkingLot.getCapacity(), savedParkingLot.getCapacity());
    assertEquals(parkingLot.getMotorcycleSpots(), savedParkingLot.getMotorcycleSpots());
    assertEquals(parkingLot.getCarSpots(), savedParkingLot.getCarSpots());
    assertEquals(parkingLot.getVanSpots(), savedParkingLot.getVanSpots());
  }

  @Test
  void testParkVehicleWithMotorcycle() throws ServiceException {
    // given
    final Vehicle motorcycle = new Vehicle();
    motorcycle.setType(VehicleType.MOTORCYCLE);

    final List<Spot> availableSpots = new ArrayList<>();
    final Spot freeSpot = new Spot();
    availableSpots.add(freeSpot);
    when(spotService.findAllSpots()).thenReturn(availableSpots);

    // when
    final Spot parkedSpot = parkingLotService.parkVehicle(motorcycle);

    // then
    verify(spotService).findAllSpots();
    verify(spotService).createSpot(freeSpot);
    verify(vehicleService).createVehicle(motorcycle);
    assertTrue(freeSpot.isOccupied());
    assertEquals(motorcycle, freeSpot.getVehicle());
    assertEquals(freeSpot, motorcycle.getSpot());
    assertEquals(freeSpot, parkedSpot);
  }

  @Test
  void testUnparkVehicleWithMotorcycle() throws ServiceException {
    // given
    final Vehicle motorcycle = new Vehicle();
    motorcycle.setType(VehicleType.MOTORCYCLE);

    final Spot parkedSpot = new Spot();
    parkedSpot.setId(1L);
    parkedSpot.setVehicle(motorcycle);
    motorcycle.setSpot(parkedSpot);

    // when
    when(vehicleService.getVehicleByLicensePlate(anyString())).thenReturn(motorcycle);
    when(spotService.updateSpot(anyLong(), any())).thenReturn(parkedSpot);

    final Spot unparkedSpot = parkingLotService.unparkVehicle("ABC123");

    // then
    verify(vehicleService).getVehicleByLicensePlate("ABC123");
    verify(spotService).updateSpot(parkedSpot.getId(), parkedSpot);
    verify(vehicleService).removeVehicle(motorcycle.getId());
    assertFalse(parkedSpot.isOccupied());
    assertNull(parkedSpot.getVehicle());
    assertEquals(parkedSpot, unparkedSpot);
  }

  @Test
  void testParkVehicleWithCar() throws ServiceException {
    // given
    final List<Spot> availableSpots = new ArrayList<>();
    availableSpots.add(new Spot(SpotType.CAR, false, null, null));
    availableSpots.add(new Spot(SpotType.VAN, false, null, null));
    final Vehicle vehicle = new Vehicle(VehicleType.CAR, new Spot(), "ABC123");

    // when
    Mockito.when(spotService.findAllSpots()).thenReturn(availableSpots);
    Mockito.when(spotService.createSpot(Mockito.any(Spot.class))).thenReturn(availableSpots.get(0));
    Mockito.when(vehicleService.createVehicle(Mockito.any(Vehicle.class))).thenReturn(vehicle);

    // act
    final Spot parkedSpot = parkingLotService.parkVehicle(vehicle);

    // then
    assertNotNull(parkedSpot);
    assertEquals(SpotType.CAR, parkedSpot.getType());
    assertTrue(parkedSpot.isOccupied());
    assertEquals(vehicle, parkedSpot.getVehicle());
  }

  @Test
  void testUnparkVehicleWithCar() throws ServiceException {
    // given
    final String licensePlate = "123ABC";
    final Vehicle vehicle = new Vehicle(VehicleType.CAR, Spot.builder().id(1L).type(SpotType.CAR).build(), "123ABC");
    final Spot spot = new Spot(SpotType.CAR, true, null, vehicle);

    // when
    lenient().when(vehicleService.getVehicleByLicensePlate(any())).thenReturn(vehicle);
    lenient().when(spotService.updateSpot((long) anyInt(), any())).thenReturn(spot);

    // act
    final Spot unparkedSpot = parkingLotService.unparkVehicle(licensePlate);

    // then
    assertNull(unparkedSpot);
    assertFalse(spot.isOccupied());
  }

  @Test
  void testParkVehicleWithVan() throws Exception {
    // given
    final Spot spot1 = new Spot();
    spot1.setId(1L);
    spot1.setType(SpotType.CAR);
    spot1.setOccupied(false);

    final Spot spot2 = new Spot();
    spot2.setId(2L);
    spot2.setType(SpotType.MOTORCYCLE);
    spot2.setOccupied(false);

    final Spot spot3 = new Spot();
    spot3.setId(3L);
    spot3.setType(SpotType.VAN);
    spot3.setOccupied(false);

    final List<Spot> spots = new ArrayList<>();
    spots.add(spot1);
    spots.add(spot2);
    spots.add(spot3);

    // when
    Mockito.when(spotService.findAllSpots()).thenReturn(spots);
    final Vehicle vehicle = new Vehicle();
    vehicle.setType(VehicleType.VAN);

    // then
    final Spot result = parkingLotService.parkVehicle(vehicle);
    assertEquals(spot3, result);
    Mockito.verify(vehicleService).createVehicle(vehicle);
  }

  @Test
  void testUnparkVehicleWithVan() throws ServiceException {
    // given
    final String licensePlate = "ABC123";
    final Vehicle vehicle = new Vehicle();
    vehicle.setId(1L);
    vehicle.setLicensePlate(licensePlate);
    vehicle.setType(VehicleType.VAN);

    final Spot spot = new Spot();
    spot.setId(1L);
    spot.setType(SpotType.VAN);
    spot.setOccupied(true);
    spot.setVehicle(vehicle);
    vehicle.setSpot(spot);

    final List<Spot> availableSpots = Arrays.asList(
        spot, new Spot(SpotType.CAR, true, null, null),
        new Spot(SpotType.CAR, true, null, null)
    );

    // when
    when(vehicleService.getVehicleByLicensePlate(licensePlate)).thenReturn(vehicle);
    when(spotService.updateSpot(spot.getId(), spot)).thenReturn(spot);

    // Act
    final Spot result = parkingLotService.unparkVehicle(licensePlate);

    // then
    assertFalse(spot.isOccupied());
    assertNull(spot.getVehicle());
    verify(vehicleService).removeVehicle(vehicle.getId());
    assertEquals(spot, result);
  }

  @Test
  void testFindParkingLotById() {
    // given
    final Long id = 1L;
    final ParkingLot parkingLot = new ParkingLot();
    parkingLot.setId(id);

    // when
    when(parkingLotRepository.findById(id)).thenReturn(Optional.of(parkingLot));

    // test
    final ParkingLot result = parkingLotService.findParkingLotById(id);

    // then
    assertEquals(parkingLot, result);
  }

  @Test
  void testFindParkingLotByIdNotFound() {
    // given
    final Long id = 1L;
    when(parkingLotRepository.findById(id)).thenReturn(Optional.empty());

    // when and then
    final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
          () -> parkingLotService.findParkingLotById(id));
  }

  @Test
  void testIsParkingLotFull() throws ServiceException {
    // given
    int count = 0;
    when(spotService.countAvailableSpots()).thenReturn(count);

    // when
    boolean result = parkingLotService.isParkingLotFull();
    // then
    assertTrue(result);
  }

  @Test
  void testIsParkingLotFullError() {
    // given
    when(spotService.countAvailableSpots()).thenThrow(new RuntimeException());
    // when and then
    assertThrows(ServiceException.class, () -> parkingLotService.isParkingLotFull());
  }

  @Test
  void testCountAvailableSpots() throws ServiceException {
    // given
    int count = 10;
    when(spotService.countAvailableSpots()).thenReturn(count);

    // when
    int result = parkingLotService.countAvailableSpots();
    // then
    assertEquals(count, result);
  }

  @Test
  void testCountAvailableSpotsError() {
    // given
    when(spotService.countAvailableSpots()).thenThrow(new RuntimeException());

    // when and then
    assertThrows(ServiceException.class, () -> {
      parkingLotService.countAvailableSpots();
    });
  }

  @Test
  void testCountOccupiedSpots() throws ServiceException {
    // given
    int count = 5;
    when(spotService.countOccupiedSpots()).thenReturn(count);

    // when
    int result = parkingLotService.countOccupiedSpots();
    // then
    assertEquals(count, result);
  }

  @Test
  void testCountVanSpots() throws ServiceException {
    // given
    final List<Spot> spots = new ArrayList<>();
    spots.add(new Spot(SpotType.CAR, true, null, Vehicle.builder().type(VehicleType.CAR).build()));
    spots.add(new Spot(SpotType.VAN, true, null, Vehicle.builder().type(VehicleType.VAN).build()));
    spots.add(new Spot(SpotType.VAN, true, null, Vehicle.builder().type(VehicleType.VAN).build()));

    // when
    when(spotService.findAllSpots()).thenReturn(spots);
    // Test
    int result = parkingLotService.countVanSpots();
    // then
    assertEquals(2, result);
  }
}

package com.sealed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;

import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.enums.VehicleType;
import com.sealed.exception.ServiceException;
import com.sealed.repository.SpotRepository;
import com.sealed.repository.VehicleRepository;
import com.sealed.service.impl.VehicleServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

  @Mock
  private VehicleRepository vehicleRepository;

  @Mock
  private SpotRepository spotRepository;

  @InjectMocks
  private VehicleServiceImpl vehicleServiceImpl;

  @Test
  void testCreateVehicle() throws ServiceException {
    final Vehicle vehicle = new Vehicle(VehicleType.MOTORCYCLE, null, "MM234567");
    when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

    final Vehicle savedVehicle = vehicleServiceImpl.createVehicle(vehicle);

    assertEquals(vehicle.getLicensePlate(), savedVehicle.getLicensePlate());
    assertEquals(vehicle.getType(), savedVehicle.getType());
  }

  @Test
  void testRemoveVehicle() throws ServiceException {
    // given
    final Long vehicleId = 1L;
    final Vehicle vehicle = new Vehicle();
    vehicle.setId(vehicleId);
    final Spot spot = new Spot();
    spot.setOccupied(true);
    vehicle.setSpot(spot);

    // when
    when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
    when(spotRepository.save(spot)).thenReturn(spot);

    // then
    vehicleServiceImpl.removeVehicle(vehicleId);

    verify(spotRepository, times(1)).save(spot);
    verify(vehicleRepository, times(1)).delete(vehicle);
  }

  @Test
  void testRemoveVehicleFailure() {
    // given
    final Long vehicleId = 1L;
    final Vehicle vehicle = new Vehicle();
    vehicle.setId(vehicleId);
    final Spot spot = new Spot();
    spot.setOccupied(true);
    vehicle.setSpot(spot);

    // when
    when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
    when(spotRepository.save(spot)).thenReturn(spot);
    doThrow(new EntityNotFoundException()).when(vehicleRepository).delete(vehicle);

    // then
    assertThrows(ServiceException.class, () -> {
      vehicleServiceImpl.removeVehicle(vehicleId);
    });

    verify(spotRepository, times(1)).save(spot);
    verify(vehicleRepository, times(1)).delete(vehicle);
  }

  @Test
  void testGetVehicleByLicensePlate() {
    // given
    final String licensePlate = "ABC123";
    final Vehicle vehicle = new Vehicle();
    when(vehicleRepository.findByLicensePlate(licensePlate)).thenReturn(vehicle);

    // when
    final Vehicle result = vehicleServiceImpl.getVehicleByLicensePlate(licensePlate);

    // then
    assertNotNull(result);
    verify(vehicleRepository, times(1)).findByLicensePlate(licensePlate);
  }

  @Test
  void testGetVehicleByLicensePlateFailure() {
    // given
    final String licensePlate = "ABC123";
    when(vehicleRepository.findByLicensePlate(licensePlate)).thenThrow(new EntityNotFoundException());

    // when
    assertThrows(EntityNotFoundException.class, () -> {
      vehicleServiceImpl.getVehicleByLicensePlate(licensePlate);
    });

    // then
    verify(vehicleRepository, times(1)).findByLicensePlate(licensePlate);
  }

  @Test
  void testGetAllVehicles() {
    // given
    final Vehicle vehicle1 = new Vehicle();
    final Vehicle vehicle2 = new Vehicle();
    final List<Vehicle> vehicleList = Arrays.asList(vehicle1, vehicle2);
    when(vehicleRepository.findAll()).thenReturn(vehicleList);

    // when
    final List<Vehicle> result = vehicleServiceImpl.getAllVehicles();

    // then
    assertNotNull(result);
    assertEquals(result.size(), 2);
    verify(vehicleRepository, times(1)).findAll();
  }

  @Test
  void testCreateVehicleWhenVehicleRepositoryThrowsException() {
    doThrow(new RuntimeException("Failed to persist vehicle")).when(vehicleRepository).save(any(Vehicle.class));
    assertThrows(ServiceException.class, () -> vehicleServiceImpl.createVehicle(new Vehicle()));
  }
}

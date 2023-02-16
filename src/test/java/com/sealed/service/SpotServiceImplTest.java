package com.sealed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sealed.entity.Spot;
import com.sealed.enums.SpotType;
import com.sealed.exception.ServiceException;
import com.sealed.repository.SpotRepository;
import com.sealed.service.impl.SpotServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SpotServiceImplTest {

  @Mock
  private SpotRepository spotRepository;

  @InjectMocks
  private SpotServiceImpl spotService;

  @Test
  void testCreateSpot() throws ServiceException {
    // given
    final Spot spot = new Spot();
    spot.setOccupied(false);
    spot.setVehicle(null);
    spot.setType(SpotType.CAR);

    final Spot expectedSpot = new Spot();
    expectedSpot.setId(1L);
    expectedSpot.setOccupied(false);
    expectedSpot.setVehicle(null);
    expectedSpot.setType(SpotType.CAR);

    when(spotRepository.save(spot)).thenReturn(expectedSpot);

    // when
    final Spot resultSpot = spotService.createSpot(spot);

    // then
    assertNotNull(resultSpot);
    assertEquals(expectedSpot.getId(), resultSpot.getId());
    assertEquals(expectedSpot.isOccupied(), resultSpot.isOccupied());
    assertEquals(expectedSpot.getVehicle(), resultSpot.getVehicle());
    assertEquals(expectedSpot.getType(), resultSpot.getType());

    verify(spotRepository, times(1)).save(spot);
  }

  @Test
  void testUpdateSpot() throws ServiceException {
    // given
    Long id = 1L;

    final Spot existingSpot = new Spot();
    existingSpot.setId(id);
    existingSpot.setOccupied(false);
    existingSpot.setVehicle(null);
    existingSpot.setType(SpotType.CAR);

    final Spot updatedSpot = new Spot();
    updatedSpot.setId(id);
    updatedSpot.setOccupied(true);
    updatedSpot.setVehicle(null);
    updatedSpot.setType(SpotType.MOTORCYCLE);

    when(spotRepository.findById(id)).thenReturn(java.util.Optional.of(existingSpot));
    when(spotRepository.save(existingSpot)).thenReturn(updatedSpot);

    // when
    final Spot resultSpot = spotService.updateSpot(id, updatedSpot);

    // then
    assertNotNull(resultSpot);
    assertEquals(updatedSpot.getId(), resultSpot.getId());
    assertEquals(updatedSpot.isOccupied(), resultSpot.isOccupied());
    assertEquals(updatedSpot.getVehicle(), resultSpot.getVehicle());
    assertEquals(updatedSpot.getType(), resultSpot.getType());

    verify(spotRepository, times(1)).findById(id);
    verify(spotRepository, times(1)).save(existingSpot);
  }

  @Test
  void testFindAllSpots() {
    // given
    final List<Spot> expectedSpots = Arrays.asList(new Spot(), new Spot(), new Spot());

    when(spotRepository.findAll()).thenReturn(expectedSpots);

    // when
    final List<Spot> resultSpots = spotService.findAllSpots();

    // then
    assertNotNull(resultSpots);
    assertEquals(expectedSpots.size(), resultSpots.size());
    assertEquals(expectedSpots.get(0).getId(), resultSpots.get(0).getId());
    assertEquals(expectedSpots.get(1).getId(), resultSpots.get(1).getId());
    assertEquals(expectedSpots.get(2).getId(), resultSpots.get(2).getId());

    verify(spotRepository, times(1)).findAll();
  }

  @Test
  void testCountOccupiedSpots() {
    // given
    var spots = new ArrayList<>();
    spots.add(new Spot(SpotType.CAR, true, null, null));
    spots.add(new Spot(SpotType.CAR, false, null, null));
    spots.add(new Spot(SpotType.VAN, true, null, null));

    // when
    when(spotRepository.countByOccupied(true)).thenReturn(2L);
    int occupiedSpots = spotService.countOccupiedSpots();

    // then
    assertEquals(2, occupiedSpots);
    verify(spotRepository, times(1)).countByOccupied(true);
  }
}

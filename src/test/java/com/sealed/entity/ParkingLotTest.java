package com.sealed.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sealed.enums.SpotType;
import com.sealed.repository.ParkingLotRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ParkingLotTest {

  @Autowired
  private ParkingLotRepository parkingLotRepository;

  @Test
  void testParkingLotCreation() {
    final ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName("Test Parking Lot");
    parkingLot.setCapacity(100);
    parkingLot.setMotorcycleSpots(30);
    parkingLot.setCarSpots(50);
    parkingLot.setVanSpots(20);

    final Spot carSpot = new Spot(SpotType.CAR, false, parkingLot, null);
    final Spot motorcycleSpot = new Spot(SpotType.MOTORCYCLE, false, parkingLot, null);
    final Spot vanSpot = new Spot(SpotType.VAN, false, parkingLot, null);

    parkingLot.setSpots(List.of(carSpot, motorcycleSpot, vanSpot));

    parkingLotRepository.save(parkingLot);

    assertNotNull(parkingLot.getId());
    assertNotNull(carSpot.getId());
    assertNotNull(motorcycleSpot.getId());
    assertNotNull(vanSpot.getId());
  }
}

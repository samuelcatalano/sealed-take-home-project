package com.sealed.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sealed.enums.SpotType;
import com.sealed.enums.VehicleType;
import com.sealed.repository.SpotRepository;
import com.sealed.repository.VehicleRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class VehicleTest {

  @Autowired
  private VehicleRepository vehicleRepository;

  @Autowired
  private SpotRepository spotRepository;

  @Test
  void testVehicleCreation() {
    final Spot spot = new Spot(SpotType.CAR, false, null, null);
    spotRepository.save(spot);

    final Vehicle vehicle = new Vehicle(VehicleType.CAR, spot, "ABC123");
    vehicleRepository.save(vehicle);

    assertNotNull(vehicle.getId());
  }
}

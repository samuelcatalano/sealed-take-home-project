package com.sealed.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sealed.enums.SpotType;
import com.sealed.repository.SpotRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SpotTest {

  @Autowired
  private SpotRepository spotRepository;

  @Test
  void testSpotCreation() {
    final Spot spot = new Spot(SpotType.CAR, false, null, null);
    spotRepository.save(spot);
    assertNotNull(spot.getId());
  }
}

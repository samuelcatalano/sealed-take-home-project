package com.sealed.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sealed.entity.ParkingLot;
import com.sealed.entity.Spot;
import com.sealed.entity.Vehicle;
import com.sealed.enums.SpotType;
import com.sealed.enums.VehicleType;
import com.sealed.service.ParkingLotService;
import com.sealed.service.impl.ParkingLotServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingLotControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ParkingLotService parkingLotService;

  @Autowired
  private ParkingLotController parkingLotController;

  @MockBean
  private ParkingLotServiceImpl parkingLotServiceImpl;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testCreateParkingLot() throws Exception {
    final ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName("Test Parking Lot");
    parkingLot.setCapacity(6);
    parkingLot.setCarSpots(2);
    parkingLot.setMotorcycleSpots(2);
    parkingLot.setVanSpots(2);

    var spotList = Arrays.asList(
        Spot.builder().type(SpotType.MOTORCYCLE).build(),
        Spot.builder().type(SpotType.MOTORCYCLE).build(),
        Spot.builder().type(SpotType.CAR).build(),
        Spot.builder().type(SpotType.CAR).build(),
        Spot.builder().type(SpotType.VAN).build(),
        Spot.builder().type(SpotType.VAN).build()
    );

    parkingLot.setSpots(spotList);

    // Serialize the ParkingLot object to a JSON string
    final String parkingLotJson = objectMapper.writeValueAsString(parkingLot);

    // Make a POST request to create the parking lot
    mockMvc.perform(post("/api/parking-lot")
           .contentType(MediaType.APPLICATION_JSON)
           .content(parkingLotJson))
           .andExpect(status().isCreated());
  }

  @Test
  void testParkVehicle() throws Exception {
    final ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName("Test Parking Lot");
    parkingLot.setCapacity(6);
    parkingLot.setCarSpots(2);
    parkingLot.setMotorcycleSpots(2);
    parkingLot.setVanSpots(2);

    var spotList = Arrays.asList(
        Spot.builder().type(SpotType.MOTORCYCLE).build(),
        Spot.builder().type(SpotType.MOTORCYCLE).build(),
        Spot.builder().type(SpotType.CAR).build(),
        Spot.builder().type(SpotType.CAR).build(),
        Spot.builder().type(SpotType.VAN).build(),
        Spot.builder().type(SpotType.VAN).build()
    );

    parkingLot.setSpots(spotList);

    // Create a parking lot using the ParkingLotService
    final ParkingLot createdParkingLot = parkingLotService.createParkingLot(parkingLot);

    final Vehicle vehicle = new Vehicle();
    vehicle.setType(VehicleType.CAR);
    vehicle.setLicensePlate("ABC123");

    final Spot parkedSpot = new Spot();
    parkedSpot.setId(1L);
    parkedSpot.setType(SpotType.CAR);
    parkedSpot.setVehicle(vehicle);

    mockMvc.perform(post("/api/parking-lot/park-vehicle")
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(vehicle)))
           .andExpect(status().isOk());

  }

  @Test
  void testUnparkVehicle() throws Exception {
    final ParkingLot parkingLot = new ParkingLot();
    parkingLot.setName("Test Parking Lot");
    parkingLot.setCapacity(6);
    parkingLot.setCarSpots(2);
    parkingLot.setMotorcycleSpots(2);
    parkingLot.setVanSpots(2);

    var spotList = Arrays.asList(
        Spot.builder().type(SpotType.MOTORCYCLE).build(),
        Spot.builder().type(SpotType.MOTORCYCLE).build(),
        Spot.builder().type(SpotType.CAR).build(),
        Spot.builder().type(SpotType.CAR).build(),
        Spot.builder().type(SpotType.VAN).build(),
        Spot.builder().type(SpotType.VAN).build()
    );

    parkingLot.setSpots(spotList);

    // Create a parking lot using the ParkingLotService
    final ParkingLot createdParkingLot = parkingLotService.createParkingLot(parkingLot);

    // given
    final Vehicle vehicle = new Vehicle();
    vehicle.setType(VehicleType.CAR);
    vehicle.setLicensePlate("ABC123");

    Spot unparkedSpot = new Spot();
    unparkedSpot.setId(1L);
    unparkedSpot.setVehicle(vehicle);

    // when
    given(parkingLotServiceImpl.unparkVehicle(vehicle.getLicensePlate())).willReturn(unparkedSpot);

    // then
    mockMvc.perform(post("/api/parking-lot/unpark-vehicle/{licensePlate}", vehicle.getLicensePlate()))
           .andExpect(status().isOk());
  }

  @Test
  void testParkingLotFull() throws Exception {
    // Mock the parkingLotService's isParkingLotFull method to return false (i.e. parking lot is not full)
    when(parkingLotService.isParkingLotFull()).thenReturn(true);

    // Make the HTTP request to the "/is-full" endpoint
    ResponseEntity<String> response = parkingLotController.isParkingLotFull();

    // Assert that the response status code is 200 OK
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Assert that the response body is "Available Spots"
    assertEquals("Full Parking Lot", response.getBody());
  }

  @Test
  void testParkingLotIsNotFull() throws Exception {
    // Mock the parkingLotService's isParkingLotFull method to return false (i.e. parking lot is not full)
    when(parkingLotService.isParkingLotFull()).thenReturn(false);

    // Make the HTTP request to the "/is-full" endpoint
    ResponseEntity<String> response = parkingLotController.isParkingLotFull();

    // Assert that the response status code is 200 OK
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Assert that the response body is "Available Spots"
    assertEquals("Available Spots", response.getBody());
  }

  @Test
  void testCountAvailableSpotsIsZero() throws Exception {
    final int expectedCount = 0;
    final ParkingLotService parkingLotService = mock(ParkingLotService.class);
    Mockito.when(parkingLotService.countAvailableSpots()).thenReturn(expectedCount);

    final MvcResult result = mockMvc.perform(get("/api/parking-lot/available-spots"))
                                    .andExpect(status().isOk())
                                    .andReturn();

    final String responseBody = result.getResponse().getContentAsString();
    final int actualCount = Integer.parseInt(responseBody);
    assertEquals(expectedCount, actualCount);
  }

  @Test
  void testCountOccupiedSpots() throws Exception {
    // setup
    int expectedOccupiedSpots = 3;
    when(parkingLotService.countOccupiedSpots()).thenReturn(expectedOccupiedSpots);

    // execute
    ResponseEntity<Integer> response = parkingLotController.countOccupiedSpots();

    // verify
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedOccupiedSpots, response.getBody().intValue());
    verify(parkingLotService, times(1)).countOccupiedSpots();
  }

  @Test
  void testCountVanSpots() throws Exception {
    // Arrange
    ParkingLotService parkingLotService = mock(ParkingLotService.class);
    ParkingLotController parkingLotController = new ParkingLotController(parkingLotService);

    // Set up the mock service to return a specific number of van spots
    int expectedCount = 5;
    when(parkingLotService.countVanSpots()).thenReturn(expectedCount);

    // Act
    ResponseEntity<Integer> responseEntity = parkingLotController.countVanSpots();

    // Assert
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCount, responseEntity.getBody().intValue());
  }

  private String asJsonString(final Object obj) throws Exception {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new Exception(e.getMessage(), e);
    }
  }
}

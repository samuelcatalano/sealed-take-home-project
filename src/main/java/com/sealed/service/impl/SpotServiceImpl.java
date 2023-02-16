package com.sealed.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import com.sealed.entity.Spot;
import com.sealed.exception.ServiceException;
import com.sealed.repository.SpotRepository;
import com.sealed.service.SpotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SpotServiceImpl implements SpotService {

  private final SpotRepository spotRepository;

  @Autowired
  public SpotServiceImpl(final SpotRepository spotRepository) {
    this.spotRepository = spotRepository;
  }

  /**
   * Persists a single {@link Spot} entity in the database.
   * @param spot the spot to be persisted
   * @return the persisted spot
   * @throws ServiceException if an error occurs during data access or persistence
   */
  @Override
  public Spot createSpot(final Spot spot) throws ServiceException {
    try {
      return spotRepository.save(spot);
    } catch (final Exception e) {
      log.error("Error persisting a new spot: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting a new spot", e);
    }
  }

  /**
   * Updates an existing {@link Spot} entity in the database with the new information provided.
   * @param id   the id of the spot to be updated
   * @param spot the new information for the spot
   * @return the updated spot
   * @throws ServiceException if the spot is not found or an error occurs during data access or persistence
   */
  @Override
  public Spot updateSpot(final Long id, final Spot spot) throws ServiceException {
    final Spot existingSpot = spotRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Spot not found with id: " + id));
    try {
      existingSpot.setOccupied(spot.isOccupied());
      existingSpot.setVehicle(spot.getVehicle());
      existingSpot.setType(spot.getType());
      return spotRepository.save(existingSpot);
    } catch (final Exception e) {
      log.error("Error persisting a new spot: {}", e.getMessage(), e);
      throw new ServiceException("Error persisting a new spot", e);
    }
  }

  /**
   * Retrieves all the {@link Spot} entities from the database.
   * @return a list of all spots
   */
  @Override
  public List<Spot> findAllSpots() {
    return spotRepository.findAll();
  }

  /**
   * Counts the number of occupied {@link Spot} entities in the database.
   * @return the number of occupied spots
   */
  @Override
  public int countOccupiedSpots() {
    return spotRepository.countByOccupied(true).intValue();
  }

  /**
   * Counts the number of available {@link Spot} entities in the database.
   * @return the number of available spots
   */
  @Override
  public int countAvailableSpots() {
    return spotRepository.countByOccupied(false).intValue();
  }

  @Override
  public Spot findById(final Long id) {
    return spotRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Spot not found with id: " + id));
  }
}

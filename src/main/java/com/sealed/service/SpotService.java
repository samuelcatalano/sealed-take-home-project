package com.sealed.service;

import com.sealed.entity.Spot;
import com.sealed.exception.ServiceException;
import java.util.List;

public interface SpotService {

  Spot createSpot(Spot spot) throws ServiceException;
  Spot updateSpot(Long id, Spot spot) throws ServiceException;
  List<Spot> findAllSpots();
  int countOccupiedSpots();
  int countAvailableSpots();
  Spot findById(Long id);
}

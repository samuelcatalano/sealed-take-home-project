package com.sealed.repository;

import com.sealed.entity.Spot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

  @Query(value = "SELECT COUNT(*) FROM spot WHERE occupied = :occupied", nativeQuery = true)
  Long countByOccupied(@Param("occupied") boolean occupied);
}

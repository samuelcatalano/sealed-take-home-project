package com.sealed.repository;

import jakarta.persistence.EntityNotFoundException;

import com.sealed.entity.Vehicle;
import com.sealed.enums.VehicleType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

  Vehicle findByLicensePlate(String licensePlate) throws EntityNotFoundException;
}

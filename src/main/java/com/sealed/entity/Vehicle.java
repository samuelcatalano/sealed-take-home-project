package com.sealed.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sealed.entity.base.BaseEntity;
import com.sealed.enums.VehicleType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "vehicle")
public class Vehicle extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private VehicleType type;

  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "spot_id")
  private Spot spot;

  @Column(name = "license_plate", nullable = false)
  private String licensePlate;

}

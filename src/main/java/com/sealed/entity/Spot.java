package com.sealed.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sealed.entity.base.BaseEntity;
import com.sealed.enums.SpotType;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "spot")
public class Spot extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private SpotType type;

  @Column(name = "occupied")
  private boolean isOccupied;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "parking_lot_id", referencedColumnName = "id")
  private ParkingLot parkingLot;

  @OneToOne(mappedBy = "spot", cascade = CascadeType.ALL)
  private Vehicle vehicle;

}

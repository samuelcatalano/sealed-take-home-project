package com.sealed.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sealed.entity.base.BaseEntity;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "parking_lot")
public class ParkingLot extends BaseEntity {

  @Column(name = "name")
  private String name;

  @JsonManagedReference
  @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Spot> spots;

  @Column(name = "capacity", nullable = false)
  private Integer capacity;

  @Column(name = "motorcycle_spots", nullable = false)
  private Integer motorcycleSpots;

  @Column(name = "car_spots", nullable = false)
  private Integer carSpots;

  @Column(name = "van_spots", nullable = false)
  private Integer vanSpots;

  public void setSpots(List<Spot> spots) {
    for (final Spot child : spots) {
      child.setParkingLot(this);
    }
    this.spots = spots;
  }
}

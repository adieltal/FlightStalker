package com.flightStalker.main.Repository;


import com.flightStalker.main.Entity.RoundTrip;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

  @Repository
  public interface RoundTripRepository extends CrudRepository<RoundTrip, String> {
}

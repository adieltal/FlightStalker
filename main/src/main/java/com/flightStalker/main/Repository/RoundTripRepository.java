package com.flightStalker.main.Repository;


import com.flightStalker.main.Entity.RoundTrip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
  //todo read about @Transactional annotation
  @Repository
  public interface RoundTripRepository extends CrudRepository<RoundTrip, String> {

    @Query("SELECT MAX(lastCheck) FROM RoundTrip")
    Long findByMaxLastCheck();

    List<RoundTrip> findAllByLastCheck(long lastCheck);
  }

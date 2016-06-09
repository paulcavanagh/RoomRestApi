package com.galvanize.repositories;

/**
 * Created by localadmin on 6/7/16.
 */

import com.galvanize.models.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomsRepository extends MongoRepository<Room, String> { }


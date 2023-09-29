package com.knucapstone.rudoori.repository;


import com.knucapstone.rudoori.model.entity.UserInvolve;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatInvolveRepository extends MongoRepository<UserInvolve, String> {

}

package it.smartcommunitylab.loratb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.smartcommunitylab.loratb.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

}

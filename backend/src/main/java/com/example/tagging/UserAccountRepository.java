package com.example.tagging;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/** Mongo-backed access to the {@code users} collection. */
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    List<UserAccount> findByContractType(ContractType contractType);
}

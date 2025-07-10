package com.example.Throws.Repository;

import com.example.Throws.domain.Member;
import com.example.Throws.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long>{
    Optional<Provider> findByProviderName(String providerName);
}

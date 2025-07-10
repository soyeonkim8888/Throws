package com.example.Throws.Repository;

import com.example.Throws.domain.Member;
import com.example.Throws.domain.Subscribe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository {
    List<Subscribe> findByUser(Member user);
}

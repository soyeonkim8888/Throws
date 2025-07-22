package com.example.Throws.repository;

import com.example.Throws.domain.Member;
import com.example.Throws.domain.Subscribe;

import com.example.Throws.domain.SubscribeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long>{
    Optional<Subscribe> findFirstByMemberOrderByEndDateDesc(Member member);

    List<Subscribe> findByMemberAndStatus(Member member, SubscribeStatus status);
    List<Subscribe>findByMember(Member member);
}

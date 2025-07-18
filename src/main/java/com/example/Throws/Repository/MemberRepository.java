package com.example.Throws.Repository;

import com.example.Throws.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    List<Member> findByProvider_ProviderName(String provider);

    @Query("SELECT DISTINCT m FROM Member m JOIN m.subscriptions s WHERE s.status = 'ACTIVE'")
    List<Member> findMembersWithActiveSubscription();

    @Query("SELECT DISTINCT m FROM Member m JOIN m.subscriptions s")
    List<Member> findMembersWithSubscriptionHistory();

    @Query("SELECT DISTINCT m FROM Member m JOIN m.subscriptions s WHERE s.status = 'CANCELED'")
    List<Member> findMembersWithCanceledSubscription();

    @Query("SELECT DISTINCT m FROM Member m JOIN m.subscriptions s WHERE s.status = :status AND s.updatedAt BETWEEN :start AND :end")
    List<Member> findMembersBySubscriptionPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("status") String status);
}




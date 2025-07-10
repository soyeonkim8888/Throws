package com.example.Throws.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Throws.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberManageRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}

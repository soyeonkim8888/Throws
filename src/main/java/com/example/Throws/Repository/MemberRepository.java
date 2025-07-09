package com.example.Throws.Repository;

import com.example.Throws.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository(Member, Long){
    Optional<Member> findByEmail(String email);
}

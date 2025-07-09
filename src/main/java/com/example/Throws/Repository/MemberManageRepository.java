package com.example.Throws.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberManageRepository extends JpaRepository(Member, Long) {
}

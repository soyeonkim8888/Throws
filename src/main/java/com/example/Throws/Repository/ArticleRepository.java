package com.example.Throws.Repository;

import com.example.Throws.domain.Article;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Throws.domain.Member;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{
    List<Article> findByMember (Member member);
}

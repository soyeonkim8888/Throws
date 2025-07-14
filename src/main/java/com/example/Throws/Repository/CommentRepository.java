package com.example.Throws.Repository;

import com.example.Throws.domain.Comment;
import com.example.Throws.domain.Member;
import com.example.Throws.domain.Article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository {
    List<Comment> findByArticle(Article article);
    List<Comment> findByMember(Member member);
}

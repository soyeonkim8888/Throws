package com.example.Throws.Service;

import com.example.Throws.domain.Article;
import com.example.Throws.domain.Member;
import com.example.Throws.Repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Article createArticle(String title, String body, Member member) {
        Article article = Article.builder()
                .title(title)
                .body(body)
                .member(member)
                .build();
        return articleRepository.save(article);
    }

    public Optional<Article> getArticle(Long id) {
        return articleRepository.findById(id);
    }

    public List<Article> getArticlesByMember(Member member) {
        return articleRepository.findByMember(member);
    }

    public Article updateArticle(Long id, String title, String body) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        article.setTitle(title);
        article.setBody(body);
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}

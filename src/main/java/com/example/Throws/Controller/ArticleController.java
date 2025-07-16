package com.example.Throws.Controller;

import com.example.Throws.DTO.Article.ArticleDTO;
import com.example.Throws.domain.Article;
import com.example.Throws.domain.Member;
import com.example.Throws.Service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {
    private final ArticleService articleService;

    // 글 생성
    @PostMapping
    public Article createArticle(@RequestBody ArticleDTO dto, @RequestAttribute Member member) {
        return articleService.createArticle(dto.getTitle(), dto.getBody(), member);
    }

    // 글 단건 조회
    @GetMapping("/{id}")
    public Article getArticle(@PathVariable Long id) {
        return articleService.getArticle(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    // 작성자별 글 목록
    @GetMapping("/member/{memberId}")
    public List<Article> getArticlesByMember(@PathVariable Long memberId) {
        // memberId로 Member 객체 조회하는 코드 필요!
        // 임시로 생략
        return null;
    }

    // 글 수정
    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody ArticleDTO dto) {
        return articleService.updateArticle(id, dto.getTitle(), dto.getBody());
    }

    // 글 삭제
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
    }
}

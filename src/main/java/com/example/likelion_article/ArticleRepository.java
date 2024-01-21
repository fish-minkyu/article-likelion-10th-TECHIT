package com.example.likelion_article;

import com.example.likelion_article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
  // ID가 큰 순서대로 최상위 20개
  List<Article> findTop20ByOrderByIdDesc();
}

package com.example.likelion_article.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
CREATE TABLE article(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT,
  content TEXT,
  writer TEXT
)
*/

// entity와 dto 중 entity를 먼저 적어주자!
@Getter
@Entity // Entity의 조건은 default constructor가 있어야 한다는 것이다.
@NoArgsConstructor
public class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Setter
  private String title;
  @Setter
  private String content;
  @Setter
  private String writer;

  // 1:N관계 설정 시, readAll할 때 너무 많은 데이터가 불러오질 수 있으므로 일단 하지 않는다.
//  @OneToMany
//  private List<Comment> comments = new ArrayList<>();

  public Article(String title, String content, String writer) {
    this.title = title;
    this.content = content;
    this.writer = writer;
  }
}

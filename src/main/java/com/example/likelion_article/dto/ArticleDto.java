package com.example.likelion_article.dto;

import com.example.likelion_article.entity.Article;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
  // id를 만들어준 이유
  // : 게시글을 만들었다란 정보를 사용자에게 전달을 해줘야 함
  // 즉, 새로 만든 데이터의 상태를 전달하기 위해 dto의 id를 만드는 것을 권장
  private Long id;
  @Setter
  private String title;
  @Setter
  private String content;
  @Setter
  private String writer;

  // 특정 재료가 있을 때, ArticleDto를 만드는 방법을
  // 클래스에 기록해서 코드 중복을 줄이고 사용성을 높이는
  // Static Factory Method Pattern
  // : 클래스에 똑같은 조건으로 어떠한 물건을 만들어내는 방법이다.
  public static ArticleDto fromEntity(Article entity) {
    // 다음 행동을 위해 id를 포함해서 응답을 해주는 것이 중요
    // (read, update, delete에서 id를 사용하니까)
    return new ArticleDto(
      entity.getId(),
      entity.getTitle(),
      entity.getContent(),
      entity.getWriter()
    );
  }
}

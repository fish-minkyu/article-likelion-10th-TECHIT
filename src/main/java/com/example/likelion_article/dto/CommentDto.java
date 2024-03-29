package com.example.likelion_article.dto;

import com.example.likelion_article.entity.Comment;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
  private Long id;
  @Setter
  private String content;
  @Setter
  private String writer;

  // static factory method
  public static CommentDto fromEntity(Comment entity) {
    return new CommentDto(
      entity.getId(),
      entity.getContent(),
      entity.getWriter()
    );
  }
}

package com.example.likelion_article;

import com.example.likelion_article.dto.CommentDto;
import com.example.likelion_article.entity.Article;
import com.example.likelion_article.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service // 비즈니스 로직: 내가 실제로 사용할 수 있는지 아닌지 판단하는 로직
@RequiredArgsConstructor
public class CommentService {
  private final ArticleRepository articleRepository;
  private final CommentRepository commentRepository;

  // CREATE
  public CommentDto create(Long articleId, CommentDto dto) {
    Optional<Article> optionalArticle
      = articleRepository.findById(articleId);
    // 실존하는 게시글인지 확인
    if (optionalArticle.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Comment newComment = new Comment(
      dto.getContent(),
      dto.getWriter(),
      optionalArticle.get()
    );

    return CommentDto.fromEntity(commentRepository.save(newComment));
  }

  // READ

  // way1. Article에 OneToMany 어노테이션 붙여주기
  // : CommentService이므로 comment 중점으로 코드를 구성하는 것이 좀 더 옳다.

  // way2. CommentRepository의 Query Method

  public List<CommentDto> readALL(Long articleId) {
    // 게시글 존재 여부에 따른 에러 반환
    if (!articleRepository.existsById(articleId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    List<CommentDto> commentList = new ArrayList<>();
    // articleId를 바탕으로 댓글 조회
    List<Comment> comments = commentRepository.findAllByArticleId(articleId);
    for (Comment entity: comments) {
      new CommentDto();
      commentList.add(CommentDto.fromEntity(entity));
    }

    return commentList;
  }

  // UPDATE
  public CommentDto update(Long articleId, Long commentId, CommentDto dto) {


    // 수정 대상 댓글이 존재하는지 확인
    Optional<Comment> optionalComment = commentRepository.findById(commentId);
    // 없으면 404
    if (optionalComment.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Comment comment = optionalComment.get();
    // 댓글 - 게시글 불일치
    // : 게시글의 id와 댓글이 속한 게시글의 id가 불일치하면 에러 발생
    if (!articleId.equals(comment.getArticle().getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    comment.setContent(dto.getContent());
    comment.setWriter(dto.getWriter());
    return CommentDto.fromEntity(commentRepository.save(comment));
  }


  // DELETE
  public void delete(Long articleId, Long commentId) {

    Optional<Comment> optionalComment =
      commentRepository.findById(commentId);

    if (optionalComment.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    Comment comment = optionalComment.get();
    // 댓글 - 게시글 불일치
    if (!articleId.equals(comment.getArticle().getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    commentRepository.deleteById(commentId);
  }
}

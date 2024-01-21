package com.example.likelion_article;

import com.example.likelion_article.dto.ArticleDto;
import com.example.likelion_article.entity.Article;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service // 비즈니스 로직을 담당하는 클래스
@RequiredArgsConstructor
public class ArticleService {
  private final ArticleRepository repository;

  // CREATE
  public ArticleDto create(ArticleDto dto) {
    // 이 부분 수업 놓침
    // way1. new 생성자 사용
    Article newArticle = new Article(
      dto.getTitle(),
      dto.getContent(),
      dto.getWriter()
    );
    // way2. setter 사용
    // article.setTitle(dto.getTitle());

    // 11시 수업,설명 놓침
    newArticle = repository.save(newArticle);
    // 여기 왜 id를 넣어주는게 좋은지 설명 자세히 적어두기
    // 다음 행동을 위해 id를 포함해서 응답을 해주는 것이 중요
    // (read, update, delete에서 id를 사용하니까)
   return ArticleDto.fromEntity(repository.save(newArticle));
  }

  // READ ALL
//  public List<ArticleDto> readAll() {
//    List<ArticleDto> articleList = new ArrayList<>();
//
//    List<Article> articles = repository.findAll();
//    for(Article entity: articles) {
//      articleList.add(ArticleDto.fromEntity(entity));
//    }
//    return articleList;
//  }

  // READ ALL PAGED
  // page: 몇번째 페이지인지 나타내는 인자
  // limit: 한 페이지에 몇개가 들어갈지 나타내는 인자
  public List<ArticleDto> readAllPage(Integer page, Integer limit) {

    List<ArticleDto> articleDtoList = new ArrayList<>();
    // way1. findAll의 결과 List를 일부만 반환하는 방법 - 실무에서 쓴다면 안되는 방법
/*    List<Article> articles = repository.findAll();
    for (int i = 0; i < 20; i++) {
      articleDtoList.add(ArticleDto.fromEntity(articles.get(i))); // page * limit + i
    }*/

    // way2. DB 자체에서 limit만큼만 조회
    // Query Method를 이용해서 특정 갯수 이후의 게시글만 조회하게 한다.
    List<Article> articles = repository.findTop20ByOrderByIdDesc();
    for (Article entity: articles) {
      articleDtoList.add(ArticleDto.fromEntity(entity));
    }

    return articleDtoList;
  }

  // 페이지네이션(JPA 페이지네이션)
  public Page<ArticleDto> readAllPagination(Integer page, Integer limit) {
    // 3. JPA PagingAndSortingRepository
    Pageable pageable = PageRequest.of(
      page -1, // page 기본값이 1이므로 사용자 친화적이게 -1을 해주자
      limit,
      Sort.by(Sort.Direction.DESC, "id"));

    Page<Article> articleEntityPage = repository.findAll(pageable);
  /*Page<ArticleDto> articleDtoPage = articleEntityPage.map(article -> {
      return ArticleDto.fromEntity(article);
  });*/

    Page<ArticleDto> articleDtoPage = articleEntityPage
      .map(ArticleDto::fromEntity);

    return articleDtoPage;
  }


  // READ ONE
  public ArticleDto readOne(Long id) {
    Optional<Article> optionalArticle = repository.findById(id);
    // 해당하는 Article이 있었다.
    if (optionalArticle.isPresent()) {
     Article article = optionalArticle.get();
     return ArticleDto.fromEntity(article);
    }
    // 없으면 예외를 발생시킨다.
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  // UPDATE
  public ArticleDto update(Long id, ArticleDto dto) {
    Optional<Article> optionalArticle = repository.findById(id);
    if (optionalArticle.isPresent()) {
      Article targetEntity = optionalArticle.get();
      targetEntity.setTitle(dto.getTitle());
      targetEntity.setContent(dto.getContent());
      targetEntity.setWriter(dto.getWriter());

      return ArticleDto.fromEntity(repository.save(targetEntity));
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  }

  //DELETE
  public void delete(Long id) {
    // existsById()는 id로 존재하는지 안하는지 찾아주는 메소드
    if (repository.existsById(id)) {
      repository.deleteById(id);
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}

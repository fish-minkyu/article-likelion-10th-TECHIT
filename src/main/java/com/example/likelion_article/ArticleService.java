package com.example.likelion_article;

import com.example.likelion_article.dto.ArticleDto;
import com.example.likelion_article.entity.Article;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    // way1. new 생성자 사용 -> 그 안에다가 데이터를 한꺼번에 넣어서 객체 생성
    Article newArticle = new Article(
      dto.getTitle(),
      dto.getContent(),
      dto.getWriter()
    );
    // way2. setter 사용
    // article.setTitle(dto.getTitle());

    // save 메소드는 entity를 받으면 entity를 반환한다.
    // 그 이유는 JPA를 만든 사람들이
    // 한번 저장하거나 수정한 entity는 다시 사용하는 것을 권장하지 않는다고 말했기 때문이다.
    newArticle = repository.save(newArticle);

    return ArticleDto.fromEntity(repository.save(newArticle));
  }

  // READ ALL
  // 기존 readAll
/*  public List<ArticleDto> readAll() {
    List<ArticleDto> articleList = new ArrayList<>();

    List<Article> articles = repository.findAll();
    for(Article entity: articles) {
      articleList.add(ArticleDto.fromEntity(entity));
    }
    return articleList;
  }
*/

  // READ ALL PAGED
  // page: 몇번째 페이지인지 나타내는 인자
  // limit: 한 페이지에 몇개가 들어갈지 나타내는 인자
  /*
  public List<ArticleDto> readAllPaged(Integer page, Integer limit) {

    List<ArticleDto> articleDtoList = new ArrayList<>();
    // way1. findAll의 결과 List를 일부만 반환하는 방법 - 실무에서 쓴다면 안되는 방법
    // : page랑 limit의 값 상관없이 앞에서부터 20개까지 read
    // => 성능상에 문제가 있기 때문에 실무에선 쓰이지 않음
   *//* List<Article> articles = repository.findAll();
    for (int i = 0; i < 20; i++) { // page 변수를 활용하여, i = page * limit
      articleDtoList.add(ArticleDto.fromEntity(articles.get(i)));
    }*//*

    // way2. DB 자체에서 limit만큼만 조회
    // Query Method를 이용해서 특정 갯수 이후의 게시글만 조회하게 한다.
    // => 페이지 이동을 위해 각 페이지의 마지막 id가 무엇인지 알아야하기 때문에 비효율적
    *//*List<Article> articles = repository.findTop20ByOrderByIdDesc();
    for (Article entity: articles) {
      articleDtoList.add(ArticleDto.fromEntity(entity));
    }*//*

    return articleDtoList;
  }
  */

  // 페이지네이션(JPA 페이지네이션)
  // : JPA에서는 "pageable"이란 기능을 가지고 있다.
  // JpaRepository에서 상속받고 있는 PagingAndSortingRepository라는 페이지 기능 지원 인터페이스가 있다.
  // 그 안에 Page<T> findAll(Pageable pageable)란 메소드가 있다.
  // 즉, 페이지에 대한 정보만 있어도 쉽게 페이지를 나눠서 결과를 반환할 수 있다.
  public Page<ArticleDto> readAllPagination(
    Integer page, // 몇번째 페이지인지
    Integer limit // 한 페이지에 몇개가 들어갈지
  ) {
    // way3. JPA PagingAndSortingRepository
    // Pageable이란 객체를 통해서 JAP repository의 findAll이 넘겨받음으로써 Page를 돌려준다.
    Pageable pageable = PageRequest.of(
      page -1, // 첫 page는 0이므로 사용친화적이게 -1을 해주자. => 1페이지는 컴퓨터에겐 0페이지
      limit,
      Sort.by(Sort.Direction.DESC, "id")); // 1번째 인자, 내림차순, 2번째 인자: 기준 컬럼

    Page<Article> articleEntityPage = repository.findAll(pageable);

    // Page는 Optional과 Stream의 map 메소드와 비슷한 map을 가지고 있다.
    // Page<ArticleDto> articleDtoPage = articleEntityPage.map(article -> {
    //     return ArticleDto.fromEntity(article); // map은 내부에 함수를 인자로 받기 때문에 return이 온다.
    // });

    Page<ArticleDto> articleDtoPage = articleEntityPage
      .map(ArticleDto::fromEntity);

    return articleDtoPage;

    // Page는 iterable 객체여서 순회하면서 데이터를 확인할 수 있다.
    // for (Article entity: articleEntityPage) {
    //   log.info(ArticleDto.fromEntity(entity).toString());
    // }
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

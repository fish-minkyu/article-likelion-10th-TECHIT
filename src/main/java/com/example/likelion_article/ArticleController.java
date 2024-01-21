package com.example.likelion_article;

import com.example.likelion_article.dto.ArticleDto;
import com.example.likelion_article.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
CRUD를 위한 URL
POST /articles -> create()
GET /articles -> readAll()
GET /articles/{id} -> read()
PUT /articles/{id} -> update()
DELETE /articles/{id} -> delete()
*/
@Slf4j
@RestController // 모든 method에 @ResponseBody 어노테이션을 추가 -> 반환값이 데이터 그 자체를 반환
@RequestMapping("/articles")// 모든 Method가 공유하는 경로를 붙일 수 있다.
@RequiredArgsConstructor
public class ArticleController {
  private final ArticleService service;

  @PostMapping // 괄호를 굳이 열 필요는 없다.
  public ArticleDto create(
    // JSON 데이터는 Body에 들어가므로
    @RequestBody ArticleDto dto
  ) {
    return service.create(dto);
  }

  // 기존 readAll API
/*  @GetMapping
  public List<ArticleDto> readAll() {
    return service.readAll();
  }*/

/*  @GetMapping
  public List<ArticleDto> readAllPaged(
    @RequestParam(value = "page", defaultValue = "1") Integer page,
    @RequestParam(value = "limit", defaultValue = "20") Integer limit
  ) {
    return service.readAllPage(page, limit);
  }*/

  @GetMapping
  public Page<ArticleDto> readAllPagination(
    @RequestParam(value = "page", defaultValue = "1") // localhost:8080/articles?page=2&limit=20
    Integer page,
    @RequestParam(value = "limit", defaultValue = "20")
    Integer limit
  ) {
    return service.readAllPagination(page, limit);
  }


  @GetMapping("/{id}")
  public ArticleDto read(
    @PathVariable("id") Long id
  ) {
    return service.readOne(id);
  }

  // Data를 일부 수정해서 전달하는 것보다
  // 전체를 수정하고 전달하는 것이 더 쉽다.
  @PutMapping("/{id}")
  public ArticleDto update(
    @PathVariable("id") Long id,
    @RequestBody ArticleDto dto
  ) {
    return service.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(
    @PathVariable("id") Long id
  ) {
    service.delete(id);
  }
}

# likelion_article
- 2024.01.18 ~ 01.19
- 01.18: RESTful 구현
- 01.19: Query Parameter, pagination

RESTful를 기준으로 CRUD를 작성  
해당 프로젝트에선 특히 `pagination`을 구현한 코드로직이 핵심 부분이다.

## 스택

- Spring Boot 3.2.1
- Spring Boot Data JPA
- Lombok
- SQLite

## Key Point

[관계 설정 시, 주의할 점](/src/main/java/com/example/likelion_article/entity/Article.java)
```java
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

  // 1:N관계 설정 시, 생길 수 있는 문제 2가지
  // Problem 1. readAll할 때 너무 많은 데이터가 불러오질 수 있으므로 일단 하지 않는다.
  // Problem 2. 양방향 관계 시, 서로 순환 참조를 하게 되어 stackOverFlow를 일으킬 수 있다.
//  @OneToMany
//  private List<Comment> comments = new ArrayList<>();

  public Article(String title, String content, String writer) {
    this.title = title;
    this.content = content;
    this.writer = writer;
  }
}
```
[Static Factory Method Pattern](/src/main/java/com/example/likelion_article/dto/ArticleDto.java)
```java
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
```
[Query Parameter](/src/main/java/com/example/likelion_article/QueryController.java)
```java
@Slf4j
@RestController
public class QueryController {
  // http://localhost:8080/query-test?name=minkyu
  // ? 뒤쪽은 query parameter라고 부른다.
  // key-value 형태로 기입이 된다.
  // 두개 이상 넣을 시 앤퍼센트연산자(&)을 넣는다.
  // Query parameter는 어떤 자원이 어떤 특정한 조건을 만족시킨다란 의미에서 주로 많이 활용하는 URL의 component다.
  @GetMapping("/query-test")
  public void queryParams(
    // Query Parameter를 받아올 때 사용 가능
    @RequestParam("name") String name,
    @RequestParam(value = "age", required = false) Integer age, // 필수값 설정은 required 속성으로 조절할 수 있다.
    @RequestParam(value = "height", defaultValue = "175") Integer height, // defaultValue은 문자열만 넣을 수 있다.
    @RequestParam("weight") Integer weight
  ) {
    // RequestParam으로 데이터를 받아 crud를 하는 것은 URL의 정의에 맞지 않다. + 보안 문제
    log.info(name);
    log.info(String.valueOf(age));
    log.info(height.toString());
    log.info(weight.toString());
  }
}
```
[Pagination](/src/main/java/com/example/likelion_article/ArticleService.java)
```java
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
```


## 복습
~~2024.01.21 완료~~


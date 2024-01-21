package com.example.likelion_article;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Controller에서 query parameter 받아보기

@Slf4j
@RestController
public class QueryController {
  // http://localhost:8080/query-test?name=minkyu
  // ? 뒤쪽은 query parameter라고 부른다.
  // key-value 형태로 기입이 된다.
  // 두개 이상 넣을 시 앤퍼센트연산자(&)을 넣는다.
  // 이 자원이 특정한 조건을 만족시킨다에 쓰는 ???
  @GetMapping("/query-test")
  public void queryParams(
    // Query Parameter를 받아올 때 사용 가능
    @RequestParam("name") String name,
    @RequestParam(value = "age", required = false) Integer age, // 필수값 설정은 required 속성으로 조절할 수 있다.
    @RequestParam(value = "height", defaultValue = "175") Integer height, // defaultValue은 문자열만 넣을 수 있다.
    @RequestParam("weight") Integer weight
  ) {
    // RequestParam으로 데이터를 받아 crud를 하는 것은 URL의 정의에 맞지 않다. + 보안 문ㅈ
    log.info(name);
    log.info(String.valueOf(age));
    log.info(height.toString());
    log.info(weight.toString());
  }
}

# Spring Cloud Gateway
Spring Cloud Gateway는 (이하 ```SCG```) MSA 환경에서 사용하는 API Gateway 중 하나로 API 라우팅 및 보안, 모니터링 / 메트릭 등의 기능을 간단하고 효과적인 방법으로 제공해준다.

SCG를 알아보기 앞서 우선 Gateway란 무엇인가?

> Gateway
>
> 게이트웨이는 서로 다른 두 개 이상의 네트워크를 연결에 사용되는 네트워크 노드이다.
> 모든 데이터는 라우팅되기 전 게이트웨이를 통과하거나 게이트웨이와 통신을 해야 한다.
> 고로, 게이트웨이는 네트워크의 진입점과 종료점 역할을 한다.

이제 API Gateway에 대해 알아보자.

## API Gateway

![image](https://github.com/jekyllPark/spring-cloud-gateway-tutorial/assets/114489012/eea83cf5-262f-4955-a4ed-187da883a2bb)

백엔드 시스템 및 서비스에 대한 액세스를 제어하는 방법으로, 클라이언트 앞 단에 위치하여 모든 서버로의 요청을 단일 지점을 거쳐 처리하도록 한다.

이를 통해 공통된 로직의 처리나 인증 및 인가, 라우팅 등을 할 수 있다.

### 역할
- API 라우팅
- 인증 및 권한 부여
- 속도 제한
- 로드 밸런싱
- 모니터링
- 오케스트레이션
- 메디에이션

다시 SCG로 돌아와, 이를 사용하는 이유는 무엇일까?

## 사용 이유
SCG는 게이트웨이 구성 및 배포 프로세스를 단순화한다.

일련의 구성 파일을 사용하여 사용자는 게이트웨이를 신속하게 설정하고 실행할 수 있으며, 개발자가 특정 요구 사항에 맞게 게이트웨이를 조정할 수 있도록 다양한 사용자 정의 옵션을 제공한다.

## SCG의 아키텍처
![image](https://github.com/jekyllPark/spring-cloud-gateway-tutorial/assets/114489012/40a9b74d-f5ef-4b67-9ca9-becbe2e26f78)

1. Routes
   - 고유ID, 목적지 URI, Predicate, Filter로 구성.
   - Gateway로 요청된 URI와 매핑 시켜준다.
2. Predicates
   - 요청이 주어진 조건을 충족하는지를 검증한다.
   - 매칭되지 않을 경우, 404 Not Found 응답을 반환.
3. Filters
   - 인증, 권한 부여, 속도 제한, 로깅, 요청/응답 변환 등 다양한 목적으로 사용된다.

> 프로세스
>
> 클라이언트가 SCG에 요청 -> Gateway Handler Mapping을 통해 요청과 Predicates 일치 확인
> 
> -> 일치할 경우, Gateway Web Handler로 요청을 보냄
> 
> -> Pre Filter 체인을 거쳐 요청을 서비스에 라우팅 -> 서비스 실행
> 
> -> Post Filter 체인을 통해 클라이언트에게 데이터 반환

## 장 / 단점
### 장점
- 요청을 다른 서버로 라우팅 시키는 것을 간단히 자바 혹은 yml 파일로 구성할 수 있다.
- Non-blocking 기술 기반 (Netty)
- 서비스 요청 전/후 필터를 통해 부가 기능 추가를 손 쉽게 구현할 수 있다.
### 단점
- 대상 서버가 많아질 경우, 관리가 어려워질 수 있음.

## 구현

SCG는 ```yml``` 및 ```java``` 구성을 통해 구현할 수 있다.

### yml 방식

```
spring:
  cloud:
    gateway:
      routes:
        - id: google
          uri: http://google.com
          predicates:
            - Path=/google/{param}
          filters:
              - RewritePath=/google(?<segment>/?.*),/search
              - AddRequestParameter=q,{param}
        - id: naver
          uri: http://naver.com
          predicates:
            - Path=/naver/{param}
          filters:
              -   name: RewritePath
                  args:
                      regexp: /naver(?<segment>/?.*)
                      replacement: /search.naver
              -   name: AddRequestParameter
                  args:
                      name: query
                      value: '{param}'
```

### Java 구성 방식
```
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("google", r -> r.path("/google/{param}")
                        .filters(f -> f.rewritePath("/google(?<segment>/?.*)", "/search")
                                .addRequestParameter("q", "{param}"))
                        .uri("http://google.com"))
                .route("naver", r -> r.path("/naver/{param}")
                        .filters(f -> f.rewritePath("/naver(?<segment>/?.*)", "/search.naver")
                                .addRequestParameter("query", "{param}"))
                        .uri("http://search.naver.com"))
                .build();
    }
}
```

# Ref
- https://medium.com/@jayaramanan.kumar/getting-started-with-spring-cloud-gateway-part-one-c2177b32d01d
- https://yoonchang.tistory.com/86
- https://www.techtarget.com/iotagenda/definition/gateway
- https://www.msaschool.io/operation/implementation/implementation-six/

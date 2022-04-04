# spring-batch-example

## Spring-boot
- version: 2.6.5

## Elasticsearch
- version: 8.1.2
- 8.0 부터는 보안(SSL, https)이 기본 적용되었기 때문에 로컬 테스트를 할 경우에는<br>
  `xpack.security.enabled: false` 로 설정으로 해야 `http://localhost:9200`으로 호출 가능하다.
- 위의 설정을 하지 않고 local에서 Elasticsearch Java API Client와 연동하는 경우<br>
  Java SSL 인증서 오류가 발생한다.
    
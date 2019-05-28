[![Latest release](https://img.shields.io/github/release/longhaoteng/api-spring-boot-starter.svg)](https://github.com/longhaoteng/api-spring-boot-starter/releases/latest)
[![License](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/longhaoteng/api-spring-boot-starter/blob/master/LICENSE)

# Api-Spring-Boot-Starter

api core for spring boot starter



## Maven

```XML
<dependency>
    <groupId>com.github.longhaoteng</groupId>
    <artifactId>api-spring-boot-starter</artifactId>
    <version>latest</version>
</dependency>
```



## 配置

```yaml
spring:
  redis:
    host: localhost
  api:
    auth:  # or auth: '' 为空则不开启header auth字符串验证
           # auth: 'test' 开启验证，验证字符串为test
```



## 示例

- api

  ```java
  @API(value = "user.test")
  public class Test extends BaseApi {
      
      /**
       * api请求
       *
       * @param request     请求参数
       * @param response    响应
       * @param resp        响应参数 response->data
       * @param accessToken 当前用户token实体
       * @throws ApiException api exception
       */
      @Override
      public void handle(Request request, Response response, Map<String, Object> resp, AccessToken accessToken) throws ApiException {
          // 使用validation校验参数
          // json转java bean (json字段名需下划线命名，会自动转为java bean字段的驼峰命名)
          // user_id -> userId
          Parameters parameters = mapper(request, Parameters.class);
          validate(parameters);
        
          // 不使用validation校验参数
          String username = request.getParameter("username");
          String password = request.getParameter("password");
          System.out.println(username);
          System.out.println(password);
        
          // 可自定义响应状态码
          // 默认：200：请求成功
          //      400：请求参数错误
          //      401：未登录
          //      404：api not found
          //      407：登录过期
          //      500：服务器内部错误
          // response.setCode(org.springframework.http.HttpStatus.FORBIDDEN.value());
        
          // 返回参数 response->data
          resp.put("user", parameters);
      }
  
      @Data
      private static class Parameters {
  
          // 用户名
          @NotBlank(message = "用户名不能为空")
          private String username;
  
          // 密码
          @NotBlank(message = "密码不能为空")
          private String password;
  
      }
  }
  ```

- js

  ```js
  method: 'post',
  headers: {
    'Content-Type': 'application/json',
    auth: 'auth字符串'
  },
  url: 'http://xxx.com/api',
  data: {
    service: 'user.test', // 请求的api value
    params: { // 参数对象
        "access_token":"xxx", // @API(needLogin = true)需要
        'username': 'test',
        'password': '123'
    }
  }
  ```

- 注解@API

  - value：api service，api唯一标识，通过前端传参service请求指定api。建议格式：业务对象.操作，例：user.login
  - needLogin：默认值：false。标明接口是否需要登录权限，前端传参params.access_token
  - admin：默认值：false。标明接口是否需要管理员权限

- accessToken操作

  ```java
  @Autowired
  AccessTokenManager accessTokenManager;
  
  @Override
  public void handle(Request request, Response response, Map<String, Object> resp, AccessToken accessToken) throws ApiException {
      // login
      String key = DigestUtils.md5Hex(admin.toString() + LocalDateTime.now().toString());
      // 管理员权限添加前缀"admin."，@API(admin = true)  普通用户前缀"user."，@API(admin = false)
      String token = "admin." + key;
      // String token = "user." + key;
      accessTokenManager.save(token, AccessToken.builder().user(admin).token(token).build(), 7200L);
      resp.put("access_token", key);
  
      // logout
      accessTokenManager.remove(accessToken.getToken());
  }
  ```

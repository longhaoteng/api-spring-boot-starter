[![Latest release](https://img.shields.io/github/release/longhaoteng/api-spring-boot-starter.svg)](https://github.com/longhaoteng/api-spring-boot-starter/releases/latest)
[![License](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://github.com/longhaoteng/api-spring-boot-starter/blob/master/LICENSE)

# Api-Spring-Boot-Starter

api core for spring boot starter



## Maven [![Latest release](https://img.shields.io/badge/dynamic/json.svg?color=lightgrey&label=latest&query=tag_name&url=https://api.github.com/repos/longhaoteng/api-spring-boot-starter/releases/latest)](https://github.com/longhaoteng/api-spring-boot-starter/releases/latest)

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
    loc: # api value在request的位置 参照js请求示例
         # HEADER  api value在request的请求头中
         # BODY  api value在request的请求体中
         # default：BODY
    restExpireTime: # 每次请求后重置access token有效时间，类型Long
                    # 不填则在请求后不重置有效时间
    key: # 不为空表示接口开启AES加密 长度可以为16/24/32 即128/192/256bit(16/24/32bytes)             
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
    auth: 'auth字符串' // header auth字符串验证
    // service: 'user.test', // 请求的api value  spring.api.loc=HEADER
  },
  url: 'http://xxx.com/api',
  data: { // 如果开启AES加密，data需为加密后的base64字符串，推荐使用crypto-js
    service: 'user.test', // 请求的api value  spring.api.loc=BODY
    params: { // 参数对象
        "token":"xxx", // @API(needLogin = true)需要
        'username': 'test',
        'password': '123'
    }
  }
  ```

- 注解@API

  - value：api service，api唯一标识，通过前端传参service请求指定api。建议格式：业务对象.操作，例：user.login
  - needLogin：默认值：false。标明接口是否需要登录权限，前端传参params.token
  - roles：默认值：{}。标明接口对应角色权限，例如{"admin", "user"}，不同角色token在redis里的key前缀不一样，用于区分，单个接口支持多个角色

- accessToken操作

  ```java
  @Autowired
  AccessTokenManager accessTokenManager;
  
  @Override
  public void handle(Request request, Response response, Map<String, Object> resp, AccessToken accessToken) throws ApiException {
      // login
      // @API(roles = "")
      String token = accessTokenManager.save(AccessToken.builder().userId(admin.getId()).user(admin).build(), 7200L);
      // @API(roles = "admin")
      String token = accessTokenManager.save(AccessToken.builder().userId(admin.getId()).user(admin).role("admin").build(), 7200L);
      resp.put("token", token);
  
      // logout
      accessTokenManager.remove(accessToken.getToken());
  }
  ```

# admin-parent

## 一、架构模式

admin-apis:集成Swagger实现对接口的定义

admin-common:通用工具类

admin-model：通用Model

admin-server:admin监控

admin-web:web应用 

## 二、接口规范

### 2.1前后端调试模式

本项目中所有接口，采用**restful风格**进行开发。

> 何为restful？请见：https://blog.csdn.net/x541211190/article/details/81141459
>
> 对于restful风格api的设计，推荐阮一峰大神的[RESTFul设计指南](http://www.ruanyifeng.com/blog/2014/05/restful_api.html)

接口是后端程序员开发的，如果等待后端程序员开发完毕，再去开发前端的内容，效率是很低的。

对于前端程序员而言，其实也只是关注请求路径、参数及响应体而已，这就意味着，我们可以根据定义使用工具生成接口文档，前后端程序员按照接口文档进行开发。哪怕以后会有接口的变动也不会很大。

对于前后端联调我们采用如下模式：

```
后端：后端通过集成swagger进行接口的定义，生成接口文档，导入easy-mock中。供前端使用。后端可以根据swagger或[postman](https://www.getpostman.com)配合进行测试。

前端：使用接口时，利用**easy-mock**进行数据请求模拟。前端程序员按照mock模拟的请求数据进行开发。
```

**总体而言前后端一定是独立开发**。

### 2.2请求规范

#### 2.2.1一些约定

1.请求路径尽量采用名词形式（如user、order、article等），尽量少采用动词如（createUser、createOrder等）。

2.路径中有多个单词时使用中划线'-'来连接

3.不允许在路径中出现大写字母（参数名除外）

4.返回的数据尽量使用json，不得使用xml等其他形式

#### 2.2.2基本操作示例定义如下：

```
GET /users                    # 获取用户列表
GET /users/{userId}       # 查看单个的用户信息
POST /users                 # 新建一个用户
PUT /users/{userId}       # 全量更新某一个用户信息
PATCH /users/{userId}   # 选择性更新某一个用户信息
DELETE /users/{userId} # 删除某一个用户
```

#### 2.2.3过滤信息、查询参数

```
?limit=10：指定返回记录的数量
?offset=10：指定返回记录的开始位置。
?page=2&per_page=100：指定第几页，以及每页的记录数。
?sortby=name&order=asc：指定返回结果按照哪个属性排序，以及排序顺序。
?animal_type_id=1：指定筛选条件
```

#### 2.2.4响应的数据格式

响应的数据格式如下：

1.**正常的响应**数据示例如下：

```json
{
  	"code":0,
    "msg":"成功",
    "data":[]|{}
}
```

2.**错误提示**及增删改等**执行**操作，数据响应示例如下：

```css
{
  	"code":0,
    "msg":'新增成功',
    "data":null
}
```

3.我们就需要额外**自定义错误码**，示例如下

```json
{
  "code": 40401,
  "msg": "user 11 not found!",
  "data":null
}
```

其中，code为自定义错误码40401，error为其对应的错误内容。40401的前缀404表示资源不存在，01可以表示具体表示user这种资源不存在。

4.分页数据响应如下：

```json
{
  	"code":0,
    "msg":'成功',
    "data":{
    	"total":200,
    	"pageSize":10,
    	"pageNumber":1,
    	"rows":[]
    }
}
```

### 2.3请求头参数

对于api的版本号、安全验证等信息，我们将其放入头信息（HTTP HEADER中），下面给出在参数命名的例子：

```json
X-Token:'125454df45ds4f5s4d5f' 		--用户的登录token
Api-Version:'v1.0.0' 							--api的版本号
```

## 三、依赖集成

目标：实现对相关依赖的集成工作，并可简单使用。

### 3.1 spring-boot集成

添加parent工程

```xml
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.11.RELEASE</version>
    <relativePath/>
  </parent>
```

设置父工程打包方式为pom

```xml
	<packaging>pom</packaging>
```

添加jdk及编码属性

```xml
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>
```

对于依赖所用版本，采用properties中定义，并引用的方式执行：

```xml
    <properties>
        <commons-lang3.version>3.8.1</commons-lang3.version>
    </properties>

	<dependencies>
        <!--commons工具包-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>${commons-lang3.version}</version>
        </dependency>
    </dependencies>
```

### 3.2 spring boot test的使用

引入依赖

```xml
        <!-- spring test-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
```

添加单元测试：

```java
/**
 * 测试类示例
 *
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/1 3:01 下午
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
public class UserControllerTest {

    @Before
    public void initUser() {
    }

    @Test
    public void testAddUser() {

    }
}
```

### 3.3引入common-lang3

引入依赖

```xml
<commons-lang3.version>3.9</commons-lang3.version>
<commons-codec.version>1.13</commons-codec.version>

<!--摘要算法加密解密包-->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>${commons-codec.version}</version>
</dependency>

<!--commons工具包-->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>${commons-lang3.version}</version>
</dependency>
```

常用API参考：

https://blog.csdn.net/qq_35418518/article/details/89519979

### 3.4 hutool集成

Hutool是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅，让Java语言也可以“甜甜的”。

依赖引入：

```
<hutool.version>5.1.2</hutool.version>

<!--hutool工具包-->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>${hutool.version}</version>
</dependency>
```

具体API参考：

https://hutool.cn/docs/#/

### 3.5 集成JSR-349实现请求参数验证

添加依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

使用：

在pojo或dto对象上添加验证注解

```java
public class Singer {  
    @NotNull(message = "名称不能为空")  
    @Size(min = 2, max = 5)   
    private String fistName;  
    private String lastName;   
    @NotNull(message = "性别不能为空")  
    private Genre genre;
    ....
}
```

常用验证注解包括：

```
1 @Null 被注释的元素必须为 null
2 @NotNull 被注释的元素必须不为 null
4 @AssertTrue 被注释的元素必须为 true
5 @AssertFalse 被注释的元素必须为 false
6 @Min(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
7 @Max(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
8 @DecimalMin(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值
9 @DecimalMax(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值
10 @Size(max=, min=) 被注释的元素的大小必须在指定的范围内
11 @Digits (integer, fraction) 被注释的元素必须是一个数字，其值必须在可接受的范围内
12 @Past 被注释的元素必须是一个过去的日期
13 @Future 被注释的元素必须是一个将来的日期
14 @Pattern(regex=,flag=) 被注释的元素必须符合指定的正则表达式
15 Hibernate Validator 附加的 constraint
16 @NotBlank(message =) 验证字符串非null，且长度必须大于0
17 @Email 被注释的元素必须是电子邮箱地址
18 @Length(min=,max=) 被注释的字符串的大小必须在指定的范围内
19 @NotEmpty 被注释的字符串的必须非空
20 @Range(min=,max=,message=) 被注释的元素必须在合适的范围内
...
```

controller 中添加：

```java
@RestController
public class SingerController { 
    /**    
    * 添加@Valid 注解    
    * @param singer    
    * @return   
    */ 
    @PostMapping("/singer")    
    private String validator(@Valid @RequestBody Singer singer) {      
        System.out.print(singer);     
        return "ok"; 
}
```

此时如果校验失败，会返回JSR349的默认信息。我们需要对其进行统一处理。参见统一异常处理部分

### 3.6 集成Druid数据库连接池

Druid是Java语言中最好的数据库连接池。Druid提供了强大的监控和扩展能力

添加依赖

```xml
<druid.version>1.1.9</druid.version>     

<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid-spring-boot-starter</artifactId>
  <version>${druid.version}</version>
</dependency>
```

application.yml

```
spring:
  datasource:
    druid:
      url: jdbc:mysql://127.0.0.1:3306/test?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&useSSL=false
      username: root
      password: 123456
      driver-class-name: com.mysql.cj.jdbc.Driver --oracle 配置：oracle.jdbc.driver.OracleDriver
      # 初始化物理连接个数
      initial-size: 1
      # 最大连接池数量
      max-active: 20
      # 最小连接池数量
      min-idle: 5
      # 获取连接时最大等待时间(ms)
      max-wait: 60000
      # 开启缓存preparedStatement(PSCache)
      pool-prepared-statements: true
      # 启用PSCache后，指定每个连接上PSCache的大小
      max-pool-prepared-statement-per-connection-size: 20
      # 用来检测连接是否有效的sql
      validation-query: select 'x'  --oracle 配置：validation-query: select 1 from dual
      # 申请连接时不检测连接是否有效
      test-on-borrow: false
      # 归还连接时不检测连接是否有效
      test-on-return: false
      # 申请连接时检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效（不影响性能）
      test-while-idle: true
      # 检测连接的间隔时间，若连接空闲时间 >= minEvictableIdleTimeMillis，则关闭物理连接
      time-between-eviction-runs-millis: 60000
      # 连接保持空闲而不被驱逐的最小时间(ms)
      min-evictable-idle-time-millis: 300000
      # 配置监控统计拦截的filters（不配置则监控界面sql无法统计），监控统计filter:stat，日志filter:log4j，防御sql注入filter:wall
      filters: stat,log4j,wall
      # 支持合并多个DruidDataSource的监控数据
      use-global-data-source-stat: true
      # 通过connectProperties属性来打开mergeSql(Sql合并)功能；慢SQL记录(配置超过5秒就是慢，默认是3秒)
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

配置

```
/**
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/1/22 9:32 下午
 * @Description:
 */
@Configuration
public class DruidConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DruidConfiguration.class);

    @Bean
    public ServletRegistrationBean druidServlet() {
        logger.info("init Druid Servlet Configuration ");
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // IP白名单
//        servletRegistrationBean.addInitParameter("allow", "*");
        // IP黑名单(共同存在时，deny优先于allow)
//        servletRegistrationBean.addInitParameter("deny", "192.168.1.100");
        //控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        //是否能够重置数据 禁用HTML页面上的“Reset All”功能
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}
```

配置说明：

1.可能有人对PSCache这两项配置存在疑问？

```bash
# 开启缓存preparedStatement(PSCache)
pool-prepared-statements: true
# 启用PSCache后，指定每个连接上PSCache的大小
max-pool-prepared-statement-per-connection-size: 20
```

Druid官方建议对于MySQL数据库，关闭preparedStatement缓存(即PSCache)，即pool-prepared-statements配置为false。原因是：PSCache对支持游标的数据库性能提升巨大，比如说oracle。

访问：

http://localhost:8080/druid/

### 3.7 集成swagger便捷进行接口测试与文档编写

添加依赖

```xml
<swagger.version>2.8.0</swagger.version>

<!--swagger-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>${swagger.version}</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>${swagger.version}</version>
</dependency>
```

配置

```
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})// 设置 dev test 环境开启 prod 环境就关闭了
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.chengxiaoxiao.web.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档标题")
                .description("接口文档描述")
//                .termsOfServiceUrl("http:/xxx/xxx")
                .contact("作者")
                .version("1.0")
                .build();
    }
}
```

```
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```

常用注解：

@Api : 用在类上，说明该类的主要作用。

@ApiOperation：用在方法上，给API增加方法说明。

@ApiImplicitParams : 用在方法上，包含一组参数说明。

@ApiImplicitParam：用来注解来给方法入参增加说明。

@ApiResponses：用于表示一组响应。

@ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息

@ApiModel：用在返回对象类上，描述一个Model的信息（一般用在请求参数无法使用@ApiImplicitParam注解进行描述的时候）

 @ApiModelProperty：描述一个model的属性

使用示例：

```java

@RestController
@RequestMapping("emp")
@Api(value = "用户管理类")
public class EmployeeController {
 @Autowired
 private EmployeeReposiroty employeeReposiroty;
      /**
      * 增加人物
      * @param employee
      * @return
      */
     @PostMapping(value = "employee")
     @ApiOperation(value = "新增一个用户",notes = "新增之后返回对象")
     @ApiImplicitParam(paramType = "query",name = "employee",value = "用户",required = true)
     public String insert(Employee employee){
         
     }
      /**
      * 删除单个用户
      * @param id
      * @return
      */
      @DeleteMapping(value = "employee/{id}")
      @ApiOperation(value = "删除用户",notes = "根据成员id删除单个用户")
      @ApiImplicitParam(paramType = "path",name = "id",value = "用户id",required = true,dataType = "Integer")
      public String delete(@PathVariable("id")Integer id){
           
      }

      /**
      * 修改单个成员
      * @param employee
      * @return
      */
      @PutMapping(value = "employee/{id}")
      @ApiOperation(value = "修改用户信息",notes = "根据成员id修改单个用户")
      public String update(Employee employee){
           /**
           * save方法如果参数属性缺失，会导致原本存在的数据为null
           */
           Employee employee1 = employeeReposiroty.saveAndFlush(employee);
           if (employee1 != null) {
                return SysNode.Judge.SUCCESS.getResult();
           }else {
               return SysNode.Judge.FAILD.getResult();
           }
      }

      /**
      * 获取所有成员,升序排列
      * @return
      */
      @GetMapping(value = "employee/sort")
      @ApiOperation(value = "查询全部用户",notes = "默认根据升序查询全部用户信息")
      public List<Employee> findAll(){
           Sort orders = new Sort(Sort.Direction.DESC,"employeeId");
           List<Employee> employeeList = employeeReposiroty.findAll(orders);
           return employeeList;
      }

      /**
     * 获取所有成员,升序排列
     * @return
      */
      @GetMapping(value = "employee/pageSort")
      @ApiOperation(value = "查询用户信息",notes = "查询用户信息")
      @ApiImplicitParams({
           @ApiImplicitParam(paramType = "query",name = "sort",value = "排序方式:asc|desc",dataType = "String",required = true),
           @ApiImplicitParam(paramType = "query",name = "pagenumber",value = "第几页",dataType = "Integer",required = true),
           @ApiImplicitParam(paramType = "query",name = "pageSize",value = "分页数",dataType = "Integer",required = true)
      })
      public List<Employee> findAllByPage(String sort,Integer pagenumber,Integer pageSize){
      }
}
```

设置接收对象的参数：

```
@Data
@Entity
@Table(name = "employee")
@ApiModel(value = "用户对象模型")
public class Employee {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "employee_id")
   @Min(value = 1,groups = Employee.Children.class)
   private Integer employeeId;

   @Column(name = "user_name",length = 20,nullable = true)
   @ApiModelProperty(value = "userName",required = true)
   private String userName;

   @Column(nullable = true)
   @Size(min = 0,max = 65,message = "年龄超过范围限制",groups = Employee.Audit.class)
   @ApiModelProperty(value = "age",required = true)
   private Integer age;

   @Column(name="gra_id")
   @ApiModelProperty(value = "graId",required = true)
   //@Digits(integer = 12,fraction = 4)  //限制必须为一个小数，且整数部分的 位数 不能超过integer，小数部分的 位数 不能超过fraction
   private Integer graId;

   public interface Audit{};

   public interface Children{};
}
```

访问：

[http:localhost:9001/swagger-ui.html]()

忽略接口显示：



### 3.8 利用spring-boot-devtools进行项目热部署与加载

添加依赖

```xml
    <!--热部署插件-->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <optional>true</optional>
    </dependency>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <fork>true</fork> <!-- 如果没有该配置，devtools不会生效 -->
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### 3.9 打包jar和war

添加依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-tomcat</artifactId>
  <scope>provided</scope>
</dependency>
    
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

> 3.修改打包方式为war

```
<packaging>war</packaging>
```

> 4.mainApplication需要继承SpringBootServletInitializer,并重写configure方法

```
@Override
protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(MainApplication.class);
}
```

> 5.项目根目录打包：mvn clean package 
>
> 6.放tomcat目录下，启动（解决需要增加应用名） 
>
> 1.可以把context设置为空 
>
> 2.直接放到ROOT目录下

### 3.10 yml实现多环境部署

在`Spring Boot`中多环境配置文件名需要满足`application-{profile}.yml`的格式，其中`{profile}`对应你的环境标识;

```
application-dev 开发环境
application-test 测试环境
application-prod 生产环境
```

如果我们要激活某一个环境，只需要在 `application.yml`里：

```
spring:
  profiles:
    active: dev
```

多环境profile打包

pom文件中添加profile节点，并在build下的resources节点添加打包过滤的配置文件规则

```xml
    <profiles>
        <profile>
            <!--	开发环境		-->
            <id>dev</id>
            <properties>
                <profileActive>dev</profileActive>
            </properties>
            <!--	默认激活的环境		-->
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <profile>
            <!--	测试环境		-->
            <id>test</id>
            <properties>
                <profileActive>test</profileActive>
            </properties>
        </profile>
        <profile>
            <!--	生产环境		-->
            <id>prod</id>
            <properties>
                <profileActive>prod</profileActive>
            </properties>
        </profile>
    </profiles>
    
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>application-${profileActive}.yml</include>
                    <include>application.yml</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
        </resources>
    </build>
```

在`application.yml`中配置一个动态属性进行占位，默认的分隔符是@属性名@，这个属性会通过maven打包时传入参数进行替换;

```yml
spring:
  profiles:
    active: @profileActive@
```

打包过滤配置文件规则也是用一个占位符进行占位，打包时也会通过maven传入参数进行替换。

- 1、`通过 -D命令传入属性值profileActive`，如：

```
clean install -Dmaven.test.skip=true -DprofileActive=dev
复制代码
```

- 2、`通过-P命令指定profile环境`，如：

```
clean package -P prod
```

### 3.11 JWT引入

添加依赖

```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>${jwt.version}</version>
</dependency>
```

使用：

登录时创建token：

```java
String token = jwtUtil.createJWT(adminUser.getId(), adminUser.getLoginname(), "admin");
```

获取权限：

```java
Claims claims=(Claims)request.getAttribute("admin_claims");
```

### 3.12 权限控制spring-security

添加依赖

```xml
     <!-- spring-boot-starter-security-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```

引入spring-security：

1.添加config

```java
package com.chengxiaoxiao.web.security.config;

import com.chengxiaoxiao.common.config.JwtConfig;
import com.chengxiaoxiao.web.security.datasource.DynamicallyUrlAccessDecisionManager;
import com.chengxiaoxiao.web.security.datasource.DynamicallyUrlInterceptor;
import com.chengxiaoxiao.web.security.datasource.MyFilterSecurityMetadataSource;
import com.chengxiaoxiao.web.security.evaluator.UserPermissionEvaluator;
import com.chengxiaoxiao.web.security.handler.*;
import com.chengxiaoxiao.web.security.jwt.JWTAuthenticationTokenFilter;
import com.chengxiaoxiao.web.security.provider.UserAuthenticationProvider;
import com.chengxiaoxiao.web.service.SysResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:40 下午
 * @Description:
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启权限注解,默认是关闭的
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtConfig jwtConfig;

    @Autowired
    SysResourceService sysResourceService;

    @Bean
    public JWTAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JWTAuthenticationTokenFilter();
    }


    /**
     * 自定义登录成功处理器
     */
    @Autowired
    private UserLoginSuccessHandler userLoginSuccessHandler;
    /**
     * 自定义登录失败处理器
     */
    @Autowired
    private UserLoginFailureHandler userLoginFailureHandler;
    /**
     * 自定义注销成功处理器
     */
    @Autowired
    private UserLogoutSuccessHandler userLogoutSuccessHandler;
    /**
     * 自定义暂无权限处理器
     */
    @Autowired
    private UserAuthAccessDeniedHandler userAuthAccessDeniedHandler;
    /**
     * 自定义未登录的处理器
     */
    @Autowired
    private UserAuthenticationEntryPointHandler userAuthenticationEntryPointHandler;
    /**
     * 自定义登录逻辑验证器
     */
    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;


    /**
     * 注入自定义PermissionEvaluator
     */
    @Bean
    public DefaultWebSecurityExpressionHandler userSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new UserPermissionEvaluator());
        return handler;
    }

    /**
     * 配置登录验证逻辑
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        //这里可启用我们自己的登陆验证逻辑
        auth.authenticationProvider(userAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/v2/**")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/druid/**");
    }


    @Bean
    public DynamicallyUrlInterceptor dynamicallyUrlInterceptor() {
        DynamicallyUrlInterceptor interceptor = new DynamicallyUrlInterceptor();
        interceptor.setSecurityMetadataSource(new MyFilterSecurityMetadataSource(sysResourceService));

        //配置RoleVoter决策
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<AccessDecisionVoter<? extends Object>>();
        decisionVoters.add(new RoleVoter());
        //设置认证决策管理器
        interceptor.setAccessDecisionManager(new DynamicallyUrlAccessDecisionManager(decisionVoters));
        return interceptor;
    }

    /**
     * 配置security的控制逻辑
     *
     * @Author Sans
     * @CreateTime 2020/02/19 16:56
     * @Param http 请求
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                //不进行权限验证的请求或资源(从配置文件中读取)
                .antMatchers(jwtConfig.getAntMatchers().split(",")).permitAll()
                //其他的需要登陆后才能访问
                .anyRequest().authenticated()
                .and()
                //配置未登录自定义处理类
                .httpBasic().authenticationEntryPoint(userAuthenticationEntryPointHandler)
                .and()
                //配置登录地址
                .formLogin()
                .loginProcessingUrl("/user/login")
                //配置登录成功自定义处理类
                .successHandler(userLoginSuccessHandler)
                //配置登录失败自定义处理类
                .failureHandler(userLoginFailureHandler)
                .and()
                //配置登出地址
                .logout()
                .logoutUrl("/user/logout")
                //配置用户登出自定义处理类
                .logoutSuccessHandler(userLogoutSuccessHandler)
                .and()
                //配置没有权限自定义处理类
                .exceptionHandling().accessDeniedHandler(userAuthAccessDeniedHandler)
                .and()
                // 开启跨域
                .cors()
                .and()
                // 取消跨站请求伪造防护
                .csrf().disable();
        // 基于Token不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 禁用缓存
        http.headers().cacheControl();
        // 添加JWT filter
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(dynamicallyUrlInterceptor(), FilterSecurityInterceptor.class);
    }


}

```

2.自定义登录成功处理器

```java
/**
 * 登录成功处理类
 *
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:38 下午
 * @Description:
 */
@Slf4j
@Component
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtConfig jwtConfig;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserEntitySecurity userEntitySecurity = (UserEntitySecurity) authentication.getPrincipal();
        Set<String> authoritys = new HashSet<>();
        for (GrantedAuthority authority : userEntitySecurity.getAuthorities()) {
            authoritys.add(authority.getAuthority());
        }

        String jwt = jwtConfig.getTokenPrefix() + jwtUtil.createJWT(userEntitySecurity.getId(), userEntitySecurity.getUsername(), JSON.toJSONString(authoritys));
        ResultUtil.responseJson(response, Result.success(jwt));
    }
}
```

3.自定义登录失败处理器

```java
/**
 * 登录失败处理类
 *
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:38 下午
 * @Description:
 */
@Slf4j
@Component
public class UserLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        // 这些对于操作的处理类可以根据不同异常进行不同处理
        if (exception instanceof UsernameNotFoundException) {
            log.info("【登录失败】" + exception.getMessage());
            ResultUtil.responseJson(response, Result.error(CodeMsg.USER_NOT_EXIST));
        }
        if (exception instanceof LockedException) {
            log.info("【登录失败】" + exception.getMessage());
            ResultUtil.responseJson(response, Result.error(CodeMsg.USER_LOCKED));
        }
        if (exception instanceof BadCredentialsException) {
            log.info("【登录失败】" + exception.getMessage());
            ResultUtil.responseJson(response, Result.error(CodeMsg.USER_PASSWORD_INCORRENT));
        }
        ResultUtil.responseJson(response, Result.error(CodeMsg.ERROR));
    }
}

```

4.自定义注销成功处理器

```java

/**
 * 用户登出类
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:39 下午
 * @Description:
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        SecurityContextHolder.clearContext();
        ResultUtil.responseJson(response, Result.success(null));
    }
}

```

5.自定义暂无权限处理器

```java
/**
 * 暂无权限处理类
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:37 下午
 * @Description:
 */
@Component
public class UserAuthAccessDeniedHandler implements AccessDeniedHandler{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception){
        ResultUtil.responseJson(response, Result.success(CodeMsg.AUTHENTICATION_ERROR));
    }
}

```

6.自定义未登录的处理器

```java
/**
 * 用户未登录处理类
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:37 下午
 * @Description:
 */
@Component
public class UserAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception){

        ResultUtil.responseJson(response, Result.success(CodeMsg.USER_NOT_LOGIN_ERROR));
    }
}
```

7.自定义登录逻辑验证器

```java
/**
 * 用户登录处理类
 *
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/2 8:39 下午
 * @Description:
 */
@Component
@SuppressWarnings("all")
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SelfUserDetailsService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleResourceMapper sysRoleResourceMapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取表单输入中返回的用户名
        String userName = (String) authentication.getPrincipal();
        // 获取表单中输入的密码
        String password = (String) authentication.getCredentials();

        // 查询用户是否存在
        UserEntitySecurity userEntitySecurity = sysUserService.loadUserByUsername(userName);
        if (userEntitySecurity == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 我们还要判断密码是否正确，这里我们的密码使用BCryptPasswordEncoder进行加密的
        if (!new BCryptPasswordEncoder().matches(password, userEntitySecurity.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        // 还可以加一些其他信息的判断，比如用户账号已停用等判断
        if (userEntitySecurity.getDeleteStatus().equals(1)) {
            throw new LockedException("该用户已被冻结");
        }

        // 角色集合
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 查询用户角色
        List<SysRoleSimpleDtos> roles = sysRoleService.getRolesByUserId(userEntitySecurity.getId());
        if (roles.size() > 0) {
            List<SysResource> auths = sysRoleResourceMapper.findResourcesByRoles(roles);

            for (SysResource auth : auths) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + auth.getScourceKey()));
            }
        }
        userEntitySecurity.setAuthorities(authorities);
        userEntitySecurity.setRoles(roles);
        // 进行登录
        return new UsernamePasswordAuthenticationToken(userEntitySecurity, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
```

与jwt结合：：

jwt拦截器：

```java
**
 * JWT接口请求校验拦截器
 * 请求接口时会进入这里验证Token是否合法和过期
 *
 * @Author Sans
 * @CreateTime 2019/10/5 16:41
 */
@Slf4j
@Component
public class JWTAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中JWT的Token
        String tokenHeaderAuth = request.getHeader(jwtConfig.getTokenHeader());

        try {
            if (null != tokenHeaderAuth && tokenHeaderAuth.startsWith(jwtConfig.getTokenPrefix())) {
                // 截取JWT前缀
                String token = tokenHeaderAuth.replace(jwtConfig.getTokenPrefix(), "");
                Claims claims = jwtUtil.parseJWT(token);

                // 获取用户名
                String username = claims.getSubject();
                String userId = claims.getId();
                if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(userId) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 获取角色
                    Set<GrantedAuthority> authorities = new HashSet<>();
                    String authority = claims.get("authorities").toString();
                    if (!StringUtils.isEmpty(authority)) {
                        Set<String> authSet = JSONObject.parseObject(authority, Set.class);
                        for (String s : authSet) {
                            authorities.add(new SimpleGrantedAuthority(s));
                        }
                    }

                    //组装参数
                    UserEntitySecurity selfUserEntity = new UserEntitySecurity();
                    selfUserEntity.setUserName(claims.getSubject());
                    selfUserEntity.setId(userId);
                    selfUserEntity.setAuthorities(authorities);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(selfUserEntity, null, selfUserEntity.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            ResultUtil.responseJson(response, Result.success(CodeMsg.AUTHENTICATION_TOKEN_EXPIRED));
        } catch (Exception e) {
            ResultUtil.responseJson(response, Result.success(CodeMsg.AUTHENTICATION_ERROR));
        }
        filterChain.doFilter(request, response);
        return;
    }

}
```

动态添加权限：

AccessDecisionManager：

```
public class DynamicallyUrlAccessDecisionManager extends AbstractAccessDecisionManager {

    public DynamicallyUrlAccessDecisionManager(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        int deny = 0;

        for (AccessDecisionVoter voter : getDecisionVoters()) {
            int result = voter.vote(authentication, object, configAttributes);

            if (logger.isDebugEnabled()) {
                logger.debug("Voter: " + voter + ", returned: " + result);
            }

            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED:
                    return;

                case AccessDecisionVoter.ACCESS_DENIED:
                    deny++;

                    break;

                default:
                    break;
            }
        }

        if (deny > 0) {
            throw new AccessDeniedException(messages.getMessage(
                    "AbstractAccessDecisionManager.accessDenied", "Access is denied"));
        }

        // To get this far, every AccessDecisionVoter abstained
        checkAllowIfAllAbstainDecisions();
    }
}

```

拦截器：

```java
/**
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/21 2:45 下午
 * @Description:
 */
public class DynamicallyUrlInterceptor extends AbstractSecurityInterceptor implements Filter {

    //标记自定义的url拦截器已经加载
    private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied_dynamically";

    private FilterInvocationSecurityMetadataSource securityMetadataSource;
    private boolean observeOncePerRequest = true;


    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public FilterInvocationSecurityMetadataSource getSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public void setSecurityMetadataSource(FilterInvocationSecurityMetadataSource newSource) {
        this.securityMetadataSource = newSource;
    }

    @Override
    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        super.setAccessDecisionManager(accessDecisionManager);
    }

    public void invoke(FilterInvocation fi) throws IOException, ServletException {

        if ((fi.getRequest() != null)
                && (fi.getRequest().getAttribute(FILTER_APPLIED) != null)
                && observeOncePerRequest) {
            // filter already applied to this request and user wants us to observe
            // once-per-request handling, so don't re-do security checking
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } else {
            // first time this request being called, so perform security checking
            if (fi.getRequest() != null) {
                fi.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
            }

            InterceptorStatusToken token = super.beforeInvocation(fi);

            try {
                fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
            } finally {
                super.finallyInvocation(token);
            }

            super.afterInvocation(token, null);
        }
    }
}
```

数据源：

```java
/**
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/21 2:42 下午
 * @Description:
 */
@Component
public class MyFilterSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;


    /*
     * 这个例子放在构造方法里初始化url权限数据，我们只要保证在 getAttributes()之前初始好数据就可以了
     */
    public MyFilterSecurityMetadataSource(SysResourceService sysResourceService) {
        List<SysResource> resourceList = sysResourceService.findAll();

        Map<RequestMatcher, Collection<ConfigAttribute>> map = new HashMap<>();
        AntPathRequestMatcher matcher;
        SecurityConfig config;
        ArrayList<ConfigAttribute> configs;
        for (SysResource sysResource : resourceList) {
            matcher = new AntPathRequestMatcher(sysResource.getSourceUrl(), sysResource.getHttpMethod());
            config = new SecurityConfig("ROLE_" + sysResource.getScourceKey());
            configs = new ArrayList<>();
            configs.add(config);
            map.put(matcher, configs);
        }

        this.requestMap = map;
    }


    /**
     * 在我们初始化的权限数据中找到对应当前url的权限数据
     *
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        HttpServletRequest request = fi.getRequest();
        String url = fi.getRequestUrl();
        String httpMethod = fi.getRequest().getMethod();

        // Lookup your database (or other source) using this information and populate the
        // list of attributes (这里初始话你的权限数据)
        //List<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();

        //遍历我们初始化的权限数据，找到对应的url对应的权限
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap
                .entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
```

前后端分离下spring-security是无法直接获取application/json数据的。修改为json方式：

重写UsernamePasswordAnthenticationFilter：

```java
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //attempt Authentication when Content-Type is json
        if(request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                ||request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)){

            //use jackson to deserialize json
            ObjectMapper mapper = new ObjectMapper();
            UsernamePasswordAuthenticationToken authRequest = null;
            try (InputStream is = request.getInputStream()){
                AuthenticationBean authenticationBean = mapper.readValue(is,AuthenticationBean.class);
                authRequest = new UsernamePasswordAuthenticationToken(
                        authenticationBean.getUsername(), authenticationBean.getPassword());
            }catch (IOException e) {
                e.printStackTrace();
                authRequest = new UsernamePasswordAuthenticationToken(
                        "", "");
            }finally {
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        }

        //transmit it to UsernamePasswordAuthenticationFilter
        else {
            return super.attemptAuthentication(request, response);
        }
    }
}
```

把这个`CustomAuthenticationFilter`加到spring security的众多filter里面.

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
            .cors().and()
            .antMatcher("/**").authorizeRequests()
            .antMatchers("/", "/login**").permitAll()
            .anyRequest().authenticated()
            //这里必须要写formLogin()，不然原有的UsernamePasswordAuthenticationFilter不会出现，也就无法配置我们重新的UsernamePasswordAuthenticationFilter
            .and().formLogin().loginPage("/")
            .and().csrf().disable();

    //用重写的Filter替换掉原有的UsernamePasswordAuthenticationFilter
    http.addFilterAt(customAuthenticationFilter(),
    UsernamePasswordAuthenticationFilter.class);
}

//注册自定义的UsernamePasswordAuthenticationFilter
@Bean
CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
    CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
    filter.setAuthenticationSuccessHandler(new SuccessHandler());
    filter.setAuthenticationFailureHandler(new FailureHandler());
    filter.setFilterProcessesUrl("/login/self");

    //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
    filter.setAuthenticationManager(authenticationManagerBean());
    return filter;
}
```



### 3.13 日志引入

添加依赖

```xml
<!-- JWT依赖 -->
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-jwt</artifactId>
  <version>1.0.9.RELEASE</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>0.9.0</version>
</dependency>
```



### 3.14 异步调用

### 3.15 Websocket

### 3.16 全局异常处理

### 3.17 实现问卷效果

### 3.18 Mybatis

### 3.19 接口幂等性验证

### 3.20 activiti

### 3.21 mybatis引入

添加依赖

```
        <!--        mybatis依赖-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis-spring-boot-starter.version}</version>
        </dependency>
```

添加配置

```
## 该配置节点为独立的节点，有很多同学容易将这个配置放在spring的节点下，导致配置无法被识别
mybatis:
  mapper-locations: classpath:mapping/*.xml  #注意：一定要对应mapper映射xml文件的所在路径
  type-aliases-package: com.chengxiaoxiao.model.web.pojos  # 注意：对应实体类的路径
  configuration:
    # 开启驼峰映射
    map-underscore-to-camel-case: true
```

### 3.22 pagehelper

添加依赖

```
        <!--pagehelper依赖-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>${pagehelper.version}</version>
        </dependency>
```

添加配置：

```
#pagehelper分页插件
pagehelper:
  helperDialect: mysql
  reasonable: true
  supportMethodsArguments: true
  params: count=countSql
```

### 3.23 MyBatis Generator自动生成代码

添加依赖

```
<!-- mybatis generator 自动生成代码插件 -->
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.2</version>
                <configuration>
                    <configurationFile>${basedir}/src/main/resources/generator/generatorConfig.xml</configurationFile>
                    <overwrite>true</overwrite>
                    <verbose>true</verbose>
                </configuration>
                <!-- 配置数据库链接及mybatis generator core依赖 生成mapper时使用 -->
                <dependencies>
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>${mysql.version}</version>
                    </dependency>
                    <dependency>
                        <groupId>org.mybatis.generator</groupId>
                        <artifactId>mybatis-generator-core</artifactId>
                        <version>1.3.2</version>
                    </dependency>
                </dependencies>
            </plugin>
```

添加配置文件：resources/generator/generatorConfig.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<!-- 配置生成器 -->
<generatorConfiguration>
    <!--执行generator插件生成文件的命令： call mvn mybatis-generator:generate -e -->
    <!-- 引入配置文件 -->
    <properties resource="generator/mybatisGeneratorinit.properties"/>

    <!-- 一个数据库一个context -->
    <!--defaultModelType="flat" 大数据字段，不分表 -->
    <context id="MysqlTables" targetRuntime="MyBatis3Simple" defaultModelType="flat">
        <!-- 自动识别数据库关键字，默认false，如果设置为true，根据SqlReservedWords中定义的关键字列表；
        一般保留默认值，遇到数据库关键字（Java关键字），使用columnOverride覆盖 -->
        <property name="autoDelimitKeywords" value="true" />
        <!-- 生成的Java文件的编码 -->
        <property name="javaFileEncoding" value="utf-8" />
        <!-- beginningDelimiter和endingDelimiter：指明数据库的用于标记数据库对象名的符号，比如ORACLE就是双引号，MYSQL默认是`反引号； -->
        <property name="beginningDelimiter" value="`" />
        <property name="endingDelimiter" value="`" />

        <!-- 格式化java代码 -->
        <property name="javaFormatter" value="org.mybatis.generator.api.dom.DefaultJavaFormatter"/>
        <!-- 格式化XML代码 -->
        <property name="xmlFormatter" value="org.mybatis.generator.api.dom.DefaultXmlFormatter"/>
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin" />

        <plugin type="org.mybatis.generator.plugins.ToStringPlugin" />

        <!-- 注释 -->
        <commentGenerator >
            <property name="suppressAllComments" value="false"/><!-- 是否取消注释 -->
            <property name="suppressDate" value="true" /> <!-- 是否生成注释代时间戳-->
        </commentGenerator>

        <!-- jdbc连接 -->
        <jdbcConnection driverClass="${jdbc_driver}" connectionURL="${jdbc_url}" userId="${jdbc_user}" password="${jdbc_password}" />
        <!-- 类型转换 -->
        <javaTypeResolver>
            <!-- 是否使用bigDecimal， false可自动转化以下类型（Long, Integer, Short, etc.） -->
            <property name="forceBigDecimals" value="false"/>
        </javaTypeResolver>

        <!-- 生成实体类地址 -->
        <javaModelGenerator targetPackage="com.chengxiaoxiao.model.web.pojos" targetProject="${project}" >
            <property name="enableSubPackages" value="false"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>
        <!-- 生成mapxml文件 -->
        <sqlMapGenerator targetPackage="com.chengxiaoxiao.model.mappers" targetProject="${resources}" >
            <property name="enableSubPackages" value="false" />
        </sqlMapGenerator>
        <!-- 生成mapxml对应client，也就是接口dao -->
        <javaClientGenerator targetPackage="com.chengxiaoxiao.model.mappers" targetProject="${project}" type="XMLMAPPER" >
            <property name="enableSubPackages" value="false" />
        </javaClientGenerator>
        <!-- table可以有多个,每个数据库中的表都可以写一个table，tableName表示要匹配的数据库表,也可以在tableName属性中通过使用%通配符来匹配所有数据库表,只有匹配的表才会自动生成文件 -->
        <table tableName="sys_user" enableCountByExample="true" enableUpdateByExample="true" enableDeleteByExample="true" enableSelectByExample="true" selectByExampleQueryId="true">
            <property name="useActualColumnNames" value="false" />
            <!-- 数据库表主键 -->
            <generatedKey column="id" sqlStatement="Mysql" identity="true" />
        </table>
    </context>
</generatorConfiguration>
```

添加属性文件：mybatisGeneratorinit.properties

```
#Mybatis Generator configuration
#dao类和实体类的位置
project =../admin-model/src/main/java
#mapper文件的位置
resources=src/main/resources/mapping
#根据数据库中的表生成对应的pojo类、dao、mapper
jdbc_driver =com.mysql.jdbc.Driver
jdbc_url=jdbc:mysql://192.168.4.188:3306/xxadmin
jdbc_user=root
jdbc_password=123456
```

执行命令：

```
mvn mybatis-generator:generate
```

### 3.24 Swagger和spring Security的整合

在WebSecurityConfigurerAdapter里添加如下代码

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {

 
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring(). antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/v2/**")
                .antMatchers("/swagger-resources/**");
    }

}
```

### 3.25 TOKEN刷新机制

https://my.oschina.net/odetteisgorgeous/blog/1920762
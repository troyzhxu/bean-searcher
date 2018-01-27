# Bean Searcher

## 安装 

##### Gradle

```
dependencies {
    compile 'com.ejlchina:bean-searcher:1.3.1'
}

```

##### Maven

```
<dependencies>
	<dependency>
		<groupId>com.ejlchina</groupId>
		<artifactId>bean-searcher</artifactId>
		<version>1.3.1</version>
	</dependency>
</dependencies>
```

## 创建 Search Bean

```
@SearchBean(tables = "users u")
public class UserBean {

	@DbField("u.id")
	private Long id;

	@DbField("u.type")
	private String username;

	@DbField("u.phone")
	private String phone;

	@DbField("u.date_created")
	private Date dateCreated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}

```

## 配置

##### With Spring

```
<bean name="searcher" class="com.ejlchina.searcher.support.SpringSearcher">
	<property name="scanPackages">
		<list>  
	        <value>${Search Bean所在的包名}</value>  
    	</list>
	</property>
	<property name="searchSqlExecutor">
		<bean class="com.ejlchina.searcher.implement.MainSearchSqlExecutor">
			<property name="dataSource" ref="dataSource"/>
		</bean>
	</property>
</bean>
```

## 开始检索

##### With Spring

```
@RestController
@RequestMapping("/users")
public class UserController {


	@Autowired
	private Searcher searcher;


	@RequestMapping("/index")
	public SearchResult index(HttpServletRequest request) {
		
		// 检索检索参数
		Map<String, String> params = MapUtils.flat(request.getParameterMap());
		
		// 调用检索器检索数据
		return searcher.search(UserBean.class, params)
	}

}
```



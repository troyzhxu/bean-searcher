# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

Introduction
---

- git clone https://gitee.com/ejlchina-zhxu/bean-searcher.git
- cd bean-searcher && mvn install
- have fun.

Documentation
---

- 中文 https://gitee.com/ejlchina-zhxu/bean-searcher/wikis/Home



## 安装 

##### Gradle

```
dependencies {
    compile 'com.ejlchina:bean-searcher:1.4.3'
}

```

##### Maven

```
<dependencies>
	<dependency>
		<groupId>com.ejlchina</groupId>
		<artifactId>bean-searcher</artifactId>
		<version>1.4.3</version>
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

## 调用检索器

##### With Spring

```
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private Searcher searcher;

	@RequestMapping("/index")
	public SearchResult index(HttpServletRequest request) {
		
		// 检索参数
		Map<String, String> params = MapUtils.flat(request.getParameterMap());
		
		// 调用检索器检索数据
		return searcher.search(UserBean.class, params)
	}

}
```

## 检索功能

通过以上步骤，实际上写发出了一个API接口（/users/index），那么这个接口可以接收哪些参数，返回哪些数据呢？

<table>
	<thead>
		<tr>
			<td>参数名
			<td>参数类型
			<td>默认值
			<td>功能含义
	<tbody>
		<tr>
			<td>id
			<td>Long
			<td>
			<td>按ID查询
		<tr>
			<td>id-op
			<td>String
			<td>eq
			<td>ID的检索运算符，可以是等于、大于、小于，等等
		<tr>
			<td>username
			<td>String
			<td>
			<td>按用户名查询
		<tr>
			<td>username-op
			<td>String
			<td>eq
			<td>用户名的检索运算符，可以是等于、包含、以开头、已结尾，等等
		<tr>
			<td>username-ic
			<td>Boolean
			<td>false
			<td>按用户名检索时，是否忽略大小写










# Bean Searcher

## Install 

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

## Configuration

##### With Spring

```
<bean name="searcher" class="com.ejlchina.searcher.support.SpringSearcher">
	<property name="scanPackage" value="${packageToBeanClass}" />
	<property name="searchSqlExecutor">
		<bean class="com.ejlchina.searcher.implement.MainSearchSqlExecutor">
			<property name="dataSource" ref="dataSource"/>
		</bean>
	</property>
</bean>
```


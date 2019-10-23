# SpringBoot整合Mybatis
## 1、引入依赖文件
建议使用1.3.2版本，这个版本支持localdatetime时间类型
```xml
        <!--springboot的web模块-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

		<!--mybatis整合SpringBoot start-->
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>1.3.2</version>
		</dependency>
		<!--mybatis整合SpringBoot end-->
		
		<!--连接mysql包 start-->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<!--连接mysql包 end-->
```

## 2、mybatis在springboot中的配置
```properties
#配置mysql连接驱动 注意：低版本配置的类是com.mysql.jdbc.Driver
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
#配置数据库连接的路径
spring.datasource.url=jdbc:mysql://localhost:3306/demo?useUnicodeuseUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=换成自己的密码
#使用阿里巴巴的连接池
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource

#注意：一定要对应mapper映射xml文件的所在路径
mybatis.mapper-locations=classpath:mapper/*.xml
# 注意：对应实体类的路径
mybatis.type-aliases-package=classpath:com.tuorong.model
```

## 3、mybatis如何建立实体类与表的联系，将数据封装到实体类
首先我们的数据库和实体类是相对应的，我们可以把表中的某一行数据封装到一个bean类（实体类）,那么mybatis是如何做的呢

mybatis在设计上把sql语句和我们的业务代码分离开了，降低代码与sql语句的耦合度，在mybatis中使用一个xml配置文件统一管理sql语句，也是在这个xml文件中实现你的sql语句。

接下来我们就以一个实例展示mybatis是如何连接表与实体类

首先我们需要一张表和一个实体类

sql建表
```sql
CREATE TABLE `person` (
`id`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`age`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
```
对应的实体类
```java
package com.tuorong.model;

/**
 * Created by Z先生 on 2019/10/22.
 */
public class Person {
    private String personId;
    private String personName;
    private Integer personAge;

    public Person(String personId, String personName, Integer personAge) {
        this.personId = personId;
        this.personName = personName;
        this.personAge = personAge;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Integer getPersonAge() {
        return personAge;
    }

    public void setPersonAge(Integer personAge) {
        this.personAge = personAge;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personId='" + personId + '\'' +
                ", personName='" + personName + '\'' +
                ", personAge=" + personAge +
                '}';
    }
}
```
xml对应的接口文件
```java
package com.tuorong.dao;

import com.tuorong.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Z先生 on 2019/10/22.
 */

@Repository
public interface PersonMapper {
    List<Person> getAllPerson();
}
```

配置属于这张表的xml文件
```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.tuorong.dao.PersonMapper">
    <resultMap type="com.tuorong.model.Person" id="PersonMap">
        <id column="id" property="personId"/>
        <result column="name" property="personName"/>
        <result column="age" property="personAge"/>
    </resultMap>

    <select id="getAllPerson" resultMap="PersonMap">
        select * from person;
    </select>

</mapper>
```

那么这时候我们有了sql语句和实体类对象，要怎么调用我们写的sql语句呢？

mybatis提供了这样一种机制，通过接口来调用sql语句

例如person这张表，我们需要定义一个接口对应上面的xml文件
```java
package com.tuorong.dao;

import com.tuorong.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Z先生 on 2019/10/22.
 */

@Repository
public interface PersonMapper {
    List<Person> getAllPerson();
}
```
该注解向spring声明这是一个操作数据库的组件
```java
@Repository
```
其中namespace是mybatis的命名空间，用来绑定操作sql的接口，当你定义了接口的方法之后不用实现它，mybatis会通过方法的名称绑定对应id的sql语句，这里的namespace是上图的PersonMapper，注意namespace的值要写该接口的全路径

mybatis的标签解释

<mapper>是这个映射文件的根标签
<resultMap>标签定义了返回的结果集，type属性指明我们返回的结果集映射到Person这个对象，id是这个返回结果集的唯一标识

在mybatis中增删改查分别用了<select></selectc>,<delete></delete>,<update></update>,<select></select>标签，每一个标签都有一个id属性，这个id对应的就是接口mapper文件的方法名，mybatis通过id与方法名的绑定找到具体实现，执行该sql语句

## 3、测试连接数据库

打开src目录下的test文件夹找到DemoApplicationTests类，在contextLoads方法中编写测试代码

```java
package com.tuorong;

import com.tuorong.dao.PersonMapper;
import com.tuorong.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
	@Autowired  //注入我们编写的mapper接口
	PersonMapper personMapper;

	@Test
	public void contextLoads() {
		List<Person> people = personMapper.getAllPerson();
		for (Person person : people) {
			System.out.println(person.toString());
		}
	}

}
```

运行contextLoads方法，在控制台能查询出数据库及成功

查询结果：
```
Person{personId='1', personName='张三', personAge=32}
```
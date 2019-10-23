# freemaker的介绍

```txt
FreeMarker 是一款 模板引擎： 即一种基于模板和要改变的数据， 并用来生成输出文本(HTML网页，电子邮件，配置文件，源代码等)的通用工具。 它不是面向最终用户的，而是一个Java类库，是一款程序员可以嵌入他们所开发产品的组件。

模板编写为FreeMarker Template Language (FTL)。它是简单的，专用的语言， 不是 像PHP那样成熟的编程语言。 那就意味着要准备数据在真实编程语言中来显示，比如数据库查询和业务运算， 之后模板显示已经准备好的数据。在模板中，你可以专注于如何展现数据， 而在模板之外可以专注于要展示什么数据。
```
详情请了解freemaker官网

freemaker使用手册：
http://freemarker.foofun.cn/
# 整合步骤
## 1、将freemaker的依赖添加到pom.xml文件中
```java
<!-- springboot整合freemarker -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>

```

## 2、freemaker在springboot中的配置
```properties
# 是否启用模板缓存。
spring.freemarker.cache=false
# 模板编码。
spring.freemarker.charset=UTF-8
# 是否检查模板位置是否存在。
spring.freemarker.check-template-location=true
# Content-Type value.
spring.freemarker.content-type=text/html
# 设定模板的后缀.
spring.freemarker.suffix=.html
# 设定模板的加载路径，多个以逗号分隔，默认:
spring.freemarker.template-loader-path=classpath:/templates/

```
## 3、编写一个controller测试是否集成成功
1、首先在自己的package下建立一个controller包
![avatar](http://47.107.102.132:8099/images/freemaker/package-diretory.jpg)

2、创建一个DemoController类

代码：
```java
package com.tuorong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Z先生 on 2019/10/22.
 */
@Controller
public class DemoController {
    @RequestMapping(value = "/getDemoPage")
    public String getDemoPage(){
        return "index";
    }
}

```

3、在templates目录下新建一个index.html页面
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<p>这是一个html页面</p>
</body>
</html>
```
4、根据自己配置的项目路径和端口号访问
```properties

#配置项目的根路径
server.servlet.context-path=/demo
server.port=8082
```

5、访问http://localhost:8082/demo/getDemoPage

如果出现下图表示集成成功

![avatar](http://47.107.102.132:8099/images/freemaker/集成成功.PNG)

# 4、后台系统与freemaker的交互
当我们想把后台处理的数据返回给我们的html页面时需要借助ModelAndView类作为一个传输的工具，下面是一个模拟从数据库查出一个Person类的集合并返回给html页面实例

1、首先建立一个bean类与数据库的表对应
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
}

```

2、从数据库中查出数据并且封装到Person类中
```java
//模拟查询List集合
   List<Person> people = new ArrayList<>();
        Person p1 = new Person("1","张三",20);
        Person p2 = new Person("2","李四",30);
        people.add(p1);
        people.add(p2);
```
3、在controller中返回数据给html页面
借助ModelAndView返回数据给html页面

```java
package com.tuorong.controller;

import com.tuorong.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z先生 on 2019/10/22.
 */
@Controller
public class DemoController {
    @RequestMapping(value = "/getDemoPage")
    public ModelAndView getDemoPage(){
        List<Person> people = new ArrayList<>();
        Person p1 = new Person("1","张三",20);
        Person p2 = new Person("2","李四",30);
        people.add(p1);
        people.add(p2);
        ModelAndView modelAndView = new ModelAndView();
        //设置要返回的页面名称，省略.html后缀
        modelAndView.setViewName("index");
        //添加返回的数据，key为people，value为people集合
        modelAndView.addObject("people",people);
        return modelAndView;
    }
}

```

4、freemaker在html中是如何获取一个集合的值

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<p>这是一个html页面</p>
<table border="1">
    <tr>
        <td>个人id</td>
        <td>姓名</td>
        <td>年龄</td>
    </tr>
    <#list people as item>
        <tr>
            <td>${item.personId}</td>
            <td>${item.personName}</td>
            <td>${item.personAge}</td>
        </tr>
    </#list>
</table>
</body>
</html>
```
解释：
在freemaker中是通过${变量名}取值的，例如你返回了一个变量name值为张三,那么通过${name}取出来的值就是张三

当我们想要获取一个list集合时必须要借助freemaker给我们提供的一个标签<#list></#list>
```html
 <#list people as item>
 ```
 这里的people就是我们在ModelAndView中添加的属性的key
通过这个标签我们就可以取出数据，item是我们给people起的别名

这个标签还有其他的属性，这里就不一一展开了

5、访问的结果

![avatar](http://47.107.102.132:8099/images/freemaker/ModelAndvVew返回结果.PNG)

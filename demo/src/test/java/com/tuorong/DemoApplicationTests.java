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
	@Autowired //注入我们编写的mapper接口
	PersonMapper personMapper;

	@Test
	public void contextLoads() {
		List<Person> people = personMapper.getAllPerson();
		for (Person person : people) {
			System.out.println(person.toString());
		}
	}

}

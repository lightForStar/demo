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

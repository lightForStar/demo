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

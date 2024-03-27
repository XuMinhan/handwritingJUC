package spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.json.test;

import spring.sprintConsist.wendyNetty.processors.httpProcessor.utils.json.WendyJsonUtils;

public class TestSimpleJsonUtils {
    public static void main(String[] args) {
        Person person = new Person();
        person.setName("John Doe");
        person.setAge(30);

        // 序列化
        String json = WendyJsonUtils.serialize(person);
        System.out.println(json);

        // 反序列化
        Person personDeserialized = WendyJsonUtils.deserialize(json, Person.class);
        System.out.println(personDeserialized.getName() + ", " + personDeserialized.getAge());
    }
}

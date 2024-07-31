package webFrame.utils.json.test;

import webFrame.utils.json.JsonUtils;

public class TestSimpleJsonUtils {
    public static void main(String[] args) {
        Person person = new Person();
        person.setName("John Doe");
        person.setAge(30);

        // 序列化
        String json = JsonUtils.serialize(person);
        System.out.println(json);

        // 反序列化
        Person personDeserialized = JsonUtils.deserialize(json, Person.class);
        System.out.println(personDeserialized.getName() + ", " + personDeserialized.getAge());

        Integer i = 100;
        String serialize = JsonUtils.serialize(i);
        System.out.println(serialize);

        String a = "100";
        String serialize1 = JsonUtils.serialize(a);
        System.out.println(serialize1);

    }
}

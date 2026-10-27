package br.edu.fatec.sjc.pessoa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Person {

    private int id;
    private String name;
    private int age;
    private final List<Email> emails = new ArrayList<>();

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age) {
        this(0, name, age);
    }

    public Person addEmail(Email email) {
        this.emails.add(email);
        return this;
    }

    public List<Email> getEmails() {
        return Collections.unmodifiableList(this.emails);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }



}

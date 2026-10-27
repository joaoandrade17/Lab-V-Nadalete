package br.edu.fatec.sjc.pessoa;

public class Email {

    private int id;
    private String name;

    public Email(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Email(String name) {
        this(0, name);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }


}

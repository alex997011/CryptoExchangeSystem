package org.example.model;

public class User {
    private int id;
    private String User;
    private String password;
    private String mail;
    private String name;

    private String lastname;

    public User(int id, String user, String password,String mail, String name, String lastname) {
        this.id = id;
        User = user;
        this.password = password;
        this.mail=mail;
        this.name = name;
        this.lastname = lastname;
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return User;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(String user) {
        User = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", User='" + User + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}

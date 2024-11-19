package pl.projekt_inzynierski.company;

import jakarta.persistence.*;
import pl.projekt_inzynierski.user.User;

import java.util.List;

@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int timeToFirsRespond; // czas na pierwszą reakcje - w godzinach
    private int timeToResolve; // czas na obsłużenie zgłoszenia w godzinach

    @OneToMany(mappedBy = "company")
    private List<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeToFirsRespond() {
        return timeToFirsRespond;
    }

    public void setTimeToFirsRespond(int timeToFirsRespond) {
        this.timeToFirsRespond = timeToFirsRespond;
    }

    public int getTimeToResolve() {
        return timeToResolve;
    }

    public void setTimeToResolve(int timeToResolve) {
        this.timeToResolve = timeToResolve;
    }
}

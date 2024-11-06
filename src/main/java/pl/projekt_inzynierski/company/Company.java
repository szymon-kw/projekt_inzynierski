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
    private int criticalResponseTime; // czas na obsłużenie zgłoszenia kategorii CRITIAL (krytyczne) - w godzinach
    private int majorResponseTime; // czas na obsłużenie zgłoszenia kategorii MAJOR (poważne) - w godzinach
    private int minorResponseTime; // czas na obsłużenie zgoszenia kategorii MINOR (drobne) - w godzinach
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

    public int getCriticalResponseTime() {
        return criticalResponseTime;
    }

    public void setCriticalResponseTime(int criticalResponseTime) {
        this.criticalResponseTime = criticalResponseTime;
    }

    public int getMajorResponseTime() {
        return majorResponseTime;
    }

    public void setMajorResponseTime(int majorResponseTime) {
        this.majorResponseTime = majorResponseTime;
    }

    public int getMinorResponseTime() {
        return minorResponseTime;
    }

    public void setMinorResponseTime(int minorResponseTime) {
        this.minorResponseTime = minorResponseTime;
    }
}

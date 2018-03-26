package hotelapp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "boards")
public class Board {
    private Integer id;

    private String name;

    private String description;

    private Boss creator;

    private Set<Worker> workers;

    private Set<Task> tasks;

    public Board(String name, String description, Boss creator, Set<Worker> workers, Set<Task> tasks) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.workers = new HashSet<>();
        this.tasks = new HashSet<>();
    }

    public Board() {    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "creatorId")
    public Boss getCreator() {
        return creator;
    }

    public void setCreator(Boss creator) {
        this.creator = creator;
    }

    @ManyToMany(mappedBy = "boards")
    public Set<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }

    @OneToMany(mappedBy = "board")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}

package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "boards")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Board {
    private Integer id;

    private String name;

    private String description;

    private Boss creator;

    private Set<Worker> workers;

    @JsonBackReference
    private Set<Task> tasks;

    public Board() {
        this.id = 0;
    }

    public Board(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Board(String name, String description, Boss creator) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.workers = new HashSet<>();
        this.tasks = new HashSet<>();
    }

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

    public void addWorker(Worker worker) {
        this.workers.add(worker);
    }

    @OneToMany(mappedBy = "board")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}

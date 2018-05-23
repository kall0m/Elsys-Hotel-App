package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
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
        this.name = "";
        this.description = "";
        this.creator = null;
        this.workers = new TreeSet<>(new Comparator<Worker>() {
            @Override
            public int compare(Worker worker1, Worker worker2) { //TODO
                Integer id1 = worker1.getId();
                Integer id2 = worker2.getId();
                return id1.compareTo(id2);
            }
        });
        this.tasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) { //TODO
                Integer id1 = task1.getId();
                Integer id2 = task2.getId();
                return id1.compareTo(id2);
            }
        });
    }

    public Board(String name, String description, Boss creator) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.workers = new TreeSet<>(new Comparator<Worker>() {
            @Override
            public int compare(Worker worker1, Worker worker2) { //TODO
                Integer id1 = worker1.getId();
                Integer id2 = worker2.getId();
                return id1.compareTo(id2);
            }
        });
        this.tasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) { //TODO
                Integer id1 = task1.getId();
                Integer id2 = task2.getId();
                return id1.compareTo(id2);
            }
        });
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

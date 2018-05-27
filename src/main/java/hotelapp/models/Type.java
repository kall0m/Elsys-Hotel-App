package hotelapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "typeWorkersTask")
public class Type {
    private Integer id;

    private String name;

    @JsonIgnore
    private Set<Worker> workers;

    @JsonIgnore
    private Set<Task> tasks;

    private Boss boss;

    public Type() {
        this.name = "";
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
        this.boss = null;
    }

    public Type(String name) {
        this.name = name;
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

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "type")
    public Set<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "type")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "bossId")
    public Boss getBoss() {
        return boss;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }
}

package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Entity
@DiscriminatorValue("2")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Worker extends User {
    @JsonIgnore
    private Set<Task> tasks;

    private Boss boss;

    private Type type;

    @JsonIgnore
    private String realPassword;

    public Worker(String email, String fullName, String password, String realPassword) {
        super(email, fullName, password);
        this.tasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) { //TODO
                Integer id1 = task1.getId();
                Integer id2 = task2.getId();
                return id1.compareTo(id2);
            }
        });
        this.realPassword = realPassword;
    }

    public Worker() {    }

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "worker")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    @ManyToOne()
    @JoinColumn(name = "bossId")
    public Boss getBoss() {
        return boss;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    @ManyToOne()
    @JoinColumn(name = "typeWorkerId")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Column(name = "realPassword")
    public String getRealPassword() {
        return realPassword;
    }

    public void setRealPassword(String realPassword) {
        this.realPassword = realPassword;
    }
}

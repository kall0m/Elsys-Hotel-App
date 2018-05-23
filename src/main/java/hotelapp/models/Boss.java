package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement
@Entity
@DiscriminatorValue("1")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Boss extends User {
    @JsonIgnore
    private Set<Task> tasks;

    @JsonIgnore
    private Set<Board> boards;

    @JsonIgnore
    private double subscription;

    @JsonIgnore
    private int workerAccounts;

    public Boss(String email, String fullName, String password, double subscription) {
        super(email, fullName, password);
        this.tasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) { //TODO
                Integer id1 = task1.getId();
                Integer id2 = task2.getId();
                return id1.compareTo(id2);
            }
        });
        this.boards = new TreeSet<>(new Comparator<Board>() {
            @Override
            public int compare(Board board1, Board board2) { //TODO
                Integer id1 = board1.getId();
                Integer id2 = board2.getId();
                return id1.compareTo(id2);
            }
        });
        this.subscription = subscription;
    }

    public Boss() {    }

    @OneToMany(mappedBy = "assignor")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @OneToMany(mappedBy = "creator")
    public Set<Board> getBoards() {
        return boards;
    }

    public void setBoards(Set<Board> boards) {
        this.boards = boards;
    }

    @Column(name = "subscription")
    public double getSubscription() {
        return subscription;
    }

    public void setSubscription(double subscription) {
        this.subscription = subscription;
    }

    @Column(name = "workerAccounts")
    public int getWorkerAccounts() {
        return workerAccounts;
    }

    public void setWorkerAccounts(int workerAccounts) {
        this.workerAccounts = workerAccounts;
    }
}

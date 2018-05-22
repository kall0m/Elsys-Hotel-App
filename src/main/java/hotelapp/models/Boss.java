package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        this.tasks = new HashSet<>();
        this.boards = new HashSet<>();
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

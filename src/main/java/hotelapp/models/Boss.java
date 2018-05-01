package hotelapp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("1")
public class Boss extends User {
    private Set<Task> tasks;

    private Set<Board> boards;

    private double subscription;

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

    @Column(name = "subscription", nullable = false)
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

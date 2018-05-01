package hotelapp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("2")
public class Worker extends User {
    private Set<Task> tasks;

    private Set<Board> boards;

    public Worker(String email, String fullName, String password, Set<Task> tasks, Set<Board> boards) {
        super(email, fullName, password);
        this.tasks = new HashSet<>();
        this.boards = new HashSet<>();
    }

    public Worker() {    }

    @OneToMany(mappedBy = "worker")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "boards_workers")
    public Set<Board> getBoards() {
        return boards;
    }

    public void setBoards(Set<Board> boards) {
        this.boards = boards;
    }

    public void addBoard(Board board) {
        this.boards.add(board);
    }
}

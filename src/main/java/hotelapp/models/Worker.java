package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
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

    @JsonIgnore
    private Set<Board> boards;

    public Worker(String email, String fullName, String password) {
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

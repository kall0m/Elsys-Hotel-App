package hotelapp.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("1")
public class Boss extends User {
    private Set<Task> tasks;

    private Set<Board> boards;

    public Boss(String email, String fullName, String password) {
        super(email, fullName, password);
        this.tasks = new HashSet<>();
        this.boards = new HashSet<>();
    }

    public Boss(Set<Task> tasks, Set<Board> boards) {
        this.tasks = new HashSet<>();
        this.boards = new HashSet<>();
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
}

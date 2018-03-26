package hotelapp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
public class Task {
    private Integer id;

    private String description;

    private Boss assignor;

    private Worker worker;

    private Board board;

    public Task(String description, Boss assignor, Worker worker, Board board) {
        this.description = description;
        this.assignor = assignor;
        this.worker = worker;
        this.board = board;
    }

    public Task() {    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "assignorId")
    public Boss getAssignor() {
        return assignor;
    }

    public void setAssignor(Boss assignor) {
        this.assignor = assignor;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "workerId")
    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "boardId")
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}

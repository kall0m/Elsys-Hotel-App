package hotelapp.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Task {
    private Integer id;

    private String description;

    private Boss assignor;

    private Worker worker;

    private Board board;

    public Task() {
        this.description = "";
        this.assignor = null;
        this.worker = null;
        this.board = null;
    }

    public Task(String description) {
        this.description = description;
    }

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
    @JoinColumn(name = "workerId")
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

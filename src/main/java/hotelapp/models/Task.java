package hotelapp.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import hotelapp.services.TaskStatus;

import javax.persistence.*;

@Entity
@Table(name = "tasks")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "taskId")
public class Task {
    @JsonProperty("taskId")
    private Integer id;

    private String description;

    private String status;

    @JsonIgnore
    private Boss assignor;

    private Worker worker;

    @JsonProperty("taskType")
    private Type type;

    public Task() {
        this.description = "";
        this.status = TaskStatus.TODO;
        this.assignor = null;
        this.worker = null;
        this.type = null;
    }

    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.TODO;
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

    @Column(name = "status", nullable = false)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    @JoinColumn(name = "typeTaskId")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
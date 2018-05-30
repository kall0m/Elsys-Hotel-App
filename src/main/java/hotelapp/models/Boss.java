package hotelapp.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement
@Entity
@DiscriminatorValue("0")
public class Boss extends User {
    @JsonIgnore
    private Set<Task> tasks;

    @JsonIgnore
    private double subscription;

    @JsonIgnore
    private Set<Worker> workers;

    @JsonIgnore
    private Set<Type> types;

    public Boss(String email, String hotelName, String password, double subscription) {
        super(email, hotelName, password);
        this.tasks = new TreeSet<>(new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) { //TODO
                Integer id1 = task1.getId();
                Integer id2 = task2.getId();
                return id1.compareTo(id2);
            }
        });
        this.subscription = subscription;
        this.workers = new TreeSet<>(new Comparator<Worker>() {
            @Override
            public int compare(Worker worker1, Worker worker2) { //TODO
                Integer id1 = worker1.getId();
                Integer id2 = worker2.getId();
                return id1.compareTo(id2);
            }
        });
        this.types = new TreeSet<>(new Comparator<Type>() {
            @Override
            public int compare(Type type1, Type type2) { //TODO
                String name1 = type1.getName();
                String name2 = type2.getName();
                return name1.compareTo(name2);
            }
        });
    }

    public Boss() {    }

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "assignor")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @Column(name = "subscription")
    public double getSubscription() {
        return subscription;
    }

    public void setSubscription(double subscription) {
        this.subscription = subscription;
    }

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "boss")
    public Set<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }

    @OrderBy("name ASC")
    @OneToMany(mappedBy = "boss")
    public Set<Type> getTypes() {
        return types;
    }

    public void setTypes(Set<Type> types) {
        this.types = types;
    }
}
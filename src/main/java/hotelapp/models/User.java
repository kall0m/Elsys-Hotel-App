package hotelapp.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_type", discriminatorType = DiscriminatorType.INTEGER)
public class User {
    private Integer id;

    private String email;

    private String fullName;

    private String password;

    private Set<Role> roles;

    //private Set<Task> tasks;

    //private Set<Board> boards;

    public User(String email, String fullName, String password) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;

        this.roles = new HashSet<>();
        //this.tasks = new HashSet<>();
        //this.boards = new HashSet<>();
    }

    public User() {    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fullName", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "password", length = 60, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    /*@OneToMany(mappedBy = "user")
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }*/

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "boards_workers")
    @OneToMany(mappedBy = "user")
    public Set<Board> getBoards() {
        return boards;
    }

    public void setBoards(Set<Board> boards) {
        this.boards = boards;
    }*/
}

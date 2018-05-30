package hotelapp.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_type", discriminatorType = DiscriminatorType.INTEGER)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId")
public class User {
    @JsonProperty("userId")
    private Integer id;

    private String email;

    private String hotelName;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private boolean enabled;

    @JsonIgnore
    private String confirmationToken;

    @JsonIgnore
    private String forgotPasswordToken;

    @JsonIgnore
    private String jwtToken;

    @JsonIgnore
    private Set<Role> roles;

    public User(String email, String hotelName, String password) {
        this.email = email;
        this.hotelName = hotelName;
        this.password = password;

        this.roles = new HashSet<>();
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

    @Column(name = "hotelName")
    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    @Column(name = "password", length = 60, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(name = "confirmationToken")
    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    @Column(name = "forgotPasswordToken")
    public String getForgotPasswordToken() {
        return forgotPasswordToken;
    }

    public void setForgotPasswordToken(String forgotPasswordToken) {
        this.forgotPasswordToken = forgotPasswordToken;
    }

    @Column(name = "jwtToken")
    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (enabled != user.enabled) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (hotelName != null ? !hotelName.equals(user.hotelName) : user.hotelName != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (confirmationToken != null ? !confirmationToken.equals(user.confirmationToken) : user.confirmationToken != null)
            return false;
        if (forgotPasswordToken != null ? !forgotPasswordToken.equals(user.forgotPasswordToken) : user.forgotPasswordToken != null)
            return false;
        return roles != null ? roles.equals(user.roles) : user.roles == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (hotelName != null ? hotelName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (confirmationToken != null ? confirmationToken.hashCode() : 0);
        result = 31 * result + (forgotPasswordToken != null ? forgotPasswordToken.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
}
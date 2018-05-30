package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class UserBindingModel {
    @NotNull
    private String email;

    @NotNull
    private String hotelName;

    @NotNull
    private String password;

    @NotNull
    private String confirmPassword;

    @NotNull
    private double subscription;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public double getSubscription() {
        return subscription;
    }

    public void setSubscription(double subscription) {
        this.subscription = subscription;
    }
}
package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class ForgotPasswordBindingModel {
    @NotNull
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class SetNewPasswordBindingModel {
    @NotNull
    private String newPassword;

    @NotNull
    private String confirmPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

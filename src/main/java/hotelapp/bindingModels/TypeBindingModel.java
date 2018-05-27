package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class TypeBindingModel {
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

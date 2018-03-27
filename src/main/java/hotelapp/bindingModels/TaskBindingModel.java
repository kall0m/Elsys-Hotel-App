package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class TaskBindingModel {
    @NotNull
    private String description;

    @NotNull
    private String worker;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }
}

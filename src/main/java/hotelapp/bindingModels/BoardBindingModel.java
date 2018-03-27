package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;
import java.util.List;

public class BoardBindingModel {
    @NotNull
    private String name;

    private String description;

    private List<String> workers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }
}

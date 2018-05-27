package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class WorkerBindingModel {
    @NotNull
    private int count;

    @NotNull
    private String typeName;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

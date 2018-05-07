package hotelapp.bindingModels;

import javax.validation.constraints.NotNull;

public class GenerateWorkerAccountBindingModel {
    @NotNull
    public int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

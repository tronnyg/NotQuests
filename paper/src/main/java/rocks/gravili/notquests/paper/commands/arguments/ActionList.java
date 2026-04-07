package rocks.gravili.notquests.paper.commands.arguments;

import rocks.gravili.notquests.paper.structs.actions.Action;

import java.util.ArrayList;
import java.util.List;

public class ActionList {

    private final List<Action> values = new ArrayList<>();

    public void addValue(Action value) {
        values.add(value);
    }

    public void addValues(List<Action> values) {
        this.values.addAll(values);
    }

    public List<Action> getValues() {
        return values;
    }

    public void clear() {
        values.clear();
    }

    public void removeValue(Action value) {
        values.remove(value);
    }
}

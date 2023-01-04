package gg.bundlegroup.bundlescenes.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UnionSetFlag<T> extends SetFlag<T> {
    public UnionSetFlag(String name, Flag<T> subFlag) {
        super(name, subFlag);
    }

    @Nullable
    @Override
    public Set<T> chooseValue(Collection<Set<T>> values) {
        if (values.isEmpty()) {
            return Set.of();
        } else if (values.size() == 1) {
            for (Set<T> value : values) {
                return value;
            }
        }
        Set<T> result = new HashSet<>();
        for (Set<T> value : values) {
            result.addAll(value);
        }
        return result;
    }

    @Override
    public boolean hasConflictStrategy() {
        return true;
    }
}

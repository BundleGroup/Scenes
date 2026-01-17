package gg.bundlegroup.bundlescenes.worldguard.worldguard;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UnionSetFlag<T> extends SetFlag<T> {
    public UnionSetFlag(String name, Flag<T> subFlag) {
        super(name, subFlag);
    }

    @Override
    public @Nullable Set<T> chooseValue(Collection<Set<T>> values) {
        if (values.isEmpty()) {
            return Set.of();
        } else if (values.size() == 1) {
            return values.iterator().next();
        } else {
            return values.stream().flatMap(Collection::stream).collect(Collectors.toSet());
        }
    }

    @Override
    public boolean hasConflictStrategy() {
        return true;
    }
}

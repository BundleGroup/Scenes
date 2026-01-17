package gg.bundlegroup.bundlescenes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public final class MultiMap<K, V> {
    private final Map<K, Set<V>> map;

    public MultiMap(Map<K, Set<V>> map) {
        this.map = map;
    }

    public MultiMap() {
        this(new HashMap<>());
    }

    public Set<V> get(K key) {
        return map.getOrDefault(key, Set.of());
    }

    public boolean add(K key, V value) {
        return map.computeIfAbsent(key, s -> new HashSet<>()).add(value);
    }

    public boolean addAndCheckFirst(K key, V value) {
        Set<V> values = map.computeIfAbsent(key, s -> new HashSet<>());
        boolean wasEmpty = values.isEmpty();
        values.add(value);
        return wasEmpty;
    }

    public boolean remove(K key, V value) {
        Set<V> values = map.get(key);
        if (values == null) {
            return false;
        }
        boolean result = values.remove(value);
        if (values.isEmpty()) {
            map.remove(key);
        }
        return result;
    }

    public boolean removeAndCheckLast(K key, V value) {
        Set<V> values = map.get(key);
        if (values == null) {
            return false;
        }
        values.remove(value);
        if (!values.isEmpty()) {
            return false;
        }
        map.remove(key);
        return true;
    }

    public boolean removeAll(K key) {
        return map.remove(key) != null;
    }

    public boolean contains(K key, V value) {
        return map.getOrDefault(key, Set.of()).contains(value);
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void forEach(BiConsumer<K, V> consumer) {
        for (Map.Entry<K, Set<V>> entry : map.entrySet()) {
            K key = entry.getKey();
            for (V value : entry.getValue()) {
                consumer.accept(key, value);
            }
        }
    }

    public void clear() {
        map.clear();
    }
}

package cn.zhxu.bs.util;

import java.util.*;
import java.util.stream.Collectors;

public class MapWrapper {

    private final Map<String, Object> map;
    private final String prefix;
    private final String gKey;

    public MapWrapper(Map<String, Object> map) {
        this(map, null, null);
    }

    public MapWrapper(Map<String, Object> map, String gKey, String separator) {
        this.map = Objects.requireNonNull(map);
        this.prefix = gKey != null ? gKey + separator : null;
        this.gKey = gKey;
    }

    transient Set<String> keySet;

    public Set<String> keySet() {
        Set<String> ks = keySet;
        if (ks == null) {
            if (prefix != null) {
                int index = prefix.length();
                ks = map.keySet().stream()
                        .map(k -> {
                            if (k.startsWith(prefix)) {
                                return k.substring(index);
                            }
                            return k;
                        })
                        .collect(Collectors.toSet());
            } else {
                ks = map.keySet();
            }
            keySet = ks;
        }
        return ks;
    }

    public Object get0(String key) {
        if (gKey != null) {
            return map.get(gKey + key);
        }
        return map.get(key);
    }

    public Object get1(String key) {
        if (prefix != null) {
            return map.get(prefix + key);
        }
        return map.get(key);
    }

}

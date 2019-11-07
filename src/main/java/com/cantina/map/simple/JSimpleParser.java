package com.cantina.map.simple;

import com.cantina.map.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class JSimpleParser implements JParser {

    private String data;
    private SearchCache cache;
    private Object root;
    private final Set<String> topView;
    private final String cls, className, identifier;
    public JSimpleParser(String data, SearchCache cache, String cls, String classNames, String identifier ) throws ParseException {
        this.data = data;
        this.topView = null;
        this.cls = cls;
        this.className = classNames;
        this.identifier = identifier;
        this.cache = cache;
        this.root = parse();
        addToCache("~root~",this.root, null);
    }

    @Override
    public Collection<JView> find(String input) {
        return cache.find(input);
    }

    private Object parse() throws ParseException {
        Object obj = new JSONParser().parse(this.data);
        return obj;
    }

    private class SView implements JView {
        private Object o;
        SView(Object obj) {
            o=obj;
        }

        public boolean contains(JView view) {
            if (o instanceof JSONArray) {
                JSONArray ja = (JSONArray)o;
                return ja.contains(((SView)view).o);
            } else if (o instanceof JSONObject) {
                JSONObject jo = (JSONObject)o;
                return jo.containsValue(((SView)view).o);
            }
            return false;
        }

        @Override
        public String toString() {
            return o.toString();
        }

        @Override
        public boolean equals(Object o1) {
            if (this == o1) return true;
            if (!(o1 instanceof SView)) return false;
            SView sView = (SView) o1;
            return Objects.equals(o, sView.o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(o);
        }
    }

    private void addToCache(String key, Object o, Object p) {
        Set<Object> values = new HashSet<>(2);
        if (o instanceof JSONArray) {
            JSONArray ja = (JSONArray)o;
            for (Object a : ja) {
                if (a instanceof JSONObject || a instanceof JSONArray) {
                    addToCache(key, a, ja);
                } else {
                    values.add(a);
                }
            }
        } else if (o instanceof JSONObject) {
            JSONObject jo = (JSONObject)o;
            for (Object k : jo.keySet()) {
                addToCache(k.toString(),jo.get(k), jo);
            }
        } else {
            values.add(o);
        }

        // Add to cache
        if (className.equals(key)) {
            for (Object value : values) {
                cache.add(SearchCache.Type.CLASS, value.toString(), new SView(p));
            }
        } else if (identifier.equals(key)) {
            for (Object value : values) {
                cache.add(SearchCache.Type.ID, value.toString(), new SView(p));
            }
        } else if (cls.equals(key)) {
            for (Object value : values) {
                cache.add(SearchCache.Type.VIEW, value.toString(), new SView(p));
            }
        } else if (topView == null || topView.contains(key)) {
            if (values.isEmpty()) {
                cache.add(SearchCache.Type.VIEW, key, new SView(o));
            } else {
                for (Object value : values) {
                    cache.add(SearchCache.Type.VIEW, key, new SView(value));
                }
            }
        }
    }
}


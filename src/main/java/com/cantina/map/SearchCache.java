package com.cantina.map;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.IntStream;

public class SearchCache {

    public enum Type {
        VIEW, // Top level "this": {"name":"test"} or {"class":"this","name":"test"}  .. Input = this
        ID, // {"identifier": "abc"} .. Input = #abc
        CLASS; // {classNames:["c1","c2"]} .. Input = .c1  .. Input = .c2
    }

    /**
     * Stores by Type.
     * The by "attribute" or 'top level name like 'this' in {"this": {"name":"test"}}
     * The stores the associated view to compare
     */
    private Map<SearchCache.Type, Map<String, Set<JView>>> cache = new HashMap<>();

    /**
     * Validates input string.
     * Determine type by first character of string:
     * "." = Type className
     * "#" = Identifier
     * AlphaNumeric = Class
     * Space = Chaining class
     * @param input
     * @return
     */
    public Set<JView> find(String input) {
        input = input.trim();
        validate(input);
        Iterator<String> token = tokenize(input);
        Set<JView> views = null;
        while (token.hasNext()) {
            String tokenName = token.next();
            SearchCache.Type type = getType(tokenName);
            String name = getKey(tokenName);
            Set<JView> v = get(type, name);
            views = views == null ? v : merge(views, v);
        }
        return views;
    }

    /**
     * Set case for quick lookup search
     * @param type
     * @param name
     * @param data
     */
    public void add(SearchCache.Type type, String name, JView data) {
        if (data == null) return;
        Map<String, Set<JView>> typeData;
        if (null == (typeData = cache.get(type))) cache.put(type, typeData = new HashMap<>());
        Set<JView> nameData;
        if (null == (nameData = typeData.get(name))) typeData.put(name, nameData = new HashSet<>());
        nameData.add(data);
    }

    private Set<JView> get(SearchCache.Type type, String name) {
        Map<String, Set<JView>> typeData;
        if (null == (typeData = cache.get(type))) return null;
        Set<JView> nameData;
        if (null == (nameData = typeData.get(name))) return null;
        return nameData;
    }

    /**
     * This takes all the 'found' views.
     * Compares it with the new 'views'.
     * Only returns a sublist of 'views' only if the new 'views' are in the 'found' list or CONTAIN the view
     * @param found
     * @param views
     * @return
     */
    private Set<JView> merge(Set<JView> found, Set<JView> views) {
        Set<JView> merged = new HashSet<>();
        if (views != null) {
            for (JView view : views) {
                if (found==null) break;
                if (found.contains(view)) {
                    merged.add(view);
                } else {
                    for (JView jView : found) {
                        if (jView.contains(view)) {
                            merged.add(view);
                        }
                    }
                }
            }
        }
        if (merged == null) throw new Error("can not return a 'null' Set!");
        return merged;
    }

    private Iterator<String> tokenize(final String input) {
        return new Iterator<String>() {
            String data = input.trim();
            @Override
            public boolean hasNext() {
                return data != null && !data.isEmpty();
            }
            @Override
            public String next() {
                int i = IntStream.range(1, data.length()).filter(idx -> {
                    char tmp = data.charAt(idx);
                    return tmp == ' ' ||  tmp == '.' ||  tmp == '#';
                }).findFirst().orElse(data.length());
                String token = data.substring(0,i);
                data = data.substring(i).trim();
                return token;
            }
        };
    }

    private String getKey(String tokenName) {
        switch (tokenName.charAt(0)) {
            case ('.'):
            case('#'):
                return tokenName.substring(1);
            default:
                return tokenName;
        }
    }

    private Type getType(String tokenName) {
        switch (tokenName.charAt(0)) {
            case ('.'):
                return Type.CLASS;
            case('#'):
                return Type.ID;
            default:
                return Type.VIEW;
        }
    }

    private void validate(String input) {
        input.chars().forEach(c ->{
            if (c != ' ' && c != '#' && c != '.' && !Character.isAlphabetic(c) && !Character.isDigit(c))
                throw new InvalidParameterException("Illegal Character ["+c+"]");
        } );
        if (input.indexOf("  ")!=-1) throw new InvalidParameterException("Only single spaces allowed");
        if (input.indexOf("..")!=-1) throw new InvalidParameterException("Only single period allowed");
        if (input.indexOf("##")!=-1) throw new InvalidParameterException("Only single # allowed");
    }
}

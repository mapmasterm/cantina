package com.cantina.map.custom;

import com.cantina.map.JParser;
import com.cantina.map.JView;
import com.cantina.map.SearchCache;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;

/*
 * This Parser parses json without modifying the data.
 * It also using 'index' to reference the underlying data to reduce storage.
 */
public class CustomParser implements JParser {

    String data = null;
    String cls = "class";
    String classNames = "classNames";
    String identifier = "identifier";
    SearchCache cache = new SearchCache();

    public CustomParser(String data) {
        this.data = data;
        parseSelect(new int[]{0,1},0);
    }

    @Override
    public Collection<JView> find(String input) {
        return cache.find(input);
    }

    private class PReturn {
        private final int i;
        private final Map<String, String> attr;

        PReturn(int i, Map<String,String> attr) {
            this.i = i;
            this.attr = attr;
        }
    }

    //TODO: Parse fine but need to add caching in attribute
    private PReturn parseSelect(int[] arrayKey, int i) {
        int key[] = null;
        int value[] = null;
        for(; i<data.length(); i++) {
            char c = data.charAt(i);
            switch (c) {
                case '"':
                    int pos = findEndOfString(i);
                    if (key == null) {
                        key = new int[]{i,pos};
                    }
                    i = pos;
                    break;
                case ',':
                    if (key != null) key = null; //Clear to get next key
                    break;
                case ']':
                    return new PReturn(i,null); //Array does not mean anything unless it has {}
                case '}':
                    return new PReturn(i,null);
                case '{':
                    int[] tkey = key == null ? arrayKey : key;
                    PReturn r1 = parseSelect(tkey, i + 1);
                    key = null; //Clear to get next key
                    i = r1.i;
                    break;
                case '[':
                    PReturn r2 = parseSelect(key == null ? arrayKey : key, i + 1);
                    key = null; //Clear to get next key
                    i = r2.i;
                    break;
            }
        }
        return new PReturn(i,null);
    }

    private int findEndOfString(int i) {
        int pos = i;
        while (true) {
            int from = pos + 1;
            pos = data.indexOf('"', from);
            if (pos == -1) throw new MalformedParameterizedTypeException("Could not find closed \" of position[" + i + "]");
            if (pos == from) break;
            boolean escFlag = false;
            for(int ii=pos-1; ii>=0; ii--) {
                if ('/' == data.charAt(ii)) {
                    escFlag = !escFlag;
                    continue;
                }
                break;
            }
            if (!escFlag) break;
        }
        return pos;
    }

}

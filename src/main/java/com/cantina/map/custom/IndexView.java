package com.cantina.map.custom;

import com.cantina.map.JView;

import java.util.Objects;

public class IndexView implements JView {

    int start;
    int stop;

    IndexView(int start, int stop) {
        this.start = start;
        this.stop = stop;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    @Override
    public String toString() {
        return "IndexView{" +
                "start=" + start +
                ", stop=" + stop +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexView)) return false;
        IndexView indexView = (IndexView) o;
        return start == indexView.start &&
                stop == indexView.stop;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, stop);
    }

    @Override
    public boolean contains(JView view) {
        return false;
    }
}

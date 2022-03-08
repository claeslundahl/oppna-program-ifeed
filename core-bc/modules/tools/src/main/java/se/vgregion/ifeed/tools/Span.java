package se.vgregion.ifeed.tools;

import java.io.Serializable;

public class Span implements Serializable {
    private int start;
    private int length;

    public Span(int start, int length) {
        this.start = start;
        this.length = length;
    }

    public Span() {
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String toString() {
        return "Span{start=" + this.start + ", length=" + this.length + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Span)) {
            return false;
        } else {
            Span span = (Span) o;
            if (this.start != span.start) {
                return false;
            } else {
                return this.length == span.length;
            }
        }
    }

    public int hashCode() {
        int result = this.start ^ this.start >>> 32;
        result = 31 * result + (this.length ^ this.length >>> 32);
        return result;
    }
}
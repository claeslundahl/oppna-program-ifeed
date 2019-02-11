package se.vgregion.ifeed.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Column {

    // dc.title|Titel (autokomplettering)|left|70

    private String key, title, alignment, width;

    public Column() {
        super();
    }

    public Column(String parsableInput) {
        this();
        String[] parts = parsableInput.split(Pattern.quote("|"));
        int c = 0;
        if (parts.length > c) {
            setKey(parts[c]);
            c++;
        }
        if (parts.length > c) {
            setTitle(parts[c]);
            c++;
        }
        if (parts.length > c) {
            setAlignment(parts[c]);
            c++;
        }
        if (parts.length > c) {
            setWidth(parts[c]);
            c++;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Column{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", alignment='" + alignment + '\'' +
                ", width='" + width + '\'' +
                '}';
    }

    public static List<Column> toColumns(String parsable) {
        String[] parts = parsable.split(Pattern.quote(","));
        List<Column> result = new ArrayList<>();
        for (String part : parts) {
            result.add(new Column(part));
        }
        return result;
    }

}

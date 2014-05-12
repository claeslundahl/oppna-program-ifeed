package se.vgregion.ifeed.client;

/**
 * Created by clalu4 on 2014-03-14.
 */
public class ColumnDef {

    private String name;
    private String label;
    private String alignment;
    private String width;

    public void parseAndSet(String values) {
        // dc.title|Titel|left|70
        String[] fragments = values.split("['|']");
        setName(fragments[0]);
        setLabel(fragments[1]);
        setAlignment(fragments[2]);
        if (fragments.length > 3) setWidth(fragments[3]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
}

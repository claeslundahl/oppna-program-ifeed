package se.vgregion.ifeed.shared;

/**
 * Holds description of a column. How it should be rendered to the user.
 */
public class ColumnDef {

    private String name;
    private String label;
    private String alignment;
    private String width;

    /**
     * Takes a text that contains information about the column in question. Then it parses that string and puts
     * appropriate values inside its own properties based on that.
     * @param values the text describing the object. An example woud be "dc.title|Titel|left|70".
     */
    public void parseAndSet(String values) {
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

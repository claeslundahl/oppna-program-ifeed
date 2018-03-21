package se.vgregion.ifeed.formbean;

public class Note {

    private String label;
    private String text;

    public Note() {
        super();
    }

    public Note(String label, String text) {
        this();
        this.label = label;
        this.text = text;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

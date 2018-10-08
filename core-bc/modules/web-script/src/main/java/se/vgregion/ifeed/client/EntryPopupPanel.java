package se.vgregion.ifeed.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A small popup display for entry details.
 */
public class EntryPopupPanel extends PopupPanel {

    private Entry entry;

    FlexTable plate = new FlexTable();

    /**
     * Constructs an instance. Doing all the layout at once.
     *
     * @param entry the data to render.
     */
    public EntryPopupPanel(Entry entry) {
        super();
        this.entry = entry;

        plate.addStyleName("ifeed-popup-inf");

        SimplePanel sp = new SimplePanel();
        sp.getElement().getStyle().setBackgroundColor("#A9A9A9");
        sp.getElement().getStyle().setPadding(1, Style.Unit.PX);
        sp.add(plate);

        int row = 0;
        // Label label = new Label("Titel: " + Util.formatValueForDisplay(entry, "dc.title"));
        Label label = new Label("Titel: " + Util.formatValueForDisplay(entry, "title"));
        label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        label.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        plate.setWidget(row, 0, label);
        plate.getFlexCellFormatter().getElement(row, 0).getStyle().setBackgroundColor("#A9A9A9");

        plate.getElement().getStyle().setMargin(2, Style.Unit.PX);
        plate.getFlexCellFormatter().setColSpan(row++, 0, 2);

        String sourceSystem = entry.get("vgr:VgrExtension.vgr:SourceSystem");
        if (!"SOFIA".equals(sourceSystem)) {
            addLabelAndDocumentMeta("Publicerat för enhet", "DC.publisher.forunit", row++);
            addLabelAndDocumentMeta("Beskrivning", "DC.description", row++);
            addLabelAndDocumentMeta("Innehållsansvarig", "DC.creator.document", row++);
            addLabelAndDocumentMeta("Innehållsansvarig, roll", "DC.creator.function", row++);
            addLabelAndDocumentMeta("Godkänt av", "DC.contributor.acceptedby", row++);
            addLabelAndDocumentMeta("Godkänt av, roll", "DC.contributor.acceptedby.role", row++);
            addLabelAndDocumentMeta("Giltig fr o m", "DC.date.validfrom", row++);
            addLabelAndDocumentMeta("Giltig t o m", "DC.date.validto", row++);
            addLabelAndDocumentMeta("Dokumentstruktur VGR", "DC.type.document.structure", row++);
        }else {
            addLabelAndDocumentMeta("N/A", "core:ArchivalObject.idType", row++);
            addLabelAndDocumentMeta("N/A", "core:ArchivalObject.id", row++);
            addLabelAndDocumentMeta("Upprättad datum", "core:ArchivalObject.core:CreatedDateTime", row++);
            addLabelAndDocumentMeta("Bevarande och gallringsåtgärd", "core:ArchivalObject.core:PreservationPlanning.action", row++);
            addLabelAndDocumentMeta("Bevarande och gallringsbeslut", "core:ArchivalObject.core:PreservationPlanning.RDA", row++);
            addLabelAndDocumentMeta("Gallringsdatum", "revisiondate", row++);
            addLabelAndDocumentMeta("Åtkomsträtt i slutarkiv", "core:ArchivalObject.core:AccessRight", row++);
            addLabelAndDocumentMeta("Dokumentbeskrivning i Sharepoint, Beskrivning i Mellanarkivet", "core:ArchivalObject.core:Description", row++);
            addLabelAndDocumentMeta("Handlingstyp", "core:ArchivalObject.core:ObjectType", row++);
            addLabelAndDocumentMeta("", "core:ArchivalObject.core:ObjectType.id", row++);
            addLabelAndDocumentMeta("Dokumenthanteringsplan", "core:ArchivalObject.core:ObjectType.filePlan", row++);
            addLabelAndDocumentMeta("Id på klassificering", "core:ArchivalObject.core:Classification.core:Classification.id", row++);
            addLabelAndDocumentMeta("Punktnotation på klassificering", "core:ArchivalObject.core:Classification.core:Classification.classCode", row++);
            addLabelAndDocumentMeta("Nivå på klassificering", "core:ArchivalObject.core:Classification.core:Classification.level", row++);
            addLabelAndDocumentMeta("Namn på klassificering", "core:ArchivalObject.core:Classification.core:Classification.name", row++);
            addLabelAndDocumentMeta("Rubrik", "core:ArchivalObject.core:Unit", row++);
            addLabelAndDocumentMeta("Signum", "core:ArchivalObject.core:Unit.refcode", row++);
            addLabelAndDocumentMeta("Nivå i arkivförteckningen", "core:ArchivalObject.core:Unit.level", row++);
            addLabelAndDocumentMeta("Myndighet/Arkivbildare", "core:ArchivalObject.core:Producer", row++);
            addLabelAndDocumentMeta("", "core:ArchivalObject.core:Producer.idType", row++);
            addLabelAndDocumentMeta("Myndighetens HSA-ID", "core:ArchivalObject.core:Producer.id", row++);
            addLabelAndDocumentMeta("Arkivobjekt-ID", "vgr:VgrExtension.itemId", row++);
            addLabelAndDocumentMeta("Källsystem", "vgr:VgrExtension.vgr:SourceSystem", row++);
            addLabelAndDocumentMeta("Källsystem-ID", "vgr:VgrExtension.vgr:SourceSystem.id", row++);
            addLabelAndDocumentMeta("Käll-ID", "vgr:VgrExtension.vgr:Source.id", row++);
            addLabelAndDocumentMeta("Version i källsystem", "vgr:VgrExtension.vgr:Source.version", row++);
            addLabelAndDocumentMeta("N/A", "vgr:VgrExtension.vgr:Source.versionId", row++);
            addLabelAndDocumentMeta("Rubrik i Sharepoint, Titel i Mellanarkivet", "vgr:VgrExtension.vgr:Title", row++);
            addLabelAndDocumentMeta("Tillgänglig från", "vgr:VgrExtension.vgr:AvailableFrom", row++);
            addLabelAndDocumentMeta("Tillgänglig till", "vgr:VgrExtension.vgr:AvailableTo", row++);
            addLabelAndDocumentMeta("Reviderat tillgänglig från", "vgr:VgrExtension.vgr:RevisedAvailableFrom", row++);
            addLabelAndDocumentMeta("Reviderat tillgänglig till", "vgr:VgrExtension.vgr:RevisedAvailableTo", row++);
            addLabelAndDocumentMeta("Åtkomsträtt", "vgr:VgrExtension.vgr:SecurityClass", row++);
            addLabelAndDocumentMeta("Skyddskod", "vgr:VgrExtension.vgr:RestrictionCode", row++);
            addLabelAndDocumentMeta("Lagparagraf", "vgr:VgrExtension.vgr:LegalParagraph", row++);
            addLabelAndDocumentMeta("Upprättad av enhet", "vgr:VgrExtension.vgr:CreatedByUnit", row++);
            addLabelAndDocumentMeta("", "vgr:VgrExtension.vgr:CreatedByUnit.id", row++);
            addLabelAndDocumentMeta("Upprättad för enhet", "vgr:VgrExtension.vgr:PublishedForUnit", row++);
            addLabelAndDocumentMeta("", "vgr:VgrExtension.vgr:PublishedForUnit.id", row++);
            addLabelAndDocumentMeta("Upprättad av", "vgr:VgrExtension.vgr:CreatedBy", row++);
            addLabelAndDocumentMeta("Upprättad av (vgrid)", "vgr:VgrExtension.vgr:CreatedBy.id", row++);
            addLabelAndDocumentMeta("Upprättad av (org)", "vgr:VgrExtension.vgr:CreatedBy.org", row++);
            addLabelAndDocumentMeta("Företagsnyckelord i Sharepoint, Nyckelord i Mellanarkivet", "vgr:VgrExtension.vgr:Tag", row++);
            addLabelAndDocumentMeta("", "vgrsy:DomainExtension.itemId", row++);
            addLabelAndDocumentMeta("Domännamn", "vgrsy:DomainExtension.domain", row++);
            addLabelAndDocumentMeta("Regional ämnesindelning", "vgrsy:DomainExtension.vgrsy:SubjectClassification", row++);
            addLabelAndDocumentMeta("Egen ämnesindelning", "vgrsy:DomainExtension.vgrsy:SubjectLocalClassification", row++);
            addLabelAndDocumentMeta("", "vgrsy:DomainExtension.domain", row++);
            addLabelAndDocumentMeta("Skapad datum", "core:ArchivalObject.core:CreatedDateTime", row++);
            addLabelAndDocumentMeta("Bevarande och gallringsåtgärd", "core:ArchivalObject.core:PreservationPlanning.action", row++);
            addLabelAndDocumentMeta("Bevarande och gallringsbeslut", "core:ArchivalObject.core:PreservationPlanning.RDA", row++);
            addLabelAndDocumentMeta("Gallringsdatum", "revisiondate", row++);
            addLabelAndDocumentMeta("Åtkomsträtt i slutarkiv", "core:ArchivalObject.core:AccessRight", row++);
            addLabelAndDocumentMeta("", "core:ArchivalObject.core:Description", row++);
            addLabelAndDocumentMeta("Skapad datum", "core:ArchivalObject.core:CreatedDateTime", row++);
        }
        plate.getElement().getStyle().setWidth(500d, Style.Unit.PX);
        plate.getElement().getStyle().setBackgroundColor("white");
        plate.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        plate.getElement().getStyle().setBorderColor("#A9A9A9");
        plate.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        addDomHandler(
            new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    try {
                        hide();
                    } catch (Exception e) {
                        Window.alert("" + e);
                    }
                }
            },
            MouseOutEvent.getType()
        );

        /*
        addDomHandler(event -> {
            try {
                hide();
            } catch (Exception e) {
                Window.alert("" + e);
            }
        }, MouseOutEvent.getType());
        */

        add(sp);
    }

    private void addLabelAndDocumentMeta(String explainingText, String keyToGetWithFromDocument, int row) {
        keyToGetWithFromDocument = keyToGetWithFromDocument.toLowerCase();
        String propertyValue = Util.formatValueForDisplay(entry, keyToGetWithFromDocument) + "";
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            plate.setText(row, 0, explainingText + ": ");
            plate.getFlexCellFormatter().getElement(row, 0).getStyle().setWidth(30, Style.Unit.PC);
            plate.getFlexCellFormatter().getElement(row, 0).getStyle().setVerticalAlign(Style.VerticalAlign.TOP);
            plate.setText(row, 1, propertyValue);
            plate.getFlexCellFormatter().getElement(row, 1).getStyle().setWidth(70, Style.Unit.PC);
        }
    }

}

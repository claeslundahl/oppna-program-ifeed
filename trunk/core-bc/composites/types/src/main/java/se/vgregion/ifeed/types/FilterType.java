package se.vgregion.ifeed.types;

import static se.vgregion.ifeed.types.FilterType.Filter.*;
import static se.vgregion.ifeed.types.MetadataType.*;

import java.util.EnumSet;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public enum FilterType {

    CREATED_FOR(EnumSet.<Filter> of(H_O_S, /* CREATED_FOR_UNIT, PUBLISHED_FOR_UNIT,*/ CREATED_FOR_PROJECT,
            PUBLISHED_FOR_PROJECT)),
            CONTEXT(EnumSet.<Filter> of(/*RECORD_TYPE, */DOC_TYPE, /*ARCHIVE_CREATOR, */PROCESS_NAME, CASE_TYPE,
                    CASE, JOURNAL_ID, DOCUMENT_SERIES, DOCUMENT_SERIES_ID, PHYSICAL_LOCATION)),
                    /*MANAGED_BY(EnumSet.<Filter> of(PUBLISHED_BY, RESPONSIBLE, RESPONSIBLE_FUNC)),*/
                    STATUS_AND_LIMITATIONS(EnumSet.<Filter> of(DOCUMENT_STATUS, VALID_FROM_DATE, VALID_TO_DATE,AVAILABLE_FROM_DATE,
                            AVAILABLE_TO_DATE, PUBLISHING_FROM_DATE, PUBLISHING_TO_DATE, PUBLIC_ACCESSIBILITY)),
                            CREATED_BY(EnumSet.<Filter> of(/*SAVED_BY, CREATED_BY_FIX, */CREATED_BY_FREE)),
                            DOC_DESCRIPTION(EnumSet.<Filter> of(TITLE, ALT_TITLE)),
                            KEYWORDS_FOR(EnumSet.<Filter> of(KEYWORDS_FIX, KEYWORDS_AUTHORS)),
                            HSA_CONTEXT(EnumSet.<Filter> of(HSA)),
                            OTHER(EnumSet.<Filter> of(LANGUAGE, FILE_EXTENSION));

    private EnumSet<Filter> filters;
    private String keyString;
    private static final Locale SV_SE = new Locale("sv_SE");

    private FilterType(EnumSet<Filter> filters) {
        this.filters = filters;
    }

    public EnumSet<Filter> getFilters() {
        return filters;
    }

    public String getKeyString() {
        if (StringUtils.isBlank(keyString)) {
            keyString = name().replace('_', '-').toLowerCase(SV_SE);
        }
        return keyString;
    }

    public enum Filter {
        H_O_S("dc.audience", TEXT_FIX, "HosPersKat"),
        CREATED_FOR_UNIT("dc.creator.forunit", TEXT_FREE, ""),
        PUBLISHED_FOR_UNIT("dc.publisher.forunit", TEXT_FREE, ""),
        CREATED_FOR_PROJECT("dc.creator.project-assignment", TEXT_FREE, ""),
        PUBLISHED_FOR_PROJECT("dc.publisher.project-assignment", TEXT_FREE, ""),
        //        RECORD_TYPE("dc.type.record", TEXT_FIX, "Handlingstyp"),
        DOC_TYPE("dc.type.document", TEXT_FIX, "Dokumenttyp VGR"),
        //        ARCHIVE_CREATOR("dc.creator.recordscreator", LDAP_VALUE, ""),
        PROCESS_NAME("dc.type.process.name", TEXT_FREE, ""),
        CASE_TYPE("dc.type.file.process", TEXT_FREE, ""),
        CASE("dc.type.file", TEXT_FREE, ""),
        JOURNAL_ID("dc.identifier.diarie.id", TEXT_FREE, ""),
        DOCUMENT_SERIES("dc.type.document.serie", TEXT_FREE, ""),
        DOCUMENT_SERIES_ID("dc.type.document.id", TEXT_FREE, ""),
        PHYSICAL_LOCATION("dc.identifier.location", TEXT_FREE, ""),
        //        PUBLISHED_BY("dc.publisher", LDAP_VALUE, ""),
        //        RESPONSIBLE("dc.creator.document", LDAP_VALUE, ""),
        RESPONSIBLE_FUNC("dc.creator.function", TEXT_FREE, ""),
        VALID_FROM_DATE("dc.date.validto", DATE, ""),
        VALID_TO_DATE("dc.date.validfrom", DATE, ""),
        AVAILABLE_FROM_DATE("dc.date.availableto", DATE, ""),
        AVAILABLE_TO_DATE("dc.date.availablefrom", DATE, ""),
        PUBLISHING_FROM_DATE("dc.date.issued", DATE, ""),
        PUBLISHING_TO_DATE("dc.date.issued", DATE, ""),
        PUBLIC_ACCESSIBILITY("dc.rights.accessrights", TEXT_FREE, ""),
        //        SAVED_BY("dc.contributor.savedby", LDAP_VALUE, ""),
        //        CREATED_BY_FIX("dc.creator", LDAP_VALUE, ""),
        CREATED_BY_FREE("dc.creator.freetext", TEXT_FREE, ""),
        TITLE("dc.title", TEXT_FREE, ""),
        ALT_TITLE("dc.title.alternative", TEXT_FREE, ""),
        KEYWORDS_FIX("dc.subject.keywords", TEXT_FREE, ""),
        KEYWORDS_AUTHORS("dc.subject.authorkeywords", TEXT_FREE, ""),
        HSA("dc.coverage.hsacode", TEXT_FIX, "Verksamhetskod"),
        LANGUAGE("dc.language", TEXT_FIX, "Språk"),
        FILE_EXTENSION("dc.format.extension", TEXT_FREE, ""),
        DOCUMENT_STATUS("vgregion.status.document", TEXT_FIX, "Dokumentstatus");

        private String metadataField;
        private String keyString;
        private String metadataKey;
        private MetadataType metadataType;

        Filter(String filterField, MetadataType metadataType, String metadataKey) {
            this.metadataField = filterField;
            this.metadataType = metadataType;
            this.metadataKey = metadataKey;
        }

        public String getFilterField() {
            return metadataField;
        }

        public MetadataType getMetadataType() {
            return metadataType;
        }

        public String getMetadataKey() {
            return metadataKey;
        }

        public String getKeyString() {
            if (StringUtils.isBlank(keyString)) {
                keyString = name().replace('_', '-').toLowerCase(SV_SE);
            }
            return keyString;
        }
    }
}
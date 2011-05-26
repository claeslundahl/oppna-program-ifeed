package se.vgregion.ifeed.types;

import static se.vgregion.ifeed.types.FilterType.Filter.*;
import static se.vgregion.ifeed.types.MetadataType.*;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

public enum FilterType {

    CREATED_FOR(EnumSet.<Filter> of(H_O_S, CREATED_FOR_UNIT, PUBLISHED_FOR_UNIT, CREATED_FOR_PROJECT, PUBLISHED_FOR_PROJECT)),
    CONTEXT(EnumSet.<Filter> of(RECORD_TYPE, DOC_TYPE, ARCHIVE_CREATOR, PROCESS_NAME, CASE_TYPE, CASE, JOURNAL_ID, DOCUMENT_SERIES, DOCUMENT_SERIES_ID, PHYSICAL_LOCATION)),
    MANAGED_BY(EnumSet.<Filter> of(PUBLISHED_BY, RESPONSIBLE, RESPONSIBLE_FUNC)),
    STATUS_AND_LIMITATIONS(EnumSet.<Filter> of(DOCUMENT_STATUS, VALID_FROM_DATE, VALID_TO_DATE, AVAILABLE_FROM_DATE, AVAILABLE_TO_DATE, PUBLISHING_FROM_DATE, PUBLISHING_TO_DATE, PUBLIC_ACCESSIBILITY)),
    CREATED_BY(EnumSet.<Filter> of(SAVED_BY, CREATED_BY_FIX, CREATED_BY_FREE)),
    DESCRIPTION(EnumSet.<Filter> of(TITLE, ALT_TITLE)),
    KEYWORDS_FOR(EnumSet.<Filter> of(KEYWORDS_FIX, AUTHORS_KEYWORDS)),
    HSA_CONTEXT(EnumSet.<Filter> of(HSA)),
    OTHER(EnumSet.<Filter> of(LANGUAGE, FILE_EXTENSION));

    private EnumSet<Filter> filters;
    private String keyString;

    private FilterType(EnumSet<Filter> filters) {
        this.filters = filters;
    }

    public EnumSet<Filter> getFilters() {
        return filters;
    }

    public String getKeyString() {
        if (StringUtils.isBlank(keyString)) {
            keyString = name().replace('_', '-').toLowerCase();
        }
        return keyString;
    }

    public enum Filter {
        H_O_S("dc.audience", MULTI_VALUE, "HosPersKat"),
        CREATED_FOR_UNIT("dc.creator.forunit", TEXT, ""),
        PUBLISHED_FOR_UNIT("dc.publisher.forunit", TEXT, ""),
        CREATED_FOR_PROJECT("dc.creator.project-assignment", TEXT, ""),
        PUBLISHED_FOR_PROJECT("dc.publisher.projects-assignment", TEXT, ""),
        RECORD_TYPE("dc.type.record", MULTI_VALUE, "Handlingstyp"),
        DOC_TYPE("dc.type.document", MULTI_VALUE, "Dokumenttyp VGR"),
        ARCHIVE_CREATOR("dc.creator.recordscreator", TEXT, ""),
        PROCESS_NAME("dc.type.process.name", TEXT, ""),
        CASE_TYPE("dc.type.file.process", TEXT, ""),
        CASE("dc.type.file", TEXT, ""),
        JOURNAL_ID("dc.identifier.diarie.id", TEXT, ""),
        DOCUMENT_SERIES("dc.type.document.serie", TEXT, ""),
        DOCUMENT_SERIES_ID("dc.type.document.id", TEXT, ""),
        PHYSICAL_LOCATION("dc.identifier.location", TEXT, ""),
        PUBLISHED_BY("dc.publisher", TEXT, ""),
        RESPONSIBLE("dc.creator.document", TEXT, ""),
        RESPONSIBLE_FUNC("dc.creator.function", TEXT, ""),
        VALID_FROM_DATE("dc.date.validfrom", DATE, ""),
        VALID_TO_DATE("dc.date.validto", DATE, ""),
        AVAILABLE_FROM_DATE("dc.date.availablefrom", DATE, ""),
        AVAILABLE_TO_DATE("dc.date.availableto", DATE, ""),
        PUBLISHING_FROM_DATE("dc.date.issued", DATE, ""),
        PUBLISHING_TO_DATE("dc.date.issued", DATE, ""),
        PUBLIC_ACCESSIBILITY("dc.rights.accessrights", TEXT, ""),
        SAVED_BY("dc.contributor.savedby", TEXT, ""),
        CREATED_BY_FIX("dc.creator", TEXT, ""),
        CREATED_BY_FREE("dc.creator.freetext", TEXT, ""),
        TITLE("dc.title", TEXT, ""),
        ALT_TITLE("dc.title.alternative", TEXT, ""),
        KEYWORDS_FIX("dc.subject.keywords", TEXT, ""),
        AUTHORS_KEYWORDS("dc.subject.authorkeywords", TEXT, ""),
        HSA("dc.coverage.hsacode", MULTI_VALUE, "Verksamhetskod"),
        LANGUAGE("dc.language", MULTI_VALUE, "Språk"),
        FILE_EXTENSION("dc.format.extension", TEXT, ""),
        DOCUMENT_STATUS("vgr.status.document", MULTI_VALUE, "Dokumentstatus");

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
                keyString = name().replace('_', '-').toLowerCase();
            }
            return keyString;
        }
    }
}
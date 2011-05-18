package se.vgregion.ifeed.types;

import static se.vgregion.ifeed.types.FilterType.Filter.*;
import static se.vgregion.ifeed.types.MetadataType.*;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

public enum FilterType {

    CREATED_FOR(EnumSet.<Filter> of(H_O_S, CREATED_FOR_UNIT, PUBLISHED_FOR_UNIT, CREATED_FOR_PROJECT, PUBLISHED_FOR_PROJECT)),
    CONTEXT(EnumSet.<Filter> of(RECORD_TYPE, DOC_TYPE, ARCHIVE_CREATOR, PROCESS_NAME, CASE_TYPE, CASE, JOURNAL_ID, DOCUMENT_SERIES, DOCUMENT_SERIES_ID, PHYSICAL_LOCATION)),
    MANAGED_BY(EnumSet.<Filter> of(PUBLISHED_BY, RESPONSIBLE, RESPONSIBLE_FUNC)),
    STATUS_AND_LIMITATIONS(EnumSet.<Filter> of(DOCUMENT_STATUS, VALID_FROM_DATE, VALID_TO_DATE, AVAILABLE_FROM_DATE, AVAILABLE_TO_DATE, PUBLISHING_DATE, PUBLIC_ACCESSIBILITY)),
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
        H_O_S("DC:audience", MULTI_VALUE, ""),
        CREATED_FOR_UNIT("DC:creator.forunit", MULTI_VALUE, ""),
        PUBLISHED_FOR_UNIT("DC:publisher.forunit", MULTI_VALUE_EXT, ""),
        CREATED_FOR_PROJECT("DC:creator.project-assignment", TEXT, ""),
        PUBLISHED_FOR_PROJECT("DC:publisher.projects-assignment", TEXT, ""),
        RECORD_TYPE("DC:type.record", MULTI_VALUE, "Handlingstyp"),
        DOC_TYPE("DC:type.document", MULTI_VALUE, "Dokumenttyp VGR"),
        ARCHIVE_CREATOR("DC:creator.recordscreator", MULTI_VALUE_EXT, ""),
        PROCESS_NAME("DC:type.process.name", TEXT, ""),
        CASE_TYPE("DC:type.file.process", TEXT, ""),
        CASE("DC:type.file", TEXT, ""),
        JOURNAL_ID("DC:identifier.diarie.id", TEXT, ""),
        DOCUMENT_SERIES("DC:type.document.serie", TEXT, ""),
        DOCUMENT_SERIES_ID("DC:type.document.id", TEXT, ""),
        PHYSICAL_LOCATION("DC:identifier.location", TEXT, ""),
        PUBLISHED_BY("DC:publisher", MULTI_VALUE_EXT, ""),
        RESPONSIBLE("DC:creator.document", MULTI_VALUE_EXT, ""),
        RESPONSIBLE_FUNC("DC:creator.function", TEXT, ""),
        VALID_FROM_DATE("DC:date.validfrom", DATE, ""),
        VALID_TO_DATE("DC:date.validto", DATE, ""),
        AVAILABLE_FROM_DATE("DC:date.availablefrom", DATE, ""),
        AVAILABLE_TO_DATE("DC:date.availableto", DATE, ""),
        PUBLISHING_DATE("DC:date.issued", DATE, ""),
        PUBLIC_ACCESSIBILITY("DC:rights.accessrights", TEXT, ""),
        SAVED_BY("DC:contributor.savedby", MULTI_VALUE_EXT, ""),
        CREATED_BY_FIX("DC:creator", MULTI_VALUE_EXT, ""),
        CREATED_BY_FREE("DC:creator.freetext", TEXT, ""),
        TITLE("DC:title", TEXT, ""),
        ALT_TITLE("DC:title.alternative", TEXT, ""),
        KEYWORDS_FIX("DC:subject.keywords", MULTI_VALUE, ""),
        AUTHORS_KEYWORDS("DC:subject.authorkeywords", TEXT, ""),
        HSA("DC:coverage.hsacode", MULTI_VALUE_EXT, "Verksamhetskod"),
        LANGUAGE("DC:language", MULTI_VALUE, "Språk"),
        FILE_EXTENSION("DC:format.extension", MULTI_VALUE_EXT, ""),
        DOCUMENT_STATUS("HC:status.document", MULTI_VALUE, "Dokumentstatus");

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
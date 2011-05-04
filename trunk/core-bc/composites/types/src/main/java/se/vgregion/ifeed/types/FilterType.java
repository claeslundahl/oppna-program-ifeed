package se.vgregion.ifeed.types;

import static se.vgregion.ifeed.types.FilterType.Filter.*;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

public enum FilterType {

    CREATED_FOR(EnumSet.<Filter> of(H_O_S, CREATED_FOR_UNIT, PUBLISHED_FOR_UNIT, CREATED_FOR_PROJECT, PUBLISHED_FOR_PROJECT)),
    CONTEXT(EnumSet.<Filter> of(RECORD_TYPE, DOC_TYPE, ARCHIVE_CREATOR, PROCESS_NAME, CASE_TYPE, CASE, JOURNAL_ID, DOCUMENT_SERIES, DOCUMENT_SERIES_ID, PHYSICAL_LOCATION)),
    MANAGED_BY(EnumSet.<Filter> of(PUBLISHED_BY, RESPONSIBLE, RESPONSIBLE_FUNC)),
    STATUS_AND_LIMITATIONS(EnumSet.<Filter> of(DOCUMENT_STATUS, VALID_FROM_DATE, VALID_TO_DATE, AVAILABLE_FROM_DATE, AVAILABLE_TO_DATE, PUBLISHING_DATE, PUBLIC_ACCESSIBILITY)),
    CREATED_BY(EnumSet.<Filter> of(SAVED_BY, CREATED_BY_FIX, CREATED_BY_FREE)),
    DESCRIPTION(EnumSet.<Filter> of(TITLE, ALT_TITLE)),
    KEYWORDS(EnumSet.<Filter> of(Filter.KEYWORDS, AUTHORS_KEYWORDS)),
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
        H_O_S("DC:audience"),
        CREATED_FOR_UNIT("DC:creator.forunit"),
        PUBLISHED_FOR_UNIT("DC:publisher.forunit"),
        CREATED_FOR_PROJECT("DC:creator.project-assignment "),
        PUBLISHED_FOR_PROJECT("DC:publisher.projects-assignment"),
        RECORD_TYPE("DC:type.record"),
        DOC_TYPE("DC:type.document"),
        ARCHIVE_CREATOR("DC:creator.recordscreator"),
        PROCESS_NAME("DC:type.process.name"),
        CASE_TYPE("DC:type.file.process"),
        CASE("DC:type.file"),
        JOURNAL_ID("DC:identifier.diarie.id"),
        DOCUMENT_SERIES("DC:type.document.serie"),
        DOCUMENT_SERIES_ID("DC:type.document.id"),
        PHYSICAL_LOCATION(""),
        PUBLISHED_BY("DC:publisher"),
        RESPONSIBLE("DC:creator.document"),
        RESPONSIBLE_FUNC("DC:creator.function"),
        VALID_FROM_DATE("DC:date.validfrom"),
        VALID_TO_DATE("DC:date.validto"),
        AVAILABLE_FROM_DATE("DC:date.availablefrom"),
        AVAILABLE_TO_DATE("DC:date.availableto"),
        PUBLISHING_DATE("DC:date.issued"),
        PUBLIC_ACCESSIBILITY("DC:rights.accessrights"),
        SAVED_BY("DC:contributor.savedby"),
        CREATED_BY_FIX("author"),
        //        CREATED_BY_FIX("DC:creator"),
        CREATED_BY_FREE("DC:creator.freetext"),
        TITLE("title"),
        //        TITLE("DC:title"),
        ALT_TITLE("DC:title.alternative"),
        KEYWORDS("DC:subject.keywords"),
        AUTHORS_KEYWORDS("DC:subject.authorkeywords"),
        HSA("DC:coverage.hsacode"),
        LANGUAGE("DC:language"),
        FILE_EXTENSION("DC:format.extension"),
        DOCUMENT_STATUS("HC:status.document");

        private String metadataField;
        private String keyString;

        Filter(String filterField) {
            this.metadataField = filterField;
        }

        public String getFilterField() {
            return metadataField;
        }

        public String getKeyString() {
            if (StringUtils.isBlank(keyString)) {
                keyString = name().replace('_', '-').toLowerCase();
            }
            return keyString;
        }
    }
}
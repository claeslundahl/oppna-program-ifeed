package se.vgregion.ifeed.types;

import static se.vgregion.ifeed.types.FilterType.Filter.*;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

public enum FilterType {

    CREATED_FOR(
            EnumSet.<Filter> of(HO_S, CREATED_FOR_UNIT, PUBLISHED_FOR_UNIT, CREATED_FOR_PROJECT, PUBLISHED_FOR_PROJECT)
    ),
    CONTEXT(
            EnumSet.<Filter> of(DOC_TYPE_GROUP, DOC_TYPE, ARCHIVE_CREATOR, PROCESS_NAME, CASE_TYPE, CASE, JOURNAL_ID, DOCUMENT_SERIES, DOCUMENT_SERIES_ID, PHYSICAL_LOCATION)
    ),
    MANAGED_BY(
            EnumSet.<Filter> of(PUBLISHED_BY, RESPONSIBLE, RESPONSIBLE_FUNC)
    ),
    STATUS_AND_LIMITATIONS(
            EnumSet.<Filter> of(VALID_FORM_DATE, VALID_TO_DATE, AVAILABLE_FROM_DATE, AVAILABLE_TO_DATE, PUBLISHING_DATE, PUBLIC_ACCESSIBILITY)
    ),
    CREATED_BY(
            EnumSet.<Filter> of(SAVED_BY, CREATED_BY_FIX, CREATED_BY_FREE)
    ),
    DESCRIPTION(
            EnumSet.<Filter> of(TITLE, ALT_TITLE)
    ),
    KEYWORDS(
            EnumSet.<Filter>of(Filter.KEYWORDS, AUTHORS_KEYWORDS)
    ),
    HSA_CONTEXT(
            EnumSet.<Filter> of(HSA)
    ),
    OTHER(
            EnumSet.<Filter> of(LANGUAGE, FILE_EXTENSION)
    );

    private EnumSet<Filter> filters;
    private String keyString;

    private FilterType(EnumSet<Filter> filters) {
        this.filters = filters;
    }

    public EnumSet<Filter> getFilters() {
        return filters;
    }

    public String getKeyString() {
        if(StringUtils.isBlank(keyString)) {
            keyString = name().replace('_', '-').toLowerCase();
        }
        return keyString;
    }



    public enum Filter {
        HO_S(""), CREATED_FOR_UNIT(""), PUBLISHED_FOR_UNIT(""), CREATED_FOR_PROJECT(""), PUBLISHED_FOR_PROJECT(""),
        DOC_TYPE_GROUP(""), DOC_TYPE(""), ARCHIVE_CREATOR(""), PROCESS_NAME(""), CASE_TYPE(""), CASE(""), JOURNAL_ID(""),
        DOCUMENT_SERIES(""), DOCUMENT_SERIES_ID(""), PHYSICAL_LOCATION(""), PUBLISHED_BY(""), RESPONSIBLE(""), RESPONSIBLE_FUNC(""),
        VALID_FORM_DATE(""), VALID_TO_DATE(""), AVAILABLE_FROM_DATE(""), AVAILABLE_TO_DATE(""), PUBLISHING_DATE(""),
        PUBLIC_ACCESSIBILITY(""), SAVED_BY(""), CREATED_BY_FIX(""), CREATED_BY_FREE(""), TITLE(""), ALT_TITLE(""),
        KEYWORDS(""), AUTHORS_KEYWORDS(""), HSA(""), LANGUAGE(""), FILE_EXTENSION("");

        private String filterField;
        private String keyString;

        Filter(String filterField) {
            this.filterField = filterField;
        }

        public String getFilterField() {
            return filterField;
        }

        public String getKeyString() {
            if(StringUtils.isBlank(keyString)) {
                keyString = name().replace('_', '-').toLowerCase();
            }
            return keyString;
        }
    }
}
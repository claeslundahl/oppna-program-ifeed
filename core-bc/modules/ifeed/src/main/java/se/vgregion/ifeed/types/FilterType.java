package se.vgregion.ifeed.types;

@Deprecated // Is this used in a practical way?
public enum FilterType {
/*

    CREATED_FOR(EnumSet.<Filter>of(Filter.H_O_S, */
    /* CREATED_FOR_UNIT, PUBLISHED_FOR_UNIT, *//*
Filter.CREATED_FOR_PROJECT,
            Filter.PUBLISHED_FOR_PROJECT)), CONTEXT(EnumSet.<Filter>of(*/
    /* RECORD_TYPE, *//*
Filter.DOC_TYPE, */
    /* ARCHIVE_CREATOR, *//*

            Filter.PROCESS_NAME, Filter.CASE_TYPE, Filter.CASE, Filter.JOURNAL_ID, Filter.DOCUMENT_SERIES, Filter.DOCUMENT_SERIES_ID, Filter.PHYSICAL_LOCATION)), MANAGED_BY(
            EnumSet.<Filter>of(Filter.PUBLISHED_BY, Filter.RESPONSIBLE, Filter.RESPONSIBLE_FUNC)), STATUS_AND_LIMITATIONS(EnumSet
            .<Filter>of(Filter.DOCUMENT_STATUS, Filter.VALID_FROM_DATE, Filter.VALID_TO_DATE, Filter.AVAILABLE_FROM_DATE, Filter.AVAILABLE_TO_DATE,
                    Filter.PUBLISHING_FROM_DATE, Filter.PUBLISHING_TO_DATE, Filter.PUBLIC_ACCESSIBILITY)), CREATED_BY(EnumSet
            .<Filter>of(Filter.SAVED_BY, Filter.CREATED_BY_FIX, Filter.CREATED_BY_FREE)), DOC_DESCRIPTION(EnumSet.<Filter>of(Filter.TITLE,
            Filter.ALT_TITLE)), KEYWORDS_FOR(EnumSet.<Filter>of(Filter.KEYWORDS_FIX, Filter.KEYWORDS_AUTHORS)), HSA_CONTEXT(EnumSet
            .<Filter>of(Filter.HSA)), OTHER(EnumSet.<Filter>of(Filter.LANGUAGE, Filter.FILE_EXTENSION));

    private EnumSet<Filter> filters;
    private String keyString;
    private static final Locale SV_SE = new Locale("sv_SE");

    private FilterType(EnumSet<Filter> filters) {
        this.filters = filters;
    }

    public EnumSet<Filter> getFilters() {
        return filters;
    }

    public enum Filter {
        H_O_S("dc.audience", TEXT_FIX, "HosPersKat"), CREATED_FOR_UNIT("dc.creator.forunit", TEXT_FREE, ""), PUBLISHED_FOR_UNIT(
                "dc.publisher.forunit", TEXT_FREE, ""), CREATED_FOR_PROJECT("dc.creator.project-assignment",
                TEXT_FREE, ""), PUBLISHED_FOR_PROJECT("dc.publisher.project-assignment", TEXT_FREE, ""),
        // RECORD_TYPE("dc.type.record", TEXT_FIX, "Handlingstyp"),
        DOC_TYPE("dc.type.document", TEXT_FIX, "Dokumenttyp VGR"),
        // ARCHIVE_CREATOR("dc.creator.recordscreator", LDAP_VALUE, ""),
        PROCESS_NAME("dc.type.process.name", TEXT_FREE, ""), CASE_TYPE("dc.type.file.process", TEXT_FREE, ""), CASE(
                "dc.type.file", TEXT_FREE, ""), JOURNAL_ID("dc.identifier.diarie.id", TEXT_FREE, ""), DOCUMENT_SERIES(
                "dc.type.document.serie", TEXT_FREE, ""), DOCUMENT_SERIES_ID("dc.type.document.id", TEXT_FREE, ""), PHYSICAL_LOCATION(
                "dc.identifier.location", TEXT_FREE, ""),
        // PUBLISHED_BY("dc.publisher", LDAP_VALUE, ""),

        PUBLISHED_BY("dc.publisher.id", LDAP_VALUE, ""),

        RESPONSIBLE("dc.creator.document.id", LDAP_VALUE, ""),

        RESPONSIBLE_FUNC("dc.creator.function", TEXT_FREE, ""), VALID_FROM_DATE("dc.date.validto", DATE, ""), VALID_TO_DATE(
                "dc.date.validfrom", DATE, ""), AVAILABLE_FROM_DATE("dc.date.availableto", DATE, ""), AVAILABLE_TO_DATE(
                "dc.date.availablefrom", DATE, ""), PUBLISHING_FROM_DATE("dc.date.issued", DATE, ""), PUBLISHING_TO_DATE(
                "dc.date.issued", DATE, ""), PUBLIC_ACCESSIBILITY("dc.rights.accessrights", TEXT_FREE, ""),

        SAVED_BY("dc.contributor.savedby.id", LDAP_VALUE, ""),

        // CREATED_BY_FIX("dc.creator", LDAP_VALUE, ""),
        CREATED_BY_FIX("dc.creator.id", LDAP_VALUE, ""), // Is this the same as the commented line above?
        CREATED_BY_FREE("dc.creator.freetext", TEXT_FREE, ""), TITLE("dc.title", TEXT_FREE, ""), ALT_TITLE(
                "dc.title.alternative", TEXT_FREE, ""), KEYWORDS_FIX("dc.subject.keywords", TEXT_FREE, ""), KEYWORDS_AUTHORS(
                "dc.subject.authorkeywords", TEXT_FREE, ""), HSA("dc.coverage.hsacode", TEXT_FIX, "Verksamhetskod"), LANGUAGE(
                "language", */
    /* "dc.language", *//*
TEXT_FIX, "Språk"), FILE_EXTENSION("dc.format.extension",
                TEXT_FREE, ""), DOCUMENT_STATUS("vgr.status.document", TEXT_FIX, "Dokumentstatus");

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

        public String getLabelKey() {
            return metadataField + getSuffix() + ".label";
        }

        public String getHelpKey() {
            return metadataField + getSuffix() + ".help";
        }

        private String getSuffix() {
            String suffix = "";
            if (PUBLISHING_TO_DATE.equals(this)) {
                suffix = ".to";
            } else if (PUBLISHING_FROM_DATE.equals(this)) {
                suffix = ".from";
            }
            return suffix;
        }

        public String getKeyString() {
            if (StringUtils.isBlank(keyString)) {
                keyString = name().replace('_', '-').toLowerCase(SV_SE);
            }
            return keyString;
        }
    }

    public static void main(String[] args) {
        for (FilterType ft : FilterType.values()) {
            for (Filter f : ft.filters) {
                System.out.println("update vgr_ifeed_filter set filterkey='" + f.metadataField
                        + "' where filter ='" + f + "';");
            }
        }
        String s = "hej?foo=123&rows=501&bla";

        s = s.replaceAll("rows" + Pattern.quote("=") + "[0-9]+", "rows=0");
        System.out.println("\ns= " + s);

    }
*/
}

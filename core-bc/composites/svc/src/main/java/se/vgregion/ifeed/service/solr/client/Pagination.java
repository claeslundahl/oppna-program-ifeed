package se.vgregion.ifeed.service.solr.client;

import java.util.ArrayList;

public class Pagination {
    private int offset;
    private int hitsPerPage;
    private Page firstPage;
    private Page previousPage;
    private NextPage nextPage;
    private LastPage lastPage;
    private ArrayList<Page> pages;

    private int getOffset() {
        return offset;
    }

    private void setOffset(int offset) {
        this.offset = offset;
    }

    private int getHitsPerPage() {
        return hitsPerPage;
    }

    private void setHitsPerPage(int hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    private NextPage getNextPage() {
        return nextPage;
    }

    private void setNextPage(NextPage nextPage) {
        this.nextPage = nextPage;
    }

    private LastPage getLastPage() {
        return lastPage;
    }

    private void setLastPage(LastPage lastPage) {
        this.lastPage = lastPage;
    }

    private ArrayList<Page> getPages() {
        return pages;
    }

    private void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }
}

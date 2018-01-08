package se.vgregion.ifeed.service.solr;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by clalu4 on 2016-11-09.
 */
public class SolrServerFactoryTest {

    @Test
    public void getPropertiesPath() {
        System.out.println(SolrServerFactory.getPropertiesPath());
    }

    @Test
    public void create() {
        System.out.println(SolrServerFactory.create());
    }

}
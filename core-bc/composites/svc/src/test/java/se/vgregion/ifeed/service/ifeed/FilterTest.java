package se.vgregion.ifeed.service.ifeed;

import org.junit.Test;

public class FilterTest {

    @Test
    public void fixUnIndexedParameterQuestionMarks() {
        String input = "select distinct o from IFeed o left join fetch o.ownerships ow  left join o.composites cs  left join o.partOf pof  left join o.dynamicTableDefs dtab  left join fetch o.filters fters  where (o.userId like ? or ow.userId like ?) order by o.name\n";
        String result = Filter.fixUnIndexedParameterQuestionMarks(input);
        System.out.println(input);
        System.out.println(result);
    }

}

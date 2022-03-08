package se.vgregion.ifeed.tools;

import java.util.ArrayList;
import java.util.List;

public class Department extends Tuple {

    public static List<? extends Department> toDepartments(List<Tuple> findings) {
        List<Department> result = new ArrayList<>();
        for (Tuple finding : findings) {
            result.add(toDepartment(finding));
        }
        return result;
    }

    public static Department toDepartment(Tuple tuple) {
        Department result = new Department();
        result.putAll(tuple);
        return result;
    }

}

package se.vgregion.ifeed.types.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class Junctor extends ArrayList<String> {

    private final String sign;

    public Junctor(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean add(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        return super.add(s);
    }

    public String toQuery() {
        String r = StringUtils.join(this, sign);
        if (size() > 1) {
            r = "(" + r + ")";
        }
        return r;
    }

}

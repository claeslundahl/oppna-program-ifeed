package se.vgregion.ldap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class LdapSupportFileSys implements LdapSupportService {

    File root;

    public LdapSupportFileSys(File root) {
        this.root = root;
    }

    @Override
    public <T extends HasCommonLdapFields> List<T> findChildNodes(T org) {
        List<T> result = new ArrayList<T>();

        List<String> path = Arrays.asList(org.getDn().split(Pattern.quote(",")));
        Collections.reverse(path);

        File finding = findDirectory(root, path);

        for (File child : finding.listFiles()) {
            if (child.isDirectory()) {
                try {
                    T item = (T) org.getClass().newInstance();
                    List<String> localPath = new ArrayList<String>(path);
                    localPath.add(0, child.getName());
                    item.setDn(merge(localPath, ","));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    File findDirectory(File start, List<String> steps) {
        String path = toFilePath(steps);
        File result = new File(root.getAbsolutePath() + File.separator + toFilePath(steps));
        return result;
    }

    String toFilePath(List<String> parts) {
        return merge(parts, File.separator);
    }

    String merge(List<String> parts, String junctor) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
            sb.append(junctor);
        }
        if (parts.size() > 1) {
            sb.delete(sb.length() - junctor.length(), sb.length());
        }
        return sb.toString();
    }

}

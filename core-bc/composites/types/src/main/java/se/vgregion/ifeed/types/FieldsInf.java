package se.vgregion.ifeed.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import se.vgregion.common.utils.BeanMap;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Entity
@Table(name = "vgr_ifeed_fields_inf")
public class FieldsInf extends AbstractEntity<Long> implements Serializable, Comparable<FieldsInf> {

    private static final long serialVersionUID = 1L;

    public FieldsInf() {
        // For the jpa.
    }

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    private String creatorId;

    @Column(length = 200_000)
    private String text;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        // toList(text);
        this.text = text;
    }

    private List<FieldInf> toList(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(json, List.class);
        } catch (JsonSyntaxException je) {
            throw new RuntimeException("Could not parse: " + json, je);
        }
    }

    private List<FieldInf> toList() {
        return toList(getText());
    }

    public List<FieldInf> getFieldInfs() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String t = "{ \"fieldInfs\": " + text.trim() + "}";

        FieldsJsonWrapper result = gson.fromJson(t, FieldsJsonWrapper.class);
        for (FieldInf fi : result.getFieldInfs()) {
            fi.init();
        }
        return result.getFieldInfs();
    }

    public List<FieldInf> getFieldInfsFromTabularText() {
        List<FieldInf> result = new ArrayList<FieldInf>();

        String[] rows = getText().split(Pattern.quote("\n"));

        Map<Integer, String> fieldPosition = new HashMap<Integer, String>();
        String[] first = rows[0].split(Pattern.quote("|"));

        for (int i = 0; i < first.length; i++) {
            fieldPosition.put(i, first[i].trim());
        }

        List<FieldInf> nestedResult = result;

        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            String[] cells = row.split(Pattern.quote("|"));
            FieldInf fi = new FieldInf();

            BeanMap bm = new BeanMap(fi);
            for (int c = 0, j = fieldPosition.size(); c < j; c++) {
                if (c >= cells.length) {
                    continue;
                }
                String name = fieldPosition.get(c);
                Class<?> type = bm.getType(name);

                if (type.equals(Boolean.TYPE)) {
                    String part = cells[c].trim();
                    boolean b = "yes".equalsIgnoreCase(part);
                    bm.put(name, b);
                } else if (type.equals(List.class) && name.equals("children")) {
                    ((List) bm.get(name)).add(cells[c].trim());
                } else {
                    bm.put(name, cells[c].trim());
                }
            }

            if (fi.getLabel()) {
                nestedResult = fi.getChildren();
                result.add(fi);
            } else {
                fi.setId(fi.getId().toLowerCase());
                nestedResult.add(fi);
            }
        }

        return result;
    }

    public void putFieldInfInto(IFeedFilter item) {
        item.setFieldInf(getInfByFilterKey(item.getFilterKey()));
    }

    public void putFieldInfInto(Iterable<IFeedFilter> items) {
        for (IFeedFilter item : items) {
            putFieldInfInto(item);
        }
    }

    FieldInf getInfByFilterKey(String id) {
        return getInfByFilterKey(getFieldInfs(), id);
    }

    FieldInf getInfByFilterKey(List<FieldInf> infs, String id) {
        for (FieldInf fi : infs) {
            if (fi.getId() != null && fi.getId().equalsIgnoreCase(id)) {
                return fi;
            }
            FieldInf result = getInfByFilterKey(fi.getChildren(), id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public int compareTo(FieldsInf o) {
        return (int) (id - o.id);
    }

    public static class FieldsJsonWrapper {

        public FieldsJsonWrapper() {
            super();
        }

        public FieldsJsonWrapper(List<FieldInf> fieldInfs) {
            super();
            this.fieldInfs = fieldInfs;
        }

        private List<FieldInf> fieldInfs;

        public List<FieldInf> getFieldInfs() {
            return fieldInfs;
        }

        public void setFieldInfs(List<FieldInf> fieldInfs) {
            this.fieldInfs = fieldInfs;
        }

    }

    public static void putDataIntoCache(String jsonTxt) {
        try {
            putDataIntoCacheImp(jsonTxt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void putDataIntoCacheImp(String jsonTxt) throws IOException {
        File userHome = new File(System.getProperty("user.home"));
        Path path = Paths.get(userHome.getAbsolutePath(), ".hotell", "ifeed", "fields.cache.json");
        Files.write(path, jsonTxt.getBytes());
    }

    public static String getDataFromCache() {
        try {
            return getDataFromCacheImp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getDataFromCacheImp() throws IOException {
        File userHome = new File(System.getProperty("user.home"));
        Path path = Paths.get(userHome.getAbsolutePath(), ".hotell", "ifeed", "fields.cache.json");
        return new String(Files.readAllBytes(path));
    }

}

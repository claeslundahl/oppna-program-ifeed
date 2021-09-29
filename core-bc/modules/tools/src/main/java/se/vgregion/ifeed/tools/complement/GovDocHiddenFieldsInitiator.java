package se.vgregion.ifeed.tools.complement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.vgregion.common.utils.MultiMap;
import se.vgregion.ifeed.tools.DatabaseApi;
import se.vgregion.ifeed.tools.Tuple;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated // Is this needed? It is also done in the HiddenFieldsUtil/Starter.
public class GovDocHiddenFieldsInitiator {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static DatabaseApi database;


    static MultiMap<String, Map> getRecordsToAdd() throws IOException {
        String toTypes = (new File(".").getAbsolutePath().replace(".", ""));
        Path path = Paths.get(toTypes, "core-bc", "modules", "tools", "src", "main", "resources", "more.hidden.field.infs.json");
        //System.out.println(Files.readString(path));
        MultiMap<String, Map> items = gson.fromJson(Files.readString(path), MultiMap.class);
        /*for (Object key : items.keySet()) {
            System.out.println(key);
            for (Map map : items.get(key)) {
                System.out.println(map);
            }
        }*/
        return items;
    }

    public static void main(String[] args) throws IOException {
        database = DatabaseApi.getLocalApi();
        GoverningDocComplettion governingDocComplettion = new GoverningDocComplettion(database);
        final List<Tuple> alreadyThere = governingDocComplettion.getHiddenFields();
        Set<Map> rta = getRecordsToAdd().values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        System.out.println("rta count " + rta.size());
        List<Map> notAlreadyThere = rta.stream()
                .filter(map -> !alreadyThere.stream().map(at -> at.get("id").toString().toLowerCase(Locale.ROOT))
                        .collect(Collectors.toSet()).contains(map.get("id").toString().toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        for (Map map : notAlreadyThere) {
            System.out.println(map.get("id"));
        }
    }



}

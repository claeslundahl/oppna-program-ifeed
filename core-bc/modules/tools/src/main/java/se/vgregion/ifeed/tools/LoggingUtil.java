package se.vgregion.ifeed.tools;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LoggingUtil {

    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy:HH:mm:ss");

    public static void main(String[] args) throws IOException {
        String textualNowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        DatabaseApi database = DatabaseApi.getLocalApi();

        makeTableIfMissing(database);

        Path path = Paths.get(System.getProperty("user.home"), ".hotell", "ifeed", "access-logs");
        final NavigableSet<String> previouslyAdded = new TreeSet(database.query("select id from access_log").stream().map(m -> m.get("id")).collect(Collectors.toSet()));
        NavigableSet<String> allFeedIds = new TreeSet<>(database.query("select id from vgr_ifeed").stream().map(m -> m.get("id").toString()).collect(Collectors.toSet()));
        Files.list(path).forEach(c -> {
            final NavigableSet<Tuple> toAdd = new TreeSet<>();
            System.out.println(c);
            try {
                if (c.toString().endsWith(".txt") && !c.toString().contains(textualNowDate)) {
                    List<String> content = Files.readAllLines(c);
                    for (final String s : content) {
                        if (previouslyAdded.contains(s))
                            continue;
                        previouslyAdded.add(s);
                        List<String> parts = Arrays.asList(s.split(Pattern.quote(" ")));
                        String host = parts.get(0).replaceAll("[^0-9]^\\.", "");
                        String textDate = parts.get(3).replace("[", "");
                        Tuple tuple = new Tuple();
                        tuple.put("id", s);
                        tuple.put("host", host);
                        tuple.put("time", toDate(textDate));
                        final String url = parts.get(6);
                        tuple.put("path", url);
                        tuple.put("http_status", Integer.parseInt(parts.get(parts.size() - 2).trim()));
                        String[] urlParts = url.split("[/%\\=\\?\\&]");
                        Long ifeedId = Arrays.stream(urlParts).filter(u -> allFeedIds.contains(u)).map(u -> Long.parseLong(u))
                                .max(Comparator.naturalOrder()).orElse(null);
                        tuple.put("ifeed_id", ifeedId);
                        toAdd.add(tuple);
                    }
                    for (Tuple item : toAdd) {
                        try {
                            database.insert("access_log", item);
                        } catch (Exception e) {
                            System.out.println("Error for item: " + new GsonBuilder().setPrettyPrinting().create().toJson(item));
                            database.rollback();
                            throw new RuntimeException(e);
                        }
                    }
                    database.commit();
                    Files.delete(c);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static Map<String, Integer> months = new HashMap<>();

    static {
        List raw = Arrays.asList(1, "Jan", 2, "Feb", 3, "Mars", 4, "Apr", 5, "Maj", 6, "Juni", 7, "Juli", 8, "Aug", 9, "Sep", 10, "Okt", 11, "Nov", 12, "Dec");
        for (int i = 0; i < raw.size(); i += 2) {
            months.put((String) raw.get(i + 1), (Integer) raw.get(i));
        }
    }

    static Timestamp toDate(String fromThat) {
        String[] stuff = fromThat.split("[^0-9a-öA-Ö]");
        // 24/sep/2021:14:31:11
        Date date = new Date();
        date.setDate(Integer.parseInt(stuff[0]));
        date.setMonth(months.get(stuff[1]));
        date.setHours(Integer.parseInt(stuff[2]));
        date.setMinutes(Integer.parseInt(stuff[3]));
        date.setSeconds(Integer.parseInt(stuff[4]));

        /*List<String> names = new ArrayList<>();
        int i = 1;
        for (String str : new DateFormatSymbols().getShortMonths()) {
            if (str.length() < 2) continue;
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
            str = str.replace(".", "");
            names.add(String.format("%d, \"%s\"", i++, str));
        }
        System.out.println("hej: " + names.stream().collect(Collectors.joining(", ")));*/

        return new Timestamp(date.getTime());
    }

    private static void makeTableIfMissing(DatabaseApi here) {
        int result = here.update("create table if not exists access_log (\n" +
                "    id character varying(1000) NOT NULL,\n" +
                "    host character varying(255),\n" +
                "    http_status character varying(255),\n" +
                "    ifeed_id bigint,\n" +
                "    path character varying(1000),\n" +
                "    \"time\" timestamp without time zone,\n" +
                "    CONSTRAINT access_log_pkey PRIMARY KEY (id)\n" +
                ");");
        if (result > 0) {
            here.commit();
            System.out.println("Created the access_log table.");
        }
    }

}

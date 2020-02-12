package se.vgregion.ifeed.service.kiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import se.vgregion.common.utils.Props;
import se.vgregion.ifeed.service.kiv.organization.Roles;
import se.vgregion.ifeed.service.kiv.organization.Unit;
import se.vgregion.ifeed.service.kiv.organization.Units;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author Patrik Björk
 */
@Service
public class UnitSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitSearchService.class);

    private Units unitsRoot;
    private Roles rolesRoot;

    @Value("${kiv.units.url}")
    private String kivUnitsUrl;

    @Value("${kiv.roles.url}")
    private String kivRolesUrl;

    @Value("${trustStore}")
    private String trustStore;

    @Value("${trustStorePassword}")
    private String trustStorePassword;

    @Value("${keyStore}")
    private String keyStore;

    @Value("${keyStorePassword}")
    private String keyStorePassword;

    @Value("${trustStoreType}")
    private String trustStoreType;

    @Value("${keyStoreType}")
    private String keyStoreType;

    @PostConstruct
    public void init() throws IOException {
        try {
            updateUnits();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 25 6 * * *")
    public void update() {
        try {
            updateUnits();
            LOGGER.info("Updated units.");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static UnitSearchService newInstanceFromConfig() {
        Properties props = Props.fetchProperties();
        UnitSearchService uss = new UnitSearchService();

        uss.setKeyStore(props.getProperty("keyStore"));
        uss.setKeyStorePassword(props.getProperty("keyStorePassword"));
        uss.setKeyStoreType(props.getProperty("keyStoreType"));
        uss.setKivRolesUrl(props.getProperty("kiv.roles.url"));
        uss.setKivUnitsUrl(props.getProperty("kiv.units.url"));
        uss.setTrustStore(props.getProperty("trustStore"));
        uss.setTrustStoreType(props.getProperty("trustStoreType"));
        uss.setTrustStorePassword(props.getProperty("trustStorePassword"));

        return uss;
    }

    void updateUnits() throws IOException {
        if (!StringUtils.isEmpty(kivUnitsUrl) && !StringUtils.isEmpty(kivRolesUrl)) {
            this.unitsRoot = fetchAndParse(new URL(kivUnitsUrl), Units.class);
            this.rolesRoot = fetchAndParse(new URL(kivRolesUrl), Roles.class);
        } else {
            LOGGER.info("No kivUnitsUrl set. Skipping units update.");
        }
    }

    private <T> T fetchAndParse(URL url, Class<T> clazz) throws IOException {
        URLConnection urlConnection = url.openConnection();

        if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection connection = (HttpsURLConnection) urlConnection;

            connection.setSSLSocketFactory(new ConvenientSslContextFactory(trustStore, trustStorePassword,
                    trustStoreType,
                    keyStore,
                    keyStorePassword,
                    keyStoreType).createSslContext().getSocketFactory());
        }

        T value;
        try (InputStream inputStream = urlConnection.getInputStream()) {
            String text = toText(inputStream);
            value = gson.fromJson(text, clazz);
        }
        return value;
    }

    static Gson gson = new GsonBuilder().create();

    static String toText(InputStream is) {
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return buf.toString("UTF-8");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public List<Unit> searchUnits(String query) {
        if (query == null) {
            throw new IllegalArgumentException("searchTerm must not be null");
        }

        if (this.unitsRoot == null || this.rolesRoot == null) {
            throw new RuntimeException("UnitsRoot and/or rolesRoot is not intialized.");
        }

        List<Unit> results = new ArrayList<>();

        List<Unit> units = new ArrayList<>(this.unitsRoot.getUnits());
        units.addAll(this.rolesRoot.getRoles());

        for (Unit unit : units) {
            List<String> ou = unit.getAttributes().getOu();
            List<String>    hsaIdentity = unit.getAttributes().getHsaIdentity();
            if (ou != null && ou.size() > 0 && matches(ou.get(0), query.toLowerCase())) {
                results.add(unit);
            } else if (hsaIdentity != null && hsaIdentity.size() > 0 && hsaIdentity.get(0).toLowerCase().contains(query.toLowerCase())) {
                results.add(unit);
            }
        }

        return results;
    }

    private boolean matches(String text, String query) {
        String[] split = query.split(" ");

        for (String word : split) {
            if (!text.toLowerCase().contains(word)) {
                return false;
            }
        }

        return true;
    }

    public Units getUnitsRoot() {
        return unitsRoot;
    }

    public void setUnitsRoot(Units unitsRoot) {
        this.unitsRoot = unitsRoot;
    }

    public Roles getRolesRoot() {
        return rolesRoot;
    }

    public void setRolesRoot(Roles rolesRoot) {
        this.rolesRoot = rolesRoot;
    }

    public String getKivUnitsUrl() {
        return kivUnitsUrl;
    }

    public void setKivUnitsUrl(String kivUnitsUrl) {
        this.kivUnitsUrl = kivUnitsUrl;
    }

    public String getKivRolesUrl() {
        return kivRolesUrl;
    }

    public void setKivRolesUrl(String kivRolesUrl) {
        this.kivRolesUrl = kivRolesUrl;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
}

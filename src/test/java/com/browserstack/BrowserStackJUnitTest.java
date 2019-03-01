package com.browserstack;

import com.browserstack.local.Local;

import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parallelized.class)
public class BrowserStackJUnitTest {
    public WebDriver driver;
    private Local bsLocal;

    private static JSONObject config;

    @Parameter(value = 0)
    public int taskID;

    @Parameters
    public static Iterable<? extends Object> data() throws Exception {
        List<Integer> taskIDs = new ArrayList<Integer>();

        if (System.getProperty("config") != null) {
            JSONParser parser = new JSONParser();
            config = (JSONObject) parser
                    .parse(new FileReader("src/test/resources/conf/" + System.getProperty("config")));
            int envs = ((JSONArray) config.get("environments")).size();

            for (int i = 0; i < envs; i++) {
                taskIDs.add(i);
            }
        }

        return taskIDs;
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        JSONArray envs = (JSONArray) config.get("environments");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, Object> envCapabilities = (Map<String, Object>) envs.get(taskID);
        Iterator<Map.Entry<String, Object>> it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue());
        }

        Map<String, Object> commonCapabilities = (Map<String, Object>) config.get("capabilities");
        it = commonCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
            Object envData = capabilities.getCapability(pair.getKey().toString());
            Object resultData = pair.getValue();
            if (envData != null && envData.getClass() == JSONObject.class) {
                resultData = ((JSONObject) resultData).clone(); // do not modify actual common caps
                ((JSONObject) resultData).putAll((JSONObject) envData);
            }
            capabilities.setCapability(pair.getKey().toString(), resultData);
        }

        String username = System.getenv("BROWSERSTACK_USERNAME");
        if (username == null) {
            username = (String) config.get("user");
        }

        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = (String) config.get("key");
        }

        this.checkAndStartBrowserStackLocal(capabilities, accessKey);

        driver = new RemoteWebDriver(
                new URL("http://" + username + ":" + accessKey + "@" + config.get("server") + "/wd/hub"), capabilities);
    }

    public void checkAndStartBrowserStackLocal(DesiredCapabilities capabilities, String accessKey) {
        if (bsLocal != null) {
            return;
        }
        if (capabilities.getCapability("bstack:options") != null
                && ((JSONObject) capabilities.getCapability("bstack:options")).get("local") != null
                && ((Boolean) ((JSONObject) capabilities.getCapability("bstack:options")).get("local")) == true) {
            bsLocal = new Local();
            Map<String, String> options = new HashMap<String, String>();
            options.put("key", accessKey);
            try {
                bsLocal.start(options);
            } catch (Exception e) {
                System.out.println("Error: could not start browserstack local");
                e.printStackTrace();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        if (bsLocal != null) {
            bsLocal.stop();
        }
    }
}

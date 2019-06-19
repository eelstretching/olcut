package com.oracle.labs.mlrg.olcut.config.edn;

import com.oracle.labs.mlrg.olcut.config.ConfigurationManager;
import com.oracle.labs.mlrg.olcut.config.MapConfigurable;
import com.oracle.labs.mlrg.olcut.config.property.PropertySheet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Tests the extraction of {@link Map} objects from a {@link PropertySheet}.
 */

public class MapTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        ConfigurationManager.addFileFormatFactory(new EdnConfigFactory());
    }

    public MapTest() { }

    @Test
    public void mapTest() throws IOException {
        ConfigurationManager cm = new ConfigurationManager("mapConfig.edn");
        MapConfigurable m = (MapConfigurable) cm.lookup("mapTest");
        Map<String,String> map = m.map;
        Assert.assertEquals("stuff",map.get("things"));
        Assert.assertEquals("quux",map.get("foo"));
        Assert.assertNull(map.get("bar"));
    }
}

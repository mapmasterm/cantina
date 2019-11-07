package com.cantina.map;

import com.cantina.map.simple.JSimpleParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void simpleInputTest() throws IOException, ParseException {
        Path path = Paths.get("./data/cantina.json");
        String data = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

        JParser parser = new JSimpleParser(data, new SearchCache(), "class", "classNames", "identifier");
        Collection<JView> views = parser.find("Input");
        assertEquals("Should be 26 'class'='Input'",26,views.size());
}

}
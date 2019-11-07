package com.cantina.map;

import com.cantina.map.simple.JSimpleParser;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

    public static void main(String[] args) throws Exception {
        String data;
        Path path;
        System.out.println("Supply a Local-File or a URL as a command line argument!");
        System.out.println("On 'prompt# ' Give the 'cantina.json' file the following example inputs are: ");
        System.out.println("'Input' or 'subviews Input' // Are equivalent");
        System.out.println("'StackView.accessoryView'");
        System.out.println("'StackView.column'");
        System.out.println("'.column'");
        System.out.println("'CvarSelect'");
        System.out.println("'CvarSelect#rate' or '#rate' // Are equal");

        if (args[0].trim().startsWith("http")) {
            // Get File from URL and save to local
            URL website = new URL(args[0]);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File file = new File("./cantina.json");
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.flush();
            fos.close();
            path = Paths.get(file.getAbsolutePath());
        } else {
            // Read From local
            path = Paths.get(args[0].trim());
        }
        data = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

        //Select Parser
        JParser parser = new JSimpleParser(data, new SearchCache(), "class", "classNames", "identifier");

        //Console Input
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("prompt# ");
                String input = scanner.nextLine();
                try {
                    Collection<JView> views = parser.find(input);
                    AtomicInteger cnt = new AtomicInteger();
                    if (views!=null) views.forEach(v -> System.out.println((cnt.incrementAndGet())+")"+v.toString()));
                } catch (Throwable t) {
                    System.out.println(t.getClass().getSimpleName()+" "+t.getMessage());
                }
            }
        } catch(Throwable t) {
            System.out.println("Exit exception");
        }
    }
}

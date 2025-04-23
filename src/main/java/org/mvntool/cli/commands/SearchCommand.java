package org.mvntool.cli.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Callable;
import java.util.Map;
import java.io.InputStream;
import java.net.URL;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

@Command(
        name = "search",
        description = "Search for Maven dependencies"
)
public class SearchCommand implements Callable<Integer> {

    @Parameters(
            paramLabel = "<query>",
            description = "Search term (e.g., 'opencsv', 'guava')",
            defaultValue = ""
    )
    private String query;

    @Override
    public Integer call() throws Exception {
        // URL to the dependencies JSON
        URL url = new URL("https://raw.githubusercontent.com/JASIM0021/mvntool/refs/heads/main/dependencies.json");

        // Open an InputStream from the URL
        try (InputStream inputStream = url.openStream()) {
            ObjectMapper mapper = new ObjectMapper();

            // Parse the JSON into a Map
            Map<String, Object> topLevel = mapper.readValue(inputStream, Map.class);

            // Get the dependencies map from the top-level map
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> db = (Map<String, Map<String, Object>>) topLevel.get("dependencies");

            if (db == null) {
                System.err.println("❌ Error: No dependencies found in the database");
                return 1;
            }

            // Use AtomicInteger to count matches
            AtomicInteger matchCount = new AtomicInteger(0);

            // Filter and display results
            db.entrySet().stream()
                    .filter(entry -> query.isEmpty() ||
                            entry.getKey().toLowerCase().contains(query.toLowerCase()) ||
                            entry.getValue().get("groupId").toString().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        matchCount.incrementAndGet();
                        Map<String, Object> dep = entry.getValue();
                        System.out.printf(
                                "%-20s %s:%s (latest: %s)%n",
                                entry.getKey(),
                                dep.get("groupId"),
                                dep.get("artifactId"),
                                dep.get("latest")
                        );

                        // Show available versions if query isn't empty
                        if (!query.isEmpty()) {
                            @SuppressWarnings("unchecked")
                            Map<String, String> versions = (Map<String, String>) dep.get("versions");
                            if (versions != null) {
                                String versionList = versions.entrySet().stream()
                                        .map(e -> e.getKey() + " → " + e.getValue())
                                        .collect(Collectors.joining(", "));
                                System.out.println("    Versions: " + versionList);
                            }
                        }
                    });

            // Show message if no matches found
            if (matchCount.get() == 0 && !query.isEmpty()) {
                System.out.println("No dependencies found matching: " + query);
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading the dependency database: " + e.getMessage());
            e.printStackTrace();
            return 1; // Return error code
        }

        return 0; // Success
    }
}
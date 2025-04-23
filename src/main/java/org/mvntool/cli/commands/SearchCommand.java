package org.mvntool.cli.commands;



import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.Callable;
import java.util.Map;
import java.io.File;
import java.util.stream.Collectors;

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
        // Load the dependency database
        File dbFile = new File(System.getProperty("user.home") + "/.mvntool/dependencies.json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Object>> db = mapper.readValue(dbFile, Map.class);

        // Filter and display results
        db.entrySet().stream()
                .filter(entry -> query.isEmpty() ||
                        entry.getKey().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getValue().get("groupId").toString().toLowerCase().contains(query.toLowerCase()))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
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
                        Map<String, String> versions = (Map<String, String>) dep.get("versions");
                        String versionList = versions.entrySet().stream()
                                .map(e -> e.getKey() + " → " + e.getValue())
                                .collect(Collectors.joining(", "));
                        System.out.println("    Versions: " + versionList);
                    }
                });

        return 0;
    }
}
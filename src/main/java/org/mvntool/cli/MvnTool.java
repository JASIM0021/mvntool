package org.mvntool.cli;


import org.mvntool.cli.commands.InstallCommand;
import org.mvntool.cli.commands.SearchCommand;
import org.mvntool.cli.commands.UpdateCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "mvntool",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "A CLI tool for managing Maven dependencies",
        subcommands = {
                InstallCommand.class,
                SearchCommand.class,
                UpdateCommand.class
        }
)
public class MvnTool implements Runnable {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new MvnTool()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
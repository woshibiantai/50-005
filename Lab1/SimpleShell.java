import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleShell {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws java.io.IOException {
        List<String> history = new ArrayList<>();
        File currDir = new File(System.getProperty("user.dir"));
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // read what the user entered
            System.out.print("jsh>");
            commandLine = console.readLine();
            history.add(commandLine);

            // TODO: adding a history feature

            // if the user entered a return, just loop again
            if (commandLine.equals("")) {
                continue;
            }

            // put the commands into the process builder:
            String[] myCommand = commandLine.split(" ");
            Arrays.asList(myCommand);

            // working with the "cd" commands
            if (myCommand[0].equals("cd")) {
                File newDir;
                if (myCommand.length == 1) {
                    newDir = new File(System.getProperty("user.home"));
                } else if (myCommand[1].equals("..")) {
                    newDir = new File(currDir.getParent());
                } else {
                    newDir = new File(currDir + "/" + myCommand[1]);
                }
                // now check if this directory is valid
                if (newDir.isDirectory()) {
                    currDir = newDir;
                } else if (newDir.isFile()) {
                    System.out.println(newDir + " is a file and not a directory. Please enter directory");
                } else {
                    System.out.println(newDir + " is an invalid directory");
                }
                continue;
            }

            String historyCommand;
            if (myCommand[0].equals("!!")) {
                if ((history.size() <= 1)) {
                    System.out.println("Invalid command history search!");
                    continue;
                } else {
                    historyCommand = history.get(history.size() - 2);
                    history.set(history.size()-1,historyCommand);
                    myCommand = historyCommand.split(" ");
                }
            } else if (isInteger(myCommand[0])) {
                if ((history.size()-1 < Integer.parseInt(myCommand[0]))) {
                    System.out.println("Invalid command history search!");
                    continue;
                } else {
                    historyCommand = history.get(history.size() - Integer.parseInt(myCommand[0]) - 1);
                    history.set(history.size()-1,historyCommand);
                    myCommand = historyCommand.split(" ");
                }
            } else if (myCommand[0].equals("history")) {
                history.remove(history.size()-1);
                if (history.size() < 1) {
                    System.out.println("Invalid command history search!");
                } else {
                    int i = 0;
                    while (i < history.size()) {
//                        System.out.println(i + " " + history.get(history.size()-i-1));
                        System.out.println(i + " " + history.get(i));
                        i++;
                    }
                }
                continue;
            }


            // Invoking another shell to run your process:
            try {
                ProcessBuilder pb = new ProcessBuilder();
                pb.command(myCommand);
                pb.directory(currDir);
                Process p = pb.start();
                // Put your process into the shell created
                BufferedReader altConsole = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
                String altCommand;
                while ((altCommand = altConsole.readLine()) != null) {
                    System.out.println(altCommand);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            // TODO: creating the external process and executing the command in that process
            // TODO: modifying the shell to allow changing directories
        }
    }
}
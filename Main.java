import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Main{
    public static void main(String args[]) throws FileNotFoundException {
        ArrayList<Media> list = new ArrayList<>();
        PrintWriter errors = new PrintWriter("error.txt");
        PrintWriter report = new PrintWriter("report.txt");

        System.out.println("Welcome to Media Library");

        Functions.readDataFile("mediaList.txt", list, errors);

        Functions.readCommandFile("mediaCommands.txt", list, report, errors);

        report.close();
        errors.close();

        


    }
}
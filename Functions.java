import java.io.*;
import java.util.*;

public class Functions { //must wrap class



public static String validateMedia(String [] parts, int start) { 
    if (parts.length < start + 7) { 

        return "not enough fields";
    }
    String type = parts[start].trim();
    if (!type.equals("M") && !type.equals("B") && !type.equals("S")) { 
        return "invalid type";
    }

    int rating;
    try { 
        rating = Integer.parseInt(parts[start+3].trim());
    } catch (NumberFormatException e) { 
        return  "rating is not a number";
    }
    if (rating <= 0 || rating > 10) { 
        return "rating out of range";
    }

    int length;
    
    try { 
        length = Integer.parseInt(parts[start + 5].trim());
    } catch (NumberFormatException e) { 
        return "length is not a number";
    }
    if (length <= 0) { 
        return "length out of range";
    }

    int year;
    try { 
        year = Integer.parseInt(parts[start+6].trim());
    } catch (NumberFormatException e) { 
        return "year is not a number";
    }
    if (year < 1920 || year > 2024) { 
        return "year out of range"; 
    }
    return null;
}
 // PRE:  parts already passed validateMedia with the same start value
    // POST: returns a new Media object built from those fields
    public static Media makeMedia(String[] parts, int start) {
        return new Media(parts[start].trim().charAt(0),
                parts[start + 1].trim(),
                parts[start + 2].trim(),
                Integer.parseInt(parts[start + 3].trim()),
                parts[start + 4].trim(),
                Integer.parseInt(parts[start + 5].trim()),
                Integer.parseInt(parts[start + 6].trim()));
    }

    // PRE:  fileName exists; list is empty; errors is open for writing
    // POST: valid lines are added to list, bad lines are written to errors
    public static void readDataFile(String fileName, ArrayList<Media> list,
                                    PrintWriter errors) throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            if (line.trim().equals("Q")) {
                break;
            }

            String[] parts = line.split(",");
            String problem = validateMedia(parts, 0);

            if (problem != null) {
                errors.println(line + "  --> " + problem);
            } else {
                list.add(makeMedia(parts, 0));
            }
        }
        in.close();
    }

    // PRE:  type is 'A' (all), 'M', 'B' or 'S'; minRating is 0 for "no rating
    //       filter"; genre is null for "no genre filter"; report is open
    // POST: matching items are written to report, or a message if none match
    public static void printList(ArrayList<Media> list, char type, int minRating,
                                 String genre, PrintWriter report, String label) {
        report.println("=== " + label + " ===");
        report.printf("%-37s%10s%10s     %-23s%7s\n",
                "Title", "Year", "Rating", "Genre", "Type");

        int found = 0;
        for (Media m : list) {
            boolean typeOk = (type == 'A') || (m.getType() == type);
            boolean ratingOk = m.getRating() >= minRating;
            boolean genreOk = (genre == null) || m.getGenre().equalsIgnoreCase(genre);

            if (typeOk && ratingOk && genreOk) {
                report.print(m.printMedia());
                found++;
            }
        }

        if (found == 0) {
            report.println("No items found.");
        }
        report.println();
    }

    // PRE:  list holds the current library; report is open
    // POST: counts of movies, books and songs are written to report
    public static void printTotals(ArrayList<Media> list, PrintWriter report) {
        int movies = 0, books = 0, songs = 0;
        for (Media m : list) {
            if (m.getType() == 'M') movies++;
            else if (m.getType() == 'B') books++;
            else if (m.getType() == 'S') songs++;
        }
        report.println("=== T ===");
        report.println("Movies: " + movies);
        report.println("Books: " + books);
        report.println("Songs: " + songs);
        report.println();
    }

    // PRE:  parts is an N command split on commas (parts[0] is "N")
    // POST: a valid record is added to list; an invalid one goes to errors
    public static void newContent(String[] parts, String line,
                                  ArrayList<Media> list, PrintWriter errors) {
        String problem = validateMedia(parts, 1);
        if (problem != null) {
            errors.println(line + "  --> " + problem);
        } else {
            list.add(makeMedia(parts, 1));
        }
    }

    // PRE:  A/M/B/S command with one argument; line is the original text
    // POST: argument is a rating (1-10) or a genre; output goes to report/errors
    public static void filterCommand(String[] parts, String line, ArrayList<Media> list,
                                     PrintWriter report, PrintWriter errors) {
        char type = parts[0].trim().charAt(0);
        String arg = parts[1].trim();

        int min = 0;
        boolean isNumber = true;
        try {
            min = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            isNumber = false;
        }

        if (!isNumber) {
            printList(list, type, 0, arg, report, line);        // Part 3: genre
        } else if (min < 1 || min > 10) {
            errors.println(line + "  --> rating filter must be 1-10");
        } else {
            printList(list, type, min, null, report, line);     // Part 2: rating
        }
    }

    // PRE:  fileName exists; list is filled; report and errors are open
    // POST: every command is carried out, or its line is written to errors
    public static void readCommandFile(String fileName, ArrayList<Media> list,
                                       PrintWriter report, PrintWriter errors)
                                       throws FileNotFoundException {
        Scanner in = new Scanner(new File(fileName));

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");
            String cmd = parts[0].trim();

            if (cmd.equals("Q")) {
                break;
            }

            boolean isPrint = cmd.equals("A") || cmd.equals("M")
                           || cmd.equals("B") || cmd.equals("S");

            if (isPrint && parts.length == 1) {
                printList(list, cmd.charAt(0), 0, null, report, line);
            } else if (isPrint && parts.length == 2) {
                filterCommand(parts, line, list, report, errors);
            } else if (cmd.equals("T") && parts.length == 1) {
                printTotals(list, report);
            } else if (cmd.equals("N")) {
                newContent(parts, line, list, errors);
            } else {
                errors.println(line + "  --> invalid command");
            }
        }
        in.close();
    }
}


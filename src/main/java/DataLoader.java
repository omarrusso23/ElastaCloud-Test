import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataLoader {
    //This is the path of the CSV file in this case is in the same path as the project
    private static final String DATA_FILE = "data.csv";

    //A Map with the info of the columns
    private static final Map<String, Integer> COLUMN_INDEX_BY_NAME = Map.of(
            "Ticket number", 0,
            "Issue Date", 1,
            "Issue time", 2,
            "Plate Expiry Date", 6,
            "Make", 8,
            "Agency", 13,
            "Fine Amount", 16,
            "Latitude", 17,
            "Longitude", 18
    );

    //A map to store the agency names corresponding to their IDs
    private static final Map<String, String> AGENCY_NAMES_BY_ID = Map.of(
            "1", "Agency 1",
            "2", "Agency 2",
            "3", "Agency 3",
            "4", "Agency 4",
            "5", "Agency 5",
            "13", "Agency 13",
            "51", "Agency 51",
            "53", "Agency 53",
            "54", "Agency 54",
            "36", "Agency 36"

            );

    public static void main(String[] args) throws ParseException {
        //Line that will help us go through the CSV file
        String line;

        //The Delimiter is what will tell us what separates two columns
        final String CSV_DELIMITER = ",";

        //These maps are used to calculate all the fines
        Map<String, Integer> finesByMakeAndYear = new HashMap<>();
        Map<String, Integer> finesByAgencyAndYear = new HashMap<>();
        Map<String, List<Integer>> fineAmountsByAgencyAndYear = new HashMap<>();

        // Try-with-resources block to open and read the file, and handle any exceptions that may arise
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            //Here we skip the header
            String[] header = br.readLine().split(CSV_DELIMITER);
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < header.length; i++) {
                columnIndexMap.put(header[i], i);
            }

            //With this while we are going to read the CSV file
            while ((line = br.readLine()) != null) {

                //Here we get the necessary Strings
                String[] data = line.split(CSV_DELIMITER);
                String ticketNumber = data[COLUMN_INDEX_BY_NAME.get("Ticket number")];
                String issueDateStr = data[COLUMN_INDEX_BY_NAME.get("Issue Date")];
                String issueTimeStr = data[COLUMN_INDEX_BY_NAME.get("Issue time")];
                String plateExpiryDateStr = data[COLUMN_INDEX_BY_NAME.get("Plate Expiry Date")];
                String latitudeStr = data[COLUMN_INDEX_BY_NAME.get("Latitude")];
                String longitudeStr = data[COLUMN_INDEX_BY_NAME.get("Longitude")];
                String agency = data[COLUMN_INDEX_BY_NAME.get("Agency")];
                String make = data[COLUMN_INDEX_BY_NAME.get("Make")];

                //Get the agency name from the ID
                String agencyName = AGENCY_NAMES_BY_ID.get(agency);

                //These use the helper methods as they must do some extra steps
                String issueDateTime = processIssueDateTime(issueDateStr, issueTimeStr);
                String plateExpiryDate = processPlateExpiryDate(plateExpiryDateStr);
                String latitude = String.valueOf(processLatitude(latitudeStr));
                String longitude = String.valueOf(processLongitude(longitudeStr));

                //Here we print the data
                System.out.println("Ticket Number: " + ticketNumber);
                System.out.println("Issue Date and Time: " + issueDateTime);
                System.out.println("Plate Expiry Date: " + plateExpiryDate);
                System.out.println("Latitude: " + latitude);
                System.out.println("Longitude: " + longitude);
                System.out.println("Agency ID: " + agency);
                System.out.println("Agency Name: " + agencyName);
                System.out.println("-------------------------------------");

                //This String is special as it will be used to calculate the necessary staff for the second part that is
                // why we must check if is not empty as you can't calculate with an empty int
                String amount = data[COLUMN_INDEX_BY_NAME.get("Fine Amount")];
                int fineAmount = 0;
                if (!amount.equals("")) {
                    fineAmount = Integer.parseInt(amount);
                }

                //Here we get the year and the agency and create a key to move through the Maps
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date issueDate = sdf.parse(issueDateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(issueDate);
                int year = cal.get(Calendar.YEAR);
                String key = agency + "_" + year;

                if (finesByMakeAndYear.containsKey(key)) {
                    int totalFines = finesByMakeAndYear.get(key);
                    finesByMakeAndYear.put(key, totalFines + 1);
                } else {
                    finesByMakeAndYear.put(key, 1);
                }

                List<Integer> fineAmounts = fineAmountsByAgencyAndYear.containsKey(key) ? fineAmountsByAgencyAndYear.get(key) : new ArrayList<>();
                fineAmounts.add(fineAmount);
                fineAmountsByAgencyAndYear.put(key, fineAmounts);

                // Calculate fines by agency and year
                if (finesByAgencyAndYear.containsKey(key)) {
                    int totalFines2 = finesByAgencyAndYear.get(key);
                    finesByAgencyAndYear.put(key, totalFines2 + 1);
                } else {
                    finesByAgencyAndYear.put(key, 1);
                }


            }

            // Print fines by make and year
            System.out.println("Total fines by make and year:");
            System.out.println("-------------------------------------");
            for (Map.Entry<String, Integer> entry : finesByMakeAndYear.entrySet()) {
                String[] parts = entry.getKey().split("_");
                String make = parts[0];
                String year = parts[1];

                int totalFines = entry.getValue();
                System.out.println("Make: " + make + " | Year: " + year + " | Total Fines: " + totalFines);
            }
            System.out.println("-------------------------------------");

            // Print average and standard deviation of fine amounts per year per agency
            System.out.println("Average and Standard Deviation of Fine Amount per Year per Agency:");
            System.out.println("-------------------------------------");
            for (Map.Entry<String, List<Integer>> entry : fineAmountsByAgencyAndYear.entrySet()) {
                String[] parts = entry.getKey().split("_");
                String agency = parts[0];
                String year = parts[1];

                List<Integer> fineAmounts = entry.getValue();
                int totalFines = finesByAgencyAndYear.get(entry.getKey());
                double average = (double) fineAmounts.stream().mapToInt(Integer::intValue).sum() / totalFines;

                double variance = fineAmounts.stream().mapToDouble(x -> Math.pow(x - average, 2)).sum() / totalFines;
                double stdDev = Math.sqrt(variance);

                System.out.printf("Agency: %s | Year: %s | Average Fine Amount: %.2f | Standard Deviation: %.2f\n", agency, year, average, stdDev);
            }
            System.out.println("-------------------------------------");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static String processIssueDateTime(String issueDateStr, String issueTimeStr) {
        if (!issueTimeStr.matches("\\d+(\\.\\d+)?")) {
            return null;
        }

        int issueTime = Integer.parseInt(issueTimeStr);

        // Convert issue time to hours (assuming it's given in 24-hour format)
        int hours = issueTime / 100;
        int minutes = issueTime % 100;

        // Parse the Issue Date value using a SimpleDateFormat object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date issueDate;

        try {
            issueDate = sdf.parse(issueDateStr);
        } catch (ParseException e) {
            System.err.println("Invalid Issue Date value: " + issueDateStr);
            return null;
        }

        // Combine the Issue Date and Issue Time values into a single date object
        Calendar cal = Calendar.getInstance();
        cal.setTime(issueDate);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date issueDateTime = cal.getTime();

        // Format the Issue Date Time value using a SimpleDateFormat object
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return sdf2.format(issueDateTime);
    }

    private static String processPlateExpiryDate(String plateExpiryStr) {
        // Parse the Plate Expiry value using a SimpleDateFormat object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        try {
            Date plateExpiryDate = sdf.parse(plateExpiryStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(plateExpiryDate);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            sdf.applyPattern("yyyy-MM-dd");
            return sdf.format(cal.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    //For latitude and longitude is being use the standard latitude-longitude coordinate system (WGS 84) used by most
    // mapping tools and APIs witch means that the latitude must be between 90 and -90 and the longitude must be between
    // 180 and -180 you will see that in the last line it works

    private static Double processLatitude(String latitudeStr) {
        if (!latitudeStr.matches("[-+]?[0-9]*\\.?[0-9]+")) {
            return null;
        }

        Double latitude = Double.parseDouble(latitudeStr);

        if (latitude < -90.0 || latitude > 90.0) {
            return null;
        }

        return latitude;
    }

    private static Double processLongitude(String longitudeStr) {
        if (!longitudeStr.matches("-?\\d+(\\.\\d+)?")) {
            return null;
        }
        Double longitude = Double.parseDouble(longitudeStr);
        if (longitude < -180 || longitude > 180) {
            return null;
        }
        return longitude;
    }
}

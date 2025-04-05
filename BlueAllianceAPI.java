import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class BlueAllianceAPI {

    private static final String BASE_URL = "https://www.thebluealliance.com/api/v3/";
    private static final String EVENT_KEY = "2025chcmp"; // Change to your event key
    private static final String AUTH_KEY = "tD5zTqislusLlVgErcawIdnqG50ziLq3Kqqnl27bw2LxLKenNGO3fXrxFaoIaFmr"; // Replace with your TBA key

    public static void saveAllMatchTeamsToFile() {
        try {
            // Get all matches for the event
            URL url = new URL(BASE_URL + "event/" + EVENT_KEY + "/matches");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-TBA-Auth-Key", AUTH_KEY);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Parse the response
            JSONArray matches = new JSONArray(response.toString());

            // Create a file to save all team data
            PrintWriter out = new PrintWriter(new FileWriter("all_match_teams.txt"));

            // Iterate over all matches and write teams to the file
            for (int i = 0; i < matches.length(); i++) {
                JSONObject matchData = matches.getJSONObject(i);
                JSONObject alliances = matchData.getJSONObject("alliances");

                // Get red and blue teams
                JSONArray redTeams = alliances.getJSONObject("red").getJSONArray("team_keys");
                JSONArray blueTeams = alliances.getJSONObject("blue").getJSONArray("team_keys");

                // Create a comma-separated list of teams for this match
                StringBuilder teamList = new StringBuilder();

                for (int j = 0; j < redTeams.length(); j++) {
                    // Remove "frc" from the team key
                    String team = redTeams.getString(j).replace("frc", "");
                    teamList.append(team).append(",");
                }
                for (int j = 0; j < blueTeams.length(); j++) {
                    // Remove "frc" from the team key
                    String team = blueTeams.getString(j).replace("frc", "");
                    teamList.append(team);
                    if (j < blueTeams.length() - 1) {
                        teamList.append(",");
                    }
                }

                // Write the list to the file
                out.println(teamList.toString());
            }

            out.close();
            System.out.println("All match teams saved to: all_match_teams.txt");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error fetching or saving match data.");
        }
    }
}

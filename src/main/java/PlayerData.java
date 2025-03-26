import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Player {
    String name;
    int number;
    String team;
    int points;
    int rebounds;
    int assists;

    public Player(String name, int number, String team, int points, int rebounds, int assists) {
        this.name = name;
        this.number = number;
        this.team = team;
        this.points = points;
        this.rebounds = rebounds;
        this.assists = assists;
    }

    @Override
    public String toString() {
        return name + " (#" + number + ", " + team + ") - Pts: " + points + ", Reb: " + rebounds + ", Ast: " + assists;
    }
}

public class PlayerData {
    private List<Player> players;

    public PlayerData(String filename) {
        players = new ArrayList<>();
        loadPlayersFromFile(filename);
    }

    private void loadPlayersFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    String name = data[0].trim();
                    int number = Integer.parseInt(data[1].trim());
                    String team = data[2].trim();
                    int points = Integer.parseInt(data[3].trim());
                    int rebounds = Integer.parseInt(data[4].trim());
                    int assists = Integer.parseInt(data[5].trim());
                    players.add(new Player(name, number, team, points, rebounds, assists));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading players from file: " + e.getMessage());
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> searchPlayers(String query) {
        query = query.toLowerCase();
        List<Player> results = new ArrayList<>();
        for (Player p : players) {
            if (p.name.toLowerCase().contains(query) || p.team.toLowerCase().contains(query) || String.valueOf(p.number).equals(query)) {
                results.add(p);
            }
        }
        return results;
    }

    public List<String> getSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        query = query.toLowerCase();
        for (Player p : players) {
            if (p.name.toLowerCase().startsWith(query)) {
                suggestions.add(p.name);
            }
        }
        return suggestions;
    }

    public int comparePoints(String player1, String player2) {
        Player p1 = findPlayerByName(player1);
        Player p2 = findPlayerByName(player2);

        if (p1 == null || p2 == null) {
            System.err.println("❌ ERROR: One or both players not found.");
            return Integer.MIN_VALUE; // Error code for invalid comparison
        }

        return Integer.compare(p1.points, p2.points);
    }

    /**
     * Finds a player by name.
     */
    public Player findPlayerByName(String name) {
        for (Player p : players) {
            if (p.name.equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public List<Player> getTopPlayers(String stat, int count) {
        List<Player> sorted = new ArrayList<>(players);
        sorted.sort((a, b) -> {
            int valA = switch (stat.toLowerCase()) {
                case "points" -> a.points;
                case "rebounds" -> a.rebounds;
                case "assists" -> a.assists;
                default -> 0;
            };
            int valB = switch (stat.toLowerCase()) {
                case "points" -> b.points;
                case "rebounds" -> b.rebounds;
                case "assists" -> b.assists;
                default -> 0;
            };
            return Integer.compare(valB, valA); // Descending
        });
        return sorted.subList(0, Math.min(count, sorted.size()));
    }
}

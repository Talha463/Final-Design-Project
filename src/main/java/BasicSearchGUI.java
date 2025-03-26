import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BasicSearchGUI extends JFrame {
    private JTextField searchField;
    private JTextArea resultArea;
    private PlayerData playerData;
    private DefaultListModel<String> suggestionModel;
    private JComponent currentCenterComponent;

    public BasicSearchGUI() {
        // Apply FlatLaf modern look
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf");
        }

        setTitle("NBA Stats Search");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        playerData = new PlayerData("src/Resources/players.txt");

        // ---------------- TOP: Search Panel ----------------
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(50, 50, 50));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(new Color(200, 200, 200));
        JButton searchButton = new JButton("Search");
        styleButton(searchButton);

        inputPanel.add(new JLabel("Search Player, Number, Team, or Stats:"), BorderLayout.WEST);
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        // ---------------- LEFT: Tabbed Pane ----------------
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setPreferredSize(new Dimension(200, 0));
        tabbedPane.addTab("Sort by Team", createSortByTeamPanel());
        tabbedPane.addTab("Stat Leaders", createStatLeadersPanel());
        tabbedPane.addTab("Compare Stats", createCompareStatsPanel());
        tabbedPane.addTab("Search by Stat", createSearchByStatPanel());
        add(tabbedPane, BorderLayout.WEST);

        // ---------------- CENTER: Search Results Area ----------------
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBackground(new Color(240, 240, 240));
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setCenterComponent(new JScrollPane(resultArea));

        // ---------------- RIGHT: Top Players Panel ----------------
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setBackground(new Color(50, 50, 50));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel topLabel = new JLabel("Top Players");
        topLabel.setForeground(Color.WHITE);
        topLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(topLabel, BorderLayout.NORTH);

        JPanel topPlayersPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        topPlayersPanel.setBackground(new Color(50, 50, 50));
        addTopPlayers(topPlayersPanel);
        rightPanel.add(topPlayersPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // ---------------- Event Handling ----------------
        suggestionModel = new DefaultListModel<>();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        searchButton.addActionListener(e -> searchPlayers());
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 60));
            }
        });
    }

    private String formatAsLeaderboard(List<Player> players, String statFocus) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-25s %-15s %-8s %-8s %-8s%n",
                "Rank", "Name", "Team", "Points", "Reb", "Ast"));
        sb.append("---------------------------------------------------------------\n");

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            sb.append(String.format("%-5d %-25s %-15s %-8d %-8d %-8d%n",
                    i + 1, p.name, p.team, p.points, p.rebounds, p.assists));
        }

        return sb.toString();
    }

    private void updateSuggestions() {
        String text = searchField.getText().toLowerCase();
        suggestionModel.clear();
        if (!text.isEmpty()) {
            for (String suggestion : playerData.getSuggestions(text)) {
                suggestionModel.addElement(suggestion);
            }
        }
    }

    private void searchPlayers() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            updateCenterWithText("Please enter a search term.");
            return;
        }

        List<Player> results = playerData.searchPlayers(query);
        if (results.isEmpty()) {
            updateCenterWithText("No players found.");
        } else {
            updateCenterWithText(formatAsLeaderboard(results, "All"));
        }
    }

    private void updateCenterWithText(String text) {
        resultArea.setText(text);
        setCenterComponent(new JScrollPane(resultArea));
    }

    private void setCenterComponent(JComponent newComponent) {
        if (currentCenterComponent != null) {
            getContentPane().remove(currentCenterComponent);
        }
        currentCenterComponent = newComponent;
        getContentPane().add(currentCenterComponent, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void addTopPlayers(JPanel panel) {
        String[] topPlayers = {
                "LeBron James", "Stephen Curry", "Giannis Antetokounmpo",
                "Nikola Jokic", "Kevin Durant", "Luka Doncic"
        };
        for (String playerName : topPlayers) {
            JButton playerButton = new JButton(playerName);
            playerButton.setFont(new Font("Arial", Font.BOLD, 12));
            playerButton.setBackground(new Color(70, 130, 180));
            playerButton.setForeground(Color.WHITE);
            playerButton.setFocusPainted(false);
            playerButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            playerButton.addActionListener(e -> displayPlayerProfile(playerName));
            panel.add(playerButton);
        }
    }

    private void displayPlayerProfile(String playerName) {
        Player player = playerData.searchPlayers(playerName).stream()
                .filter(p -> p.name.equalsIgnoreCase(playerName))
                .findFirst()
                .orElse(null);

        if (player == null) {
            updateCenterWithText("❌ Player not found.");
            return;
        }

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout(10, 10));
        profilePanel.setBackground(new Color(245, 245, 245));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Player Image (center)
        JLabel imageLabel = new JLabel(loadImage("src/Resources/Images/Players/" + player.name + ".png", 300, 300));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        profilePanel.add(imageLabel, BorderLayout.CENTER);

        // Stats panel (bottom)
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        statsPanel.setBackground(new Color(245, 245, 245));

        Font statFont = new Font("Arial", Font.BOLD, 18);

        JLabel pointsLabel = new JLabel("Points: " + player.points, JLabel.CENTER);
        JLabel reboundsLabel = new JLabel("Rebounds: " + player.rebounds, JLabel.CENTER);
        JLabel assistsLabel = new JLabel("Assists: " + player.assists, JLabel.CENTER);

        pointsLabel.setFont(statFont);
        reboundsLabel.setFont(statFont);
        assistsLabel.setFont(statFont);

        statsPanel.add(pointsLabel);
        statsPanel.add(reboundsLabel);
        statsPanel.add(assistsLabel);

        profilePanel.add(statsPanel, BorderLayout.SOUTH);

        setCenterComponent(profilePanel);
    }

    private JPanel createSortByTeamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> teamModel = new DefaultListModel<>();
        JList<String> teamList = new JList<>(teamModel);
        teamList.setFont(new Font("Arial", Font.PLAIN, 14));
        teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        playerData.getPlayers().stream()
                .map(p -> p.team)
                .distinct()
                .sorted()
                .forEach(teamModel::addElement);

        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTeam = teamList.getSelectedValue();
                if (selectedTeam != null) {
                    StringBuilder sb = new StringBuilder("Players for " + selectedTeam + ":\n");
                    for (Player p : playerData.getPlayers()) {
                        if (p.team.equalsIgnoreCase(selectedTeam)) {
                            sb.append(p.toString()).append("\n");
                        }
                    }
                    updateCenterWithText(sb.toString());
                }
            }
        });

        panel.add(new JScrollPane(teamList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatLeadersPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] stats = {"Points", "Rebounds", "Assists"};

        for (String stat : stats) {
            JButton btn = new JButton("Top 10 " + stat + " Leaders");
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> displayStatLeadersWithImages(stat));

            panel.add(btn);
        }

        return panel;
    }

    private void displayStatLeadersWithImages(String stat) {
        List<Player> sorted = playerData.getPlayers();
        sorted.sort((a, b) -> {
            int statA = switch (stat) {
                case "Points" -> a.points;
                case "Rebounds" -> a.rebounds;
                case "Assists" -> a.assists;
                default -> 0;
            };
            int statB = switch (stat) {
                case "Points" -> b.points;
                case "Rebounds" -> b.rebounds;
                case "Assists" -> b.assists;
                default -> 0;
            };
            return Integer.compare(statB, statA);
        });

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            Player p = sorted.get(i);
            JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT));
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(new Color(245, 245, 245));

            JLabel playerImg = new JLabel(loadImage("src/Resources/Images/Players/" + p.name + ".png", 60, 60));
            JLabel teamImg = new JLabel(loadImage("src/Resources/Images/Teams/" + p.team + ".png", 40, 40));

            String statValue = switch (stat) {
                case "Points" -> p.points + " PPG";
                case "Rebounds" -> p.rebounds + " RPG";
                case "Assists" -> p.assists + " APG";
                default -> "";
            };

            JLabel label = new JLabel((i + 1) + ". " + p.name + " - " + statValue);
            label.setFont(new Font("Arial", Font.PLAIN, 14));

            card.add(playerImg);
            card.add(Box.createHorizontalStrut(10));
            card.add(teamImg);
            card.add(Box.createHorizontalStrut(10));
            card.add(label);
            displayPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(displayPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        setCenterComponent(scrollPane);
    }

    private ImageIcon loadImage(String path, int width, int height) {
        try {
            Image img = new ImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    private JPanel createCompareStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> player1Box = new JComboBox<>();
        JComboBox<String> player2Box = new JComboBox<>();
        for (Player p : playerData.getPlayers()) {
            player1Box.addItem(p.name);
            player2Box.addItem(p.name);
        }

        JButton compareBtn = new JButton("Compare");
        compareBtn.setBackground(new Color(70, 130, 180));
        compareBtn.setForeground(Color.WHITE);
        compareBtn.setFont(new Font("Arial", Font.BOLD, 13));

        compareBtn.addActionListener(e -> {
            String p1 = (String) player1Box.getSelectedItem();
            String p2 = (String) player2Box.getSelectedItem();
            int comparison = playerData.comparePoints(p1, p2);

            StringBuilder sb = new StringBuilder("Comparison Result:\n\n");
            Player playerA = playerData.searchPlayers(p1).get(0);
            Player playerB = playerData.searchPlayers(p2).get(0);

            sb.append(playerA).append("\n");
            sb.append(playerB).append("\n\n");

            if (comparison == Integer.MIN_VALUE) {
                sb.append("❌ Error: One or both players not found.");
            } else if (comparison > 0) {
                sb.append(p1).append(" scores more points than ").append(p2);
            } else if (comparison < 0) {
                sb.append(p2).append(" scores more points than ").append(p1);
            } else {
                sb.append(p1).append(" and ").append(p2).append(" have the same points.");
            }

            updateCenterWithText(sb.toString());
        });

        panel.add(new JLabel("Player 1:"));
        panel.add(player1Box);
        panel.add(new JLabel("Player 2:"));
        panel.add(player2Box);
        panel.add(new JLabel());
        panel.add(compareBtn);

        return panel;
    }

    private JPanel createSearchByStatPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] statOptions = {"Points", "Rebounds", "Assists"};
        JComboBox<String> statBox = new JComboBox<>(statOptions);
        JTextField thresholdField = new JTextField();

        JButton filterBtn = new JButton("Filter");
        filterBtn.setBackground(new Color(70, 130, 180));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFont(new Font("Arial", Font.BOLD, 13));

        filterBtn.addActionListener(e -> {
            String stat = (String) statBox.getSelectedItem();
            String thresholdText = thresholdField.getText().trim();

            try {
                int threshold = Integer.parseInt(thresholdText);
                List<Player> filtered = new ArrayList<>();

                for (Player p : playerData.getPlayers()) {
                    int statValue = switch (stat) {
                        case "Points" -> p.points;
                        case "Rebounds" -> p.rebounds;
                        case "Assists" -> p.assists;
                        default -> 0;
                    };
                    if (statValue >= threshold) {
                        filtered.add(p);
                    }
                }

                if (filtered.isEmpty()) {
                    updateCenterWithText("No players found with " + stat + " ≥ " + threshold);
                } else {
                    updateCenterWithText(formatAsLeaderboard(filtered, stat));
                }

            } catch (NumberFormatException ex) {
                updateCenterWithText("❌ Please enter a valid number.");
            }
        });

        panel.add(new JLabel("Stat:"));
        panel.add(statBox);
        panel.add(new JLabel("Threshold:"));
        panel.add(thresholdField);
        panel.add(new JLabel());
        panel.add(filterBtn);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BasicSearchGUI gui = new BasicSearchGUI();
            gui.setVisible(true);
        });
    }
}
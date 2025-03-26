import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BasicSearchGUI extends JFrame {
    private JTextField searchField;
    private JTextArea resultArea;
    private PlayerData playerData;
    private DefaultListModel<String> suggestionModel;
    private JComponent currentCenterComponent;

    public BasicSearchGUI() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf");
        }

        setTitle("NBA Stats Search - ESPN Style");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        playerData = new PlayerData("src/Resources/players.txt");

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(45, 45, 45));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JButton searchButton = new JButton("Search");
        styleButton(searchButton);

        inputPanel.add(new JLabel("Search Player:"), BorderLayout.WEST);
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        // LEFT TABS
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setPreferredSize(new Dimension(200, 0));
        tabbedPane.addTab("Sort by Team", createSortByTeamPanel());
        tabbedPane.addTab("Stat Leaders", createStatLeadersPanel());
        tabbedPane.addTab("Compare Stats", createCompareStatsPanel());
        tabbedPane.addTab("Search by Stat", createSearchByStatPanel());
        add(tabbedPane, BorderLayout.WEST);

        // RIGHT PANEL
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setBackground(new Color(45, 45, 45));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel topLabel = new JLabel("Top Players");
        topLabel.setForeground(Color.WHITE);
        topLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(topLabel, BorderLayout.NORTH);

        JPanel topPlayersPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        topPlayersPanel.setBackground(new Color(45, 45, 45));
        addTopPlayers(topPlayersPanel);
        rightPanel.add(topPlayersPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // CENTER
        JLabel welcome = new JLabel("<html><center><h1 style='color:white;'>🏀 NBA Stats Dashboard</h1><p style='color:lightgray;'>Search players, compare stats, and view leaders.</p></center></html>", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        setCenterComponent(welcome);

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
        if (query.isEmpty()) return;

        List<Player> results = playerData.searchPlayers(query);
        if (results.isEmpty()) {
            setCenterComponent(new JLabel("No players found.", SwingConstants.CENTER));
        } else {
            JPanel resultPanel = new JPanel();
            resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
            resultPanel.setBackground(new Color(30, 30, 30));
            for (Player p : results) {
                resultPanel.add(createPlayerCard(p));
            }
            JScrollPane scroll = new JScrollPane(resultPanel);
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            setCenterComponent(scroll);
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
        String[] topPlayers = {"LeBron James", "Stephen Curry", "Giannis Antetokounmpo", "Nikola Jokic", "Kevin Durant", "Luka Doncic"};
        for (String name : topPlayers) {
            JButton btn = new JButton(name);
            styleButton(btn);
            btn.addActionListener(e -> displayPlayerProfile(name));
            panel.add(btn);
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

        JPanel profilePanel = new JPanel(new BorderLayout(10, 10));
        profilePanel.setBackground(new Color(245, 245, 245));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel pic = new JLabel(loadImage("src/Resources/Images/Players/" + player.name + ".png", 280, 280));
        JLabel logo = new JLabel(loadImage("src/Resources/Images/Teams/" + player.team + ".png", 80, 80));
        pic.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        top.add(pic, BorderLayout.CENTER);
        top.add(logo, BorderLayout.SOUTH);

        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setBackground(new Color(245, 245, 245));
        Font font = new Font("Segoe UI", Font.BOLD, 20);
        stats.add(new JLabel("Points: " + player.points, SwingConstants.CENTER)).setFont(font);
        stats.add(new JLabel("Rebounds: " + player.rebounds, SwingConstants.CENTER)).setFont(font);
        stats.add(new JLabel("Assists: " + player.assists, SwingConstants.CENTER)).setFont(font);

        profilePanel.add(top, BorderLayout.CENTER);
        profilePanel.add(stats, BorderLayout.SOUTH);
        setCenterComponent(profilePanel);
    }

    private void showPlayerCards(List<Player> players) {
        JPanel result = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
        result.setBackground(new Color(30, 30, 30));
        for (Player p : players) result.add(createPlayerCard(p));
        JScrollPane scroll = new JScrollPane(result);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        setCenterComponent(scroll);
    }

    private JPanel createPlayerCard(Player p) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(50, 50, 50));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel image = new JLabel(loadImage("src/Resources/Images/Players/" + p.name + ".png", 80, 80));
        JLabel logo = new JLabel(loadImage("src/Resources/Images/Teams/" + p.team + ".png", 40, 40));

        JLabel info = new JLabel("<html><b>" + p.name + "</b><br>Team: " + p.team + "<br>PPG: " + p.points + " RPG: " + p.rebounds + " APG: " + p.assists + "</html>");
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(image, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(logo, BorderLayout.EAST);

        return card;
    }

    private JPanel createSortByTeamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> teamList = new JList<>(model);
        playerData.getPlayers().stream().map(p -> p.team).distinct().sorted().forEach(model::addElement);

        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String team = teamList.getSelectedValue();
                List<Player> filtered = playerData.getPlayers().stream()
                        .filter(p -> p.team.equalsIgnoreCase(team))
                        .collect(Collectors.toList());
                showPlayerCards(filtered);
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
            JButton btn = new JButton("Top 10 " + stat);
            styleButton(btn);
            btn.addActionListener(e -> {
                List<Player> sorted = playerData.getTopPlayers(stat.toLowerCase(), 10);
                showPlayerCards(sorted);
            });
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
        JComboBox<String> player1Box = new JComboBox<>();
        JComboBox<String> player2Box = new JComboBox<>();
        for (Player p : playerData.getPlayers()) {
            player1Box.addItem(p.name);
            player2Box.addItem(p.name);
        }

        JButton compare = new JButton("Compare");
        styleButton(compare);
        compare.addActionListener(e -> {
            Player p1 = playerData.findPlayerByName((String) player1Box.getSelectedItem());
            Player p2 = playerData.findPlayerByName((String) player2Box.getSelectedItem());
            if (p1 != null && p2 != null) {
                JPanel cmp = new JPanel(new GridLayout(1, 2, 10, 0));
                cmp.add(createPlayerCard(p1));
                cmp.add(createPlayerCard(p2));
                setCenterComponent(cmp);
            }
        });

        panel.add(new JLabel("Player 1:"));
        panel.add(player1Box);
        panel.add(new JLabel("Player 2:"));
        panel.add(player2Box);
        panel.add(new JLabel());
        panel.add(compare);

        return panel;
    }

    private JPanel createSearchByStatPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        String[] options = {"Points", "Rebounds", "Assists"};
        JComboBox<String> statBox = new JComboBox<>(options);
        JTextField threshold = new JTextField();
        JButton filter = new JButton("Filter");
        styleButton(filter);

        filter.addActionListener(e -> {
            try {
                int min = Integer.parseInt(threshold.getText());
                String stat = (String) statBox.getSelectedItem();
                List<Player> filtered = playerData.getPlayers().stream().filter(p -> {
                    return switch (stat) {
                        case "Points" -> p.points >= min;
                        case "Rebounds" -> p.rebounds >= min;
                        case "Assists" -> p.assists >= min;
                        default -> false;
                    };
                }).collect(Collectors.toList());
                showPlayerCards(filtered);
            } catch (Exception ignored) {}
        });

        panel.add(new JLabel("Stat:"));
        panel.add(statBox);
        panel.add(new JLabel("Min Value:"));
        panel.add(threshold);
        panel.add(new JLabel());
        panel.add(filter);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BasicSearchGUI gui = new BasicSearchGUI();
            gui.setVisible(true);
        });
    }
}
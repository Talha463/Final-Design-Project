import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BasicSearchGUI extends JFrame {
    // Define a consistent color palette
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color PANEL_COLOR = new Color(45, 45, 45);
    private static final Color BUTTON_COLOR = new Color(60, 60, 60);
    private static final Color BUTTON_HOVER_COLOR = new Color(80, 80, 80);
    private static final Color TEXT_COLOR = Color.WHITE;

    private JTextField searchField;
    private JTextArea resultArea;
    private PlayerData playerData;
    private DefaultListModel<String> suggestionModel;

    // Keep a reference to the main tabbed pane and a search results panel
    private JTabbedPane tabbedPane;
    private JPanel searchResultsPanel = null;

    public BasicSearchGUI() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to apply FlatLaf");
        }

        setTitle("NBA Stats Search - ESPN Style");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        playerData = new PlayerData("src/Resources/players.txt");

        // -------- Top Input Panel --------
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel searchLabel = new JLabel("Search Player:");
        searchLabel.setForeground(TEXT_COLOR);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputPanel.add(searchLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(BACKGROUND_COLOR);
        searchField.setForeground(TEXT_COLOR);
        searchField.setCaretColor(TEXT_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        inputPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        styleButton(searchButton);
        inputPanel.add(searchButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        // -------- Main Tabs (Full-Page) --------
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.addTab("Home", createHomePanel());
        tabbedPane.addTab("Sort by Team", createSortByTeamPanel());
        tabbedPane.addTab("Stat Leaders", createStatLeadersPanel());
        tabbedPane.addTab("Compare Stats", createCompareStatsPanel());
        tabbedPane.addTab("Search by Stat", createSearchByStatPanel());
        tabbedPane.addTab("FAQ", createFAQPanel());
        add(tabbedPane, BorderLayout.CENTER);

        // -------- Right Panel (Top Players) --------
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setBackground(PANEL_COLOR);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel topLabel = new JLabel("Top Players");
        topLabel.setForeground(TEXT_COLOR);
        topLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightPanel.add(topLabel, BorderLayout.NORTH);

        JPanel topPlayersPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        topPlayersPanel.setBackground(PANEL_COLOR);
        addTopPlayers(topPlayersPanel);
        rightPanel.add(topPlayersPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // -------- Event Handling --------
        suggestionModel = new DefaultListModel<>();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        searchButton.addActionListener(e -> searchPlayers());
    }

    // Home panel with a welcome message
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(BACKGROUND_COLOR);
        JLabel welcome = new JLabel(
                "<html><div style='text-align:center;'>" +
                        "<h1 style='color:white;margin:0;'>🏀 NBA Stats Dashboard</h1>" +
                        "<p style='color:lightgray;font-size:16px;'>Search players, compare stats, and view leaders.</p>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        homePanel.add(welcome, BorderLayout.CENTER);
        return homePanel;
    }

    // FAQ Panel
    private JPanel createFAQPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        String faqText = """
        Q: How do I search for a player?
        A: Type the player's name in the search bar and press Enter.

        Q: How can I compare two players?
        A: Go to the 'Compare Stats' tab and select two players.

        Q: Where does the data come from?
        A: The data is loaded from 'players.txt' located in the Resources folder.

        Q: Can I filter players by stats?
        A: Yes, use the 'Search by Stat' tab and enter a minimum value.

        Q: Why is a player missing from the database?
        A: The dataset might be outdated; ensure 'players.txt' is up to date.
        """;

        JTextArea faqArea = new JTextArea(faqText);
        faqArea.setEditable(false);
        faqArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        faqArea.setForeground(TEXT_COLOR);
        faqArea.setBackground(new Color(45, 45, 45));
        faqArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(faqArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Frequently Asked Questions",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
        ));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Refined button styling with rounded corners
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(BUTTON_COLOR);
            }
        });
    }

    // Update suggestions (for future use if you add a suggestion popup)
    private void updateSuggestions() {
        String text = searchField.getText().toLowerCase();
        suggestionModel.clear();
        if (!text.isEmpty()) {
            for (String suggestion : playerData.getSuggestions(text)) {
                suggestionModel.addElement(suggestion);
            }
        }
    }

    // Search players and update (or create) the "Search Results" tab
    private void searchPlayers() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) return;

        List<Player> results = playerData.searchPlayers(query);
        if (results.isEmpty()) {
            updateSearchResultsPanel(new JLabel("❌ Player not found.", SwingConstants.CENTER));
        } else {
            JPanel resultPanel = new JPanel();
            resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
            resultPanel.setBackground(BACKGROUND_COLOR);
            for (Player p : results) {
                resultPanel.add(createPlayerCard(p));
                resultPanel.add(Box.createVerticalStrut(10));
            }
            JScrollPane scroll = new JScrollPane(resultPanel);
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            updateSearchResultsPanel(scroll);
        }
    }

    // Creates or updates the "Search Results" tab
    private void updateSearchResultsPanel(Component comp) {
        if (searchResultsPanel == null) {
            searchResultsPanel = new JPanel(new BorderLayout());
            tabbedPane.addTab("Search Results", searchResultsPanel);
        }
        searchResultsPanel.removeAll();
        searchResultsPanel.add(comp, BorderLayout.CENTER);
        searchResultsPanel.revalidate();
        searchResultsPanel.repaint();
        tabbedPane.setSelectedComponent(searchResultsPanel);
    }

    // Add Top Players to right panel
    private void addTopPlayers(JPanel panel) {
        String[] topPlayers = {"LeBron James", "Stephen Curry", "Giannis Antetokounmpo", "Nikola Jokic", "Kevin Durant", "Luka Doncic"};
        for (String name : topPlayers) {
            JButton btn = new JButton(name);
            styleButton(btn);
            btn.addActionListener(e -> displayPlayerProfile(name));
            panel.add(btn);
        }
    }

    // Display an individual player's profile
    private void displayPlayerProfile(String playerName) {
        Player player = playerData.searchPlayers(playerName).stream()
                .filter(p -> p.name.equalsIgnoreCase(playerName))
                .findFirst()
                .orElse(null);

        if (player == null) {
            // Instead of replacing the center, update Search Results tab
            updateSearchResultsPanel(new JLabel("❌ Player not found.", SwingConstants.CENTER));
            return;
        }

        JPanel profilePanel = new JPanel(new BorderLayout(15, 15));
        profilePanel.setBackground(Color.WHITE);
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
        stats.setBackground(Color.WHITE);
        Font font = new Font("Segoe UI", Font.BOLD, 20);
        JLabel pts = new JLabel("Points: " + player.points, SwingConstants.CENTER);
        pts.setFont(font);
        JLabel rbs = new JLabel("Rebounds: " + player.rebounds, SwingConstants.CENTER);
        rbs.setFont(font);
        JLabel ast = new JLabel("Assists: " + player.assists, SwingConstants.CENTER);
        ast.setFont(font);
        stats.add(pts);
        stats.add(rbs);
        stats.add(ast);

        profilePanel.add(top, BorderLayout.CENTER);
        profilePanel.add(stats, BorderLayout.SOUTH);
        updateSearchResultsPanel(profilePanel);
    }

    // Display a player card (used in search results and team panels)
    private JPanel createPlayerCard(Player p) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(50, 50, 50));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel image = new JLabel(loadImage("src/Resources/Images/Players/" + p.name + ".png", 80, 80));
        JLabel logo = new JLabel(loadImage("src/Resources/Images/Teams/" + p.team + ".png", 40, 40));

        JLabel info = new JLabel("<html><b>" + p.name + "</b><br>Team: " + p.team +
                "<br>PPG: " + p.points + " RPG: " + p.rebounds + " APG: " + p.assists + "</html>");
        info.setForeground(TEXT_COLOR);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(image, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(logo, BorderLayout.EAST);

        return card;
    }

    // "Sort by Team" tab with dynamic content update
    private JPanel createSortByTeamPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Left list panel
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> teamList = new JList<>(model);
        teamList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teamList.setBackground(BACKGROUND_COLOR);
        teamList.setForeground(TEXT_COLOR);
        playerData.getPlayers().stream()
                .map(p -> p.team)
                .distinct()
                .sorted()
                .forEach(model::addElement);
        JScrollPane listScroll = new JScrollPane(teamList);

        // Right display panel for players of selected team
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
        displayPanel.setBackground(BACKGROUND_COLOR);
        JScrollPane displayScroll = new JScrollPane(displayPanel);
        displayScroll.getVerticalScrollBar().setUnitIncrement(12);

        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String team = teamList.getSelectedValue();
                List<Player> filtered = playerData.getPlayers().stream()
                        .filter(p -> p.team.equalsIgnoreCase(team))
                        .collect(Collectors.toList());

                displayPanel.removeAll();
                for (Player p : filtered) {
                    displayPanel.add(createPlayerCard(p));
                    displayPanel.add(Box.createVerticalStrut(10));
                }
                displayPanel.revalidate();
                displayPanel.repaint();
            }
        });

        mainPanel.add(listScroll, BorderLayout.WEST);
        mainPanel.add(displayScroll, BorderLayout.CENTER);
        return mainPanel;
    }

    // "Stat Leaders" tab with buttons to show top 10 players by stat
    private JPanel createStatLeadersPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        String[] stats = {"Points", "Rebounds", "Assists"};

        for (String stat : stats) {
            JButton btn = new JButton("Top 10 " + stat);
            styleButton(btn);
            btn.addActionListener(e -> {
                List<Player> sorted = playerData.getTopPlayers(stat.toLowerCase(), 10);
                JPanel resultPanel = new JPanel();
                resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
                resultPanel.setBackground(BACKGROUND_COLOR);
                for (Player p : sorted) {
                    resultPanel.add(createPlayerCard(p));
                    resultPanel.add(Box.createVerticalStrut(10));
                }
                JScrollPane scroll = new JScrollPane(resultPanel);
                scroll.getVerticalScrollBar().setUnitIncrement(12);
                updateSearchResultsPanel(scroll);
            });
            panel.add(btn);
        }
        return panel;
    }

    // "Compare Stats" tab to compare two players side-by-side
    private JPanel createCompareStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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
                updateSearchResultsPanel(cmp);
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

    // "Search by Stat" tab to filter players based on a minimum value
    private JPanel createSearchByStatPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
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

                JPanel resultPanel = new JPanel();
                resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
                resultPanel.setBackground(BACKGROUND_COLOR);
                for (Player p : filtered) {
                    resultPanel.add(createPlayerCard(p));
                    resultPanel.add(Box.createVerticalStrut(10));
                }
                JScrollPane scroll = new JScrollPane(resultPanel);
                scroll.getVerticalScrollBar().setUnitIncrement(12);
                updateSearchResultsPanel(scroll);
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

    // Helper method to load and scale images
    private ImageIcon loadImage(String path, int width, int height) {
        try {
            Image img = new ImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BasicSearchGUI gui = new BasicSearchGUI();
            gui.setVisible(true);
        });
    }
}

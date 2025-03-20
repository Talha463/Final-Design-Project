import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BasicSearchGUI extends JFrame {
    private JTextField searchField;
    private JTextArea resultArea;
    private PlayerData playerData;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionModel;

    public BasicSearchGUI() {
        setTitle("NBA Stats Search");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        playerData = new PlayerData("src/main/players.txt");

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(new Color(50, 50, 50));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBackground(new Color(200, 200, 200));
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);

        inputPanel.add(new JLabel("Search Player, Number, Team, or Stats:"), BorderLayout.WEST);
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(new Color(240, 240, 240));
        resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionList.setFont(new Font("Arial", Font.PLAIN, 14));
        suggestionList.setBackground(new Color(220, 220, 220));
        suggestionList.setVisible(false);

        add(new JScrollPane(suggestionList), BorderLayout.EAST);

        JPanel topPlayersPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        topPlayersPanel.setBackground(new Color(50, 50, 50));
        topPlayersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addTopPlayers(topPlayersPanel);
        add(topPlayersPanel, BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });

        searchButton.addActionListener(e -> searchPlayers());
    }

    private void updateSuggestions() {
        String text = searchField.getText().toLowerCase();
        suggestionModel.clear();
        if (!text.isEmpty()) {
            for (String suggestion : playerData.getSuggestions(text)) {
                suggestionModel.addElement(suggestion);
            }
        }
        suggestionList.setVisible(!suggestionModel.isEmpty());
    }

    private void addTopPlayers(JPanel panel) {
        String[] topPlayers = {"LeBron James", "Stephen Curry", "Giannis Antetokounmpo", "Nikola Jokic", "Kevin Durant", "Luka Doncic"};
        for (String playerName : topPlayers) {
            JButton playerButton = new JButton(playerName);
            playerButton.setFont(new Font("Arial", Font.BOLD, 12));
            playerButton.setBackground(new Color(70, 130, 180));
            playerButton.setForeground(Color.WHITE);
            playerButton.setFocusPainted(false);
            playerButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            playerButton.addActionListener(e -> {
                searchField.setText(playerName);
                searchPlayers();
            });
            panel.add(playerButton);
        }
    }

    private void searchPlayers() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            resultArea.setText("Please enter a search term.");
            return;
        }

        List<Player> results = playerData.searchPlayers(query);
        if (results.isEmpty()) {
            resultArea.setText("No players found.");
        } else {
            StringBuilder resultText = new StringBuilder("Results:\n");
            for (Player p : results) {
                resultText.append(p.toString()).append("\n");
            }
            resultArea.setText(resultText.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BasicSearchGUI gui = new BasicSearchGUI();
            gui.setVisible(true);
        });
    }
}

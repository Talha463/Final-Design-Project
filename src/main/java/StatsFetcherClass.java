import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerDataTest {

    @Test
    void testComparePoints() {
        // Given a sample players.txt file
        PlayerData playerData = new PlayerData("src/main/players.txt");

        // Test comparing points between two players (feature not yet implemented)
        int comparisonResult = playerData.comparePoints("LeBron James", "Stephen Curry");

        // Expecting a valid output: -1, 0, or 1
        assertNotEquals(Integer.MIN_VALUE, comparisonResult, "❌ Expected a valid comparison result, but got Integer.MIN_VALUE.");
    }
}

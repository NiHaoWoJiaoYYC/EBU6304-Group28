package org.bupt.persosnalfinance.dto;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link TransactionInformation} class.
 * This test class covers:
 * - Adding transactions
 * - Saving to and loading from JSON file
 * - Ensuring transaction list management
 *
 * Test data is stored in a temporary file that is deleted after each test.
 *
 * @author Jing Wenrui
 */

public class TransactionInformationTest {

 // Use transactionInformationTest.json for testing to avoid affecting the actual data.
    private final String testFile = "transactionInformationTest.json";

    @BeforeEach
    void setup() {
        TransactionInformation.transactionList.clear();
    }

    @Test
    void testAddTransaction() {
        TransactionInformation t = new TransactionInformation("2024/05/25", 100.0, "Food", "KFC", "Lunch");
        TransactionInformation.addTransaction(t);
        assertEquals(1, TransactionInformation.transactionList.size());
        assertEquals("KFC", TransactionInformation.transactionList.get(0).getObject());
    }

    @Test
    void testSaveAndLoadJSON() {
        TransactionInformation t = new TransactionInformation("2024/05/25", 200.0, "Transport", "Bus", "To work");
        TransactionInformation.addTransaction(t);
        TransactionInformation.saveToJSON(testFile);

        TransactionInformation.transactionList.clear();
        assertEquals(0, TransactionInformation.transactionList.size());

        TransactionInformation.loadFromJSON(testFile);
        assertEquals(1, TransactionInformation.transactionList.size());
        assertEquals("Bus", TransactionInformation.transactionList.get(0).getObject());
    }

    @AfterEach
    void cleanup() {
        File file = new File(testFile);
        if (file.exists()) file.delete();
    }
}

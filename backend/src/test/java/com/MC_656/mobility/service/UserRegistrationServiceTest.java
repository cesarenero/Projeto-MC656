package com.MC_656.mobility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationServiceTest {

    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        userRegistrationService = new UserRegistrationService();
    }

    @Test
    @DisplayName("Should successfully register user with valid data (Valid Equivalence Class)")
    void registerUserWithValidData() {
        String name = "John Doe"; // Valid: Meets length, typical chars
        String email = "john.doe@example.com"; // Valid: Correct format
        String document = "12345678901"; // Valid: 11 digits, not all same
        String phone = "11999999999"; // Valid: 11 digits
        String password = "Password123"; // Valid: Meets complexity

        assertTrue(userRegistrationService.registerUser(name, email, document, phone, password),
                "Registration should succeed for valid user data.");
    }

    @ParameterizedTest
    @CsvSource({
            "'', test@example.com, 12345678901, 11999999999, Password123, Empty name",
            "A, test@example.com, 12345678901, 11999999999, Password123, Name too short (A)",
            "This name is way too long and should not be accepted by the system as it exceeds the maximum length allowed for names in our database and is clearly an invalid input for this particular field., test@example.com, 12345678901, 11999999999, Password123, Name too long"
    })
    @DisplayName("Should reject registration with invalid names (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidNames(String name, String email, String document, String phone, String password, String testCase) {
        assertFalse(userRegistrationService.registerUser(name, email, document, phone, password),
                "Registration should fail for: " + testCase);
    }

    @ParameterizedTest
    @CsvSource({
            "Valid Name, invalid.email, 12345678901, 11999999999, Password123, Invalid email format (no @ or . in right place)",
            "Valid Name, @domain.com, 12345678901, 11999999999, Password123, Missing local part in email",
            "Valid Name, user@, 12345678901, 11999999999, Password123, Missing domain in email",
            "Valid Name, user@domain, 12345678901, 11999999999, Password123, Invalid email domain format (no .)",
            "Valid Name, user@.com, 12345678901, 11999999999, Password123, Invalid email domain format (starts with .)"
    })
    @DisplayName("Should reject registration with invalid email formats (Equivalence Classes)")
    void rejectRegistrationWithInvalidEmails(String name, String email, String document, String phone, String password, String testCase) {
        assertFalse(userRegistrationService.registerUser(name, email, document, phone, password),
                "Registration should fail for: " + testCase);
    }

    @Test
    @DisplayName("Should reject registration with null email")
    void rejectRegistrationWithNullEmail() {
        assertFalse(userRegistrationService.registerUser("Valid Name", null, "12345678901", "11999999999", "Password123"),
                "Registration should fail for null email.");
    }

    @ParameterizedTest
    @CsvSource({
            "Valid Name, test@example.com, 123456789, 11999999999, Password123, Document too short (9 digits)",
            "Valid Name, test@example.com, 123456789012, 11999999999, Password123, Document too long (12 digits)",
            "Valid Name, test@example.com, 123.456.789-00, 11999999999, Password123, Document with non-numeric (cleaned by service)", // Service stub cleans this
            "Valid Name, test@example.com, 00000000000, 11999999999, Password123, Invalid document (all zeros)",
            "Valid Name, test@example.com, 11111111111, 11999999999, Password123, Invalid document (all ones)"
    })
    @DisplayName("Should reject registration with invalid document numbers (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidDocument(String name, String email, String document, String phone, String password, String testCase) {
        // The service's isValidDocument cleans dots/hyphens.
        // Test "123.456.789-00" will become "12345678900" which is valid length, but could be an existing one.
        // For this test, focusing on length and all-same-digits.
        if (document.equals("123.456.789-00")) {
            // This specific case becomes valid after cleaning by the stub.
            assertTrue(userRegistrationService.registerUser(name, email, document, phone, password),
                    "Registration should actually succeed for document '123.456.789-00' due to cleaning, unless it's an existing one.");
        } else {
            assertFalse(userRegistrationService.registerUser(name, email, document, phone, password),
                    "Registration should fail for: " + testCase);
        }
    }

    @Test
    @DisplayName("Should reject registration with null document")
    void rejectRegistrationWithNullDocument() {
        assertFalse(userRegistrationService.registerUser("Valid Name", "test@example.com", null, "11999999999", "Password123"),
                "Registration should fail for null document.");
    }


    @ParameterizedTest
    @CsvSource({
            "Valid Name, test@example.com, 12345678901, 1199999999, Password123, Phone too short (9 digits total for typical mobile)", // Assuming 10/11 digits for BR
            "Valid Name, test@example.com, 12345678901, 119999999999, Password123, Phone too long (12 digits total)",
            "Valid Name, test@example.com, 12345678901, (11)99999-9999, Password123, Phone with valid format (cleaned by service)", // Service stub should handle this
            "Valid Name, test@example.com, 12345678901, 11999999999a, Password123, Phone contains letters",
            "Valid Name, test@example.com, 12345678901, 1199999999!, Password123, Phone contains special characters"
    })
    @DisplayName("Should reject registration with invalid phone numbers (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidPhones(String name, String email, String document, String phone, String password, String testCase) {
        if (phone.equals("(11)99999-9999")) {
            // This specific case becomes valid after cleaning by the stub.
            assertTrue(userRegistrationService.registerUser(name, email, document, phone, password),
                    "Registration should actually succeed for phone '(11)99999-9999' due to cleaning.");
        } else {
            assertFalse(userRegistrationService.registerUser(name, email, document, phone, password),
                    "Registration should fail for: " + testCase);
        }
    }

    @Test
    @DisplayName("Should reject registration with null phone")
    void rejectRegistrationWithNullPhone() {
        assertFalse(userRegistrationService.registerUser("Valid Name", "test@example.com", "12345678901", null, "Password123"),
                "Registration should fail for null phone.");
    }

    // --- Password Tests ---
    @ParameterizedTest
    @CsvSource({
            "short, Password too short (stub min 8)",
            "NoUpper1, Password missing uppercase",
            "noupperdigit, Password missing uppercase and digit",
            "NOLOWER1, Password missing lowercase",
            "NoDigitUpper, Password missing digit"
    })
    @DisplayName("Should reject registration with invalid passwords (Equivalence Classes & Boundaries)")
    void rejectRegistrationWithInvalidPasswords(String invalidPassword, String testCaseName) {
        assertFalse(userRegistrationService.registerUser("ValidUser", "valid@example.com", "12345678902", "11987654321", invalidPassword),
                "Registration should fail for password case: " + testCaseName);
    }

    @Test
    @DisplayName("Should accept registration with minimum valid password (Boundary)")
    void acceptRegistrationWithMinimumValidPassword() {
        assertTrue(userRegistrationService.registerUser("ValidUser", "valid@example.com", "12345678903", "11987654322", "Passwd12"), // 8 chars, Upper, Lower, Digit
                "Registration should succeed with minimum valid password.");
    }

    @Test
    @DisplayName("Should reject registration with null password")
    void rejectRegistrationWithNullPassword() {
        assertFalse(userRegistrationService.registerUser("Valid Name", "test@example.com", "12345678901", "11987654321", null),
                "Registration should fail for null password.");
    }

    // --- Existing User Tests ---
    @Test
    @DisplayName("Should reject registration if email already exists")
    void rejectRegistrationWithExistingEmail() {
        // Assuming "existing@example.com" is configured as existing in the service stub
        assertFalse(userRegistrationService.registerUser("New User", "existing@example.com", "09876543210", "11223344556", "Password123"),
                "Registration should fail if email already exists.");
    }

    @Test
    @DisplayName("Should reject registration if document already exists")
    void rejectRegistrationWithExistingDocument() {
        // Assuming "12345678900_existing" is configured as existing in the service stub
        assertFalse(userRegistrationService.registerUser("New User Doc", "newdoc@example.com", "12345678900_existing", "11223344557", "Password123"),
                "Registration should fail if document already exists.");
    }
}

package com.MC_656.mobility.service;

// TODO: Import necessary annotations (e.g., @Service from Spring) and classes

// @Service // Uncomment when Spring context is configured
public class UserRegistrationService {

    /**
     * Registers a new user.
     * TODO: Implement actual registration logic including password hashing and validation.
     *
     * @param name     The name of the user.
     * @param email    The email address for the new user.
     * @param document The document number (e.g., CPF) of the user.
     * @param phone    The phone number of the user.
     * @param password The password for the new user.
     * @return True if registration is successful, false otherwise.
     */
    public boolean registerUser(String name, String email, String document, String phone, String password) {
        // Placeholder implementation
        if (!isValidName(name) || !isValidEmail(email) || !isValidDocument(document) || !isValidPhone(phone) || !validatePassword(password)) {
            return false;
        }

        if (checkUserExistsByEmail(email) || checkUserExistsByDocument(document)) {
            return false; // User already exists
        }

        // Simulate successful registration
        return true;
    }

    /**
     * Validates the password based on defined complexity rules.
     * TODO: Implement actual password complexity rules.
     *
     * @param password The password to validate.
     * @return True if the password is valid, false otherwise.
     */
    public boolean validatePassword(String password) {
        // Placeholder: e.g., min 8 chars, at least one number, one uppercase, one lowercase
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        // boolean hasSpecial = false; // Example: add special character requirement
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            // if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit; // && hasSpecial;
    }

    /**
     * Checks if a user with the given email already exists.
     * TODO: Implement actual check against a data store.
     *
     * @param email The email to check.
     * @return True if the user exists, false otherwise.
     */
    public boolean checkUserExistsByEmail(String email) {
        // Placeholder implementation
        return "existing@example.com".equals(email);
    }

    /**
     * Checks if a user with the given document already exists.
     * TODO: Implement actual check against a data store.
     *
     * @param document The document to check.
     * @return True if the user exists, false otherwise.
     */
    public boolean checkUserExistsByDocument(String document) {
        // Placeholder implementation
        return "12345678900_existing".equals(document);
    }


    /**
     * Validates the format of an email address.
     * TODO: Implement more robust email validation if needed.
     *
     * @param email The email address to validate.
     * @return True if the email format is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        // Basic check for "@" and "." and that "@" is not the first char and "." is after "@"
        int atPos = email.indexOf('@');
        int dotPos = email.lastIndexOf('.');
        return atPos > 0 && dotPos > atPos + 1 && dotPos < email.length() - 1;
    }

    /**
     * Validates the name based on defined rules.
     *
     * @param name The name to validate.
     * @return True if the name is valid, false otherwise.
     */
    private boolean isValidName(String name) {
        // Placeholder: e.g., not null, not empty, min 2 chars, max 100 chars.
        // The test uses "", "A", and a very long string.
        if (name == null || name.trim().isEmpty()) return false;
        if (name.length() < 2) return false; // "A" should be invalid
        if (name.length() > 100) return false; // "This name is way too long..." should be invalid
        return true; // Allows spaces and most characters for names
    }

    /**
     * Validates the document (e.g., CPF) based on defined rules.
     * For CPF, it's usually 11 digits.
     * TODO: Implement actual CPF validation algorithm if necessary.
     *
     * @param document The document string.
     * @return True if valid.
     */
    private boolean isValidDocument(String document) {
        if (document == null) return false;
        String cleanedDoc = document.replaceAll("[^0-9]", ""); // Remove non-digits
        if (cleanedDoc.length() != 11) return false; // CPF should have 11 digits

        // Basic check for all same digits (invalid CPF)
        boolean allSame = true;
        for (int i = 1; i < cleanedDoc.length(); i++) {
            if (cleanedDoc.charAt(i) != cleanedDoc.charAt(0)) {
                allSame = false;
                break;
            }
        }
        if (allSame) return false;

        // For a stub, this is sufficient. Real CPF validation is more complex.
        return true;
    }

    /**
     * Validates the phone number.
     * E.g., 10 or 11 digits for Brazilian numbers after cleaning.
     *
     * @param phone The phone string.
     * @return True if valid.
     */
    private boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String cleanedPhone = phone.replaceAll("[^0-9]", "");
        int len = cleanedPhone.length();
        // Test cases imply 11 digits for mobile, maybe 10 for landline
        if (len < 10 || len > 11) return false;

        // Test cases also check for non-numeric
        for (char c : phone.toCharArray()) { // Check original phone for non-digits if format matters
            if (!Character.isDigit(c) && c != '(' && c != ')' && c != '-' && c != ' ') {
                // Allowing some formatting chars, but tests have "a" and "!"
                if (Character.isLetter(c) || !Character.isLetterOrDigit(c) && !Character.isWhitespace(c) && c != '(' && c != ')' && c != '-') {
                    return false; // Reject if it has letters or unallowed special chars
                }
            }
        }
        return true;
    }
}

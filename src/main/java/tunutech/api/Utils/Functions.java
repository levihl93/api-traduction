package tunutech.api.Utils;

import org.springframework.stereotype.Service;
import tunutech.api.model.Devise;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class Functions {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + NUMBERS + SYMBOLS;
    private static final String ALL_CHARS1 = UPPERCASE + LOWERCASE + NUMBERS;

    public String generateFiveDigitCode() {
        int number = 10000 + secureRandom.nextInt(90000);
        return String.valueOf(number);
    }

    public String generateComplexPassword(int length,Boolean withSymbols) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder();

        // Ensure at least one character of each type
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(NUMBERS));
        if(withSymbols){password.append(getRandomChar(SYMBOLS));}

        // Fill the remaining length
        while (password.length() < length) {
            if(withSymbols)
            {
                password.append(getRandomChar(ALL_CHARS));
            }else { password.append(getRandomChar(ALL_CHARS1));}
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }
public String generateCodeUnique(int length,int clientId) {
        if (length < 6) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder();

        password.append(clientId);

        // Ensure at least one character of each type
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(NUMBERS));

        // Fill the remaining length
        while (password.length() < length) {
            password.append(getRandomChar(ALL_CHARS1));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    private char getRandomChar(String charSet) {
        int randomIndex = secureRandom.nextInt(charSet.length());
        return charSet.charAt(randomIndex);
    }

    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        // Shuffle using Collections.shuffle :cite[2]
        Collections.shuffle(characters);

        StringBuilder shuffledString = new StringBuilder();
        for (char c : characters) {
            shuffledString.append(c);
        }

        return shuffledString.toString();
    }

    public String formatMontant(Double montant, Devise devise) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(montant) + " " + devise;
    }
}
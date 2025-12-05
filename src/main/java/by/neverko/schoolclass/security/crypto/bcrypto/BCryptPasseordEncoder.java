package by.neverko.schoolclass.security.crypto.bcrypto;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasseordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "secret"; // ваш пароль
        String encoded = encoder.encode(rawPassword);
        System.out.println("Хэш пароля: " + encoded);
    }
}

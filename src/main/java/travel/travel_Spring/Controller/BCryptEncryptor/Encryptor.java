package travel.travel_Spring.Controller.BCryptEncryptor;

public interface Encryptor {
    String encrypt(String origin);
    boolean isMatch(String origin, String hashed);
}
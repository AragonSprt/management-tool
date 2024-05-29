package IdGenerator;

public class IdGenerator {
    public static String generateRandomId() {

        StringBuilder randomId = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int digit = (int) (Math.random() * 10);
            randomId.append(digit);
        }
        return randomId.toString();
    }
}

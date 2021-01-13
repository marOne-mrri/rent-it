package donneeInvalide;

public class DonneeInvalide extends Exception {
    String consigne;

    public DonneeInvalide(String m, String c) {
        super(m);
        consigne = c;
    }

    public String getConsigne() {
        return consigne;
    }
}






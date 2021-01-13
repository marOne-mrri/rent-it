package donneeInvalide;

import javafx.scene.control.Label;

public class DateInvalide extends DonneeInvalide {
    private Label cible;
    public DateInvalide(String messageErreur, String consigne, Label cible) {
        super(messageErreur, consigne);
        this.cible=cible;
    }
    public Label getCible() { return cible; }
}

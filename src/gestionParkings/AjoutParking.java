package gestionParkings;

import connexion.Connexion;
import donneeInvalide.CapaciteParkingInvalide;
import donneeInvalide.RueParkingInvalide;
import javafx.stage.Stage;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static donneeInvalide.Verification.*;

public class AjoutParking extends Modif_Ajout {
    private static Stage fentreAjout;
    private PreparedStatement ajoutParking;

    static void setFentreAjout(Stage fA) {
        fentreAjout=fA;
    }

    public AjoutParking() {
        String requAjoutParking="INSERT INTO parking(rue, capacite) VALUES(UPPER(?), ?)";
        try {
            ajoutParking= Connexion.connexion.prepareStatement(requAjoutParking);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void ajouter() {
        supprimerErreurs();
        try {
            verifierRueParking(rue.getText());
            ajoutParking.setString(1, rue.getText());
            verifierCapaciteParking(capacite.getText());
            ajoutParking.setString(2, capacite.getText());
            ajoutParking.executeUpdate();
            int index=ListeParkings.getControlleurParking().getParkingSlectionner();
            ListeParkings.getControlleurParking().rechercherParking();
            ListeParkings.getControlleurParking().selectionner(index);
            fentreAjout.close();
        }
        catch (RueParkingInvalide rueParkingInvalide) {
            rErreur.setText(rueParkingInvalide.getMessage());
        }
        catch (CapaciteParkingInvalide capaciteParkingInvalide) {
            cErreur.setText(capaciteParkingInvalide.getMessage());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void annuler() {
        fentreAjout.close();
    }

}

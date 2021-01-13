package gestionParkings;

import connexion.Connexion;
import donneeInvalide.CapaciteParkingInvalide;
import donneeInvalide.RueParkingInvalide;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static donneeInvalide.Verification.*;


public class ModificationParking extends Modif_Ajout implements Initializable {
    private static Parking parkingAModifier;
    private static Stage fenetreModification;
    private PreparedStatement modifParking;

    static void setParkingAModifier(Parking pAM) { parkingAModifier=pAM; }
    static void setFenetreModification(Stage fM) { fenetreModification=fM; }

    public ModificationParking() {
        String requModifParking="UPDATE parking SET rue=?, capacite=? WHERE id_parking=?";
        try {
            modifParking= Connexion.connexion.prepareStatement(requModifParking);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rue.setText(parkingAModifier.getRue());
        capacite.setText(String.valueOf(parkingAModifier.getCapacite()));
    }

    public void modifier() {
        supprimerErreurs();
        try {
            verifierRueParking(rue.getText());
            modifParking.setString(1, rue.getText());
            parkingAModifier.setCapacite( Short.valueOf(capacite.getText()) );
            verifierCapaciteParking(parkingAModifier);
            modifParking.setString(2, capacite.getText());
            modifParking.setInt(3, parkingAModifier.getId());
            modifParking.executeUpdate();
            int index=ListeParkings.getControlleurParking().getParkingSlectionner();
            ListeParkings.getControlleurParking().rechercherParking();
            ListeParkings.getControlleurParking().selectionner(index);
            fenetreModification.close();
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
        fenetreModification.close();
    }
}

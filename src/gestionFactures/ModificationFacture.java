package gestionFactures;

import com.jfoenix.controls.JFXTextField;
import connexion.Connexion;
import donneeInvalide.MontantInvalide;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static donneeInvalide.Verification.verifierMontant;

public class ModificationFacture implements Initializable {
    private static Facture factureAModifier;
    private static Stage fenetreModification;
    @FXML private Label code, montantErreur;
    @FXML private JFXTextField montant;
    private PreparedStatement modifierFacture;

    static void setFenetreModification(Stage fM) { fenetreModification=fM; }
    static void setFactureAModifier(Facture cM) { factureAModifier=cM; }

    public ModificationFacture() {
        String requModifFacture="UPDATE facture SET montant=? WHERE contrat_id=?";
        try {
            modifierFacture=Connexion.connexion.prepareStatement(requModifFacture);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        code.setText( Integer.toString(factureAModifier.getCode()) );
        montant.setText( Double.toString(factureAModifier.getMontant()) );
    }

    public void enregistrerModification() {
        montantErreur.setText(""); // supprimer message d'erreur
        try {
            verifierMontant(montant.getText());
            modifierFacture.setDouble(1, Double.valueOf(montant.getText()));
            modifierFacture.setInt(2, factureAModifier.getCode());
            modifierFacture.executeUpdate();
            ListeFactures.getControlleurFacture().rechercherFactures();
            fenetreModification.close();
        }
        catch (MontantInvalide montantInvalide) {
            montantErreur.setText(montantInvalide.getMessage());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void annuler() {
        fenetreModification.close();
    }
}

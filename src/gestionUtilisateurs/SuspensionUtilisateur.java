package gestionUtilisateurs;

import connexion.Connexion;
import donneeInvalide.DateInvalide;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static donneeInvalide.Verification.verifierDates;

public class SuspensionUtilisateur implements Initializable {
    private static Stage fenetreSuspension;
    private static Utilisateur utilisateurASuspendre;
    @FXML private Label qui, dCErreur, fCErreur;
    @FXML private DatePicker debutConge, finConge;
    private PreparedStatement suspendreUtilisateur;

    static void setFenetreSuspension(Stage fS) { fenetreSuspension=fS; }
    static void setUtilisateurASuspendre(Utilisateur u) { utilisateurASuspendre=u; }

    public SuspensionUtilisateur() {
        String requSuspendreUtilis = "UPDATE utilisateur SET debut_conge=?, " +
                "fin_conge=? WHERE nom_utilisateur=?";
        try {
            suspendreUtilisateur = Connexion.connexion.prepareStatement(requSuspendreUtilis);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        qui.setText(utilisateurASuspendre.getPrenom() + " " + utilisateurASuspendre.getNom());
        debutConge.setValue(utilisateurASuspendre.getDebutConge());
        finConge.setValue(utilisateurASuspendre.getFinConge());
    }

    public void suspendre() {
        supprimerErreurs();
        LocalDate d1 = debutConge.getValue(), d2 = finConge.getValue();
        try {
            verifierDates(d1, d2, dCErreur, fCErreur);
            Date debutConge=null, finConge=null;
             if( d1!=null ) {
                 debutConge = Date.valueOf(d1);
             }
             if (d2 != null) {
                 finConge = Date.valueOf(d2);
             }
            suspendreUtilisateur.setDate(1, debutConge);
            suspendreUtilisateur.setDate(2, finConge);
            suspendreUtilisateur.setString(3, utilisateurASuspendre.getNomUtilisateur());
            suspendreUtilisateur.executeUpdate();
            ListeUtilisateurs.getControlleurUtilisateur().rechercherUtilisateurs();
            fenetreSuspension.close();
        } catch (DateInvalide dateInvalide) {
            dateInvalide.getCible().setText(dateInvalide.getMessage() + dateInvalide.getConsigne());
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void supprimerErreurs() {
        dCErreur.setText("");
        fCErreur.setText("");
    }

    @FXML
    void annuler(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}

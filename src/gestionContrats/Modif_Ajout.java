package gestionContrats;

import com.jfoenix.controls.JFXDatePicker;
import donneeInvalide.DateInvalide;
import gestionClients.Client;
import gestionVehicules.CritereRecherche;
import gestionVehicules.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.time.LocalDate;

public class Modif_Ajout {
    @FXML protected Label dateContrat;
    @FXML protected DatePicker dateDepart, dateRetour;
    @FXML protected Label dDErreur, dRErreur;
    @FXML protected ComboBox<String> clients, vehicules;
    private ObservableList<String> listeClients= FXCollections.observableArrayList();
    private ObservableList<String> listeVehicules=FXCollections.observableArrayList();

    protected void verifierDates() throws DateInvalide {
        LocalDate d1=dateDepart.getValue(), d2=dateRetour.getValue();
        String messageErreur;   Label cible;
        if( d1==null ) {
            messageErreur="date de départ n'est pas spécifié";  cible=dDErreur;
        }
        else if( d2==null ) {
            messageErreur="date retour n'est pas spécifié";     cible=dRErreur;
        }
        else if( d1.isAfter(d2) ) {
            messageErreur="date départ et date retour ne sont pas ordonnées correctement";
            cible=dDErreur;
        }
        else return; // everything is fine
        throw new DateInvalide(messageErreur, "", cible);
    }

    protected void recupererClients() {
        ObservableList<Client> listeclients=Client.recuperer(); // tous les clients
        for(Client c:listeclients) {
            this.listeClients.add(c.getCin()+" - "+c.getNom()+" - "+c.getPrenom());
        }
        clients.setItems(listeClients);
    }

    protected void recupererVehicules() {
        ObservableList<Vehicule> listeVehicules=Vehicule.recupere(CritereRecherche.DISPONIBLE);
        for(Vehicule v:listeVehicules) {
            this.listeVehicules.add(v.getMatricule()+" - "+v.getMarque()+" - "+v.getType());
        }
        vehicules.setItems(this.listeVehicules);
    }

    protected void supprErreurs() {
        dDErreur.setText("");
        dRErreur.setText("");
    }
}

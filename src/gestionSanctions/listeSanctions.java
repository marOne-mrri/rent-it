package gestionSanctions;

import connexion.Connexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

public class listeSanctions implements Initializable {
    @FXML private Label status;
    @FXML private TextField nom, prenom;
    @FXML private TableView<Sanction> tableSanctions;
    @FXML private TableColumn<Sanction, Integer> codesContrats;
    @FXML private TableColumn<Sanction, String> clients;
    @FXML private TableColumn<Sanction, Date> datesRetoursTheoriques;
    @FXML private TableColumn<Sanction, Date> datesRetoursReeles;
    @FXML private TableColumn<Sanction, Short> retards;
    @FXML private TableColumn<Sanction, Double> montants;
    private ObservableList<Sanction> listeSanctions= FXCollections.observableArrayList();
    private PreparedStatement requSanctions;


    public listeSanctions() {
        String requRecupSanctions="SELECT cin, nom, prenom, id_contrat, date_retour, " +
                "date_retour_reele, sanction FROM contrat " +
                "INNER JOIN client ON cin=client_cin " +
                "INNER JOIN facture ON contrat_id=id_contrat " +
                "WHERE sanction > 0 " +
                "AND nom LIKE(?) AND prenom LIKE(?) " +
                "ORDER BY date_retour";
        try {
            requSanctions= Connexion.connexion.prepareStatement(requRecupSanctions);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mettreAJourSanctions();
        codesContrats.setCellValueFactory(new PropertyValueFactory<>("idContrat"));
        clients.setCellValueFactory(new PropertyValueFactory<>("client"));
        datesRetoursTheoriques.setCellValueFactory(new PropertyValueFactory<>("dateRetourTheorique"));
        datesRetoursReeles.setCellValueFactory(new PropertyValueFactory<>("dateRetourReele"));
        retards.setCellValueFactory(new PropertyValueFactory<>("retard"));
        montants.setCellValueFactory(new PropertyValueFactory<>("montant"));
        recupereSanctions();
    }
    /* calculer sanctions des vehicules non encore restitue chaque jour */
    private void mettreAJourSanctions() {
        String requ="UPDATE facture INNER JOIN contrat ON id_contrat = contrat_id SET sanction=DATEDIFF(?,date_retour)*2000 WHERE date_retour<? AND date_retour_reele IS NULL";
        try {
            PreparedStatement mettreAjour=Connexion.connexion.prepareStatement(requ);
            mettreAjour.setDate(1, Date.valueOf(LocalDate.now()));
            mettreAjour.setDate(2, Date.valueOf(LocalDate.now()));
            mettreAjour.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void recupereSanctions(String...parametres) {
        listeSanctions.clear();
        status.setText("");
        try {
            if( parametres.length==2 ) {
                requSanctions.setString(1, parametres[0]);
                requSanctions.setString(2, parametres[1]);
            }
            else if( parametres.length==0 ) {
                requSanctions.setString(1, "%");
                requSanctions.setString(2, "%");
            }
            ResultSet sanctions=requSanctions.executeQuery();
            if( sanctions.next() ) {
                sanctions.beforeFirst();
                extraireDonnees(sanctions);
                tableSanctions.setItems(listeSanctions);
            }
            else {
                status.setText("Aucun sanction trouvé");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void extraireDonnees(ResultSet sanctions) {
        try {
            while( sanctions.next() ) {
                Sanction s=new Sanction();
                s.setIdContrat(sanctions.getInt("id_contrat"));
                s.setClient(
                        sanctions.getString("cin")+" - "+
                        sanctions.getString("nom")+" - "+
                        sanctions.getString("prenom")
                );
                Date dateRetourTheorique=sanctions.getDate("date_retour");
                s.setDateRetourTheorique(dateRetourTheorique);
                Date dateRetourReele=sanctions.getDate("date_retour_reele");
                s.setDateRetourReele(dateRetourReele);
                int retard;
                if( dateRetourReele==null ) { // vehicule pas encore restitue
                    retard=Period.between(dateRetourTheorique.toLocalDate(), LocalDate.now()).getDays();
                }
                else {
                    retard=Period.between(dateRetourTheorique.toLocalDate(),
                            dateRetourReele.toLocalDate()).getDays();
                }
                s.setRetard(retard);
                s.setMontant(sanctions.getDouble("sanction"));
                listeSanctions.add(s);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void rechercherSanctions() {
        String nom=this.nom.getText();
        if( nom.equals("") )
            nom="%";
        else
            nom="%"+nom+"%";
        String prenom=this.prenom.getText();
        if( prenom.equals("") )
            prenom="%";
        else
            prenom="%"+prenom+"%";
        recupereSanctions(nom, prenom);
    }

    public void afficherTous() {
        nom.setText("");
        prenom.setText("");
        recupereSanctions();
    }

    public void reglerSanction() {
        Sanction sanctionSelectionne=tableSanctions.getSelectionModel().getSelectedItem();
        if( sanctionSelectionne==null ) { return; }
        sanctionSelectionne.regler(); // mettre sanction à zero
        recupereSanctions();
    }
}

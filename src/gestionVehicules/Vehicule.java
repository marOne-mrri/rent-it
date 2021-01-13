package gestionVehicules;

import connexion.Connexion;
import gestionParkings.Parking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

enum Carburant {
    ESSENCE, DIESEL
}

public class Vehicule {
    private String matricule, marque, type;
    private Carburant carburant;
    private long kilometrage;
    private Date dateMiseEnCirculation;
    private boolean reserve;
    private boolean enLocation;
    private double prixLocation;
    private String parking;

    public String getMatricule() { return matricule; }
    public String getMarque() { return marque; }
    public String getType() { return type; }
    public Carburant getCarburant() { return carburant; }
    public long getKilometrage() { return kilometrage; }
    public Date getDateMiseEnCirculation() { return dateMiseEnCirculation; }
    public double getPrixLocation() { return prixLocation; }
    public String getParking() { return parking; }

    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setMarque(String marque) { this.marque = marque; }
    public void setType(String type) { this.type = type; }
    public void setCarburant(Carburant carburant) { this.carburant = carburant; }
    public void setKilometrage(long kilometrage) { this.kilometrage = kilometrage; }
    public void setDateMiseEnCirculation(Date dateMiseEnCirculation) {
        this.dateMiseEnCirculation = dateMiseEnCirculation;
    }
    public void setReserve(boolean reserve) { this.reserve = reserve; }
    public void setEnLocation(boolean enLocation) { this.enLocation = enLocation; }
    public void setPrixLocation(double prixLocation) { this.prixLocation = prixLocation; }
    public void setParking(String parking) { this.parking = parking; }

    /* 2 cas possible pour appeler cette fonction:
        1-recuperer(critere): pour recuperer les vehicules qui verifie critere seulemnt sans discuter
            matricule, marque ou type
        2-recuperer(critere, matricule, marque, type): pour recuperer vehicules qui verifent critere
            et aussi rechercher par matricule, marque et type
         !!!! parametres={matricule, marque, type} !!!!
         !!!! c'est pas obligatoire d'effectuer recherche par matricule, marque et type on peut
                 choisir n'importe quel combinaison entre ces trois !!!!
     */
    public static ObservableList<Vehicule> recupere(CritereRecherche critere, String...parametres) {
        try {
            String requRecup="SELECT * FROM vehicule WHERE reserve IN(?, ?) AND " +
                    "en_location IN(?, ?) AND parking_id LIKE(?) AND matricule LIKE (?) " +
                    "AND marque LIKE (?) AND type LIKE (?)";
            PreparedStatement recupVehicules=Connexion.connexion.prepareStatement(requRecup);
            if( critere==CritereRecherche.TOUS ) {
                recupVehicules.setBoolean(1, true); // reserve={true, false}
                recupVehicules.setBoolean(2, false);
                recupVehicules.setBoolean(3, true); // en_location={true, false}
                recupVehicules.setBoolean(4, false);
                recupVehicules.setString(5, "%"); // dans un parking ou sans parking
            }
            else if( critere==CritereRecherche.DISPONIBLE) {
                recupVehicules.setBoolean(1, false); // reserve=false
                recupVehicules.setBoolean(2, false);
                recupVehicules.setBoolean(3, false); // en_location=false
                recupVehicules.setBoolean(4, false);
                recupVehicules.setString(5, "%"); // dans un parking ou sans parking
            }
            else if( critere==CritereRecherche.DISPONIBLE_SANS_PARKING ) {
                recupVehicules.setBoolean(1, false); // reserve=false
                recupVehicules.setBoolean(2, false);
                recupVehicules.setBoolean(3, false); // en_location=false
                recupVehicules.setBoolean(4, false);
                recupVehicules.setString(5, "0"); // parking_id=0 (sans parking)
            }
            else if( critere==CritereRecherche.RESERVE ) {
                recupVehicules.setBoolean(1, true); // reserve=true
                recupVehicules.setBoolean(2, true);
                recupVehicules.setBoolean(3, false); // en_location=false
                recupVehicules.setBoolean(4, false);
                recupVehicules.setString(5, "%"); // dans un parking ou sans parking
            }
            /* vehicule chez le client */
            else if( critere==CritereRecherche.EN_LOCATION ) {
                recupVehicules.setBoolean(1, true); // reserve=true
                recupVehicules.setBoolean(2, true);
                recupVehicules.setBoolean(3, true);// en_location=true
                recupVehicules.setBoolean(4, true);
                recupVehicules.setString(5, "0"); //forcement sans parking (chez client)
            }
            if( parametres.length==3 ) {
                recupVehicules.setString(6, parametres[0]);
                recupVehicules.setString(7, parametres[1]);
                recupVehicules.setString(8, parametres[2]);
            }
            else if( parametres.length==0 ) {
                recupVehicules.setString(6, "%");
                recupVehicules.setString(7, "%");
                recupVehicules.setString(8, "%");
            }
            ResultSet vehicules=recupVehicules.executeQuery();
            return extraireDonnees(vehicules);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return null; //it's almost impossible to have an error, but the complier insisted :)
    }

    private static ObservableList<Vehicule> extraireDonnees(ResultSet resultat) {
        ObservableList<Vehicule> listeVehicules= FXCollections.observableArrayList();
        try {
            while (resultat.next()) {
                Vehicule vehicule = new Vehicule();
                vehicule.setMatricule(resultat.getString("matricule"));
                vehicule.setMarque(resultat.getString("marque"));
                vehicule.setType(resultat.getString("type"));
                String carburant = resultat.getString("carburant");
                if (carburant.equals("essence")) {
                    vehicule.setCarburant(Carburant.ESSENCE);
                } else if (carburant.equals("diesel")) {
                    vehicule.setCarburant(Carburant.DIESEL);
                }
                vehicule.setKilometrage(resultat.getLong("kilometrage"));
                vehicule.setDateMiseEnCirculation(resultat.getDate("date_mise_circulation"));
                vehicule.setReserve(resultat.getBoolean("reserve"));
                vehicule.setEnLocation(resultat.getBoolean("en_location"));
                vehicule.setPrixLocation(resultat.getDouble("prix_location"));
                int parking = resultat.getInt("parking_id");
                if (parking == 0) {
                    vehicule.setParking("Sans Parking");
                } else {
                    vehicule.setParking(Integer.toString(parking));
                }
                listeVehicules.add(vehicule);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return listeVehicules;
    }

    public static void reserver(String matricule) {
        String requReserVehicule="UPDATE vehicule SET reserve=1, en_location=0 WHERE matricule=?";
        try {
            PreparedStatement reserverVehicule=Connexion.connexion.prepareStatement(requReserVehicule);
            reserverVehicule.setString(1, matricule);
            reserverVehicule.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void marquerDisponible(String matricule) {
        String requMarquDisponible="UPDATE vehicule SET reserve=0, en_location=0 " +
                "WHERE matricule=?";
        try {
            PreparedStatement marquerDispo=Connexion.connexion.prepareStatement(requMarquDisponible);
            marquerDispo.setString(1, matricule);
            marquerDispo.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void deposerDans(int idParking) {
        String requDeplacer="UPDATE vehicule SET parking_id=? WHERE matricule=?";
        try {
            PreparedStatement deplacerVehicule=Connexion.connexion.prepareStatement(requDeplacer);
            deplacerVehicule.setInt(1, idParking);
            deplacerVehicule.setString(2, matricule);
            deplacerVehicule.executeUpdate();
            // si vehicule ete dans un parking
            if( !parking.equals("Sans Parking") ) {
                Parking.decrimenterTaille(Integer.parseInt(parking)); // parking source
            }
            Parking.incrementerTaille(idParking); // parking de destination
            // si on essaye de restituer ce vehicule, on doit le marquer comme "disponible" aussi
            if( enLocation ) {
                marquerDisponible(matricule);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void supprimer() {
        String requSuppr="DELETE FROM vehicule WHERE matricule=?";
        try {
            PreparedStatement supprimerVehicule=Connexion.connexion.prepareStatement(requSuppr);
            supprimerVehicule.setString(1, matricule);
            supprimerVehicule.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void sortir() {
        String requSortirVehicule="UPDATE vehicule SET en_location=1 WHERE matricule=?";
        try {
            PreparedStatement sortirVehicule=Connexion.connexion.prepareStatement(requSortirVehicule);
            sortirVehicule.setString(1, matricule);
            sortirVehicule.executeUpdate();
            deposerDans(0); // 0: sans parking
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /* marquer les vehicules des reservations qui seront annulees automatiquement comme "disponible"
        ces reservation seront annuler car elles n'ont pas ete valide avant 2 jours
        de la date de location
     */
    public static void marquerDisponible() {
        String sql="UPDATE vehicule SET reserve=0 WHERE matricule IN( (SELECT vehicule_matricule " +
                "FROM reservation WHERE valide=0 AND annule=0 " +
                "AND DATEDIFF(date_depart, CURDATE()) < 2) )";
        try {
            Statement statement=Connexion.connexion.createStatement();
            statement.executeUpdate(sql);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
    /*
    appeller en temps de la suppression d'un parking pour faire
    la mise à jour de l'Id de parking des véhicules qu'ils été dans ce parking.
    */

    public static void marquerSansParking(int idParkingSupprime) {
        String sql = "UPDATE vehicule SET parking_id = 0 WHERE parking_id = ?";
        try {
            PreparedStatement statement = Connexion.connexion.prepareStatement(sql);
            statement.setInt(1, idParkingSupprime);
            statement.executeUpdate();
        } catch (SQLException e){
            e.getErrorCode();
        }
    }
}

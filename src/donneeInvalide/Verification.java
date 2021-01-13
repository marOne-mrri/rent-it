package donneeInvalide;

import gestionParkings.Parking;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class Verification {

    public static void verifierNomUtilisateur(String nomUtilisateur) throws NomUtilisateurInvalide {
        String nomUtilisateurValide = "[a-zA-Z][a-zA-Z0-9-_ ]*";
        if( nomUtilisateur.length() == 0 ) {
            String messageErreur="c'est vide !";
            throw new NomUtilisateurInvalide(messageErreur, "");
        }
        else if( !nomUtilisateur.matches(nomUtilisateurValide) ) {
            String messageErreur="nom d'utilisateur Invalide !";
            String consigne="";
            throw new NomUtilisateurInvalide(messageErreur, consigne);
        }
    }

    public static void verifierMotPasse(String motPasse) throws MotPasseVide {
        if( motPasse.equals("") ) {
            throw new MotPasseVide("mot de passe vide !", "");
        }
    }

    public static void verifierCin(String cin) throws CinInvalide {
        String cinValide="([a-zA-Z][0-9]{6})|([a-zA-Z]{2}[0-9]{5})";
        if( cin.length()==0 ) {
            String messageErreur="C.I.N vide !";
            throw new CinInvalide(messageErreur, "");
        }
        else if( !cin.matches(cinValide) ) {
            String messageErreur="C.I.N invalide !";
            String consigne="";
            throw new CinInvalide(messageErreur, consigne);
        }
    }

    public static void verifierNom(String nom) throws NomInvalide {
        String nomValide="([a-zA-Z]+[ ]?)+";
        if( nom.length()==0 ) {
            String messageErreur="nom vide !";
            throw new NomInvalide(messageErreur, "");
        }
        else if( !nom.matches(nomValide) ) {
            String messageErreur="";
            String consigne="les caractères seulement !";
            throw new NomInvalide(messageErreur, consigne);
        }
    }

    public static void verifierPrenom(String prenom) throws PrenomInvalide {
        String prenomValide="([a-zA-Z]+[ ]?)+";
        if( prenom.length()==0 ) {
            String messageErreur="prénom vide !";
            throw new PrenomInvalide(messageErreur, "");
        }
        else if( !prenom.matches(prenomValide) ) {
            String messageErreur="";
            String consigne="les caractères seulement !";
            throw new PrenomInvalide(messageErreur, consigne);
        }
    }

    public static void verifierMail(TextField zMail) throws MailInvalide {
        String mailValide="([a-zA-Z0-9]+(\\+?|\\.?|-?|_?))*[a-zA-Z0-9]" +
                "@([a-zA-Z0-9]+\\.?|-?)+\\.(com|net|org|ma|fr)";
        String mail=zMail.getText();
        if( mail==null || mail.length()==0 ) {
            zMail.setText(null);
        }
        else if( !mail.matches(mailValide) ) {
            String messageErreur="e-mail invalide ! ";
            throw new MailInvalide(messageErreur, "");
        }
    }

    public static void verifierGsm(TextField zGsm) throws GsmInvalide {
        String gsmValide="0[67]\\d{8}";
        String gsm=zGsm.getText();
        if( gsm==null || gsm.length()==0 ) {
            zGsm.setText(null);
        }
        else if( !gsm.matches(gsmValide) ) {
            String messageErreur="GSM invalide ! ";
            String consigne="06/07...";
            throw new GsmInvalide(messageErreur, consigne);
        }
    }

    public static void verifierDates(LocalDate d1, LocalDate d2, Label l1, Label l2)
            throws DateInvalide {
        String messageErreur;
        Label cible; // la zone ou on va afficher message d'erreur
        if( d1==null ) {
            if( d2!=null ) {
                messageErreur="date vide !";
                cible=l1;
            }
            else return;
        }
        else {
            if( d2!=null && d1.isAfter(d2) ) {
                messageErreur="Impossible !";
               cible=l2;
            }
            else return;
        }
        throw new DateInvalide(messageErreur, "", cible);
    }

    public static void verifierMontant(String montant) throws MontantInvalide {
        String montantValide="[0-9]+([.][0-9]+)?";
        String messageErreur;
        if( montant.equals("") ) {
            messageErreur="montant est vide !";
        }
        else if( !montant.matches(montantValide) ) {
            messageErreur="montant invalide !";
        }
        else { return; } // montant valide
        throw new MontantInvalide(messageErreur, "");
    }

    public static void verifierMarque(String marque) throws MarqueInvalide {
        String marqueValide="[a-zA-Z ]+";
        String messageErreur;
        if( marque.equals("") ) {
            messageErreur="marque vide !";
        }
        else if( !marque.matches(marqueValide) ) {
            messageErreur="invalide marque !";
        }
        else return;
        throw new MarqueInvalide(messageErreur, "");
    }

    public static void verifierTypeVehicule(String type) throws TypeVehiculeInvalide {
        String typeValide="[a-zA-Z0-9 ]+";
        String messageErreur;
        if( type.equals("") ) {
            messageErreur="type vide!";
        }
        else if( !type.matches(typeValide) ) {
            messageErreur="invalide type !";
        }
        else return;
        throw new TypeVehiculeInvalide(messageErreur, "");
    }

    public static void verifierKilometrage(String kilometrage) throws KilometrageInvalide {
        String kilometrageValide="[0-9]+";
        String messageErreur;
        if( kilometrage.equals("") ) {
            messageErreur="Kilometrage est vide !";
        }
        else if( !kilometrage.matches(kilometrageValide) ) {
            messageErreur="invalide kilometrage!";
        }
        else return;
        throw new KilometrageInvalide(messageErreur, "");
    }

    public static void verifierPrixLocation(String prixLocation) throws PrixLocationInvalide {
        String prixValide="[0-9]+([.][0-9]+)?";
        String messageErreur;
        if( prixLocation.equals("") ) {
            messageErreur="prix est vide !";
        }
        else if( !prixLocation.matches(prixValide) ) {
            messageErreur="invalide prix !";
        }else if (Double.parseDouble(prixLocation) > 9999.99){
            messageErreur = "prix max est 9999.99";
        }
        else return;
        throw new PrixLocationInvalide(messageErreur, "");
    }

    public static void verifierMatricule(String matricule) throws MatriculeInvalide {
        String matriculeValide="[0-9]{1,5}[a-zA-Z][0-9]{1,2}";
        String messageErreur;
        if( matricule.equals("") ) {
            messageErreur="matricule vide !";
        }
        else if( !matricule.matches(matriculeValide) ) {
            messageErreur="invalide maricule !";
        }
        else return;
        throw new MatriculeInvalide(messageErreur, "");
    }

    public static void verifierRueParking(String rue) throws RueParkingInvalide {
        String rueValide="[a-zA-Z0-9 ]+";
        String messageErreur;
        if( rue.equals("") ) {
            messageErreur="rue est vide !";
        }
        else if( !rue.matches(rueValide) ) {
            messageErreur="invalide rue !";
        }
        else return;
        throw new RueParkingInvalide(messageErreur, "");
    }

    /* utilise lors de l'ajout d'un parking, elle veriie que la capacite est un nombre >0 */
    public static void verifierCapaciteParking(String capacite) throws CapaciteParkingInvalide {
        String capaciteValide="[1-9][0-9]*";
        String messageErreur;
        if( capacite.equals("") ) {
            messageErreur="capacité vide !";
        }
        else if( !capacite.matches(capaciteValide) ) {
            messageErreur="invalide capacité !";
        }
        else return;
        throw new CapaciteParkingInvalide(messageErreur, "");
    }
    /* utilise quand on veut modifier un parking pour assurer que la capacite est valide et aussi
        que la capacite >= taille( nombre vehicule dans ce parking)
     */
    public static void verifierCapaciteParking(Parking parking) throws CapaciteParkingInvalide {
        // verifier que la capacite est un nombre >0
        verifierCapaciteParking( String.valueOf(parking.getCapacite()) );
        if( parking.getCapacite()<parking.getTaille() ) {
            String messageErreur="capacité est inférieur au nombre de véhicule !";
            throw new CapaciteParkingInvalide(messageErreur, "");
        }
    }
}
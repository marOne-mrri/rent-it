package menu;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.*;
import connexion.Connexion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label afterLoginLabel;

    @FXML
    private JFXDrawer quitDrawer;

    @FXML
    private JFXHamburger hamburger;

    @FXML
    private Button threeDotsMenu;

    @FXML
    private JFXDrawer drawer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String toShow="Bienvenu " + Connexion.utilisateur.getPrenom() + " " + Connexion.utilisateur.getNom() + " !";
        this.afterLoginLabel.setText(toShow);
        HamburgerBackArrowBasicTransition task = new HamburgerBackArrowBasicTransition(this.hamburger);
        task.setRate(-1);
        this.hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            task.setRate(task.getRate() * -1);
            task.play();
            if (this.drawer.isClosed()) {
                this.drawer.setPrefWidth(240);
                try {
                    VBox box = FXMLLoader.load(getClass().getResource("menu.fxml"));
                    this.drawer.setSidePane(box);
                    for (Node node : box.getChildren()){
                        node.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
                            if (node.getAccessibleText() != null){
                                switch (node.getAccessibleText()){
                                    case "clients" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionClients/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "users" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionUtilisateurs/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "agreements" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionContrats/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "invoices" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionFactures/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "reservations" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionReservation/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "sanctions" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionSanctions/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "vehicles" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionVehicules/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case "parkings" :
                                        try {
                                            onClick(FXMLLoader.load(getClass().getResource("../gestionParkings/liste.fxml")));
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                }
                            }
                        });
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.drawer.open();
            } else {
                this.drawer.close();
                this.drawer.setSidePane();
                this.drawer.setPrefWidth(1);
            }
        });
        this.threeDotsMenu.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (this.quitDrawer.isClosed()) {
                this.quitDrawer.setPrefWidth(170);
                try {
                    VBox quitBox = FXMLLoader.load(getClass().getResource("quitMenu.fxml"));
                    this.quitDrawer.setSidePane(quitBox);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.quitDrawer.open();
            } else {
                this.quitDrawer.close();
                this.quitDrawer.setSidePane();
                this.quitDrawer.setPrefWidth(1);
            }
        });
    }

    private void onClick(AnchorPane pane) {
        this.mainPane.getChildren().remove(0);
        this.mainPane.getChildren().add(0, pane);
        Node thePane = this.mainPane.getChildren().get(0);
        AnchorPane.setBottomAnchor(thePane, 0.0);
        AnchorPane.setLeftAnchor(thePane, 0.0);
        AnchorPane.setRightAnchor(thePane, 90.0);
        AnchorPane.setTopAnchor(thePane, 0.0);
    }
}
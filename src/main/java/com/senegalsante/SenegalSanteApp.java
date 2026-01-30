package com.senegalsante;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.senegalsante.util.SpringContext;

/**
 * Point d'entrée de l'application JavaFX Sénégal Santé
 * 
 * Cette classe initialise :
 * 1. Le contexte Spring (pour la base de données et les services)
 * 2. L'interface JavaFX (fenêtre principale)
 * 
 * Architecture :
 * - Stage : La fenêtre principale
 * - Scene : Le contenu affiché
 * - FXML : Les fichiers de structure d'interface
 * - Controllers : La logique de chaque écran
 */
@SpringBootApplication
public class SenegalSanteApp extends Application {

    private ConfigurableApplicationContext springContext;
    private static Stage primaryStageRef;

    /**
     * Méthode init() : Appelée avant start()
     * Initialise le contexte Spring pour la base de données
     */
    @Override
    public void init() {
        // Démarrage de Spring Boot pour la gestion de la base de données
        springContext = SpringApplication.run(SenegalSanteApp.class);
        // Initialisation explicite du gestionnaire de contexte
        SpringContext.setContext(springContext);
    }

    /**
     * Méthode start() : Point d'entrée JavaFX
     * Crée et affiche la fenêtre principale
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStageRef = primaryStage;

            // Chargement du fichier FXML de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Création de la scène avec le contenu chargé
            Scene scene = new Scene(root, 1200, 800);

            // Application du CSS
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            // Configuration de la fenêtre (Stage)
            primaryStage.setTitle("HEALTHSen - La Santé Est Prioritaire");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            // Optionnel : Icône de l'application
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            } catch (Exception e) {
                // Ignore if icon not found
            }

            // Affichage de la fenêtre
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Méthode stop() : Appelée à la fermeture de l'application
     * Nettoie les ressources (ferme la base de données)
     */
    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
        System.exit(0);
    }

    /**
     * Méthode utilitaire pour obtenir le Stage principal
     * Utilisée par les controllers pour changer de scène
     */
    public static Stage getPrimaryStage() {
        return primaryStageRef;
    }

    /**
     * Point d'entrée Java standard
     */
    public static void main(String[] args) {
        launch(args);
    }
}

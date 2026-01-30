package com.senegalsante.controller.javafx;

import com.senegalsante.model.*;
import com.senegalsante.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.senegalsante.util.SpringContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Contrôleur pour le Dashboard (Tableau de Bord)
 * 
 * Responsabilités :
 * - Afficher les statistiques de santé de l'utilisateur
 * - Afficher les derniers enregistrements de santé
 * - Afficher les graphiques de suivi
 * - Gérer la navigation vers les autres écrans
 */
@Component
@org.springframework.context.annotation.Scope(org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardController {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MedicationIntakeRepository medicationIntakeRepository;

    private User currentUser;

    // Éléments de l'interface
    @FXML
    private BorderPane mainContainer;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label currentDateLabel;

    @FXML
    private Label totalRecordsLabel;

    @FXML
    private Label lastCheckupLabel;

    @FXML
    private Label healthScoreLabel;

    @FXML
    private Label healthStatusLabel;

    @FXML
    private VBox wellnessCard;

    @FXML
    private Label nextMedNameLabel;

    @FXML
    private Label nextMedTimeLabel;

    @FXML
    private Label nextMedPatientLabel;

    @FXML
    private Label nextAppointmentLabel;

    @FXML
    private Label nextAppointmentDateLabel;

    @FXML
    private Label nextAppointmentPatientLabel;

    @FXML
    private TableView<Medication> medicationTable;

    @FXML
    private TableColumn<Medication, String> medNameCol;

    @FXML
    private TableColumn<Medication, String> medDosageCol;

    @FXML
    private TableColumn<Medication, String> medFreqCol;

    @FXML
    private TableColumn<Medication, String> medNextCol;

    @FXML
    private TableColumn<Medication, String> medStatusCol;

    @FXML
    private TableColumn<Medication, Void> medActionCol;

    @FXML
    private TextField medSearchField;

    @FXML
    private VBox appointmentsContainer;

    @FXML
    private TextField aptSearchField;

    @FXML
    private DatePicker filterDatePicker;

    // Champs pour l'ajout de rendez-vous
    @FXML
    private ComboBox<String> aptTypeCombo;

    @FXML
    private ComboBox<Profile> aptMemberCombo;

    @FXML
    private DatePicker aptDatePicker;

    @FXML
    private TextField aptDoctorField;

    @FXML
    private TextField aptSpecialtyField;

    @FXML
    private TextField aptLocationField;

    @FXML
    private ComboBox<String> aptHourCombo;

    @FXML
    private ComboBox<String> aptMinuteCombo;

    @FXML
    private TextArea aptNotesArea;

    @FXML
    private LineChart<String, Number> healthChart;

    @FXML
    private LineChart<String, Number> wellnessChart;

    @FXML
    private LineChart<String, Number> painChart;

    @FXML
    private VBox healthHistoryContainer;

    @FXML
    private VBox recentRecordsContainer;

    @FXML
    private ComboBox<String> filterRecordTypeCombo;

    @FXML
    private ComboBox<Profile> filterMemberCombo;

    @FXML
    private DatePicker filterRecordDatePicker;

    @FXML
    private TableView<?> prescriptionsTable;

    @FXML
    private FlowPane familyFlowPane;

    @FXML
    private TextField profileFirstNameField;

    @FXML
    private TextField profileLastNameField;

    @FXML
    private TextField profileEmailField;

    @FXML
    private TextField profilePhoneField;

    @FXML
    private TextField medNameField;

    @FXML
    private TextField medDosageField;

    @FXML
    private ComboBox<String> medFrequencyCombo;

    @FXML
    private ComboBox<Profile> medMemberCombo;

    @FXML
    private TextArea medNotesArea;

    @FXML
    private Label intakePlaceholderLabel;

    @FXML
    private VBox dailyMedicationsContainer;

    @FXML
    private VBox intakeTimesContainer;

    @FXML
    private DatePicker medStartDatePicker;

    @FXML
    private TextField medDurationDaysField;

    // Health Record fields (Add)
    @FXML
    private ComboBox<Profile> recordMemberCombo;
    @FXML
    private ComboBox<String> recordTypeCombo;
    @FXML
    private TextArea recordObservationsArea;

    @FXML
    private VBox panePainSymptom, paneWellness, paneObservation;
    @FXML
    private GridPane paneVitals;
    @FXML
    private ComboBox<String> bodyZoneCombo, evolutionCombo;
    @FXML
    private Slider intensitySlider, wellnessSlider;
    @FXML
    private Label intensityValueLabel, wellnessValueLabel;
    @FXML
    private TextField recordDurationField, tempField, weightField, sysField, diaField, heartRateField;

    // Family fields
    @FXML
    private TextField memberFirstNameField;
    @FXML
    private TextField memberLastNameField;
    @FXML
    private ComboBox<String> memberRelationCombo;
    @FXML
    private DatePicker memberBirthDatePicker;
    @FXML
    private ComboBox<String> memberGenderCombo;

    private javafx.stage.Stage dialogStage;

    @FXML
    private Button medicationsButton;

    @FXML
    private Button appointmentsButton;

    @FXML
    private Button healthTrackingButton;

    @FXML
    private Button familyButton;

    @FXML
    private Button prescriptionsButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    /**
     * Définit l'utilisateur connecté et charge ses données
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadDashboardData();

        // Charger les données spécifiques à l'écran actuel
        if (healthHistoryContainer != null) {
            loadHealthTrackingData();
        }
        if (appointmentsContainer != null) {
            loadAppointments();
        }
        if (medicationTable != null) {
            loadMedications();
            loadDailyMedications();
        }
        if (familyFlowPane != null) {
            refreshFamilyMembers();
        }

        // Update summary cards
        updateNextMedicationCard();
        updateNextAppointmentCard();
    }

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        if (medicationTable != null)
            setupTableColumns();

        // Listeners pour les sliders du formulaire santé
        if (intensitySlider != null) {
            intensitySlider.valueProperty().addListener((obs, oldV, newV) -> {
                if (intensityValueLabel != null)
                    intensityValueLabel.setText(String.valueOf(newV.intValue()));
            });
        }
        if (wellnessSlider != null) {
            wellnessSlider.valueProperty().addListener((obs, oldV, newV) -> {
                if (wellnessValueLabel != null)
                    wellnessValueLabel.setText(newV.intValue() + "%");
            });
        }

        // Les données seront chargées une fois que setCurrentUser sera appelé
        // Si on est sur le dashboard, charger les données du dashboard
        if (recentRecordsContainer != null) {
            loadRecentRecords();
        }
    }

    private void setupTableColumns() {

        // Table des médicaments
        if (medicationTable != null && medNameCol != null)

        {
            medNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            medDosageCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
            medFreqCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

            if (medNextCol != null) {
                medNextCol.setCellValueFactory(cellData -> {
                    // Pour l'instant on affiche "Planifié"
                    return new javafx.beans.property.SimpleStringProperty("⏰ Planifié");
                });
            }

            medStatusCol.setCellValueFactory(cellData -> {
                boolean taken = cellData.getValue().isTaken();
                return new javafx.beans.property.SimpleStringProperty(taken ? "✅ Pris" : "⏳ En attente");
            });

            // Boutons d'action pour le tableau des médicaments
            if (medActionCol != null) {
                medActionCol.setCellFactory(param -> new TableCell<>() {
                    private final Button deleteBtn = new Button("🗑");
                    {
                        deleteBtn.setStyle(
                                "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
                        deleteBtn.setOnAction(event -> {
                            Medication med = getTableView().getItems().get(getIndex());
                            if (med != null) {
                                System.out.println("Action: delete clicked for " + med.getName());
                                handleDeleteMedication(med);
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteBtn);
                            setAlignment(javafx.geometry.Pos.CENTER);
                        }
                    }
                });
            }
        }

        // Table des rendez-vous - Retiré car passage au mode Cartes
    }

    /**
     * Charge les données du dashboard
     */
    private void loadDashboardData() {
        if (currentUser == null)
            return;

        // Message de bienvenue professionnel (Intemporel)
        if (welcomeLabel != null) {
            String displayName = "";
            // Recherche systématique du profil pour garantir l'affichage du nom
            List<Profile> profiles = profileRepository.findByUser(currentUser);
            if (!profiles.isEmpty()) {
                Profile main = profiles.stream()
                    .filter(Profile::isMainProfile)
                    .findFirst()
                    .orElse(profiles.get(0));
                displayName = main.getFirstName() + " " + main.getLastName();
            } else {
                displayName = currentUser.getNom();
            }
            welcomeLabel.setText("Bienvenue, " + displayName + " !");
        }

        if (currentDateLabel != null) {
            String dateFormatted = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", java.util.Locale.FRENCH));
            // Capitalize first letter
            dateFormatted = dateFormatted.substring(0, 1).toUpperCase() + dateFormatted.substring(1);
            currentDateLabel.setText(dateFormatted);
        }

        // Charger les statistiques
        loadStatistics();

        // Charger les enregistrements récents
        loadRecentRecords();

        // Charger les médicaments
        loadMedications();

        // Charger les médicaments du jour
        loadDailyMedications();

        // Charger les rendez-vous
        loadAppointments();

        // Charger la famille
        loadFamilyMembers();

        // Charger le graphique
        loadHealthChart();

        // Mettre à jour les cartes de résumé
        updateNextMedicationCard();
        updateNextAppointmentCard();
    }

    /**
     * Charge les médicaments
     */
    private void loadMedications() {
        if (medicationTable == null)
            return;
        List<Medication> medications = medicationRepository.findByUser(currentUser);
        ObservableList<Medication> masterList = FXCollections.observableArrayList(medications);

        if (medSearchField != null) {
            // Utilisation d'une FilteredList pour une recherche locale instantanée
            FilteredList<Medication> filteredData = new FilteredList<>(masterList, p -> true);
            medSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(med -> {
                    if (newValue == null || newValue.isEmpty())
                        return true;
                    String lowerCaseFilter = newValue.toLowerCase().trim();
                    if (med.getName().toLowerCase().contains(lowerCaseFilter))
                        return true;
                    if (med.getDosage() != null && med.getDosage().toLowerCase().contains(lowerCaseFilter))
                        return true;
                    return false;
                });
            });
            medicationTable.setItems(filteredData);
        } else {
            medicationTable.setItems(masterList);
        }
    }

    /**
     * Charge les médicaments du jour
     */
    private void loadDailyMedications() {
        if (dailyMedicationsContainer == null)
            return;
        dailyMedicationsContainer.getChildren().clear();

        List<MedicationIntake> todayIntakes = medicationIntakeRepository
                .findByUserAndDateRange(currentUser,
                        LocalDateTime.now().withHour(0).withMinute(0).withSecond(0),
                        LocalDateTime.now().withHour(23).withMinute(59).withSecond(59));

        if (todayIntakes.isEmpty()) {
            Label noMedLabel = new Label("Aucun médicament prévu pour aujourd'hui.");
            noMedLabel.getStyleClass().add("label-muted");
            dailyMedicationsContainer.getChildren().add(noMedLabel);
        } else {
            for (MedicationIntake intake : todayIntakes) {
                HBox intakeRow = new HBox(12);
                intakeRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                intakeRow.getStyleClass().add("card-small");
                intakeRow.getStyleClass().add("intake-row");
                intakeRow.setPrefWidth(280);

                boolean isTaken = intake.getStatus() == MedicationIntake.Status.TAKEN;

                CheckBox takenCheckbox = new CheckBox();
                takenCheckbox.getStyleClass().add("med-checkbox");
                takenCheckbox.setSelected(isTaken);

                Label timeLabel = new Label(intake.getScheduledDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                timeLabel.getStyleClass().add("text-bold");

                Label medLabel = new Label(
                        intake.getMedication().getName() + " (" + intake.getMedication().getDosage() + ")");
                medLabel.getStyleClass().add("label-muted");

                // Apply "Taken" styles initially
                if (isTaken) {
                    intakeRow.getStyleClass().add("intake-row-taken");
                    timeLabel.getStyleClass().add("intake-time-taken");
                    medLabel.getStyleClass().add("intake-text-taken");
                    medLabel.setStyle("-fx-strikethrough: true;"); // Works if it's a Text node or in some specific
                                                                   // implementations, but safer to use opacity
                }

                takenCheckbox.setOnAction(event -> {
                    if (takenCheckbox.isSelected()) {
                        intake.setStatus(MedicationIntake.Status.TAKEN);
                        intake.setTakenDateTime(LocalDateTime.now());
                    } else {
                        intake.setStatus(MedicationIntake.Status.PENDING);
                        intake.setTakenDateTime(null);
                    }
                    medicationIntakeRepository.save(intake);
                    loadDailyMedications(); // Refresh to apply all visual changes
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                intakeRow.getChildren().addAll(takenCheckbox, timeLabel, medLabel, spacer);
                dailyMedicationsContainer.getChildren().add(intakeRow);
            }
            updateNextMedicationCard();
        }
    }

    private void loadAppointments() {
        if (appointmentsContainer == null)
            return;

        List<Appointment> allAppointments = appointmentRepository.findByUserOrderByDateTimeAsc(currentUser);
        String searchText = (aptSearchField != null) ? aptSearchField.getText() : "";
        LocalDate filterDate = (filterDatePicker != null) ? filterDatePicker.getValue() : null;

        // Filtrage manuel
        List<Appointment> filtered = allAppointments.stream()
                .filter(apt -> {
                    boolean matchesText = true;
                    if (searchText != null && !searchText.trim().isEmpty()) {
                        String lower = searchText.toLowerCase().trim();
                        matchesText = (apt.getDoctorName() != null && apt.getDoctorName().toLowerCase().contains(lower))
                                ||
                                (apt.getSpecialty() != null && apt.getSpecialty().toLowerCase().contains(lower)) ||
                                (apt.getAppointmentType() != null
                                        && apt.getAppointmentType().toLowerCase().contains(lower));
                    }
                    boolean matchesDate = true;
                    if (filterDate != null) {
                        matchesDate = apt.getDateTime() != null && apt.getDateTime().toLocalDate().equals(filterDate);
                    }
                    return matchesText && matchesDate;
                })
                .collect(java.util.stream.Collectors.toList());

        appointmentsContainer.getChildren().clear();

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
            emptyBox.setPadding(new Insets(50, 0, 0, 0));
            Label iconLabel = new Label("📅");
            iconLabel.setStyle("-fx-font-size: 48px;");
            Label msgLabel = new Label("Aucun rendez-vous trouvé");
            msgLabel.getStyleClass().add("label-subtitle");
            emptyBox.getChildren().addAll(iconLabel, msgLabel);
            appointmentsContainer.getChildren().add(emptyBox);
            return;
        }

        // Groupement par date
        LocalDate today = LocalDate.now();
        List<Appointment> listToday = new java.util.ArrayList<>();
        List<Appointment> listThisWeek = new java.util.ArrayList<>();
        List<Appointment> listFuture = new java.util.ArrayList<>();

        for (Appointment apt : filtered) {
            LocalDate date = apt.getDateTime().toLocalDate();
            if (date.equals(today))
                listToday.add(apt);
            else if (date.isAfter(today) && date.isBefore(today.plusDays(7)))
                listThisWeek.add(apt);
            else
                listFuture.add(apt);
        }

        if (!listToday.isEmpty())
            addAppointmentGroup("Aujourd'hui", listToday);
        if (!listThisWeek.isEmpty())
            addAppointmentGroup("Cette semaine", listThisWeek);
        if (!listFuture.isEmpty())
            addAppointmentGroup("À venir", listFuture);
    }

    private void addAppointmentGroup(String title, List<Appointment> appointments) {
        VBox group = new VBox(15);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: #1e293b;");
        group.getChildren().add(titleLabel);

        for (Appointment apt : appointments) {
            HBox card = createAppointmentCard(apt);
            group.getChildren().add(card);
        }
        appointmentsContainer.getChildren().add(group);
    }

    private HBox createAppointmentCard(Appointment apt) {
        HBox card = new HBox(20);
        card.getStyleClass().add("card");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));

        // Colonne Date & Heure
        VBox timeBox = new VBox(2);
        timeBox.setMinWidth(100);
        timeBox.setAlignment(javafx.geometry.Pos.CENTER);
        Label dayLabel = new Label(apt.getDateTime().format(DateTimeFormatter.ofPattern("dd MMM")));
        dayLabel.setStyle("-fx-font-weight: 800; -fx-font-size: 16px; -fx-text-fill: #3b82f6;");
        Label hourLabel = new Label(apt.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        hourLabel.getStyleClass().add("label-muted");
        timeBox.getChildren().addAll(dayLabel, hourLabel);

        // Colonne Informations
        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label doctorLabel = new Label(apt.getDoctorName() + " (" + apt.getSpecialty() + ")");
        doctorLabel.setStyle("-fx-font-weight: 700; -fx-font-size: 15px;");
        Label typeLabel = new Label(apt.getAppointmentType() + " • "
                + (apt.getLocation() != null ? apt.getLocation() : "Lieu non précisé"));
        typeLabel.getStyleClass().add("label-muted");

        // Membre de la famille
        if (apt.getProfile() != null) {
            Label memberLabel = new Label("👤 " + apt.getProfile().getFirstName());
            memberLabel.setStyle(
                    "-fx-font-size: 11px; -fx-background-color: #f1f5f9; -fx-padding: 2 6; -fx-background-radius: 10;");
            infoBox.getChildren().addAll(doctorLabel, typeLabel, memberLabel);
        } else {
            infoBox.getChildren().addAll(doctorLabel, typeLabel);
        }

        // Actions
        Button deleteBtn = new Button("🗑");
        deleteBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 18px; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteAppointment(apt));

        card.getChildren().addAll(timeBox, new Separator(javafx.geometry.Orientation.VERTICAL), infoBox, deleteBtn);
        return card;
    }

    @FXML
    public void handleAptSearch() {
        loadAppointments();
    }

    /**
     * Charge les membres de la famille
     */
    private void loadFamilyMembers() {
        if (familyFlowPane == null)
            return;
        familyFlowPane.getChildren().clear();
        List<Profile> profiles = profileRepository.findByUser(currentUser);

        for (Profile profile : profiles) {
            // Utiliser la même carte que dans l'onglet Famille pour plus de cohérence
            familyFlowPane.getChildren().add(createFamilyMemberCard(profile));
        }
    }

    private VBox createFamilyCard(Profile profile) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPrefWidth(250);
        card.setAlignment(javafx.geometry.Pos.CENTER);

        HBox avatarBox = new HBox();
        avatarBox.setAlignment(javafx.geometry.Pos.CENTER);
        avatarBox.setStyle(
                "-fx-background-color: #f0f9ff; -fx-background-radius: 50; -fx-pref-width: 80; -fx-pref-height: 80;");
        Label avatar = new Label("👤");
        avatar.setStyle("-fx-font-size: 40px;");
        avatarBox.getChildren().add(avatar);

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(javafx.geometry.Pos.CENTER);
        Label nameLabel = new Label(profile.getFirstName() + " " + profile.getLastName());
        nameLabel.setStyle("-fx-font-weight: 700; -fx-font-size: 16px;");
        Label detailLabel = new Label("Membre de la famille");
        detailLabel.getStyleClass().add("label-muted");
        infoBox.getChildren().addAll(nameLabel, detailLabel);

        Button manageBtn = new Button("Gérer la santé");
        manageBtn.getStyleClass().add("btn-outline");
        manageBtn.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(avatarBox, infoBox, manageBtn);
        return card;
    }

    private void loadStatistics() {
        if (healthScoreLabel == null || currentUser == null)
            return;

        // Calculer le Score de santé (moyenne des 30 derniers jours)
        List<HealthRecord> records = healthRecordRepository.findByUserId(currentUser.getId());
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        double averageWellness = records.stream()
                .filter(r -> r.getWellnessScore() != null)
                .filter(r -> !r.getRecordDate().isBefore(thirtyDaysAgo))
                .mapToInt(HealthRecord::getWellnessScore)
                .average()
                .orElse(0.0);

        if (averageWellness == 0.0) {
            healthScoreLabel.setText("-- %");
            if (healthStatusLabel != null)
                healthStatusLabel.setText("Aucune donnée récente");
        } else {
            int score = (int) Math.round(averageWellness);
            healthScoreLabel.setText(score + " %");

            if (healthStatusLabel != null && wellnessCard != null) {
                wellnessCard.getStyleClass().removeAll("summary-orange", "summary-green", "summary-red");

                if (score >= 80) {
                    healthStatusLabel.setText("Excellent état global");
                    wellnessCard.getStyleClass().add("summary-green");
                } else if (score >= 50) {
                    healthStatusLabel.setText("Bon état général");
                    wellnessCard.getStyleClass().add("summary-orange");
                } else {
                    healthStatusLabel.setText("État de fatigue détecté");
                    wellnessCard.getStyleClass().add("summary-red");
                }
            }
        }

        // Nombre total d'enregistrements
        if (totalRecordsLabel != null) {
            totalRecordsLabel.setText(String.valueOf(records.size()));
        }
    }

    private void loadHealthTrackingData() {
        if (healthHistoryContainer == null)
            return;

        // Init des filtres
        if (filterRecordTypeCombo != null && filterRecordTypeCombo.getItems().isEmpty()) {
            filterRecordTypeCombo.setItems(FXCollections.observableArrayList(
                    "Tous les types", "Douleur", "Symptôme", "Paramètre vital", "Bien-être général",
                    "Observation médicale"));
            filterRecordTypeCombo.setValue("Tous les types");
        }
        if (filterMemberCombo != null && filterMemberCombo.getItems().isEmpty()) {
            List<Profile> profiles = profileRepository.findByUser(currentUser);
            filterMemberCombo.setItems(FXCollections.observableArrayList(profiles));

            // Configurer l'affichage pour montrer "Moi" pour le profil principal
            filterMemberCombo.setCellFactory(listView -> new ListCell<Profile>() {
                @Override
                protected void updateItem(Profile item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("👨‍👩‍👧 Toute la famille");
                    } else {
                        setText(item.isMainProfile() ? "👤 Moi (Principal)"
                                : "👤 " + item.getFirstName() + " (" + item.getRelation() + ")");
                    }
                }
            });
            // ButtonCell gère l'affichage quand l'élément est sélectionné
            filterMemberCombo.setButtonCell(new ListCell<Profile>() {
                @Override
                protected void updateItem(Profile item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("👨‍👩‍👧 Toute la famille");
                    } else {
                        setText(item.isMainProfile() ? "👤 Moi" : "👤 " + item.getFirstName());
                    }
                }
            });
        }

        loadHealthCharts();
        loadHealthHistory();
    }

    private void loadHealthCharts() {
        if (wellnessChart == null || painChart == null)
            return;

        wellnessChart.getData().clear();
        painChart.getData().clear();

        XYChart.Series<String, Number> wellnessSeries = new XYChart.Series<>();
        wellnessSeries.setName("Niveau de Bien-être (%)");

        XYChart.Series<String, Number> painSeries = new XYChart.Series<>();
        painSeries.setName("Intensité Douleur/Symptôme");

        List<HealthRecord> all = healthRecordRepository.findByUserId(currentUser.getId());

        // Récupérer les filtres
        Profile memberFilter = (filterMemberCombo != null) ? filterMemberCombo.getValue() : null;
        LocalDate dateValue = (filterRecordDatePicker != null) ? filterRecordDatePicker.getValue() : null;
        LocalDate finalDateFilter = (dateValue != null) ? dateValue : LocalDate.now().minusDays(30);

        // Appliquer les filtres aux données du graphique
        List<HealthRecord> filtered = all.stream()
                .filter(r -> r != null && r.getRecordDate() != null)
                .filter(r -> {
                    if (memberFilter == null)
                        return true;
                    if (r.getProfile() == null)
                        return false;
                    return memberFilter.getId().equals(r.getProfile().getId());
                })
                .filter(r -> !r.getRecordDate().isBefore(finalDateFilter))
                .collect(Collectors.toList());

        // Agréger par jour
        Map<LocalDate, Double> wellnessAvg = filtered.stream()
                .filter(r -> r.getWellnessScore() != null)
                .collect(Collectors.groupingBy(
                        HealthRecord::getRecordDate,
                        Collectors.averagingDouble(HealthRecord::getWellnessScore)));

        Map<LocalDate, Double> painAvg = filtered.stream()
                .filter(r -> r.getIntensity() != null)
                .collect(Collectors.groupingBy(
                        HealthRecord::getRecordDate,
                        Collectors.averagingDouble(HealthRecord::getIntensity)));

        // Ajouter les données triées par date
        wellnessAvg.keySet().stream().sorted().forEach(date -> {
            String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            if (wellnessChart != null)
                wellnessSeries.getData().add(new XYChart.Data<>(label, wellnessAvg.get(date)));
        });

        painAvg.keySet().stream().sorted().forEach(date -> {
            String label = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            if (painChart != null)
                painSeries.getData().add(new XYChart.Data<>(label, painAvg.get(date)));
        });

        if (wellnessChart != null)
            wellnessChart.getData().add(wellnessSeries);
        if (painChart != null)
            painChart.getData().add(painSeries);
    }

    private void loadHealthHistory() {
        if (healthHistoryContainer == null)
            return;
        healthHistoryContainer.getChildren().clear();

        List<HealthRecord> records = healthRecordRepository.findByUserId(currentUser.getId());

        // Appliquer filtres si présents
        String typeFilter = filterRecordTypeCombo != null ? filterRecordTypeCombo.getValue() : "Tous les types";
        Profile memberFilter = filterMemberCombo != null ? filterMemberCombo.getValue() : null;
        LocalDate dateFilter = filterRecordDatePicker != null ? filterRecordDatePicker.getValue() : null;

        records.stream()
                .filter(r -> r != null && r.getRecordDate() != null)
                .filter(r -> "Tous les types".equals(typeFilter) || typeFilter.equals(r.getType()))
                .filter(r -> {
                    if (memberFilter == null)
                        return true;
                    if (r.getProfile() == null)
                        return false;
                    return memberFilter.getId().equals(r.getProfile().getId());
                })
                .filter(r -> dateFilter == null || !r.getRecordDate().isBefore(dateFilter))
                .sorted((a, b) -> b.getRecordDate().compareTo(a.getRecordDate())) // Plus récent en premier
                .forEach(r -> {
                    healthHistoryContainer.getChildren().add(createHealthRecordCard(r));
                });
    }

    private VBox createHealthRecordCard(HealthRecord r) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        String icon = "📝";
        String color = "#64748b";
        if ("Douleur".equals(r.getType())) {
            icon = "⚡";
            color = "#ef4444";
        } else if ("Symptôme".equals(r.getType())) {
            icon = "🤒";
            color = "#f59e0b";
        } else if ("Paramètre vital".equals(r.getType())) {
            icon = "💓";
            color = "#ec4899";
        } else if ("Bien-être général".equals(r.getType())) {
            icon = "😊";
            color = "#10b981";
        }

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 24px;");

        VBox titleBox = new VBox(2);
        Label typeLbl = new Label(r.getType().toUpperCase());
        typeLbl.setStyle("-fx-font-weight: 800; -fx-font-size: 12px; -fx-text-fill: " + color);
        Label dateLbl = new Label(r.getRecordDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLbl.getStyleClass().add("label-muted");
        titleBox.getChildren().addAll(typeLbl, dateLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label memberLbl = new Label("👤 " + (r.getProfile() != null ? r.getProfile().getFirstName() : "Moi"));
        memberLbl.setStyle(
                "-fx-background-color: #f1f5f9; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px;");

        header.getChildren().addAll(iconLbl, titleBox, spacer, memberLbl);

        Label descLbl = new Label(r.getDescription());
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: 600;");

        VBox adviceBox = new VBox(5);
        adviceBox.setStyle(
                "-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 4; -fx-border-radius: 0 5 5 0;");
        Label adviceHeader = new Label("💡 Conseil Santé");
        adviceHeader.setStyle("-fx-font-weight: 800; -fx-font-size: 11px; -fx-text-fill: #3b82f6;");
        Label adviceLbl = new Label(generateHealthAdvice(r));
        adviceLbl.setWrapText(true);
        adviceLbl.setStyle("-fx-font-size: 12px; -fx-italic: true;");
        adviceBox.getChildren().addAll(adviceHeader, adviceLbl);

        card.getChildren().addAll(header, descLbl, adviceBox);
        return card;
    }

    private String generateHealthAdvice(HealthRecord r) {
        if ("Douleur".equals(r.getType())) {
            if (r.getIntensity() >= 7)
                return "Douleur intense signalée. Veuillez consulter un médecin rapidement si elle persiste.";
            if (r.getIntensity() >= 4)
                return "Douleur modérée. Reposez-vous et surveillez l'évolution. Hydratez-vous bien.";
            return "Douleur légère. Un peu de repos devrait aider. Notez si cela revient.";
        }
        if ("Symptôme".equals(r.getType())) {
            if ("Aggravation".equals(r.getEvolution()))
                return "Aggravation notée. Un avis médical est fortement conseillé.";
            return "Continuez de surveiller l'évolution. Si de nouveaux symptômes apparaissent, notez-les.";
        }
        if ("Paramètre vital".equals(r.getType())) {
            if (r.getTemperature() != null && r.getTemperature() > 38.5)
                return "Fièvre élevée détectée. Buvez beaucoup d'eau et consultez si elle ne baisse pas.";
            if (r.getSystolicPressure() != null && r.getSystolicPressure() > 140)
                return "Tension un peu élevée. Évitez le sel et le stress aujourd'hui.";
            return "Paramètres dans la norme. Maintenez vos bonnes habitudes.";
        }
        if ("Bien-être général".equals(r.getType())) {
            if (r.getWellnessScore() < 50)
                return "C'est une période difficile. Prenez du temps pour vous et n'hésitez pas à en parler.";
            return "Excellente forme ! Continuez votre routine bien-être actuelle.";
        }
        return "Enregistrement bien noté dans votre historique médical.";
    }

    @FXML
    private void loadRecentRecords() {
        if (recentRecordsContainer == null || currentUser == null)
            return;

        recentRecordsContainer.getChildren().clear();

        List<HealthRecord> records = healthRecordRepository.findByUserId(currentUser.getId());
        List<HealthRecord> sortedRecords = records.stream()
                .sorted((a, b) -> b.getRecordDate().compareTo(a.getRecordDate()))
                .limit(4) // Limiter à 4 pour un dashboard aéré
                .toList();

        if (sortedRecords.isEmpty()) {
            Label placeholder = new Label("Aucun bilan récent. Prenez soin de vous !");
            placeholder.getStyleClass().add("label-subtitle");
            recentRecordsContainer.getChildren().add(placeholder);
            return;
        }

        for (HealthRecord record : sortedRecords) {
            recentRecordsContainer.getChildren().add(createModernRecordRow(record));
        }
    }

    private javafx.scene.Node createModernRecordRow(HealthRecord record) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: #f8fafc; -fx-background-radius: 12px; -fx-border-color: #f1f5f9; -fx-border-width: 1px; -fx-border-radius: 12px;");

        // Icone basée sur le type
        String icon = "📝";
        String color = "#64748b";
        if ("Douleur".equals(record.getType())) {
            icon = "⚠️";
            color = "#ef4444";
        } else if ("Bien-être général".equals(record.getType())) {
            icon = "❤️";
            color = "#10b981";
        } else if ("Paramètre vital".equals(record.getType())) {
            icon = "📊";
            color = "#3b82f6";
        }

        StackPane iconBox = new StackPane(new Label(icon));
        iconBox.setMinSize(40, 40);
        iconBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        VBox info = new VBox(2);

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(record.getType());
        title.setStyle("-fx-font-weight: 700; -fx-text-fill: -primary-dark; -fx-font-size: 14px;");

        // Membre de la famille concerné
        Label patientLabel = new Label();
        Profile p = record.getProfile();
        String patientName = (p != null) ? p.getNom() : (currentUser != null ? currentUser.getNom() : "");
        patientLabel.setText("•  " + patientName);
        patientLabel.setStyle("-fx-text-fill: -secondary; -fx-font-weight: 600; -fx-font-size: 12px;");

        titleBox.getChildren().addAll(title, patientLabel);

        Label desc = new Label(record.getDescription());
        desc.setStyle("-fx-text-fill: -text-medium; -fx-font-size: 13px;");
        desc.setWrapText(false);
        desc.setMaxWidth(600);

        info.getChildren().addAll(titleBox, desc);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label dateLabel = new Label(
                record.getRecordDate().format(DateTimeFormatter.ofPattern("dd MMM", java.util.Locale.FRENCH)));
        dateLabel.setStyle("-fx-text-fill: -text-light; -fx-font-weight: 600; -fx-font-size: 13px;");

        // Bouton de suppression
        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("btn-delete-small");
        deleteBtn.setOnAction(e -> confirmAndDeleteRecord(record));
        deleteBtn.setVisible(false); // Caché par défaut, visible au survol

        row.getChildren().addAll(iconBox, info, dateLabel, deleteBtn);

        // Effet hover
        row.setOnMouseEntered(e -> {
            row.setStyle(
                    "-fx-background-color: #f1f5f9; -fx-background-radius: 12px; -fx-border-color: -secondary; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-cursor: hand;");
            deleteBtn.setVisible(true);
        });
        row.setOnMouseExited(e -> {
            row.setStyle(
                    "-fx-background-color: #f8fafc; -fx-background-radius: 12px; -fx-border-color: #f1f5f9; -fx-border-width: 1px; -fx-border-radius: 12px;");
            deleteBtn.setVisible(false);
        });

        return row;
    }

    private void confirmAndDeleteRecord(HealthRecord record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cet enregistrement ?");
        alert.setContentText("Cette action est irréversible.");

        // Custom styling for Alert (basic approach for now)
        ButtonType deleteButton = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteButton, cancelButton);

        alert.showAndWait().ifPresent(type -> {
            if (type == deleteButton) {
                healthRecordRepository.delete(record);
                loadRecentRecords();
                loadHealthChart(); // Rafraîchir aussi le graphique
            }
        });
    }

    @FXML
    public void handleFilterRecords() {
        loadHealthCharts();
        loadHealthHistory();
    }

    @FXML
    public void handleResetHealthFilters() {
        if (filterRecordTypeCombo != null)
            filterRecordTypeCombo.setValue("Tous les types");
        if (filterMemberCombo != null)
            filterMemberCombo.setValue(null);
        if (filterRecordDatePicker != null)
            filterRecordDatePicker.setValue(null);
        loadHealthHistory();
    }

    /**
     * Charge le graphique de suivi de santé
     */
    private void loadHealthChart() {
        if (healthChart == null)
            return;

        healthChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Moyenne Bien-être (%)");

        List<HealthRecord> records = healthRecordRepository.findByUserId(currentUser.getId());
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);

        // Agréger par jour (Moyenne) pour une courbe lisse et compréhensible
        Map<LocalDate, Double> stats = records.stream()
                .filter(r -> r.getWellnessScore() != null)
                .filter(r -> !r.getRecordDate().isBefore(tenDaysAgo))
                .collect(Collectors.groupingBy(
                        HealthRecord::getRecordDate,
                        Collectors.averagingDouble(HealthRecord::getWellnessScore)));

        if (!stats.isEmpty()) {
            stats.keySet().stream().sorted().forEach(date -> {
                String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                series.getData().add(new XYChart.Data<>(dateStr, stats.get(date)));
            });
            healthChart.getData().add(series);
        }
    }

    @FXML
    public void handleMedications() {
        navigateToScreen("/fxml/medications.fxml");
    }

    @FXML
    public void handleAppointments() {
        navigateToScreen("/fxml/appointments.fxml");
    }

    @FXML
    public void handleFamily() {
        navigateToScreen("/fxml/family.fxml");
    }

    @FXML
    public void handleDashboard() {
        navigateToScreen("/fxml/dashboard.fxml");
    }

    /**
     * Navigation vers le suivi de santé
     */
    @FXML
    public void handleHealthTracking() {
        navigateToScreen("/fxml/health-tracking.fxml");
    }

    /**
     * Navigation vers les prescriptions
     */
    @FXML
    public void handlePrescriptions() {
        navigateToScreen("/fxml/ordonnance.fxml");
    }

    /**
     * Navigation vers le profil
     */
    @FXML
    public void handleProfile() {
        navigateToScreen("/fxml/profile.fxml");
    }

    /**
     * Déconnexion
     */
    @FXML
    public void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous devrez vous reconnecter pour accéder à votre compte.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                navigateToScreen("/fxml/login.fxml");
            }
        });
    }

    /**
     * Méthode utilitaire pour naviguer vers un autre écran
     */
    private void navigateToScreen(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(SpringContext.getContext()::getBean);
            Parent root = loader.load();

            // S'assurer que le nouveau contrôleur reçoit l'utilisateur actuel
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setCurrentUser(currentUser);
            } else if (controller instanceof OrdonnanceController) {
                ((OrdonnanceController) controller).setCurrentUser(currentUser);
            }

            Scene tempScene = null;
            if (logoutButton != null && logoutButton.getScene() != null) {
                tempScene = logoutButton.getScene();
            } else if (mainContainer != null && mainContainer.getScene() != null) {
                tempScene = mainContainer.getScene();
            } else if (welcomeLabel != null && welcomeLabel.getScene() != null) {
                tempScene = welcomeLabel.getScene();
            }

            final Scene scene = tempScene;
            if (scene == null) {
                throw new IllegalStateException("Impossible de trouver la scène actuelle pour la navigation.");
            }

            // Animation de transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), scene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                scene.setRoot(root);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'écran : " + fxmlPath);
        }
    }

    @FXML
    public void handleAddMedication() {
        showDialog("/fxml/add-medication.fxml", "Nouveau Médicament", controller -> {
            if (controller.medMemberCombo != null) {
                List<Profile> profiles = profileRepository.findByUser(currentUser);
                controller.medMemberCombo.setItems(FXCollections.observableArrayList(profiles));

                // Sélectionner le profil principal par défaut
                Profile main = profiles.stream().filter(Profile::isMainProfile).findFirst()
                        .orElse(profiles.isEmpty() ? null : profiles.get(0));
                controller.medMemberCombo.setValue(main);

                // Style d'affichage des noms
                controller.medMemberCombo.setCellFactory(lv -> new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile p, boolean empty) {
                        super.updateItem(p, empty);
                        if (empty || p == null)
                            setText(null);
                        else
                            setText(p.isMainProfile() ? "👤 Moi (Principal)"
                                    : "👤 " + p.getFirstName() + " (" + p.getRelation() + ")");
                    }
                });
                controller.medMemberCombo.setButtonCell(new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile p, boolean empty) {
                        super.updateItem(p, empty);
                        if (empty || p == null)
                            setText(null);
                        else
                            setText(p.isMainProfile() ? "👤 Moi" : "👤 " + p.getFirstName());
                    }
                });
            }
            if (controller.medFrequencyCombo != null) {
                controller.medFrequencyCombo.setItems(FXCollections.observableArrayList(
                        "1 fois par jour", "2 fois par jour", "3 fois par jour", "4 fois par jour"));
                controller.medFrequencyCombo.setValue("1 fois par jour");
            }
            if (controller.medStartDatePicker != null) {
                controller.medStartDatePicker.setValue(LocalDate.now());
            }
        });
        loadMedications();
        loadDailyMedications();
    }

    @FXML
    public void handleMedSearch() {
        // La recherche est maintenant gérée par le listener dans loadMedications
        // (FilteredList)
        // Mais on garde la méthode pour l'événement FXML si nécessaire
        System.out.println("Recherche déclenchée: " + (medSearchField != null ? medSearchField.getText() : "null"));
    }

    private void handleDeleteMedication(Medication med) {
        System.out.println("handleDeleteMedication called for: " + med.getName());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression");
        alert.setHeaderText("Supprimer le médicament : " + med.getName());
        alert.setContentText("Êtes-vous sûr ? Cela supprimera également tous les rappels associés.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            medicationRepository.delete(med);
            loadMedications();
            loadDailyMedications();
        }
    }

    @FXML
    public void handleFrequencyChange() {
        if (medFrequencyCombo == null || intakeTimesContainer == null)
            return;
        intakeTimesContainer.getChildren().clear();
        intakePlaceholderLabel.setVisible(false);
        intakePlaceholderLabel.setManaged(false);

        String freq = medFrequencyCombo.getValue();
        int count = 0;
        String[] defaultTimes = {};

        if ("1 fois par jour".equals(freq)) {
            count = 1;
            defaultTimes = new String[] { "08:00" };
        } else if ("2 fois par jour".equals(freq)) {
            count = 2;
            defaultTimes = new String[] { "08:00", "20:00" };
        } else if ("3 fois par jour".equals(freq)) {
            count = 3;
            defaultTimes = new String[] { "08:00", "14:00", "20:00" };
        } else if ("4 fois par jour".equals(freq)) {
            count = 4;
            defaultTimes = new String[] { "08:00", "12:00", "18:00", "22:00" };
        }

        for (int i = 0; i < count; i++) {
            HBox row = new HBox(15);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label lbl = new Label("Prise " + (i + 1) + " :");
            lbl.setPrefWidth(80);

            ComboBox<String> hourCombo = new ComboBox<>(FXCollections.observableArrayList(
                    "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21",
                    "22", "23"));
            ComboBox<String> minCombo = new ComboBox<>(FXCollections.observableArrayList("00", "15", "30", "45"));

            String[] parts = defaultTimes[i].split(":");
            hourCombo.setValue(parts[0]);
            minCombo.setValue(parts[1]);

            row.getChildren().addAll(lbl, hourCombo, new Label(":"), minCombo);
            intakeTimesContainer.getChildren().add(row);
        }
    }

    @FXML
    public void handleSaveMedication() {
        if (medNameField == null || medNameField.getText().isEmpty()) {
            showError("Le nom du médicament est obligatoire.");
            return;
        }

        Medication med = new Medication();
        med.setName(medNameField.getText());
        med.setDosage(medDosageField.getText());
        med.setFrequency(medFrequencyCombo.getValue());
        med.setNotes(medNotesArea.getText());
        med.setUser(currentUser);
        med.setProfile(medMemberCombo.getValue()); // Association au membre de la famille
        med.setStartDate(medStartDatePicker.getValue());

        int durationDays = 7;
        try {
            durationDays = Integer.parseInt(medDurationDaysField.getText());
        } catch (Exception e) {
        }
        med.setEndDate(med.getStartDate().plusDays(durationDays));

        // Collect times
        List<LocalTime> times = new ArrayList<>();
        for (javafx.scene.Node node : intakeTimesContainer.getChildren()) {
            if (node instanceof HBox row) {
                @SuppressWarnings("unchecked")
                ComboBox<String> h = (ComboBox<String>) row.getChildren().get(1);
                @SuppressWarnings("unchecked")
                ComboBox<String> m = (ComboBox<String>) row.getChildren().get(3);
                times.add(LocalTime.of(Integer.parseInt(h.getValue()), Integer.parseInt(m.getValue())));
            }
        }
        med.setIntakeTimes(times);
        medicationRepository.save(med);

        // Generate Intakes
        for (int d = 0; d < durationDays; d++) {
            LocalDate date = med.getStartDate().plusDays(d);
            for (LocalTime time : times) {
                com.senegalsante.model.MedicationIntake intake = new com.senegalsante.model.MedicationIntake(med,
                        LocalDateTime.of(date, time));
                medicationIntakeRepository.save(intake);
            }
        }

        if (dialogStage != null)
            dialogStage.close();
    }

    public void setDialogStage(javafx.stage.Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleCancelDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    // --- APPOINTMENTS HANDLERS ---
    @FXML
    public void handleAddAppointment() {
        showDialog("/fxml/add-appointment.fxml", "Nouveau Rendez-vous", controller -> {
            // Initialisation du type de rendez-vous
            if (controller.aptTypeCombo != null) {
                controller.aptTypeCombo.setItems(FXCollections.observableArrayList(
                        "Consultation", "Examen", "Contrôle", "Vaccination", "Autre"));
            }

            // Initialisation des listes d'heures et minutes
            if (controller.aptHourCombo != null) {
                List<String> hours = new java.util.ArrayList<>();
                for (int i = 0; i < 24; i++)
                    hours.add(String.format("%02d", i));
                controller.aptHourCombo.setItems(FXCollections.observableArrayList(hours));
                controller.aptHourCombo.setValue("09");
            }
            if (controller.aptMinuteCombo != null) {
                List<String> minutes = new java.util.ArrayList<>();
                for (int i = 0; i < 60; i += 5)
                    minutes.add(String.format("%02d", i));
                controller.aptMinuteCombo.setItems(FXCollections.observableArrayList(minutes));
                controller.aptMinuteCombo.setValue("00");
            }

            // Initialisation de la liste des membres (Profils)
            if (controller.aptMemberCombo != null) {
                List<Profile> profiles = profileRepository.findByUser(currentUser);
                controller.aptMemberCombo.setItems(FXCollections.observableArrayList(profiles));

                // Formater l'affichage pour montrer "Prénom Nom (Relation)"
                controller.aptMemberCombo.setCellFactory(lv -> new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            String rel = item.isMainProfile() ? "Moi" : item.getRelation();
                            setText(item.getFirstName() + " " + item.getLastName() + " (" + rel + ")");
                        }
                    }
                });
                controller.aptMemberCombo.setButtonCell(controller.aptMemberCombo.getCellFactory().call(null));

                if (!profiles.isEmpty()) {
                    // Chercher le profil principal par défaut
                    Profile main = profiles.stream().filter(Profile::isMainProfile).findFirst().orElse(profiles.get(0));
                    controller.aptMemberCombo.setValue(main);
                }
            }

            if (controller.aptDatePicker != null) {
                controller.aptDatePicker.setValue(LocalDate.now());
            }
        });
        loadAppointments();
        loadDashboardData(); // Pour mettre à jour le sommaire "Prochain RDV"
    }

    private void handleDeleteAppointment(Appointment apt) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de rendez-vous");
        alert.setHeaderText("Supprimer le rendez-vous chez " + apt.getDoctorName() + " ?");
        alert.setContentText("Cette action est irréversible et supprimera les rappels associés.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            appointmentRepository.delete(apt);
            loadAppointments();
            loadDashboardData();
        }
    }

    @FXML
    public void handleSaveAppointment() {
        if (aptDoctorField == null || aptDoctorField.getText().isEmpty()) {
            showError("Le nom du médecin est obligatoire.");
            return;
        }
        if (aptDatePicker == null || aptDatePicker.getValue() == null) {
            showError("La date du rendez-vous est obligatoire.");
            return;
        }

        Appointment apt = new Appointment();
        apt.setAppointmentType(aptTypeCombo != null ? aptTypeCombo.getValue() : "Consultation");
        apt.setDoctorName(aptDoctorField.getText());
        apt.setSpecialty(aptSpecialtyField.getText());
        apt.setLocation(aptLocationField.getText());
        apt.setNotes(aptNotesArea != null ? aptNotesArea.getText() : "");
        apt.setStatus(Appointment.Status.SCHEDULED);
        apt.setUser(currentUser);

        // Associer au membre de la famille sélectionné
        if (aptMemberCombo != null && aptMemberCombo.getValue() != null) {
            apt.setProfile(aptMemberCombo.getValue());
        }

        // Construire la date et l'heure
        int hour = 9;
        int minute = 0;
        if (aptHourCombo != null && aptHourCombo.getValue() != null)
            hour = Integer.parseInt(aptHourCombo.getValue());
        if (aptMinuteCombo != null && aptMinuteCombo.getValue() != null)
            minute = Integer.parseInt(aptMinuteCombo.getValue());

        apt.setDateTime(LocalDateTime.of(aptDatePicker.getValue(), LocalTime.of(hour, minute)));

        appointmentRepository.save(apt);
        if (dialogStage != null)
            dialogStage.close();
    }

    // --- HEALTH RECORD HANDLERS ---
    @FXML
    public void handleAddHealthRecord() {
        showDialog("/fxml/add-health-record.fxml", "Nouvel Enregistrement", controller -> {
            // Initialisation des listes
            if (controller.recordMemberCombo != null) {
                List<Profile> profiles = profileRepository.findByUser(currentUser);
                controller.recordMemberCombo.setItems(FXCollections.observableArrayList(profiles));
                Profile main = profiles.stream().filter(Profile::isMainProfile).findFirst().orElse(profiles.get(0));
                controller.recordMemberCombo.setValue(main);

                // Amélioration de l'affichage
                controller.recordMemberCombo.setCellFactory(listView -> new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.isMainProfile() ? "👤 Moi (Principal)"
                                    : "👤 " + item.getFirstName() + " (" + item.getRelation() + ")");
                        }
                    }
                });
                controller.recordMemberCombo.setButtonCell(new ListCell<Profile>() {
                    @Override
                    protected void updateItem(Profile item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.isMainProfile() ? "👤 Moi" : "👤 " + item.getFirstName());
                        }
                    }
                });
            }
            if (controller.recordTypeCombo != null) {
                controller.recordTypeCombo.setItems(FXCollections.observableArrayList(
                        "Douleur", "Symptôme", "Paramètre vital", "Bien-être général", "Observation médicale"));
                controller.recordTypeCombo.setValue("Observation médicale");
            }
            if (controller.bodyZoneCombo != null) {
                controller.bodyZoneCombo.setItems(FXCollections.observableArrayList(
                        "Tête", "Cou", "Poitrine", "Ventre", "Dos", "Bras", "Jambe", "Autre"));
            }
            if (controller.evolutionCombo != null) {
                controller.evolutionCombo.setItems(FXCollections.observableArrayList(
                        "Stable", "Amélioration", "Aggravation"));
                controller.evolutionCombo.setValue("Stable");
            }
        });
        loadHealthTrackingData();
        loadStatistics();
    }

    @FXML
    public void handleHealthRecordTypeChange() {
        if (recordTypeCombo == null)
            return;
        String selection = recordTypeCombo.getValue();

        // Cacher tous les panneaux
        if (panePainSymptom != null) {
            panePainSymptom.setVisible(false);
            panePainSymptom.setManaged(false);
        }
        if (paneVitals != null) {
            paneVitals.setVisible(false);
            paneVitals.setManaged(false);
        }
        if (paneWellness != null) {
            paneWellness.setVisible(false);
            paneWellness.setManaged(false);
        }
        if (paneObservation != null) {
            paneObservation.setVisible(false);
            paneObservation.setManaged(false);
        }

        // Afficher le bon panneau
        if ("Douleur".equals(selection) || "Symptôme".equals(selection)) {
            panePainSymptom.setVisible(true);
            panePainSymptom.setManaged(true);
        } else if ("Paramètre vital".equals(selection)) {
            paneVitals.setVisible(true);
            paneVitals.setManaged(true);
        } else if ("Bien-être général".equals(selection)) {
            paneWellness.setVisible(true);
            paneWellness.setManaged(true);
        } else {
            paneObservation.setVisible(true);
            paneObservation.setManaged(true);
        }
    }

    @FXML
    public void handleSaveHealthRecord() {
        if (recordTypeCombo == null || recordTypeCombo.getValue() == null) {
            showError("Le type d'enregistrement est obligatoire.");
            return;
        }

        HealthRecord record = new HealthRecord();
        record.setType(recordTypeCombo.getValue());
        record.setRecordDate(LocalDate.now());
        record.setUser(currentUser);
        record.setProfile(recordMemberCombo != null ? recordMemberCombo.getValue() : null);
        record.setNotes(recordObservationsArea != null ? recordObservationsArea.getText() : "");

        String typeSelection = recordTypeCombo.getValue();
        if ("Douleur".equals(typeSelection) || "Symptôme".equals(typeSelection)) {
            record.setBodyZone(bodyZoneCombo != null ? bodyZoneCombo.getValue() : "Non précisé");
            record.setIntensity(intensitySlider != null ? (int) intensitySlider.getValue() : 0);
            record.setEvolution(evolutionCombo != null ? evolutionCombo.getValue() : "Stable");
            record.setDuration(recordDurationField != null ? recordDurationField.getText() : "");
        } else if ("Paramètre vital".equals(typeSelection)) {
            try {
                if (tempField != null && !tempField.getText().isEmpty())
                    record.setTemperature(Double.parseDouble(tempField.getText()));
                if (weightField != null && !weightField.getText().isEmpty())
                    record.setWeight(Double.parseDouble(weightField.getText()));
                if (sysField != null && !sysField.getText().isEmpty())
                    record.setSystolicPressure(Integer.parseInt(sysField.getText()));
                if (diaField != null && !diaField.getText().isEmpty())
                    record.setDiastolicPressure(Integer.parseInt(diaField.getText()));
                if (heartRateField != null && !heartRateField.getText().isEmpty())
                    record.setHeartRate(Integer.parseInt(heartRateField.getText()));
            } catch (Exception e) {
            }
        } else if ("Bien-être général".equals(typeSelection)) {
            record.setWellnessScore(wellnessSlider != null ? (int) wellnessSlider.getValue() : 80);
        }

        healthRecordRepository.save(record);
        if (dialogStage != null)
            dialogStage.close();
    }

    // --- FAMILY HANDLERS ---
    @FXML
    public void handleAddFamilyMember() {
        showDialog("/fxml/add-family-member.fxml", "Nouveau Membre", controller -> {
            if (controller.memberRelationCombo != null) {
                controller.memberRelationCombo.setItems(FXCollections.observableArrayList(
                        "Conjoint(e)", "Enfant", "Parent", "Frère/Sœur", "Autre"));
            }
            if (controller.memberGenderCombo != null) {
                controller.memberGenderCombo.setItems(FXCollections.observableArrayList(
                        "Masculin", "Féminin"));
            }
        });
        refreshFamilyMembers();
    }

    private void refreshFamilyMembers() {
        if (familyFlowPane == null)
            return;
        familyFlowPane.getChildren().clear();

        List<Profile> profiles = profileRepository.findByUser(currentUser);
        for (Profile profile : profiles) {
            familyFlowPane.getChildren().add(createFamilyMemberCard(profile));
        }
    }

    private VBox createFamilyMemberCard(Profile profile) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(240);

        // Header: Icon + Name
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(profile.isMainProfile() ? "👑" : "👤");
        iconLabel.setStyle(
                "-fx-font-size: 28px; -fx-padding: 5; -fx-background-color: #f1f5f9; -fx-background-radius: 50;");

        VBox nameBox = new VBox(2);
        Label nameLabel = new Label(profile.getFirstName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label relationLabel = new Label(profile.isMainProfile() ? "Moi" : profile.getRelation());
        relationLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        nameBox.getChildren().addAll(nameLabel, relationLabel);

        header.getChildren().addAll(iconLabel, nameBox);

        // Informations Vitales
        VBox vitalsBox = new VBox(5);
        vitalsBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 8; -fx-background-radius: 6;");

        // Calculer l'âge
        LocalDate birthDate = profile.getBirthDate();
        int age = (birthDate != null) ? java.time.Period.between(birthDate, LocalDate.now()).getYears() : 0;
        Label ageLabel = new Label("🎂 " + age + " ans (" + profile.getGender() + ")");
        ageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -text-medium;");

        Label bloodLabel = new Label(
                "🩸 Groupe: " + (profile.getBloodGroup() != null ? profile.getBloodGroup() : "Non précisé"));
        bloodLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #b91c1c; -fx-font-weight: 600;");

        vitalsBox.getChildren().addAll(ageLabel, bloodLabel);

        // Allergies
        Label allergyLabel = new Label("⚠️ Allergies: "
                + (profile.getAllergies() != null && !profile.getAllergies().isEmpty() ? profile.getAllergies()
                        : "Aucune"));
        allergyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9a3412; -fx-wrap-text: true;");
        allergyLabel.setMaxWidth(210);

        // Data: Medications
        List<Medication> meds = medicationRepository.findByProfile(profile);

        long activeMeds = meds.stream()
                .filter(m -> m.getEndDate() == null || m.getEndDate().isAfter(LocalDate.now()))
                .count();

        Label medsLabel = new Label("💊 " + activeMeds + " traitement(s) en cours");
        medsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2563eb; -fx-font-weight: bold;");

        // Data: Next Appointment
        List<Appointment> nextAppts = appointmentRepository.findByProfileAndDateTimeAfterOrderByDateTimeAsc(profile,
                LocalDateTime.now());

        Label apptLabel;
        if (!nextAppts.isEmpty()) {
            Appointment next = nextAppts.get(0);
            String dateStr = next.getDateTime()
                    .format(DateTimeFormatter.ofPattern("dd MMM à HH:mm", java.util.Locale.FRENCH));
            apptLabel = new Label("📅 RDV: " + dateStr);
            apptLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #059669; -fx-font-weight: bold;");
        } else {
            apptLabel = new Label("📅 Aucun RDV prévu");
            apptLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #9ca3af;");
        }

        // Dernier bilan de santé
        Label healthLabel = new Label("Pas de bilan récent");
        healthLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: -text-light;");

        List<HealthRecord> records = healthRecordRepository.findByProfileOrderByRecordDateDesc(profile);
        if (!records.isEmpty()) {
            HealthRecord last = records.get(0);
            if (last.getWellnessScore() != null) {
                int score = last.getWellnessScore();
                healthLabel.setText("Dernier bilan: " + score + "%");
                if (score >= 80)
                    healthLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #059669; -fx-font-weight: bold;");
                else if (score >= 50)
                    healthLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #d97706; -fx-font-weight: bold;");
                else
                    healthLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
            }
        }

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(5, 0, 0, 0));

        if (!profile.isMainProfile()) {
            Button deleteBtn = new Button("🗑");
            deleteBtn.setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-cursor: hand;");
            deleteBtn.setOnMouseClicked(e -> {
                e.consume(); // Empêcher l'ouverture de la fiche
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Suppression");
                confirm.setHeaderText("Supprimer " + profile.getFirstName() + " ?");
                confirm.setContentText("Cette action supprimera également tout l'historique associé.");

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        profileRepository.delete(profile);
                        refreshFamilyMembers();
                    }
                });
            });
            footer.getChildren().add(deleteBtn);
        }

        card.getChildren().addAll(header, vitalsBox, allergyLabel, new Separator(), medsLabel, apptLabel, healthLabel,
                footer);

        card.setOnMouseClicked(e -> showFamilyMemberDetails(profile));

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 4); -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"));

        return card;
    }

    private void showFamilyMemberDetails(Profile profile) {
        // Pour l'instant on affiche juste une alerte, plus tard une vue détaillée
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de " + profile.getFirstName());
        alert.setHeaderText("Fiche de " + profile.getFirstName());
        alert.setContentText("Fonctionnalité de vue détaillée à venir !");
        alert.show();
    }

    @FXML
    public void handleSaveFamilyMember() {
        if (memberFirstNameField == null || memberFirstNameField.getText().isEmpty()) {
            showError("Le prénom est obligatoire.");
            return;
        }

        Profile profile = new Profile();
        profile.setFirstName(memberFirstNameField.getText());
        profile.setLastName(memberLastNameField != null ? memberLastNameField.getText() : "");
        profile.setUser(currentUser);

        if (memberBirthDatePicker != null && memberBirthDatePicker.getValue() != null) {
            profile.setBirthDate(memberBirthDatePicker.getValue());
        } else {
            // Par défaut si non renseigné (ou gérer l'erreur)
            profile.setBirthDate(LocalDate.now());
        }

        if (memberGenderCombo != null && memberGenderCombo.getValue() != null) {
            profile.setGender(memberGenderCombo.getValue());
        } else {
            profile.setGender("Non précisé");
        }

        if (memberRelationCombo != null && memberRelationCombo.getValue() != null) {
            profile.setRelation(memberRelationCombo.getValue());
        } else {
            profile.setRelation("Autre");
        }

        profileRepository.save(profile);

        // Rafraîchir l'affichage si on est sur l'écran famille
        if (familyFlowPane != null && familyFlowPane.getScene() != null) {
            loadFamilyMembers();
        }

        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    // --- UTILS ---
    private void showDialog(String fxmlPath, String title,
            java.util.function.Consumer<DashboardController> initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(SpringContext.getContext()::getBean);
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle(title);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            controller.setDialogStage(stage);
            initializer.accept(controller);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du dialogue.");
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Met à jour la carte "Prochaine Prise" sur le dashboard
     * Affiche le prochain médicament à prendre et l'heure prévue
     */
    private void updateNextMedicationCard() {
        if (currentUser == null || nextMedNameLabel == null || nextMedTimeLabel == null)
            return;

        // Récupérer les prises de médicaments prévues pour aujourd'hui et demain
        LocalDateTime startOfToday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfTomorrow = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59).withSecond(59);

        List<MedicationIntake> upcomingIntakes = medicationIntakeRepository
                .findByUserAndDateRange(currentUser, startOfToday, endOfTomorrow);

        // Filtrer pour trouver la PRIORITÉ : soit le plus vieux en retard, soit le
        // prochain futur
        MedicationIntake nextIntake = upcomingIntakes.stream()
                .filter(intake -> intake.getStatus() == MedicationIntake.Status.PENDING)
                // On prend le premier chronologiquement (le plus ancien en attente)
                .min((a, b) -> a.getScheduledDateTime().compareTo(b.getScheduledDateTime()))
                .orElse(null);

        if (nextIntake != null) {
            // Afficher le nom du médicament
            nextMedNameLabel.setText(nextIntake.getMedication().getName());

            // Afficher le patient concerné
            if (nextMedPatientLabel != null) {
                Profile p = nextIntake.getMedication().getProfile();
                String patientName = (p != null) ? p.getNom() : currentUser.getNom();
                nextMedPatientLabel.setText("Pour: " + patientName);
            }

            // Formater l'heure
            LocalDateTime scheduleTime = nextIntake.getScheduledDateTime();
            LocalDate today = LocalDate.now();
            LocalDate scheduleDate = scheduleTime.toLocalDate();

            String timeText;
            if (scheduleTime.isBefore(LocalDateTime.now())) {
                timeText = "EN RETARD (depuis " + scheduleTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ")";
                nextMedTimeLabel.setStyle("-fx-text-fill: #feb2b2; -fx-font-weight: 800;");
            } else if (scheduleDate.equals(today)) {
                timeText = "Aujourd'hui à " + scheduleTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                nextMedTimeLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
            } else {
                timeText = "Demain à " + scheduleTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                nextMedTimeLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
            }

            nextMedTimeLabel.setText(timeText);
        } else {
            nextMedNameLabel.setText("Aucune prise");
            if (nextMedPatientLabel != null)
                nextMedPatientLabel.setText("");
            nextMedTimeLabel.setText("Vous êtes à jour !");
        }
    }

    /**
     * Met à jour la carte "Prochain Rendez-vous" sur le dashboard
     * Affiche le prochain rendez-vous médical prévu
     */
    private void updateNextAppointmentCard() {
        if (currentUser == null || nextAppointmentLabel == null || nextAppointmentDateLabel == null)
            return;

        // Récupérer tous les rendez-vous triés par date
        List<Appointment> allAppointments = appointmentRepository
                .findByUserOrderByDateTimeAsc(currentUser);

        // Filtrer pour trouver la PRIORITÉ : soit le plus ancien en retard, soit le
        // prochain futur (Statut SCHEDULED)
        Appointment nextAppointment = allAppointments.stream()
                .filter(apt -> apt.getStatus() == Appointment.Status.SCHEDULED)
                .findFirst()
                .orElse(null);

        if (nextAppointment != null) {
            // Afficher le médecin et la spécialité
            String doctorInfo = nextAppointment.getDoctorName();
            if (nextAppointment.getSpecialty() != null && !nextAppointment.getSpecialty().isEmpty()) {
                doctorInfo += " (" + nextAppointment.getSpecialty() + ")";
            }
            nextAppointmentLabel.setText(doctorInfo);

            // Afficher le patient concerné
            if (nextAppointmentPatientLabel != null) {
                Profile p = nextAppointment.getProfile();
                String patientName = (p != null) ? p.getNom() : currentUser.getNom();
                nextAppointmentPatientLabel.setText("Patient: " + patientName);
            }

            // Formater la date et l'heure
            LocalDateTime aptTime = nextAppointment.getDateTime();
            LocalDate today = LocalDate.now();
            LocalDate aptDate = aptTime.toLocalDate();

            String dateText;
            if (aptTime.isBefore(LocalDateTime.now())) {
                dateText = "EN RETARD (était prévu à " + aptTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ")";
                nextAppointmentDateLabel.setStyle("-fx-text-fill: #feb2b2; -fx-font-weight: 800;");
            } else if (aptDate.equals(today)) {
                dateText = "Aujourd'hui à " + aptTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                nextAppointmentDateLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
            } else {
                dateText = aptTime.format(DateTimeFormatter.ofPattern("dd/MM à HH:mm"));
                nextAppointmentDateLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9);");
            }

            nextAppointmentDateLabel.setText(dateText);
        } else {
            nextAppointmentLabel.setText("Aucun RDV");
            if (nextAppointmentPatientLabel != null)
                nextAppointmentPatientLabel.setText("");
            nextAppointmentDateLabel.setText("Planifiez votre prochain RDV");
        }
    }
}

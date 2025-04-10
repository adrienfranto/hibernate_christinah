package presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import dao.PatientDao;
import model.Patient;

public class PatientView extends JPanel {
    private JTable table;
    private JTextField searchField;
    private PatientDao patientDao;
    private JLabel notificationLabel;
    private Timer notificationTimer;
    private Color successColor = new Color(76, 175, 80);
    private Color errorColor = new Color(244, 67, 54);
    private Color warningColor = new Color(255, 152, 0);
    private Font boldFont = new Font("Arial", Font.BOLD, 14);
    
    // Liste des sexes disponibles
    private String[] sexes = {
        "Masculin", 
        "Feminin"
    };

    public PatientView() {
        patientDao = new PatientDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Titre de la page
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(33, 150, 243));
        JLabel titleLabel = new JLabel("GESTION DES PATIENTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Panel de notification
        notificationLabel = new JLabel();
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        notificationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        notificationLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        notificationLabel.setVisible(false);
        titlePanel.add(notificationLabel, BorderLayout.EAST);

        // Panel pour les boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JButton addButton = createStyledButton("Ajouter Patient", new Color(76, 175, 80));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPatientModal(null);
            }
        });
        actionPanel.add(addButton);

        JButton updateButton = createStyledButton("Modifier Patient", new Color(33, 150, 243));
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int patientId = (int) table.getValueAt(selectedRow, 0);
                    String codePat = table.getValueAt(selectedRow, 1).toString();
                    String nom = table.getValueAt(selectedRow, 2).toString();
                    String prenom = table.getValueAt(selectedRow, 3).toString();
                    String sexe = table.getValueAt(selectedRow, 4).toString();
                    
                    Patient patient = new Patient(patientId, codePat, nom, prenom, sexe);
                    openPatientModal(patient);
                } else {
                    showNotification("Veuillez sélectionner un patient à modifier", warningColor);
                }
            }
        });
        actionPanel.add(updateButton);

        JButton deleteButton = createStyledButton("Supprimer", new Color(244, 67, 54));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePatient();
            }
        });
        actionPanel.add(deleteButton);

        // Panel pour la recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel searchLabel = new JLabel("Recherche (Code ou Nom):");
        searchLabel.setFont(boldFont);
        searchPanel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searchField);

        JButton searchButton = createStyledButton("Rechercher", new Color(33, 150, 243));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPatients();
            }
        });
        searchPanel.add(searchButton);

        JButton refreshButton = createStyledButton("Actualiser", new Color(0, 121, 107));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                loadPatientData();
            }
        });
        searchPanel.add(refreshButton);

        // Tableau pour afficher les patients
        table = new JTable();
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(33, 150, 243, 100));
        table.setGridColor(new Color(200, 200, 200));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(boldFont);
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(100, 40));

        // Ajouter un listener pour double-clic sur une ligne pour modifier
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        int patientId = (int) table.getValueAt(selectedRow, 0);
                        String codePat = table.getValueAt(selectedRow, 1).toString();
                        String nom = table.getValueAt(selectedRow, 2).toString();
                        String prenom = table.getValueAt(selectedRow, 3).toString();
                        String sexe = table.getValueAt(selectedRow, 4).toString();
                        
                        Patient patient = new Patient(patientId, codePat, nom, prenom, sexe);
                        openPatientModal(patient);
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), 
                        "Liste des patients", TitledBorder.CENTER, TitledBorder.TOP, boldFont),
                new EmptyBorder(10, 10, 10, 10)));

        // Créer un panel principal pour organiser les éléments
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 10, 10, 10);

        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 0.0;
        mainPanel.add(actionPanel, mainGbc);

        mainGbc.gridx = 0;
        mainGbc.gridy = 1;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 0.0;
        mainPanel.add(searchPanel, mainGbc);

        mainGbc.gridx = 0;
        mainGbc.gridy = 2;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainPanel.add(tableScrollPane, mainGbc);

        // Ajouter les panels à la vue principale
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Charger la liste des patients au démarrage
        loadPatientData();
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(boldFont);
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void openPatientModal(Patient patient) {
        // Créer une fenêtre modale pour ajouter/modifier un patient
        JDialog dialog = new JDialog();
        dialog.setTitle(patient == null ? "Ajouter un patient" : "Modifier un patient");
        dialog.setModal(true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Titre du formulaire
        JLabel formTitle = new JLabel(patient == null ? "Ajouter un nouveau patient" : "Modifier les informations du patient");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        // Code Patient
        JLabel codePatLabel = new JLabel("Code Patient:");
        codePatLabel.setFont(boldFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(codePatLabel, gbc);
        
        JTextField codePatField = new JTextField();
        codePatField.setFont(new Font("Arial", Font.PLAIN, 14));
        codePatField.setPreferredSize(new Dimension(200, 30));
        if (patient != null) {
            codePatField.setText(patient.getCodepat());
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(codePatField, gbc);
        
        // Nom
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(boldFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(nomLabel, gbc);
        
        JTextField nomField = new JTextField();
        nomField.setFont(new Font("Arial", Font.PLAIN, 14));
        nomField.setPreferredSize(new Dimension(200, 30));
        if (patient != null) {
            nomField.setText(patient.getNom());
        }
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(nomField, gbc);
        
        // Prénom
        JLabel prenomLabel = new JLabel("Prénom:");
        prenomLabel.setFont(boldFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(prenomLabel, gbc);
        
        JTextField prenomField = new JTextField();
        prenomField.setFont(new Font("Arial", Font.PLAIN, 14));
        prenomField.setPreferredSize(new Dimension(200, 30));
        if (patient != null) {
            prenomField.setText(patient.getPrenom());
        }
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(prenomField, gbc);
        
        // sexe (remplace le champ sexe)
        JLabel sexeLabel = new JLabel("sexe:");
        sexeLabel.setFont(boldFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(sexeLabel, gbc);
        
        JComboBox<String> sexeComboBox = new JComboBox<>(sexes);
        sexeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        sexeComboBox.setPreferredSize(new Dimension(200, 30));
        sexeComboBox.setBackground(Color.WHITE);
        
        // Si nous modifions un patient existant, sélectionner son sexe actuel
        if (patient != null) {
            String currentsexe = patient.getSexe();
            boolean sexeFound = false;
            
            // Chercher si le sexe existe déjà dans la liste
            for (int i = 0; i < sexes.length; i++) {
                if (sexes[i].equals(currentsexe)) {
                    sexeComboBox.setSelectedIndex(i);
                    sexeFound = true;
                    break;
                }
            }
            
            // Si le sexe n'est pas dans la liste, l'ajouter
            if (!sexeFound && !currentsexe.isEmpty()) {
                sexeComboBox.addItem(currentsexe);
                sexeComboBox.setSelectedItem(currentsexe);
            }
        }
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(sexeComboBox, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton(patient == null ? "Ajouter" : "Mettre à jour", 
                patient == null ? new Color(76, 175, 80) : new Color(33, 150, 243));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codePat = codePatField.getText();
                String nom = nomField.getText();
                String prenom = prenomField.getText();
                String sexe = (String) sexeComboBox.getSelectedItem();
                
                if (!codePat.isEmpty() && !nom.isEmpty() && !prenom.isEmpty() && sexe != null) {
                    if (patient == null) {
                        // Ajouter un nouveau patient
                        Patient newPatient = new Patient(codePat, nom, prenom, sexe);
                        patientDao.savePatient(newPatient);
                        showNotification("Patient ajouté avec succès", successColor);
                    } else {
                        // Mettre à jour un patient existant
                        Patient updatedPatient = new Patient(patient.getId(), codePat, nom, prenom, sexe);
                        patientDao.updatePatient(updatedPatient);
                        showNotification("Patient mis à jour avec succès", successColor);
                    }
                    dialog.dispose();
                    loadPatientData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Tous les champs doivent être remplis",
                            "Erreur de validation",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(saveButton);
        
        JButton cancelButton = createStyledButton("Annuler", new Color(158, 158, 158));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(buttonPanel, gbc);
        
        dialog.getContentPane().add(formPanel);
        dialog.setVisible(true);
    }

    private void showNotification(String message, Color backgroundColor) {
        notificationLabel.setText(message);
        notificationLabel.setForeground(Color.WHITE);
        notificationLabel.setOpaque(true);
        notificationLabel.setBackground(backgroundColor);
        notificationLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        notificationLabel.setVisible(true);
        
        // Arrêter le timer précédent s'il existe
        if (notificationTimer != null && notificationTimer.isRunning()) {
            notificationTimer.stop();
        }
        
        // Configurer un nouveau timer pour masquer la notification après 3 secondes
        notificationTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notificationLabel.setVisible(false);
            }
        });
        notificationTimer.setRepeats(false);
        notificationTimer.start();
    }

    private void deletePatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int patientId = (int) table.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer ce patient ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                patientDao.deletePatient(patientId);
                loadPatientData();
                showNotification("Patient supprimé avec succès", successColor);
            }
        } else {
            showNotification("Veuillez sélectionner un patient à supprimer", warningColor);
        }
    }

    private void searchPatients() {
        String searchText = searchField.getText();
        if (!searchText.isEmpty()) {
            List<Patient> patients = patientDao.searchPatients(searchText);
            updateTable(patients);
        } else {
            loadPatientData();
        }
    }

    private void loadPatientData() {
        List<Patient> patients = patientDao.getAllPatients();
        updateTable(patients);
    }

    private void updateTable(List<Patient> patients) {
        String[] columnNames = {"ID", "Code Patient", "Nom", "Prénom", "sexe"};
        Object[][] data = new Object[patients.size()][5];

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            data[i][0] = patient.getId();
            data[i][1] = patient.getCodepat();
            data[i][2] = patient.getNom();
            data[i][3] = patient.getPrenom();
            data[i][4] = patient.getSexe();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non éditables
            }
        };
        
        table.setModel(model);
        
        // Ajuster la largeur des colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        // Centrer le contenu de certaines colonnes
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        // Appliquer un rendu en gras pour toutes les cellules
        DefaultTableCellRenderer boldRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Arial", Font.BOLD, 14));
                return c;
            }
        };
        
        table.getColumnModel().getColumn(2).setCellRenderer(boldRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(boldRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(boldRenderer);
        
        // Afficher un message si aucun patient n'est trouvé
        if (patients.isEmpty()) {
            showNotification("Aucun patient trouvé", warningColor);
        }
    }
    
    // Classe interne pour les cellules avec texte en gras
    class BoldTableCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setFont(boldFont);
            return component;
        }
    }
}
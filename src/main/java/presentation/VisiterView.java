package presentation;

import dao.VisiterDao;
import dao.MedecinDao;
import dao.PatientDao;
import model.Visiter;
import model.Medecin;
import model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class VisiterView extends JPanel {
    private final VisiterDao visiterDao = new VisiterDao();
    private final MedecinDao medecinDao = new MedecinDao();
    private final PatientDao patientDao = new PatientDao();
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton, clearButton, searchButton;
    private JDateChooser dateSearchChooser;
    private JTextField textSearchField;
    private JLabel statusLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public VisiterView() {
        setupUI();
        loadData();
    }
    
    private void setupUI() {
        // Set layout for the panel
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Apply bold and black text to all labels
        UIManager.put("Label.font", new Font(Font.DIALOG, Font.BOLD, 12));
        UIManager.put("Label.foreground", Color.BLACK);
        UIManager.put("Button.font", new Font(Font.DIALOG, Font.BOLD, 12));
        UIManager.put("TextField.font", new Font(Font.DIALOG, Font.BOLD, 12));
        UIManager.put("ComboBox.font", new Font(Font.DIALOG, Font.BOLD, 12));
        UIManager.put("Table.font", new Font(Font.DIALOG, Font.BOLD, 12));
        
        // Top search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche"));
        
        // Text search
        JLabel textLabel = new JLabel("Recherche:");
        textLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        textLabel.setForeground(Color.BLACK);
        searchPanel.add(textLabel);
        
        textSearchField = new JTextField(15);
        textSearchField.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        searchPanel.add(textSearchField);
        
        // Date search
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        dateLabel.setForeground(Color.BLACK);
        searchPanel.add(dateLabel);
        
        // Using JDateChooser instead of JTextField
        dateSearchChooser = new JDateChooser();
        dateSearchChooser.setDateFormatString("yyyy-MM-dd");
        dateSearchChooser.setPreferredSize(new Dimension(150, 25));
        searchPanel.add(dateSearchChooser);
        
        searchButton = new JButton("Rechercher");
        addButton = new JButton("Ajouter");
        clearButton = new JButton("Effacer");
        
        // Apply bold font to buttons
        Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
        searchButton.setFont(boldFont);
        addButton.setFont(boldFont);
        clearButton.setFont(boldFont);
        
        searchPanel.add(searchButton);
        searchPanel.add(addButton);
        searchPanel.add(clearButton);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        searchPanel.add(statusLabel);
        
        // Table setup
        String[] columns = {"ID", "Medecin", "Patient", "Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the Actions column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? JPanel.class : Object.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(40); // Increase row height for buttons
        table.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        
        // Custom renderer for the button column
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des Occupations"));
        
        // Add components to the panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Set up action listeners
        setupListeners();
    }
    
    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Visiter> occupations = visiterDao.getAllVisiter();
            for (Visiter occupation : occupations) {
                addVisiterToTable(occupation);
            }
            showStatus("Données chargées avec succès");
        } catch (Exception e) {
            showError("Erreur lors du chargement des données: " + e.getMessage());
        }
    }
    
    private void addVisiterToTable(Visiter occupation) {
        tableModel.addRow(new Object[]{
            occupation.getId(),
            occupation.getMedecin().getNom(),
            occupation.getPatient().getNom(),
            dateFormat.format(occupation.getDateoc()),
            "buttons" // This is a placeholder, the renderer will replace it with buttons
        });
    }
    
    private void setupListeners() {
        // Add button listener
        addButton.addActionListener((ActionEvent e) -> {
            showAddDialog();
        });
        
        // Clear button listener
        clearButton.addActionListener((ActionEvent e) -> {
            textSearchField.setText("");
            dateSearchChooser.setDate(null);
            loadData();
        });
        
        // Search button listener
        searchButton.addActionListener((ActionEvent e) -> {
            searchOccupations();
        });
    }
    
    private void searchOccupations() {
        try {
            String searchText = textSearchField.getText().trim();
            Date searchDate = dateSearchChooser.getDate();
            
            if ((searchText == null || searchText.isEmpty()) && searchDate == null) {
                // If no search criteria, load all data
                loadData();
                return;
            }
            
            // Use the updated search method that accepts both parameters
            List<Visiter> results = visiterDao.searchVisiter(searchText, searchDate);
            
            tableModel.setRowCount(0);
            for (Visiter occupation : results) {
                addVisiterToTable(occupation);
            }
            
            showStatus("Recherche terminée: " + results.size() + " résultat(s) trouvé(s)");
        } catch (Exception ex) {
            showError("Erreur lors de la recherche: " + ex.getMessage());
        }
    }
    
    private void showAddDialog() {
        // Create a JDialog for adding
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Ajouter une Occupation");
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create bold labels
        Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
        
        // Medecin selector
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel profLabel = new JLabel("Medecin:");
        profLabel.setFont(boldFont);
        profLabel.setForeground(Color.BLACK);
        formPanel.add(profLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        JComboBox<ComboItem<Medecin>> professeurComboBox = new JComboBox<>();
        professeurComboBox.setFont(boldFont);
        loadProfesseurs(professeurComboBox);
        formPanel.add(professeurComboBox, gbc);
        
        // Patient selector
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel salleLabel = new JLabel("Patient:");
        salleLabel.setFont(boldFont);
        salleLabel.setForeground(Color.BLACK);
        formPanel.add(salleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        JComboBox<ComboItem<Patient>> salleComboBox = new JComboBox<>();
        salleComboBox.setFont(boldFont);
        loadSalles(salleComboBox);
        formPanel.add(salleComboBox, gbc);
        
        // Date field (using JDateChooser)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(boldFont);
        dateLabel.setForeground(Color.BLACK);
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        formPanel.add(dateChooser, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        saveButton.setFont(boldFont);
        cancelButton.setFont(boldFont);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                ComboItem<Medecin> professeurItem = (ComboItem<Medecin>) professeurComboBox.getSelectedItem();
                ComboItem<Patient> salleItem = (ComboItem<Patient>) salleComboBox.getSelectedItem();
                Date date = dateChooser.getDate();
                
                if (professeurItem == null || salleItem == null || date == null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Veuillez remplir tous les champs correctement", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Medecin medecin = professeurItem.getValue();
                Patient patient = salleItem.getValue();
                
                if (visiterDao.isSalleOccupee(patient.getId(), date)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Cette salle est déjà occupée à cette date",
                        "Conflit", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Visiter occupation = new Visiter(date, medecin, patient);
                visiterDao.saveVisiter(occupation);
                dialog.dispose();
                loadData();
                
                // Show success modal notification
                showNotificationDialog("Succès", "Occupation ajoutée avec succès", ModalType.SUCCESS);
                
                showStatus("Occupation ajoutée avec succès");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'ajout: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditDialog(int id) {
        Visiter occupation = visiterDao.getVisiterById(id);
        if (occupation == null) {
            showError("Occupation introuvable");
            return;
        }
        
        // Create a JDialog for editing
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Modifier une Occupation");
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create bold labels
        Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
        
        // Medecin selector
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel profLabel = new JLabel("Medecin:");
        profLabel.setFont(boldFont);
        profLabel.setForeground(Color.BLACK);
        formPanel.add(profLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        JComboBox<ComboItem<Medecin>> professeurComboBox = new JComboBox<>();
        professeurComboBox.setFont(boldFont);
        loadProfesseurs(professeurComboBox);
        selectProfesseurInComboBox(professeurComboBox, occupation.getMedecin());
        formPanel.add(professeurComboBox, gbc);
        
        // Patient selector
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel salleLabel = new JLabel("Patient:");
        salleLabel.setFont(boldFont);
        salleLabel.setForeground(Color.BLACK);
        formPanel.add(salleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        JComboBox<ComboItem<Patient>> salleComboBox = new JComboBox<>();
        salleComboBox.setFont(boldFont);
        loadSalles(salleComboBox);
        selectSalleInComboBox(salleComboBox, occupation.getPatient());
        formPanel.add(salleComboBox, gbc);
        
        // Date field (using JDateChooser)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(boldFont);
        dateLabel.setForeground(Color.BLACK);
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(occupation.getDateoc());
        formPanel.add(dateChooser, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        saveButton.setFont(boldFont);
        cancelButton.setFont(boldFont);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                ComboItem<Medecin> professeurItem = (ComboItem<Medecin>) professeurComboBox.getSelectedItem();
                ComboItem<Patient> salleItem = (ComboItem<Patient>) salleComboBox.getSelectedItem();
                Date date = dateChooser.getDate();
                
                if (professeurItem == null || salleItem == null || date == null) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Veuillez remplir tous les champs correctement", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Medecin medecin = professeurItem.getValue();
                Patient patient = salleItem.getValue();
                
                // Skip occupancy check if the same room is selected on the same day
                if (patient.getId() != occupation.getMedecin().getId() && 
                    visiterDao.isSalleOccupee(patient.getId(), date)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Cette salle est déjà occupée à cette date",
                        "Conflit", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                occupation.setMedecin(medecin);
                occupation.setPatient(patient);
                occupation.setDateoc(date);
                visiterDao.updateVisiter(occupation);
                dialog.dispose();
                loadData();
                
                // Show success modal notification
                showNotificationDialog("Succès", "Occupation modifiée avec succès", ModalType.SUCCESS);
                
                showStatus("Occupation modifiée avec succès");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la modification: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Add components to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteOccupation(int id) {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer cette occupation ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                visiterDao.deleteVisiter(id);
                loadData();
                
                // Show success modal notification
                showNotificationDialog("Succès", "Occupation supprimée avec succès", ModalType.SUCCESS);
                
                showStatus("Occupation supprimée avec succès");
            } catch (Exception ex) {
                showError("Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }
    
    // Enum for notification types
    private enum ModalType {
        SUCCESS, ERROR, WARNING
    }
    
    // Method to show a custom notification dialog
    private void showNotificationDialog(String title, String message, ModalType type) {
        // Create a modal dialog
        JDialog notificationDialog = new JDialog(SwingUtilities.getWindowAncestor(this), title);
        notificationDialog.setSize(350, 150);
        notificationDialog.setLocationRelativeTo(this);
        notificationDialog.setLayout(new BorderLayout());
        
        // Create panel with gradient background
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Define gradient colors based on notification type
                Color color1, color2;
                switch (type) {
                    case SUCCESS:
                        color1 = new Color(0, 150, 0);  // Dark green
                        color2 = new Color(144, 238, 144);  // Light green
                        break;
                    case ERROR:
                        color1 = new Color(178, 34, 34);  // Firebrick red
                        color2 = new Color(255, 160, 122);  // Light salmon
                        break;
                    case WARNING:
                        color1 = new Color(255, 165, 0);  // Orange
                        color2 = new Color(255, 215, 0);  // Gold
                        break;
                    default:
                        color1 = new Color(70, 130, 180);  // Steel blue
                        color2 = new Color(176, 224, 230);  // Powder blue
                }
                
                // Create gradient paint
                GradientPaint gp = new GradientPaint(
                    0, 0, color2, 
                    0, getHeight(), color1
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create icon based on notification type
        Icon icon;
        switch (type) {
            case SUCCESS:
                icon = UIManager.getIcon("OptionPane.informationIcon");
                break;
            case ERROR:
                icon = UIManager.getIcon("OptionPane.errorIcon");
                break;
            case WARNING:
                icon = UIManager.getIcon("OptionPane.warningIcon");
                break;
            default:
                icon = UIManager.getIcon("OptionPane.informationIcon");
        }
        
        // Create message with icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);
        
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messagePanel.setOpaque(false);
        messagePanel.add(iconLabel);
        messagePanel.add(messageLabel);
        
        // Create OK button
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        okButton.addActionListener(e -> notificationDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        
        // Add components to panel
        panel.add(messagePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panel to dialog
        notificationDialog.add(panel);
        notificationDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Auto-close after 3 seconds
        Timer timer = new Timer(3000, e -> notificationDialog.dispose());
        timer.setRepeats(false);
        timer.start();
        
        // Show dialog
        notificationDialog.setVisible(true);
    }
    
    private void loadProfesseurs(JComboBox<ComboItem<Medecin>> comboBox) {
        comboBox.removeAllItems();
        try {
            List<Medecin> medecins = medecinDao.getAllMedecin();
            for (Medecin prof : medecins) {
                comboBox.addItem(new ComboItem<>(prof, prof.getNom()));
            }
        } catch (Exception e) {
            showError("Erreur lors du chargement des professeurs: " + e.getMessage());
        }
    }
    
    private void loadSalles(JComboBox<ComboItem<Patient>> comboBox) {
        comboBox.removeAllItems();
        try {
            List<Patient> patients = patientDao.getAllPatients();
            for (Patient patient : patients) {
                comboBox.addItem(new ComboItem<>(patient, patient.getCodepat()));
            }
        } catch (Exception e) {
            showError("Erreur lors du chargement des salles: " + e.getMessage());
        }
    }
    
    private void selectProfesseurInComboBox(JComboBox<ComboItem<Medecin>> comboBox, Medecin medecin) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem<Medecin> item = comboBox.getItemAt(i);
            if (item.getValue().getId() == medecin.getId()) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void selectSalleInComboBox(JComboBox<ComboItem<Patient>> comboBox, Patient patient) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem<Patient> item = comboBox.getItemAt(i);
            if (item.getValue().getId() == patient.getId()) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.BLUE);
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }
    
    // Custom renderer for the button column
    private class ButtonRenderer implements TableCellRenderer {
        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        
        public ButtonRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Modifier");
            deleteButton = new JButton("Supprimer");
            
            // Style buttons with bold font
            Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
            editButton.setFont(boldFont);
            deleteButton.setFont(boldFont);
            
            // Style buttons
            editButton.setBackground(new Color(70, 130, 180)); // Steel blue
            editButton.setForeground(Color.BLACK);
            deleteButton.setBackground(new Color(178, 34, 34)); // Firebrick
            deleteButton.setForeground(Color.BLACK);
            
            panel.add(editButton);
            panel.add(deleteButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return panel;
        }
    }
    
    // Custom editor for the button column with single-click functionality
    private class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        private int row;
        
        public ButtonEditor() {
            super(new JTextField());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = new JButton("Modifier");
            deleteButton = new JButton("Supprimer");
            
            // Add bold font to buttons
            Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
            editButton.setFont(boldFont);
            deleteButton.setFont(boldFont);
            
            // Style buttons
            editButton.setBackground(new Color(70, 130, 180)); // Steel blue
            editButton.setForeground(Color.WHITE);
            deleteButton.setBackground(new Color(178, 34, 34)); // Firebrick
            deleteButton.setForeground(Color.WHITE);
            
            panel.add(editButton);
            panel.add(deleteButton);
            
            // Add action listeners
            editButton.addActionListener(e -> {
                int id = (int) table.getValueAt(row, 0);
                showEditDialog(id);
                fireEditingStopped();
            });
            
            deleteButton.addActionListener(e -> {
                int id = (int) table.getValueAt(row, 0);
                deleteOccupation(id);
                fireEditingStopped();
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "buttons";
        }
    }
    
    // Utility class to display custom text in ComboBox
    // while keeping the original object
    private static class ComboItem<T> {
        private final T value;
        private final String displayText;
        
        public ComboItem(T value, String displayText) {
            this.value = value;
            this.displayText = displayText;
        }
        
        public T getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
}
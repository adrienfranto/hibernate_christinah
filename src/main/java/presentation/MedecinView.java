package presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import dao.MedecinDao;
import model.Medecin;

public class MedecinView extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTable table;
    private MedecinDao medecinDao;
    private JTextField searchField;
    private Timer notificationTimer;
    private JDialog notificationDialog;
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 12);
    
    public MedecinView() {
        medecinDao = new MedecinDao();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel d'en-tête avec titre et barre de recherche
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel central avec le tableau
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        setupSearchFunctionality();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setOpaque(false);
        
        // Titre avec texte en gras
        JLabel titleLabel = new JLabel("Gestion des Medecin", JLabel.LEFT);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Panel des actions (recherche et ajout)
        JPanel actionsPanel = new JPanel(new BorderLayout(10, 0));
        actionsPanel.setOpaque(false);
        
        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(SUBTITLE_FONT);
        searchField = new JTextField(15);
        searchField.setFont(REGULAR_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 204, 204)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        JButton searchButton = createStyledButton("Rechercher", PRIMARY_COLOR);
        searchButton.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Panel pour le bouton Ajouter
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addPanel.setOpaque(false);
        
        JButton addButton = createStyledButton("Ajouter un Medecin", SUCCESS_COLOR);
        addButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
        configureAddButton(addButton);
        
        addPanel.add(addButton);
        
        // Assembler le panel des actions
        actionsPanel.add(searchPanel, BorderLayout.WEST);
        actionsPanel.add(addPanel, BorderLayout.EAST);
        
        // Assembler le panel d'en-tête
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(actionsPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Création du tableau stylisé
        createStyledTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void createStyledTable() {
        String[] columns = {"ID", "Code Med", "Nom", "Prénom", "Grade", "Actions"};
        List<Medecin> medecins = medecinDao.getAllMedecin();
        Object[][] data = new Object[medecins.size()][6];
        
        for (int i = 0; i < medecins.size(); i++) {
            Medecin p = medecins.get(i);
            data[i] = new Object[]{p.getId(), p.getCodemed(), p.getNom(), p.getPrenom(), p.getGrade(), ""};
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Seule la colonne Actions est éditable
            }
        };
        
        table = new JTable(model);
        table.setFont(REGULAR_FONT);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);
        
        // Style de l'en-tête du tableau
        JTableHeader header = table.getTableHeader();
        header.setFont(SUBTITLE_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Style des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Définir le rendu de la dernière colonne pour contenir les boutons
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), this));
        
        // Ajuster les largeurs des colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(backgroundColor, 0.9f));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private Color darkenColor(Color color, float factor) {
        int r = Math.max((int)(color.getRed() * factor), 0);
        int g = Math.max((int)(color.getGreen() * factor), 0);
        int b = Math.max((int)(color.getBlue() * factor), 0);
        return new Color(r, g, b);
    }
    
    private void configureAddButton(JButton addButton) {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMedecinForm();
            }
        });
    }
    
    private void setupSearchFunctionality() {
        // Action de recherche de Medecin sur bouton
        JButton searchButton = (JButton) ((JPanel)((JPanel)((JPanel)getComponent(0)).getComponent(1)).getComponent(0)).getComponent(2);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        // Action de recherche quand on appuie sur Entrée
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }
    
    private void performSearch() {
        String searchQuery = searchField.getText().trim();
        List<Medecin> filteredMedecins = medecinDao.searchMedecins(searchQuery, "", "");
        
        if (filteredMedecins.isEmpty() && !searchQuery.isEmpty()) {
            
            showNotification("Aucun Medecin trouvé avec ce nom", WARNING_COLOR);
        }
        
        refreshTableWithData(filteredMedecins);
    }
    
    private void showAddMedecinForm() {
        // Créer un formulaire stylisé pour l'ajout
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel codeprofLabel = new JLabel("Code Med:");
        codeprofLabel.setFont(SUBTITLE_FONT);
        JTextField codeprofField = createStyledTextField();
        
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(SUBTITLE_FONT);
        JTextField nomField = createStyledTextField();
        
        JLabel prenomLabel = new JLabel("Prénom:");
        prenomLabel.setFont(SUBTITLE_FONT);
        JTextField prenomField = createStyledTextField();
        
        JLabel gradeLabel = new JLabel("Grade:");
        gradeLabel.setFont(SUBTITLE_FONT);
        String[] gradeOptions = {"Professeur Tutilaire", "Professeur", "Docteur", "Assistant", "Interne"};
        JComboBox<String> gradeComboBox = new JComboBox<>(gradeOptions);
        gradeComboBox.setFont(REGULAR_FONT);
        gradeComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 204, 204)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gradeComboBox.setBackground(Color.WHITE);
        
        formPanel.add(codeprofLabel);
        formPanel.add(codeprofField);
        formPanel.add(nomLabel);
        formPanel.add(nomField);
        formPanel.add(prenomLabel);
        formPanel.add(prenomField);
        formPanel.add(gradeLabel);
        formPanel.add(gradeComboBox);
        
        // Créer une boîte de dialogue personnalisée
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un Medecin", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JButton cancelButton = createStyledButton("Annuler", DANGER_COLOR);
        JButton saveButton = createStyledButton("Enregistrer", SUCCESS_COLOR);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Actions des boutons
        cancelButton.addActionListener(e -> dialog.dispose());
        
        saveButton.addActionListener(e -> {
            if (validateFormFields(codeprofField, nomField, prenomField) && gradeComboBox.getSelectedItem() != null) {
                Medecin medecin = new Medecin(
                        codeprofField.getText().trim(), 
                        nomField.getText().trim(), 
                        prenomField.getText().trim(), 
                        gradeComboBox.getSelectedItem().toString());
                medecinDao.saveMedecin(medecin);
                refreshTable();
                showNotification("Medecin ajouté avec succès!", SUCCESS_COLOR);
                dialog.dispose();
            } else if (gradeComboBox.getSelectedItem() == null) {
                showNotification("Veuillez sélectionner un grade", WARNING_COLOR);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(REGULAR_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 204, 204)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return textField;
    }
    
    private boolean validateFormFields(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                showNotification("Tous les champs sont obligatoires", WARNING_COLOR);
                field.requestFocus();
                return false;
            }
        }
        return true;
    }
    
    // Méthode pour modifier un Medecin
    public void modifyMedecin(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) table.getModel().getValueAt(modelRow, 0);
        
        // Trouver le Medecin dans la liste
        List<Medecin> medecins = medecinDao.getAllMedecin();
        Medecin medecin = null;
        
        for (Medecin p : medecins) {
            if (p.getId() == id) {
                medecin = p;
                break;
            }
        }
        
        if (medecin == null) {
            showNotification("Medecin introuvable", WARNING_COLOR);
            return;
        }
        
        // Créer un formulaire stylisé pour la modification
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel codeprofLabel = new JLabel("Code Med:");
        codeprofLabel.setFont(SUBTITLE_FONT);
        JTextField codeprofField = createStyledTextField();
        codeprofField.setText(medecin.getCodemed());
        
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(SUBTITLE_FONT);
        JTextField nomField = createStyledTextField();
        nomField.setText(medecin.getNom());
        
        JLabel prenomLabel = new JLabel("Prénom:");
        prenomLabel.setFont(SUBTITLE_FONT);
        JTextField prenomField = createStyledTextField();
        prenomField.setText(medecin.getPrenom());
        
        JLabel gradeLabel = new JLabel("Grade:");
        gradeLabel.setFont(SUBTITLE_FONT);
        String[] gradeOptions = {"Professeur Tutilaire", "Professeur", "Docteur", "Assistant", "Interne"};
        JComboBox<String> gradeComboBox = new JComboBox<>(gradeOptions);
        gradeComboBox.setFont(REGULAR_FONT);
        gradeComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 204, 204)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gradeComboBox.setBackground(Color.WHITE);
        // Sélectionner le grade actuel du médecin s'il existe dans la liste
        for (int i = 0; i < gradeOptions.length; i++) {
            if (gradeOptions[i].equals(medecin.getGrade())) {
                gradeComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        formPanel.add(codeprofLabel);
        formPanel.add(codeprofField);
        formPanel.add(nomLabel);
        formPanel.add(nomField);
        formPanel.add(prenomLabel);
        formPanel.add(prenomField);
        formPanel.add(gradeLabel);
        formPanel.add(gradeComboBox);
        
        // Créer une boîte de dialogue personnalisée
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier un Medecin", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JButton cancelButton = createStyledButton("Annuler", DANGER_COLOR);
        JButton saveButton = createStyledButton("Enregistrer", SECONDARY_COLOR);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Actions des boutons
        cancelButton.addActionListener(e -> dialog.dispose());
        
        final Medecin finalMedecin = medecin;
        saveButton.addActionListener(e -> {
            if (validateFormFields(codeprofField, nomField, prenomField) && gradeComboBox.getSelectedItem() != null) {
                finalMedecin.setCodemed(codeprofField.getText().trim());
                finalMedecin.setNom(nomField.getText().trim());
                finalMedecin.setPrenom(prenomField.getText().trim());
                finalMedecin.setGrade(gradeComboBox.getSelectedItem().toString());
                
                medecinDao.updateMedecin(finalMedecin);
                refreshTable();
                showNotification("Medecin modifié avec succès!", SUCCESS_COLOR);
                dialog.dispose();
            } else if (gradeComboBox.getSelectedItem() == null) {
                showNotification("Veuillez sélectionner un grade", WARNING_COLOR);
            }
        });
        
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    // Méthode pour supprimer un Medecin
    public void deleteMedecin(int row) {
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) table.getModel().getValueAt(modelRow, 0);
        
        // Créer une boîte de dialogue de confirmation personnalisée
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel messageLabel = new JLabel("Êtes-vous sûr de vouloir supprimer ce Medecin ?");
        messageLabel.setFont(SUBTITLE_FONT);
        
        confirmPanel.add(iconLabel, BorderLayout.WEST);
        confirmPanel.add(messageLabel, BorderLayout.CENTER);
        
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirmation de suppression", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.add(confirmPanel, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JButton cancelButton = createStyledButton("Annuler", SECONDARY_COLOR);
        JButton deleteButton = createStyledButton("Supprimer", DANGER_COLOR);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Actions des boutons
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        deleteButton.addActionListener(e -> {
            medecinDao.deleteMedecin(id);
            refreshTable();
            showNotification("Medecin supprimé avec succès!", DANGER_COLOR);
            confirmDialog.dispose();
        });
        
        confirmDialog.pack();
        confirmDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        confirmDialog.setResizable(false);
        confirmDialog.setVisible(true);
    }

    // Méthode pour rafraîchir le tableau après une opération
    private void refreshTable() {
        List<Medecin> medecins = medecinDao.getAllMedecin();
        refreshTableWithData(medecins);
    }
    
    // Méthode pour rafraîchir le tableau avec des données spécifiques
    private void refreshTableWithData(List<Medecin> medecins) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        for (Medecin p : medecins) {
            model.addRow(new Object[]{p.getId(), p.getCodemed(), p.getNom(), p.getPrenom(), p.getGrade(), ""});
        }
        
        // Réappliquer le rendu de la colonne Actions
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), this));
    }
    
    // Méthode pour afficher une notification professionnelle
    private void showNotification(String message, Color backgroundColor) {
        // Fermer la notification précédente si elle existe
        if (notificationDialog != null && notificationDialog.isVisible()) {
            notificationDialog.dispose();
            if (notificationTimer != null) {
                notificationTimer.stop();
            }
        }
        
        // Créer une nouvelle notification avec un design moderne
        notificationDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), false);
        notificationDialog.setUndecorated(true);
        
        // Créer le panneau de notification avec le message
        JPanel notificationPanel = new JPanel(new BorderLayout(10, 0));
        notificationPanel.setBackground(backgroundColor);
        notificationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(darkenColor(backgroundColor, 0.8f)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        
        // Icône de notification en fonction du type de message
        JLabel iconLabel = new JLabel();
        if (backgroundColor.equals(SUCCESS_COLOR)) {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        } else if (backgroundColor.equals(WARNING_COLOR)) {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
        } else if (backgroundColor.equals(DANGER_COLOR)) {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        } else {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        }
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        notificationPanel.add(iconLabel, BorderLayout.WEST);
        notificationPanel.add(messageLabel, BorderLayout.CENTER);
        
        // Ajouter un bouton de fermeture
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(backgroundColor);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> notificationDialog.dispose());
        
        notificationPanel.add(closeButton, BorderLayout.EAST);
        
        notificationDialog.add(notificationPanel);
        notificationDialog.pack();
        
        // Effet d'ombre pour la notification
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 10, 10);
                g2d.dispose();
            }
        };
        shadowPanel.setOpaque(false);
        shadowPanel.setBounds(0, 0, notificationDialog.getWidth(), notificationDialog.getHeight());
        notificationDialog.add(shadowPanel);
        
        // Positionner la notification en haut à droite
        Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            Rectangle bounds = mainFrame.getBounds();
            notificationDialog.setLocation(
                bounds.x + bounds.width - notificationDialog.getWidth() - 25,
                bounds.y + 70
            );
        }
        
        // Afficher la notification avec un effet de fondu
        notificationDialog.setOpacity(0.0f);
        notificationDialog.setVisible(true);
        
        // Animation de fondu
        final float[] opacity = {0.0f};
        Timer fadeInTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] += 0.1f;
                if (opacity[0] >= 1.0f) {
                    opacity[0] = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                notificationDialog.setOpacity(opacity[0]);
            }
        });
        fadeInTimer.start();
        
        // Créer un timer pour fermer automatiquement la notification après 3 secondes
        notificationTimer = new Timer(3000, e -> {
            // Animation de fondu à la fermeture
            Timer fadeOutTimer = new Timer(30, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    opacity[0] -= 0.1f;
                    if (opacity[0] <= 0.0f) {
                        opacity[0] = 0.0f;
                        ((Timer)evt.getSource()).stop();
                        notificationDialog.dispose();
                    }
                    notificationDialog.setOpacity(opacity[0]);
                }
            });
            fadeOutTimer.start();
        });
        notificationTimer.setRepeats(false);
        notificationTimer.start();
    }
    
    // Classe pour le rendu des boutons dans le tableau
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton modifyButton;
        private JButton deleteButton;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            setOpaque(false);
            
            modifyButton = createStyledButton("Modifier", SECONDARY_COLOR);
            modifyButton.setPreferredSize(new Dimension(100, 30));
            
            deleteButton = createStyledButton("Supprimer", DANGER_COLOR);
            deleteButton.setPreferredSize(new Dimension(100, 30));
            
            add(modifyButton);
            add(deleteButton);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
    
    //
    
    // Classe pour l'éditeur des boutons dans le tableau
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton modifyButton;
        private JButton deleteButton;
        private int currentRow;
        private MedecinView parentView;
        
        public ButtonEditor(JCheckBox checkBox, MedecinView view) {
            super(checkBox);
            this.parentView = view;
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(false);
            
            modifyButton = createStyledButton("Modifier", SECONDARY_COLOR);
            modifyButton.setPreferredSize(new Dimension(100, 30));
            
            deleteButton = createStyledButton("Supprimer", DANGER_COLOR);
            deleteButton.setPreferredSize(new Dimension(100, 30));
            
            panel.add(modifyButton);
            panel.add(deleteButton);
            
            modifyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parentView.modifyMedecin(currentRow);
                    fireEditingStopped();
                }
            });
            
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parentView.deleteMedecin(currentRow);
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
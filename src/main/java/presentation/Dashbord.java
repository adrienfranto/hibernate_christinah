package presentation;

import javax.print.DocFlavor.URL;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashbord extends JFrame {
  
	private static final long serialVersionUID = 1L;
	private CardLayout cardLayout;
    private JPanel panelContenu;
    private JButton boutonActif;
    
    private static final Color COULEUR_FOND = new Color(245, 245, 250);
    private static final Color COULEUR_PANEL_GAUCHE = new Color(50, 60, 80);
    private static final Color COULEUR_BOUTON_NORMAL = new Color(70, 80, 100);
    private static final Color COULEUR_BOUTON_ACTIF = new Color(40, 100, 170);
    private static final Color COULEUR_TEXTE = Color.WHITE;
    private static final Font POLICE_BOUTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font POLICE_TITRE = new Font("Segoe UI", Font.BOLD, 22);
    
    public Dashbord() {
        setTitle("Tableau de Bord");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Définition du look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Panel gauche pour les liens avec un layout BoxLayout
        JPanel panelGauche = creerPanelGauche();
        
        // Panel en-tête
        JPanel panelEnTete = creerPanelEnTete();
        
        // Panel droit pour le contenu
        panelContenu = new JPanel();
        cardLayout = new CardLayout();
        panelContenu.setLayout(cardLayout);
        panelContenu.setBackground(COULEUR_FOND);
        panelContenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Ajouter des vues pour chaque lien
        panelContenu.add(new MedecinView(), "Medecin");
        panelContenu.add(new PatientView(), "Patient");
        panelContenu.add(new VisiterView(), "Visiter");
        
        // Layout principal
        setLayout(new BorderLayout());
        add(panelGauche, BorderLayout.WEST);
        add(panelEnTete, BorderLayout.NORTH);
        add(panelContenu, BorderLayout.CENTER);
        
        // Activer la première vue par défaut
        cardLayout.show(panelContenu, "Medecin");
    }
    
    private JPanel creerPanelGauche() {
        JPanel panelGauche = new JPanel();
        panelGauche.setLayout(new BoxLayout(panelGauche, BoxLayout.Y_AXIS));
        panelGauche.setBackground(COULEUR_PANEL_GAUCHE);
        panelGauche.setPreferredSize(new Dimension(220, 0));
        panelGauche.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Créer un panel pour le titre avec l'image et le texte (en FlowLayout)
        JPanel panelTitre = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTitre.setOpaque(false);
        panelTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTitre.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
        
        try {
            // Charger l'image depuis les ressources
            java.net.URL imageUrl = getClass().getResource("/images/christinah.jpg");
            if (imageUrl != null) {
                ImageIcon photoIcon = new ImageIcon(imageUrl);
                Image img = photoIcon.getImage();
                Image imgResized = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                photoIcon = new ImageIcon(imgResized);

                JLabel photoLabel = new JLabel(photoIcon);
                panelTitre.add(photoLabel);
            } else {
                throw new Exception("Image non trouvée");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
            JLabel photoLabel = new JLabel("IMG");
            photoLabel.setForeground(COULEUR_TEXTE);
            panelTitre.add(photoLabel);
        }

        
        // Logo ou titre du panel gauche
        JLabel labelTitre = new JLabel("ADMINISTRATION");
        labelTitre.setForeground(COULEUR_TEXTE);
        labelTitre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelTitre.add(labelTitre);
        
        // Ajouter le panel titre au panel gauche
        panelGauche.add(panelTitre);
        
        // Ajouter un petit espace après le titre
        panelGauche.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Créer et ajouter les boutons de navigation, chacun sur une ligne
        JButton medecinButton = creerBoutonMenu("Medecin", "Medecins.png");
        medecinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        medecinButton.setMaximumSize(new Dimension(200, 50));
        medecinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activerBouton(medecinButton, "Medecin");
            }
        });
        panelGauche.add(medecinButton);
        panelGauche.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JButton patientButton = creerBoutonMenu("Patient", "salles.png");
        patientButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        patientButton.setMaximumSize(new Dimension(200, 50));
        patientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activerBouton(patientButton, "Patient");
            }
        });
        panelGauche.add(patientButton);
        panelGauche.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JButton visiterButton = creerBoutonMenu("Visiter", "occupations.png");
        visiterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        visiterButton.setMaximumSize(new Dimension(200, 50));
        visiterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activerBouton(visiterButton, "Visiter");
            }
        });
        panelGauche.add(visiterButton);
        
        // Définir le premier bouton comme actif par défaut
        boutonActif = medecinButton;
        boutonActif.setBackground(COULEUR_BOUTON_ACTIF);
        
        // Ajouter un spacer pour pousser les éléments vers le haut
        panelGauche.add(Box.createVerticalGlue());
        
        // Bouton de déconnexion en bas
        JButton boutonDeconnexion = creerBoutonMenu("Déconnexion", "logout.png");
        boutonDeconnexion.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutonDeconnexion.setMaximumSize(new Dimension(200, 50));
        panelGauche.add(boutonDeconnexion);
        panelGauche.add(Box.createRigidArea(new Dimension(0, 20)));
        
        return panelGauche;
    }
    
    private void activerBouton(JButton bouton, String nomPanel) {
        // Réinitialiser le bouton actif précédent
        if (boutonActif != null) {
            boutonActif.setBackground(COULEUR_BOUTON_NORMAL);
        }
        
        // Définir le nouveau bouton actif
        boutonActif = bouton;
        boutonActif.setBackground(COULEUR_BOUTON_ACTIF);
        
        // Afficher le contenu correspondant
        cardLayout.show(panelContenu, nomPanel);
    }
    
    private JPanel creerPanelEnTete() {
        JPanel panelEnTete = new JPanel(new BorderLayout());
        panelEnTete.setBackground(Color.WHITE);
        panelEnTete.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel labelTitre = new JLabel("Gestion des Medecins");
        labelTitre.setFont(POLICE_TITRE);
        labelTitre.setForeground(new Color(50, 60, 80));
        
        JPanel panelUtilisateur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelUtilisateur.setOpaque(false);
        JLabel labelUtilisateur = new JLabel("Admin");
        labelUtilisateur.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelUtilisateur.add(labelUtilisateur);
        
        panelEnTete.add(labelTitre, BorderLayout.WEST);
        panelEnTete.add(panelUtilisateur, BorderLayout.EAST);
        
        return panelEnTete;
    }
    
    private JButton creerBoutonMenu(String texte, String nomIcone) {
        JButton bouton = new JButton(texte);
        bouton.setFont(POLICE_BOUTON);
        bouton.setForeground(COULEUR_TEXTE);
        bouton.setBackground(COULEUR_BOUTON_NORMAL);
        bouton.setBorderPainted(false);
        bouton.setFocusPainted(false);
        
        // Centrer le texte du bouton
        bouton.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ajouter un padding
        bouton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // On pourrait ajouter une icône ici si disponible
        // bouton.setIcon(new ImageIcon(getClass().getResource("/icones/" + nomIcone)));
        
        return bouton;
    }
    
    // Méthode pour démarrer l'application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Dashbord().setVisible(true);
            }
        });
    }
}
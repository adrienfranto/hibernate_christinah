package presentation;

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
        
        // Création du panel principal avec une bordure
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(COULEUR_FOND);
        
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
        boutonActif = (JButton) panelGauche.getComponent(1); // Le premier bouton après le label
        boutonActif.setBackground(COULEUR_BOUTON_ACTIF);
        cardLayout.show(panelContenu, "Medecin");
    }
    
    private JPanel creerPanelGauche() {
        JPanel panelGauche = new JPanel();
        panelGauche.setLayout(new BoxLayout(panelGauche, BoxLayout.Y_AXIS));
        panelGauche.setBackground(COULEUR_PANEL_GAUCHE);
        panelGauche.setPreferredSize(new Dimension(220, 0));
        panelGauche.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Logo ou titre du panel gauche
        JLabel labelTitre = new JLabel("ADMINISTRATION");
        labelTitre.setForeground(COULEUR_TEXTE);
        labelTitre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTitre.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));
        panelGauche.add(labelTitre);
        
        // Ajouter des boutons de navigation avec style
        ajouterBoutonNavigation(panelGauche, "Medecin", "Medecins.png");
        ajouterBoutonNavigation(panelGauche, "Patient", "salles.png");
        ajouterBoutonNavigation(panelGauche, "Visiter", "occupations.png");
        
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
    
    private void ajouterBoutonNavigation(JPanel panel, String texte, String icone) {
        JButton bouton = creerBoutonMenu(texte, icone);
        
        // Ajouter l'action au bouton
        bouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Réinitialiser le bouton actif précédent
                if (boutonActif != null) {
                    boutonActif.setBackground(COULEUR_BOUTON_NORMAL);
                }
                
                // Définir le nouveau bouton actif
                boutonActif = bouton;
                boutonActif.setBackground(COULEUR_BOUTON_ACTIF);
                
                // Afficher le contenu correspondant
                cardLayout.show(panelContenu, texte);
            }
        });
        
        panel.add(bouton);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    private JButton creerBoutonMenu(String texte, String nomIcone) {
        JButton bouton = new JButton(texte);
        bouton.setFont(POLICE_BOUTON);
        bouton.setForeground(COULEUR_TEXTE);
        bouton.setBackground(COULEUR_BOUTON_NORMAL);
        bouton.setBorderPainted(false);
        bouton.setFocusPainted(false);
        bouton.setHorizontalAlignment(SwingConstants.LEFT);
        bouton.setMaximumSize(new Dimension(220, 50));
        bouton.setPreferredSize(new Dimension(220, 50));
        
        // Ajouter un padding
        bouton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        
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
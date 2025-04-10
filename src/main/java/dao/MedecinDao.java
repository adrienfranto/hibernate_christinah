package dao;

import java.util.List;
import java.util.Collections;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import model.Medecin;
import util.HibernateUtil;

public class MedecinDao {
	
    // 🔹 CREATE (Ajout d'un Medecin)
    public void saveMedecin(Medecin medecin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(medecin);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    
    // 🔹 READ (Récupérer tous les Medecins)
    public List<Medecin> getAllMedecin() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Medecin", Medecin.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Évite les NullPointerException
        }
    }
    
	 // 🔹 READ (Récupérer un Medecin par ID)
	    public Medecin getMedecinById(int id) {
	        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	            return session.get(Medecin.class, id);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null; // Retourne null si l'ID n'existe pas ou en cas d'erreur
	        }
	    }

    
    // 🔹 UPDATE (Mettre à jour un Medecin)
    public void updateMedecin(Medecin medecin) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(medecin); // update(Medecin) si Hibernate < 6
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    
    // 🔹 DELETE (Supprimer un Medecin)
    public void deleteMedecin(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Medecin medecin = session.get(Medecin.class, id);  
            
            if (medecin != null) {
                session.remove(medecin);
                System.out.println("Medecin supprimé avec succès.");
            } else {
                System.out.println("Aucun Medecin trouvé avec l'ID: " + id);
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    // 🔹 SEARCH (Recherche dynamique)
    public List<Medecin> searchMedecins(String recherche) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Medecin p WHERE "
                       + "lower(p.grade) LIKE lower(:recherche) OR "
                       + "lower(p.codemed) LIKE lower(:recherche) OR "
                       + "lower(p.nom) LIKE lower(:recherche) OR "
                       + "lower(p.prenom) LIKE lower(:recherche)";

            Query<Medecin> query = session.createQuery(hql, Medecin.class);
            query.setParameter("recherche", "%" + recherche + "%");

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}

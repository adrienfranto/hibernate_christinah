package dao;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import model.Patient;
import util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientDao {
    private static final Logger logger = LoggerFactory.getLogger(PatientDao.class);

    // 🔹 CREATE (Ajout d'un Patient)
    public void savePatient(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Erreur lors de la sauvegarde du Patient", e);
        }
    }
    
    // 🔹 READ (Récupérer tous les Patients)
    public List<Patient> getAllPatients() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Patient", Patient.class).list();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des Patients", e);
            return List.of();
        }
    }
    
    // 🔹 UPDATE (Mettre à jour un Patient)
    public void updatePatient(Patient patient) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(patient);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Erreur lors de la mise à jour du Patient", e);
        }
    }
    
    // 🔹 DELETE (Supprimer un Patient)
    public void deletePatient(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.remove(patient);
                logger.info("Patient supprimé avec succès.");
            } else {
                logger.warn("Aucun Patient trouvé avec l'ID: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Erreur lors de la suppression du Patient", e);
        }
    }
    
    // 🔹 SEARCH (Recherche par un seul texte à travers plusieurs champs)
    public List<Patient> searchPatients(String searchText) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            if (searchText == null || searchText.isEmpty()) {
                return getAllPatients();
            }
            
            String hql = "FROM Patient p WHERE "
                    + "lower(p.codepat) LIKE lower(:searchText) OR "
                    + "lower(p.nom) LIKE lower(:searchText) OR "
                    + "lower(p.prenom) LIKE lower(:searchText) OR "
                    + "lower(p.sexe) LIKE lower(:searchText)";
            
            Query<Patient> query = session.createQuery(hql, Patient.class);
            query.setParameter("searchText", "%" + searchText + "%");
            
            return query.list();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche des Patients", e);
            return List.of();
        }
    }
    
    // 🔹 ADVANCED SEARCH (Recherche avec critères multiples)
    public List<Patient> advancedSearchPatient(String codepat, String nom, String prenom, String sexe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Patient p";
            boolean whereAdded = false;
            
            // Construire la requête dynamiquement en fonction des paramètres fournis
            if ((codepat != null && !codepat.isEmpty()) || 
                (nom != null && !nom.isEmpty()) || 
                (prenom != null && !prenom.isEmpty()) || 
                (sexe != null && !sexe.isEmpty())) {
                
                hql += " WHERE ";
                
                if (codepat != null && !codepat.isEmpty()) {
                    hql += "lower(p.codepat) LIKE lower(:codepat)";
                    whereAdded = true;
                }
                
                if (nom != null && !nom.isEmpty()) {
                    if (whereAdded) {
                        hql += " AND ";
                    }
                    hql += "lower(p.nom) LIKE lower(:nom)";
                    whereAdded = true;
                }
                
                if (prenom != null && !prenom.isEmpty()) {
                    if (whereAdded) {
                        hql += " AND ";
                    }
                    hql += "lower(p.prenom) LIKE lower(:prenom)";
                    whereAdded = true;
                }
                
                if (sexe != null && !sexe.isEmpty()) {
                    if (whereAdded) {
                        hql += " AND ";
                    }
                    hql += "lower(p.sexe) LIKE lower(:sexe)";
                }
            }
            
            Query<Patient> query = session.createQuery(hql, Patient.class);
            
            // Définir les paramètres si nécessaire
            if (codepat != null && !codepat.isEmpty()) {
                query.setParameter("codepat", "%" + codepat + "%");
            }
            if (nom != null && !nom.isEmpty()) {
                query.setParameter("nom", "%" + nom + "%");
            }
            if (prenom != null && !prenom.isEmpty()) {
                query.setParameter("prenom", "%" + prenom + "%");
            }
            if (sexe != null && !sexe.isEmpty()) {
                query.setParameter("sexe", "%" + sexe + "%");
            }
            
            return query.list();
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche avancée des Patients", e);
            return List.of();
        }
    }
}
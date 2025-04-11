package dao;

import model.Visiter;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class VisiterDao {
    
    // Existing methods...
    
    // Updated search method to handle both text and date search
	public List<Visiter> searchVisiter(String recherche, Date dateoc) {
	    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
	        String hql = "FROM Visiter o WHERE "
	                   + "(lower(o.medecin.nom) LIKE lower(:recherche) OR "
	                   + "lower(o.medecin.prenom) LIKE lower(:recherche) OR "
	                   + "lower(o.patient.nom) LIKE lower(:recherche) OR "
	                   + "lower(o.patient.prenom) LIKE lower(:recherche)OR"
	                   + " o.dateoc = :dateoc)";

	        

	        Query<Visiter> query = session.createQuery(hql, Visiter.class);

	        query.setParameter("recherche", "%" + recherche + "%");

	        if (dateoc != null) {
	            query.setParameter("dateoc", dateoc);
	        }

	        return query.list();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}


    // Original search method for backwards compatibility
    public List<Visiter> searchVisiter(Date dateoc) {
        return searchVisiter(null, dateoc);
    }
    
    // Check if a room is already occupied on a given date
    public boolean isSalleOccupee(int salleId, Date date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Visiter o WHERE o.codesal.id = :salleId AND o.dateoc = :date";
            Query<Visiter> query = session.createQuery(hql, Visiter.class);
            query.setParameter("salleId", salleId);
            query.setParameter("date", date);
            
            List<Visiter> result = query.list();
            return !result.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all Visiter entries
    public List<Visiter> getAllVisiter() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Visiter", Visiter.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    // Get Visiter by ID
    public Visiter getVisiterById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Visiter.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Save new Visiter
    @SuppressWarnings("deprecation")
	public void saveVisiter(Visiter visiter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(visiter);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Update existing Visiter
    @SuppressWarnings("deprecation")
	public void updateVisiter(Visiter visiter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(visiter);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Delete Visiter
    @SuppressWarnings("deprecation")
	public void deleteVisiter(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Visiter visiter = session.get(Visiter.class, id);
            if (visiter != null) {
                session.delete(visiter);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
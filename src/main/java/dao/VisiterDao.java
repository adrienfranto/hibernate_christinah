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
    public List<Visiter> searchOccuper(String recherche, Date dateoc) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("FROM Visiter o WHERE 1=1"); // Start with true condition
            
            // Build the query dynamically based on provided parameters
            if (recherche != null && !recherche.isEmpty()) {
                hql.append(" AND (o.codeprof.nom LIKE :nomProf OR o.codesal.codesal LIKE :codeSalle OR o.codesal.designation LIKE :designationSalle )");
            }
            
            if (dateoc != null) {
                hql.append(" AND o.dateoc = :dateoc");
            }
            
            Query<Visiter> query = session.createQuery(hql.toString(), Visiter.class);
            
            // Set parameters only if they're provided
            if (recherche != null && !recherche.isEmpty()) {
                query.setParameter("nomProf", "%" + recherche + "%");
                query.setParameter("codeSalle", "%" + recherche + "%");
                query.setParameter("designationSalle", "%" + recherche + "%");
            }
            
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
    public List<Visiter> searchOccuper(Date dateoc) {
        return searchOccuper(null, dateoc);
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
    public List<Visiter> getAllOccuper() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Visiter", Visiter.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    // Get Visiter by ID
    public Visiter getOccuperById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Visiter.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Save new Visiter
    @SuppressWarnings("deprecation")
	public void saveOccuper(Visiter visiter) {
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
	public void updateOccuper(Visiter visiter) {
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
	public void deleteOccuper(int id) {
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
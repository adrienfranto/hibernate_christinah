package util;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import model.Visiter;
import model.Medecin;
import model.Patient;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	
	
	public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Configuration des propriétés Hibernate pour PostgreSQL
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/gestion_medecin");
                settings.put(Environment.USER, "postgres");
                settings.put(Environment.PASS, "123456789"); // Remplacez par votre mot de passe PostgreSQL
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");

                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.FORMAT_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "update"); // "update" pour éviter la perte de données

                configuration.setProperties(settings);

                // Ajout des classes annotées
                configuration.addAnnotatedClass(Medecin.class);
                configuration.addAnnotatedClass(Patient.class);
                configuration.addAnnotatedClass(Visiter.class);
                
                // Création du ServiceRegistry
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                // Création de la SessionFactory avec le serviceRegistry
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}

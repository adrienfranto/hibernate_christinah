package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="patient")
public class Patient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="codepat")
	private String codepat;
   
	@Column(name="nom")
	private String nom;

	@Column(name="prenom")
	private String prenom;
	
	@Column(name="sexe")
	private String sexe;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodepat() {
		return codepat;
	}

	public void setCodepat(String codepat) {
		this.codepat = codepat;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String grade) {
		this.sexe = grade;
	}

	public Patient(int id, String codepat, String nom, String prenom, String sexe) {
		super();
		this.id = id;
		this.codepat = codepat;
		this.nom = nom;
		this.prenom = prenom;
		this.sexe = sexe;
	}

	public Patient(String codepat, String nom, String prenom, String sexe) {
		super();
		this.codepat = codepat;
		this.nom = nom;
		this.prenom = prenom;
		this.sexe = sexe;
	}

	public Patient() {
		super();
	}

	
	
	
}

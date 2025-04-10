package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="medecin")
public class Medecin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="codemed")
	private String codemed;
   
	@Column(name="nom")
	private String nom;

	@Column(name="prenom")
	private String prenom;
	
	@Column(name="grade")
	private String grade;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodemed() {
		return codemed;
	}

	public void setCodemed(String codemed) {
		this.codemed = codemed;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Medecin(int id, String codemed, String nom, String prenom, String grade) {
		super();
		this.id = id;
		this.codemed = codemed;
		this.nom = nom;
		this.prenom = prenom;
		this.grade = grade;
	}

	public Medecin(String codemed, String nom, String prenom, String grade) {
		super();
		this.codemed = codemed;
		this.nom = nom;
		this.prenom = prenom;
		this.grade = grade;
	}

	public Medecin() {
		super();
	}

	
	
	
}

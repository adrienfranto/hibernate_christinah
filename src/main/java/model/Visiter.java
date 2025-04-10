package model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="visiter")
public class Visiter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="dateoc")
	private Date dateoc;
	
	@ManyToOne
	@JoinColumn(name="medecin")
	private Medecin medecin;

	@ManyToOne
	@JoinColumn(name="patient")
	private Patient patient;
	
	

	public Visiter() {
		super();
	}

	

	public Visiter(int id, Date dateoc, Medecin medecin, Patient patient) {
		super();
		this.id = id;
		this.dateoc = dateoc;
		this.medecin = medecin;
		this.patient = patient;
	}


	

	public Visiter(Date dateoc, Medecin medecin, Patient patient) {
		super();
		this.dateoc = dateoc;
		this.medecin = medecin;
		this.patient = patient;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateoc() {
		return dateoc;
	}

	public void setDateoc(Date dateoc) {
		this.dateoc = dateoc;
	}

	public Medecin getMedecin() {
		return medecin;
	}

	public void setMedecin(Medecin medecin) {
		this.medecin = medecin;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	
}

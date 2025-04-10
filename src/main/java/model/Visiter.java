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
	@JoinColumn(name="codeprof")
	private Medecin codeprof;

	@ManyToOne
	@JoinColumn(name="codesal")
	private Patient codesal;
	
	

	public Visiter() {
		super();
	}

	public Visiter(Date dateoc, Medecin codeprof, Patient codesal) {
		super();
		this.dateoc = dateoc;
		this.codeprof = codeprof;
		this.codesal = codesal;
	}
	public Visiter(Medecin medecin, Patient patient, Date date) {
	    super();
	    this.codeprof = medecin;
	    this.codesal = patient;
	    this.dateoc = date;
	}

	public Visiter(int id, Date dateoc, Medecin codeprof, Patient codesal) {
		super();
		this.id = id;
		this.dateoc = dateoc;
		this.codeprof = codeprof;
		this.codesal = codesal;
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

	public Medecin getCodeprof() {
		return codeprof;
	}

	public void setCodeprof(Medecin codeprof) {
		this.codeprof = codeprof;
	}

	public Patient getCodesal() {
		return codesal;
	}

	public void setCodesal(Patient codesal) {
		this.codesal = codesal;
	}
}

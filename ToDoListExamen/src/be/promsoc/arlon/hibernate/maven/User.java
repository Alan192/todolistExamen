package be.promsoc.arlon.hibernate.maven;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity 
@Table(name = "user")
public class User {

	@Id 
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "created_date")
	private Date createdDate;

	public User(String userName) {
		super();
		this.userName = userName;
		createdDate = new Date();
	}

	public User() {
		// TODO Auto-generated constructor stub
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/////
	@OneToMany(mappedBy = "user")
	private Set<Task> tasks = new HashSet<Task>();

	public void addTask(Task task) {
		this.tasks.add(task);
		task.setUser(this);
	}
/*
	/////
	@OneToMany(mappedBy = "user")
	private Set<Pack> packs = new HashSet<Pack>();

	public void addPack(Pack pack) {
		this.packs.add(pack);
		pack.setUser(this);
	}*/

	
}

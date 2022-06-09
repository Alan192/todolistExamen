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
@Table(name = "category")
public class Category {

	@Id
	@Column(name = "category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int categoryId;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "color_name")
	private String colorName;

	@Column(name = "created_date")
	private Date createdDate;

	public Category(String categoryName, String colorName) {
		super();
		this.categoryName = categoryName;
		this.colorName = colorName;
		createdDate = new Date();
	}

	public Category(int categoryId, String categoryName, String colorName) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.colorName = colorName;
		createdDate = new Date();
	}

	public Category() {
		// TODO Auto-generated constructor stub
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/////
	@OneToMany(mappedBy = "category")
	private Set<Task> tasks = new HashSet<Task>();

	public void addTask(Task task) {
		this.tasks.add(task);
		task.setCategory(this);
	}
/*
	/////
	@OneToMany(mappedBy = "category")
	private Set<Pack> packs = new HashSet<Pack>();

	public void addPack(Pack pack) {
		this.packs.add(pack);
		pack.setcategory(this);
	}*/

	
}

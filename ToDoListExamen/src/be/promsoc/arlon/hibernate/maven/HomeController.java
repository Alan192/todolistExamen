package be.promsoc.arlon.hibernate.maven;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class HomeController {
	User userObj;
	Session sessionObj;
	SessionFactory sessionFactoryObj;
	MqttClient client;

	Scanner myScanner = new Scanner(System.in);

	private SessionFactory buildSessionFactory() {
		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		return sf;
	}

	public void sendMessage(String content) {
		try {
			client = new MqttClient("tcp://localhost:1883", "pahomqttpublish1");
			client.connect();
			MqttMessage message = new MqttMessage();
			message.setPayload(content.getBytes());
			client.publish("todolist", message);
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}


	public void makeAChoice() {

		System.out.println("What do you want to do? 1-Create 2-Read 3-Update 4-Delete");
		String firstChoice = myScanner.nextLine();
		if (firstChoice.equals("1")) {
			System.out.println("What will you create today? 1-Category 2-Task 3-User");
			String choice = myScanner.nextLine();

			if (choice.equals("1") || choice.equals("Category") || choice.equals("category")) {
				this.createCategory();
			} else if (choice.equals("2") || choice.equals("Task") || choice.equals("task")) {
				this.createTask();
			} else if (choice.equals("3") || choice.equals("User") || choice.equals("user")) {
				this.createUser();
			} else {
				System.out.println("\n I can't let you do that, Dave");
			}
		} else if (firstChoice.equals("2")) {
			System.out.println("Consult which table? 1-Categories 2-Tasks 3-Users");
			String choice = myScanner.nextLine();

			if (choice.equals("1") || choice.equals("Category") || choice.equals("category")) {
				this.readCategories();
			} else if (choice.equals("2") || choice.equals("Task") || choice.equals("task")) {
				this.readTasks();
			} else if (choice.equals("3") || choice.equals("User") || choice.equals("user")) {
				this.readUsers();
			} else {
				System.out.println("\n I can't let you do that, Dave");
			}
		}else if (firstChoice.equals("3")) {
			System.out.println("What do you want to update? 1-Categories 2-Tasks 3-Users Or did you complete your task? (4)");
			String choice = myScanner.nextLine();

			if (choice.equals("1") || choice.equals("Category") || choice.equals("category")) {
				this.updateCategory();
			} else if (choice.equals("2") || choice.equals("Task") || choice.equals("task")) {
				this.updateTask();
			} else if (choice.equals("3") || choice.equals("User") || choice.equals("user")) {
				this.updateUser();
			} else if (choice.equals("4") || choice.equals("Finish") || choice.equals("finish")) {
				this.finishTask();
			} else {
				System.out.println("\n I can't let you do that, Dave");
			}
		} else if (firstChoice.equals("4")) {
			System.out.println("What will you delete? 1-Categories 2-Tasks 3-Users");
			String choice = myScanner.nextLine();

			if (choice.equals("1") || choice.equals("Category") || choice.equals("category")) {
				this.deleteCategory();
			} else if (choice.equals("2") || choice.equals("Task") || choice.equals("task")) {
				this.deleteTask();
			} else if (choice.equals("3") || choice.equals("User") || choice.equals("user")) {
				this.deleteUser();
			} else {
				System.out.println("\n I can't let you do that, Dave");
			}
		}else {
			System.out.println("\n I can't let you do that, Dave");
		}

	}

	//Tasks

	//Create
	public void createTask() {
		System.out.println("Creating Task .\n");
		try {

			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			this.readUsers();
			System.out.println("Who are you? (please give me your id)");
			String taskcreator = myScanner.nextLine();

			System.out.println("Please specify the task name");
			String taskname = myScanner.nextLine();

			System.out.println("Please enter a description");
			String taskdescription = myScanner.nextLine();

			System.out.println("When is the deadline? (dd/MM/yyyy)");
			Date tasktargetdate = new SimpleDateFormat("dd/MM/yyyy").parse(myScanner.nextLine());

			this.readCategories();
			System.out.println("Please assign a category, kindly specify its ID");
			String categoryId = myScanner.nextLine();

			String sqlCat = "SELECT C FROM Category C WHERE C.categoryId = " + categoryId;
			Query queryCat = sessionObj.createQuery(sqlCat);
			List<Category> categories = queryCat.list();

			Category currentCat;
			currentCat = categories.get(0);

			this.readUsers();
			System.out.println("Please assign the task to a user, kindly specify their ID");
			String userId = myScanner.nextLine();

			String sqlUser = "SELECT U FROM User U WHERE U.userId = " + userId;
			Query queryUser = sessionObj.createQuery(sqlUser);
			List<User> users = queryUser.list();

			User currentUser;
			currentUser = users.get(0);

			Task task;
			task = new Task(taskname, taskdescription, tasktargetdate, taskcreator);
			sessionObj.save(task);

			currentCat.addTask(task);
			currentUser.addTask(task);

			/*
			 * int id = results.get(0).getcategoryid(); String name =
			 * results.get(0).getcategoryname(); String color =
			 * results.get(0).getColorname(); currentCat = new Category(id, name, color);
			 * sessionObj.update(currentCat); //List<Category> categories =
			 * sessionObj.createCriteria(Category.class).list();
			 * 
			 * /* String sql = "SELECT C FROM Category C"; Query query =
			 * sessionObj.createQuery(sql); List<Category> results = query.list();
			 */

			System.out.println("\nRecords Saved Successfully To The Database.\n");

			sendMessage("La tâche " + taskname + " de categorie " + currentCat.getCategoryName() + " a bien ete cree");

			// Committing The Transactions To The Database
			sessionObj.getTransaction().commit();

		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}
	}
	
	//Read
	public void readTasks() {
		sessionObj = buildSessionFactory().openSession();
		sessionObj.beginTransaction();

		String sql = "SELECT T FROM Task T";
		Query query = sessionObj.createQuery(sql);
		List<Task> tasks = query.list();

		for (int i = 0; i < tasks.size(); i++) {
			System.out.println(tasks.get(i).getTaskId() + ") " + tasks.get(i).getTaskName() + " from the category" + tasks.get(i).getCategory().getCategoryName() + " that is the responsibility of " + tasks.get(i).getUser().getUserName());
		}
	}

	public void readYourTasks(int id) {
		sessionObj = buildSessionFactory().openSession();
		sessionObj.beginTransaction();

		String sql = "SELECT T FROM Task T WHERE T.user.userId = " + id;
		Query query = sessionObj.createQuery(sql);
		List<Task> tasks = query.list();

		for (int i = 0; i < tasks.size(); i++) {
			System.out.println(tasks.get(i).getTaskId() + ") " + tasks.get(i).getTaskName() + " from the category: " + tasks.get(i).getCategory().getCategoryName());
		}
	}

	//Update
	public void updateTask() {
		System.out.println("Updating Task.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();
			
			this.readTasks();
			System.out.println("Which Task will you update?");
			String taskId = myScanner.nextLine();

			String sqlTask= "SELECT T FROM Task T WHERE T.taskId = " + taskId;
			Query queryTask = sessionObj.createQuery(sqlTask);
			List<Task> tasks = queryTask.list();

			System.out.println("What is the new name?");
			String taskname = myScanner.nextLine();
			
			System.out.println("Please enter a description");
			String taskdescription = myScanner.nextLine();
			
			Task localTask;
			localTask = tasks.get(0);
			
			localTask.setTaskName(taskname);
			localTask.setTaskDescription(taskdescription);
			
			sessionObj.update(localTask);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}

	public void finishTask() {
		System.out.println("Updating Task.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();


			this.readUsers();
			
			System.out.println("Who are you?");
			int userId = myScanner.nextInt();
			
			this.readYourTasks(userId);
			
			System.out.println("Which one do you want to finish?");
			int taskId = myScanner.nextInt();
			

			String sqlTask= "SELECT T FROM Task T WHERE T.taskId = " + taskId;
			Query queryTask = sessionObj.createQuery(sqlTask);
			List<Task> tasks = queryTask.list();
			
			Task localTask;
			localTask = tasks.get(0);
			
			localTask.setTaskStatus("finished");
			
			sessionObj.update(localTask);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}

	//Delete
	public void deleteTask() {
		System.out.println("Deleting Task.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			this.readTasks();
			System.out.println("Which Task will you delete?");
			String taskId = myScanner.nextLine();
			
			String sqlTask= "SELECT T FROM Task T WHERE T.taskId = " + taskId;
			Query queryTask = sessionObj.createQuery(sqlTask);
			List<Task> tasks = queryTask.list();

			Task localTask;
			localTask = tasks.get(0);
			
			sessionObj.delete(localTask);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}
	
	
	//Categories
	
	//Create
	public void createCategory() {
		System.out.println("Creating Category .\n");
		try {

			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			System.out.println("Please specify the category name: ");

			String categoryName = myScanner.nextLine();

			System.out.println("Please specify a color : ");

			String categoryColor = myScanner.nextLine();

			Category category;
			category = new Category(categoryName, categoryColor);
			sessionObj.save(category);

			System.out.println("\nRecords Saved Successfully To The Database.\n");

			// Committing The Transactions To The Database
			sessionObj.getTransaction().commit();

		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}
	
	//Read
	public void readCategories() {
		sessionObj = buildSessionFactory().openSession();
		sessionObj.beginTransaction();

		String sql = "SELECT C FROM Category C";
		Query query = sessionObj.createQuery(sql);
		List<Category> categories = query.list();

		// List<Category> categories = sessionObj.createCriteria(Category.class).list();

		for (int i = 0; i < categories.size(); i++) {
			System.out.println(categories.get(i).getCategoryId() + ") " + categories.get(i).getCategoryName() + " of the color " + categories.get(i).getColorName());
		}
	}
	
	//Update
	public void updateCategory() {
		System.out.println("Updating Category.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			this.readCategories();
			System.out.println("Which Category will you update?");
			String id = myScanner.nextLine();
			
			String sqlCategory = "SELECT C FROM Category C WHERE C.categoryId = " + id;
			Query queryCategory = sessionObj.createQuery(sqlCategory);
			List<Category> categories = queryCategory.list();

			System.out.println("What is the new name?");

			String catname = myScanner.nextLine();


			System.out.println("What is the new color?");

			String catcolor = myScanner.nextLine();
			
			Category localCategory;
			localCategory = categories.get(0);
			localCategory.setCategoryName(catname);
			localCategory.setColorName(catcolor);
			sessionObj.update(localCategory);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}

	//Delete
	public void deleteCategory() {
		System.out.println("Deleting Category.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			this.readCategories();
			System.out.println("Which Category will you delete?");
			String id = myScanner.nextLine();
			
			String sqlCategory = "SELECT C FROM Category C WHERE C.categoryId= " + id;
			Query queryCategory = sessionObj.createQuery(sqlCategory);
			List<Category> categories= queryCategory.list();

			Category localCategory;
			localCategory = categories.get(0);
			
			String sqlTask = "SELECT T FROM Task T";
			Query queryTask =sessionObj.createQuery(sqlTask);
			List<Task> tasks= queryTask.list();
			
			for(int i = 0; i < tasks.size(); i++) {
				if(tasks.get(i).getUser().getUserId() == localCategory.getCategoryId()) sessionObj.delete(tasks.get(i));
			}
			
			sessionObj.delete(localCategory);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}
	
	
	//Users
	
	//Create
	public void createUser() {
		System.out.println("Creating user .\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			System.out.println("What is their name?");

			String username = myScanner.nextLine();
			User user;
			user = new User(username);
			sessionObj.save(user);

			String content = "L'Utilisateur " + username + " a ete cree.";
			sendMessage(content);

			System.out.println("\nRecords Saved Successfully To The Database.\n");

			// Committing The Transactions To The Database
			sessionObj.getTransaction().commit();
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}
	}
	
	//Read
	public void readUsers() {
		sessionObj = buildSessionFactory().openSession();
		sessionObj.beginTransaction();


		String sql = "SELECT U FROM User U";
		Query query = sessionObj.createQuery(sql);
		List<User> users = query.list();

		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i).getUserId() + " " + users.get(i).getUserName());
		}

	}

	//Update
	public void updateUser() {
		System.out.println("Updating User.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			this.readUsers();
			System.out.println("Which User will you update?");
			String id = myScanner.nextLine();
			
			String sqlUser = "SELECT U FROM User U WHERE U.userId = " + id;
			Query queryUser = sessionObj.createQuery(sqlUser);
			List<User> users = queryUser.list();

			System.out.println("What is their new name?");

			String username = myScanner.nextLine();
			User localUser;
			localUser = users.get(0);
			localUser.setUserName(username);
			sessionObj.update(localUser);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}

	//Delete
	public void deleteUser() {
		System.out.println("Deleting User.\n");
		try {
			sessionObj = buildSessionFactory().openSession();
			sessionObj.beginTransaction();

			this.readUsers();
			System.out.println("Which User will you delete?");
			String id = myScanner.nextLine();

			String sqlUser = "SELECT U FROM User U WHERE U.userId = " + id;
			Query queryUser = sessionObj.createQuery(sqlUser);
			List<User> users = queryUser.list();

			User localUser;
			localUser = users.get(0);
			
			String sqlTask = "SELECT T FROM Task T";
			Query queryTask =sessionObj.createQuery(sqlTask);
			List<Task> tasks= queryTask.list();
			
			for(int i = 0; i < tasks.size(); i++) {
				if(tasks.get(i).getUser().getUserId() == localUser.getUserId()) sessionObj.delete(tasks.get(i));
			}
			
			sessionObj.delete(localUser);
			
			sessionObj.getTransaction().commit();
			
		} catch (Exception sqlException) {
			sqlException.printStackTrace();
			if (sessionObj != null && null != sessionObj.getTransaction()) {
				System.out.println("\nTransaction Is Being Rolled Back.");
				sessionObj.getTransaction().rollback();
			}
			sqlException.printStackTrace();
		} finally {
			if (sessionObj != null) {
				sessionObj.close();
			}
		}

	}

}

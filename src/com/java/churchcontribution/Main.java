package com.java.churchcontribution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import net.proteanit.sql.DbUtils;

public class Main {
	public static class ex {
		public static int days = 0;
	}

	public static void main(String[] args) {

		login();
		// create();
	}

	public static Connection connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// System.out.println("Loaded driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=");
			// System.out.println("Connected to MySQL");
			return con;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void login() {

		final JFrame f = new JFrame("Login");// creating instance of JFrame
		JLabel l1, l2;
		l1 = new JLabel("Username"); // Create label Username
		l1.setBounds(30, 15, 100, 30); // x axis, y axis, width, height

		l2 = new JLabel("Password"); // Create label Password
		l2.setBounds(30, 50, 100, 30);

		final JTextField F_user = new JTextField(); // Create text field for username
		F_user.setBounds(110, 15, 200, 30);

		final JPasswordField F_pass = new JPasswordField(); // Create text field for password
		F_pass.setBounds(110, 50, 200, 30);

		JButton login_but = new JButton("Login");// creating instance of JButton for Login Button
		login_but.setBounds(130, 90, 80, 25);// Dimensions for button
		login_but.addActionListener(new ActionListener() { // Perform action

			public void actionPerformed(ActionEvent e) {

				String username = F_user.getText(); // Store username entered by the user in the variable "username"
				String password = F_pass.getText(); // Store password entered by the user in the variable "password"

				if (username.equals("")) // If username is null
				{
					JOptionPane.showMessageDialog(null, "Please enter username"); // Display dialog box with the message
				} else if (password.equals("")) // If password is null
				{
					JOptionPane.showMessageDialog(null, "Please enter password"); // Display dialog box with the message
				} else { // If both the fields are present then to login the user, check wether the user
							// exists already
							// System.out.println("Login connect");
					Connection connection = connect(); // Connect to the database
					try {
						Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
								ResultSet.CONCUR_READ_ONLY);
						stmt.executeUpdate("USE CHURCH"); // Use the database with the name "church"
						String st = ("SELECT * FROM USERS WHERE USERNAME='" + username + "' AND PASSWORD='" + password
								+ "'"); // Retreive username and passwords from users
						ResultSet rs = stmt.executeQuery(st); // Execute query
						if (rs.next() == false) { // Move pointer below
							System.out.print("No user");
							JOptionPane.showMessageDialog(null, "Wrong Username/Password!"); // Display Message

						} else {
							f.dispose();
							rs.beforeFirst(); // Move the pointer above
							while (rs.next()) {
								String admin = rs.getString("ADMIN"); // user is admin
								// System.out.println(admin);
								String UID = rs.getString("UID"); // Get user ID of the user
								if (admin.equals("1")) { // If boolean value 1
									admin_menu(); // redirect to admin menu
								} else {
									user_menu(UID); // redirect to user menu for that user ID
								}
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		f.add(F_pass); // add password
		f.add(login_but);// adding button in JFrame
		f.add(F_user); // add user
		f.add(l1); // add label1 i.e. for username
		f.add(l2); // add label2 i.e. for password

		f.setSize(400, 180);// 400 width and 500 height
		f.setLayout(null);// using no layout managers
		f.setVisible(true);// making the frame visible
		f.setLocationRelativeTo(null);

	}

	public static void create() {
		try {
			Connection connection = connect();
			ResultSet resultSet = connection.getMetaData().getCatalogs();
			// iterate each catalog in the ResultSet
			while (resultSet.next()) {
				// Get the database name, which is at position 1
				String databaseName = resultSet.getString(1);
				if (databaseName.equals("church")) {
					// System.out.print("yes");
					Statement stmt = connection.createStatement();
					// Drop database if it pre-exists to reset the complete database
					String sql = "DROP DATABASE church";
					stmt.executeUpdate(sql);
				}
			}
			Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			String sql = "CREATE DATABASE CHURCH"; // Create Database
			stmt.executeUpdate(sql);
			stmt.executeUpdate("USE CHURCH"); // Use Database
			// Create Users Table
			String sql1 = "CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(30), PASSWORD VARCHAR(30), ADMIN BOOLEAN)";
			stmt.executeUpdate(sql1);
			// Insert into users table
			stmt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, ADMIN) VALUES('admin','admin',TRUE)");
			// Create church member table
			stmt.executeUpdate(
					"CREATE TABLE CHURCHMEMBERS(CID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, MNAME VARCHAR(50), AGE VARCHAR(20), GENDER VARCHAR(20),   CONTRIBUTION INT)");
			// Create contribution Table
			stmt.executeUpdate(
					"CREATE TABLE CONTRIBUTION(IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UID INT, CID INT, CONTRIBUTION_DATE VARCHAR(20), DEADLINE_DATE VARCHAR(20), PERIOD INT, EXTRACHARGE INT)");
			// Insert into church member table
			stmt.executeUpdate(
					"INSERT INTO CHURCHMEMBERS(MNAME, AGE, GENDER,  CONTRIBUTION) VALUES ('Nakasi Pauline', '20', 'Female', 2000), ('Toskin Christopher', '19', 'Male', 1000), ('Ngasire Emmanuela', '29', 'Female', 20000),('Ngabo Klaus', '19', 'Male', 3000) ");

			resultSet.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void user_menu(final String UID) {

		final JFrame f = new JFrame("User Functions"); // Give dialog box name as User functions
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit user menu on closing
		// the dialog box
		JButton view_but = new JButton("View Church Members");// creating instance of JButton
		view_but.setBounds(20, 20, 120, 25);// x axis, y axis, width, height
		view_but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFrame f = new JFrame("Church Member List"); // View church member stored in database
				// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Connection connection = connect();
				String sql = "select * from CHURCHMEMBERS"; // Retreive data from database
				try {
					Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY); // connect to database
					stmt.executeUpdate("USE CHURCH"); // use librabry
					stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					JTable churchmember_list = new JTable(); // show data in table format
					churchmember_list.setModel(DbUtils.resultSetToTableModel(rs));

					JScrollPane scrollPane = new JScrollPane(churchmember_list); // enable scroll bar

					f.add(scrollPane); // add scroll bar
					f.setSize(800, 400); // set dimensions of view church member frame
					f.setVisible(true);
					f.setLocationRelativeTo(null);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				}

			}
		});

		JButton my_churchmemberaccounts = new JButton("Church Member Accounts");// creating instance of JButton
		my_churchmemberaccounts.setBounds(150, 20, 120, 25);// x axis, y axis, width, height
		my_churchmemberaccounts.addActionListener(new ActionListener() { // Perform action
			public void actionPerformed(ActionEvent e) {

				final JFrame f = new JFrame("Church Member Accounts"); // View church member contribution by user
				// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				int UID_int = Integer.parseInt(UID); // Pass user ID

				
				Connection connection = connect(); // connect to database
				// retrieve data
				String sql = "select distinct contribution.*,churchmembers.mname,churchmembers.gender,churchmembers.contribution from contribution,churchmembers "
						+ "where ((contribution.uid=" + UID_int
						+ ") and (churchmembers.cid in (select cid fromcontribution where contribution.uid=" + UID_int
						+ "))) group by iid";
				String sql1 = "select cid from contribution where uid=" + UID_int;
				try {
					Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					// use database
					stmt.executeUpdate("USE CHURCH");
					stmt = connection.createStatement();
					// store in array
					ArrayList churchmembers_list = new ArrayList();

					ResultSet rs = stmt.executeQuery(sql);
					JTable churchmember_list = new JTable(); // store data in table format
					churchmember_list.setModel(DbUtils.resultSetToTableModel(rs));
					// enable scroll bar
					JScrollPane scrollPane = new JScrollPane(churchmember_list);

					f.add(scrollPane); // add scroll bar
					f.setSize(800, 400); // set dimensions of my church member frame
					f.setVisible(true);
					f.setLocationRelativeTo(null);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				}

			}
		});

		f.add(my_churchmemberaccounts); // add my church member
		f.add(view_but); // add view church member
		f.setSize(300, 100);// 400 width and 500 height
		f.setLayout(null);// using no layout managers
		f.setVisible(true);// making the frame visible
		f.setLocationRelativeTo(null);
	}

	public static void admin_menu() {

		final JFrame f = new JFrame("Admin Panel"); // Give dialog box name as admin functions
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //

		JButton create_but = new JButton("Create/Reset");// creating instance of JButton to create or reset database
		create_but.setBounds(480, 60, 200, 25);// x axis, y axis, width, height
		create_but.addActionListener(new ActionListener() { // Perform action
			public void actionPerformed(ActionEvent e) {

				create(); // Call create function
				JOptionPane.showMessageDialog(null, "Your Database has been Created/Reset!"); // Open a dialog box and
																								// display the message

			}
		});

		JButton view_but = new JButton("View Church Members");// creating instance of JButton to view church member
		view_but.setBounds(20, 20, 200, 25);// x axis, y axis, width, height
		view_but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final JFrame f = new JFrame("All Church Members Registerd");
				// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Connection connection = connect(); // connect to database
				String sql = "select * from CHURCHMEMBERS"; // select all church member
				try {
					Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					stmt.executeUpdate("USE CHURCH"); // use database
					stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					JTable churchmember_list = new JTable(); // view data in table format
					churchmember_list.setModel(DbUtils.resultSetToTableModel(rs));
					// mention scroll bar
					JScrollPane scrollPane = new JScrollPane(churchmember_list);

					f.add(scrollPane); // add scrollpane
					f.setSize(800, 400); // set size for frame
					f.setVisible(true);
					f.setLocationRelativeTo(null);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				}

			}
		});

		JButton users_but = new JButton("View Users");// creating instance of JButton to view users
		users_but.setBounds(280, 20, 200, 25);// x axis, y axis, width, height
		users_but.addActionListener(new ActionListener() { // Perform action on click button
			public void actionPerformed(ActionEvent e) {

				JFrame f = new JFrame("Users List");
				// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Connection connection = connect();
				String sql = "select * from users"; // retrieve all users
				try {
					Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					stmt.executeUpdate("USE CHURCH"); // use database
					stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					JTable church_list = new JTable();
					church_list.setModel(DbUtils.resultSetToTableModel(rs));
					// mention scroll bar
					JScrollPane scrollPane = new JScrollPane(church_list);

					f.add(scrollPane); // add scrollpane
					f.setSize(800, 400); // set size for frame
					f.setVisible(true);
					f.setLocationRelativeTo(null);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				}

			}
		});

		JButton issued_but = new JButton("Contribution List");// creating instance of JButton to view the issued church member
		issued_but.setBounds(480, 20, 200, 25);// x axis, y axis, width, height
		issued_but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final JFrame f = new JFrame("Contribution List");
				// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Connection connection = connect();
				String sql = "select * from contribution";
				try {
					Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					stmt.executeUpdate("USE CHURCH");
					stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					JTable church_list = new JTable();
					church_list.setModel(DbUtils.resultSetToTableModel(rs));

					JScrollPane scrollPane = new JScrollPane(church_list);

					f.add(scrollPane);
					f.setSize(1000, 400);
					f.setVisible(true);
					f.setLocationRelativeTo(null);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				}

			}
		});

		JButton add_user = new JButton("Add User"); // creating instance of JButton to add users
		add_user.setBounds(20, 60, 200, 25); // set dimensions for button

		add_user.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final JFrame g = new JFrame("Enter User Details"); // Frame to enter user details
				// g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				// Create label
				JLabel l1, l2;
				l1 = new JLabel("Username"); // label 1 for username
				l1.setBounds(30, 15, 100, 30);

				l2 = new JLabel("Password"); // label 2 for password
				l2.setBounds(30, 50, 100, 30);

				// set text field for username
				final JTextField F_user = new JTextField();
				F_user.setBounds(110, 15, 200, 30);

				// set text field for password
				final JPasswordField F_pass = new JPasswordField();
				F_pass.setBounds(110, 50, 200, 30);
				// set radio button for admin
				final JRadioButton a1 = new JRadioButton("Admin");
				a1.setBounds(55, 80, 200, 30);
				// set radio button for user
				JRadioButton a2 = new JRadioButton("User");
				a2.setBounds(130, 80, 200, 30);
				// add radio buttons
				ButtonGroup bg = new ButtonGroup();
				bg.add(a1);
				bg.add(a2);

				JButton create_but = new JButton("Create");// creating instance of JButton for Create
				create_but.setBounds(130, 130, 80, 25);// x axis, y axis, width, height
				create_but.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						String username = F_user.getText();
						String password = F_pass.getText();
						boolean admin = false;

						if (a1.isSelected()) {
							admin = true;
						}

						Connection connection = connect();

						try {
							Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							stmt.executeUpdate("USE CHURCH");
							stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('" + username + "','"
									+ password + "'," + admin + ")");
							JOptionPane.showMessageDialog(null, "User added!");
							g.dispose();

						}

						catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}

					}

				});

				g.add(create_but);
				g.add(a2);
				g.add(a1);
				g.add(l1);
				g.add(l2);
				g.add(F_user);
				g.add(F_pass);
				g.setSize(350, 200);// 400 width and 500 height
				g.setLayout(null);// using no layout managers
				g.setVisible(true);// making the frame visible
				g.setLocationRelativeTo(null);

			}
		});

		JButton add_churchmember = new JButton("Add Church Member"); // creating instance of JButton for adding church member
		add_churchmember.setBounds(20, 100, 200, 25);

		add_churchmember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// set frame wot enter church member details
				final JFrame g = new JFrame("Enter Church Member Details");
				// g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				// set labels
				JLabel l1, l2, l3, l4;
				l1 = new JLabel("Church Member Name"); // label 1 for Church member name
				l1.setBounds(30, 15, 200, 30);

				l2 = new JLabel("Age"); // label 2 for Age
				l2.setBounds(30, 40, 200, 30);

				l3 = new JLabel("Gender"); // label 3 for Gender
				l3.setBounds(30, 70, 200, 30);

				l4 = new JLabel("Contribution"); // label 4 for Contribution
				l4.setBounds(30, 100, 200, 30);

				// set text field for church member name
				final JTextField F_mname = new JTextField();
				F_mname.setBounds(250, 15, 200, 30);

				// set text field for age
				final JTextField F_age = new JTextField();
				F_age.setBounds(250, 40, 200, 30);

				// set text field for gender
				final JTextField F_gender = new JTextField();
				F_gender.setBounds(250, 70, 200, 30);

				// set text field for price
				final JTextField F_contribution = new JTextField();
				F_contribution.setBounds(250, 100, 200, 30);

				JButton create_but = new JButton("Submit");// creating instance of JButton to submit details
				create_but.setBounds(130, 150, 80, 25);// x axis, y axis, width, height
				create_but.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						// assign the church member name, genre, price
						String mname = F_mname.getText();
						String age = F_age.getText();
						String gender = F_gender.getText();
						String contribution = F_contribution.getText();
						// convert price of integer to int
						int contribution_int = Integer.parseInt(contribution);

						Connection connection = connect();

						try {
							Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							stmt.executeUpdate("USE CHURCH");
							stmt.executeUpdate("INSERT INTO CHURCHMEMBERS(MNAME, AGE, GENDER,CONTRIBUTION) VALUES ('"
									+ mname + "','" + age + "','" + gender + "'," + contribution_int + ")");
							JOptionPane.showMessageDialog(null, "Church Member added!");
							g.dispose();

						}

						catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}

					}

				});

				g.add(l3);
				g.add(create_but);
				g.add(l1);
				g.add(l2);
				// g.add(l3);
				g.add(l4);
				g.add(F_mname);
				g.add(F_age);
				g.add(F_gender);
				g.add(F_contribution);
				g.setSize(550, 400);// 400 width and 500 height
				g.setLayout(null);// using no layout managers
				g.setVisible(true);// making the frame visible
				g.setLocationRelativeTo(null);

			}
		});

		JButton issue_contribution = new JButton("New Contribution"); // creating instance of JButton to contribution church member
		issue_contribution.setBounds(280, 60, 200, 25);

		issue_contribution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// enter details
				final JFrame g = new JFrame("Enter Contribution Details");
				// g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				// create labels
				JLabel l1, l2, l3, l4;
				l1 = new JLabel("Church Member ID(CID)"); // Label 1 for Church ID
				l1.setBounds(30, 15, 200, 30);

				l2 = new JLabel("User ID(UID)"); // Label 2 for user ID
				l2.setBounds(30, 53, 200, 30);

				l3 = new JLabel("Period(days)"); // Label 3 for period
				l3.setBounds(30, 90, 200, 30);

				l4 = new JLabel("Contribution Date(DD-MM-YYYY)"); // Label 4 for contribution date
				l4.setBounds(30, 127, 250, 30);

				final JTextField F_cid = new JTextField();
				F_cid.setBounds(310, 15, 200, 30);

				final JTextField F_uid = new JTextField();
				F_uid.setBounds(310, 53, 200, 30);

				final JTextField F_period = new JTextField();
				F_period.setBounds(310, 90, 200, 30);

				final JTextField F_contribution = new JTextField();
				F_contribution.setBounds(380, 130, 200, 30);

				JButton create_but = new JButton("Submit");// creating instance of JButton
				create_but.setBounds(130, 170, 80, 25);// x axis, y axis, width, height
				create_but.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						String uid = F_uid.getText();
						String cid = F_cid.getText();
						String period = F_period.getText();
						String contibution_date = F_contribution.getText();

						int period_int = Integer.parseInt(period);

						Connection connection = connect();

						try {
							Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							stmt.executeUpdate("USE CHURCH");
							stmt.executeUpdate("INSERT INTO CONTRIBUTION(UID,CID,CONTRIBUTION_DATE,PERIOD) VALUES ('"
									+ uid + "','" + cid + "','" + contibution_date + "'," + period_int + ")");
							JOptionPane.showMessageDialog(null, "Contribution Details Added!");
							g.dispose();

						}

						catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}

					}

				});

				g.add(l3);
				g.add(l4);
				g.add(create_but);
				g.add(l1);
				g.add(l2);
				g.add(F_uid);
				g.add(F_cid);
				g.add(F_period);
				g.add(F_contribution);
				g.setSize(1000, 550);// 400 width and 500 height
				g.setLayout(null);// using no layout managers
				g.setVisible(true);// making the frame visible
				g.setLocationRelativeTo(null);

			}
		});

		JButton logout = new JButton("Logout"); // creating instance of JButton to return church member
		logout.setBounds(280, 100, 200, 25);

		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				f.dispose();
				// northInformation.removeAll();
				// init();
				login();
			}

		});

		JButton updatechurchmember = new JButton("Update Church Member"); // creating instance of JButton to return
																			// church member
		updatechurchmember.setBounds(20, 150, 200, 25);

		updatechurchmember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame g = new JFrame("Enter User Details");

				JLabel l1, l2, l3, l4;
				l1 = new JLabel("CID");
				l1.setBounds(30, 10, 100, 30);

				l2 = new JLabel("Member Name");
				l2.setBounds(30, 40, 105, 30);

				l3 = new JLabel("Age");
				l3.setBounds(30, 70, 100, 30);

				l4 = new JLabel("Gender");
				l4.setBounds(30, 100, 100, 30);

				final JTextField F_cid = new JTextField();
				F_cid.setBounds(110, 10, 200, 30);

				final JTextField F_mname = new JTextField();
				F_mname.setBounds(120, 40, 200, 30);

				final JTextField F_age = new JTextField();
				F_age.setBounds(110, 70, 200, 30);

				final JTextField F_gender = new JTextField();
				F_gender.setBounds(110, 100, 200, 30);

				JButton update_but = new JButton("Update");// creating instance of JButton for Create
				update_but.setBounds(130, 130, 80, 25);// x axis, y axis, width, height
				update_but.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						String cid = F_cid.getText();
						String mname = F_mname.getText();
						String age = F_age.getText();
						String gender = F_gender.getText();

						Connection connection = connect();

						try {
							Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							stmt.executeUpdate("USE CHURCH");
							stmt.executeUpdate("UPDATE CHURCHMEMBERS SET MNAME='" + mname + "', AGE='" + age
									+ "', GENDER='" + gender + "' WHERE CID=" + cid);
							JOptionPane.showMessageDialog(null, "Member Updated!");
							g.dispose();

						}

						catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}

					}

				});

				g.add(update_but);
				g.add(l3);
				g.add(l4);
				g.add(l1);
				g.add(l2);
				g.add(F_cid);
				g.add(F_mname);
				g.add(F_age);
				g.add(F_gender);
				g.setSize(1000, 500);// 400 width and 500 height
				g.setLayout(null);// using no layout managers
				g.setVisible(true);// making the frame visible
				g.setLocationRelativeTo(null);

			}

		});

		JButton deletechurchmember = new JButton("Delete Church Member"); // creating instance of JButton to return
																			// church member
		deletechurchmember.setBounds(280, 150, 200, 25);

		deletechurchmember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame g = new JFrame("Delete Church Member");

				JLabel l1;
				l1 = new JLabel("CID");
				l1.setBounds(30, 10, 100, 30);

				final JTextField F_cid = new JTextField();
				F_cid.setBounds(110, 10, 200, 30);

				JButton delete_but = new JButton("Delete");// creating instance of JButton for Create
				delete_but.setBounds(130, 130, 80, 25);// x axis, y axis, width, height
				delete_but.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						String cid = F_cid.getText();

						Connection connection = connect();

						try {
							Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
									ResultSet.CONCUR_READ_ONLY);
							stmt.executeUpdate("USE CHURCH");
							stmt.executeUpdate("DELETE FROM  CHURCHMEMBERS  WHERE CID=" + cid);
							JOptionPane.showMessageDialog(null, "Church Member Deleted!");
							g.dispose();

						}

						catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}

					}

				});

				g.add(delete_but);

				g.add(l1);

				g.add(F_cid);

				g.setSize(1000, 500);// 400 width and 500 height
				g.setLayout(null);// using no layout managers
				g.setVisible(true);// making the frame visible
				g.setLocationRelativeTo(null);

			}

		});

		f.add(create_but);
		f.add(logout);
		f.add(updatechurchmember);
		f.add(issue_contribution);
		f.add(add_churchmember);
		f.add(deletechurchmember);
		f.add(issued_but);
		f.add(users_but);
		f.add(view_but);
		f.add(add_user);
		f.setSize(1000, 600);// 400 width and 500 height
		f.setLayout(null);// using no layout managers
		f.setVisible(true);// making the frame visible
		f.setLocationRelativeTo(null);

	}
}

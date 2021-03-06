
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The Class JavaDatabaseProgram. Java GUI-based program that allows the user to
 * view, insert and update information within a database. The database contains
 * only one table, called Staff, with the following fields: An ID (the primary
 * key), last name, first name, middle initial (MI), address, city, state (ACT,
 * NSW, NT, QLD, SA, TAS, VIC, WA), and a telephone number.
 * 
 * Allows the user to view a staff record with a specified ID, to insert a new
 * record into the table, to update any field (except the ID) of an existing
 * staff member record, and to clear all fields from the display. Displays an
 * appropriate message indicating the success or failure of the
 * View/Insert/Update operations. For example, "Record found/record not found"
 * when the user selects View, "Record Inserted/failed to insert" when the user
 * selects Insert, and "Record updated/failed to update" when the user selects
 * Update. When no record is displayed (e.g., when the program first starts), or
 * when the user selects Clear, display a message inviting the user to view or
 * insert a new record. When inserting a new record, the ID field should be
 * generated automatically so that it is unique for the table. The ID field is
 * only editable when the user chooses to search for a new record to View. IDs
 * returned from the database through the View operation, or generated as part
 * of the Insert operation, are not be editable. When Inserting or Updating a
 * record, all fields (e.g., Last name, first name, etc.) throw an error if left
 * blank, or if the telephone number doesn't contain only digits or the state is
 * invalid.
 * 
 * Uses Hsqldb embedded database to avoid any external dependencies for tester.
 * Database is stored in the Temporary directory returned by System
 * .getProperty("java.io.tmpdir") e.g. C:\Users\Matthew\AppData\Local\Temp\
 */
public class JavaDatabaseProgram extends Application {

	/** The temp directory. */
	private static String	tempDirectory	= System
	        .getProperty("java.io.tmpdir");

	/** The stage. */
	private static Stage	stage;

	/**
	 * Creates the staff database table if it doesn't already exist.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static void createTable() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "CREATE TABLE IF NOT EXISTS Staff (\r\n"
		        + "id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,\r\n"
		        + "lastName VARCHAR(30) NOT NULL,\r\n"
		        + "firstName VARCHAR(30) NOT NULL,\r\n"
		        + "middleInitial VARCHAR(2) NOT NULL,\r\n"
		        + "address VARCHAR(100) NOT NULL,\r\n"
		        + "city VARCHAR(30) NOT NULL,\r\n"
		        + "state VARCHAR(3) NOT NULL,\r\n"
		        + "telephoneNumber VARCHAR(10) NOT NULL,\r\n" + ");";

		try {
			conn = DriverManager.getConnection(
			        "jdbc:hsqldb:file:" + tempDirectory + "db ", "SA", "");
			pstmt = conn.prepareStatement(sql);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	/**
	 * Inserts a new staff member in to the database.
	 *
	 * @param staff
	 *            the staff
	 * @return the int
	 * @throws Exception
	 *             the exception
	 */
	public static int insertStaff(Staff staff) throws Exception {

		if (!isStaffValid(staff)) {
			throw new Exception(
			        "Failed to insert. Ensure all fields (e.g., Last name, first name, etc.) are not left blank, and that the telephone number contains only digits. Ensure the state is valid.");
		}

		int id = 0;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO Staff (lastName, firstName, middleInitial, address, city, state, telephoneNumber) VALUES (?, ?, ?, ?, ?, ?, ?);";

		try {
			conn = DriverManager.getConnection(
			        "jdbc:hsqldb:file:" + tempDirectory + "db ", "SA", "");
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			int count = 1;
			pstmt.setString(count++, staff.getLastName());
			pstmt.setString(count++, staff.getFirstName());
			pstmt.setString(count++, staff.getMiddleInitial());
			pstmt.setString(count++, staff.getAddress());
			pstmt.setString(count++, staff.getCity());
			pstmt.setString(count++, staff.getState());
			pstmt.setString(count++, staff.getTelephoneNumber());

			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();

			if (rs.next()) {
				id = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
		return id;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	public static Staff viewStaff(String string) throws Exception {

		int id = Integer.parseInt(string);

		Staff staff = null;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM Staff WHERE id = ? LIMIT 1;";

		try {
			conn = DriverManager.getConnection(
			        "jdbc:hsqldb:file:" + tempDirectory + "db ", "SA", "");
			pstmt = conn.prepareStatement(sql);
			int count = 1;
			pstmt.setInt(count++, id);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				staff = new Staff();
				staff.setId(rs.getInt("id"));
				staff.setLastName(rs.getString("lastName"));
				staff.setFirstName(rs.getString("firstName"));
				staff.setMiddleInitial(rs.getString("middleInitial"));
				staff.setAddress(rs.getString("address"));
				staff.setCity(rs.getString("city"));
				staff.setState(rs.getString("state"));
				staff.setTelephoneNumber(rs.getString("telephoneNumber"));
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
		return staff;
	}

	/**
	 * Checks if is String is null, 0 length, or only contains whitespace.
	 *
	 * @param string
	 *            the string
	 * @return true, if is blank
	 */
	public static boolean isBlank(String string) {
		if (string != null && string.length() > 0 && !string.matches("\\s+")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks if any part of a staff object is blank, or if the telephone number
	 * only contain numbers. Checks that the state is a valid Australian State.
	 *
	 * @param staff
	 *            the staff
	 * @return true, if the staff member is valid
	 */
	private static boolean isStaffValid(Staff staff) {
		return staff.getTelephoneNumber().matches("\\d+")
		        && !isBlank(staff.getLastName())
		        && !isBlank(staff.getLastName())
		        && !isBlank(staff.getLastName())
		        && !isBlank(staff.getLastName())
		        && !isBlank(staff.getLastName())
		        && !isBlank(staff.getLastName())
		        && staff.getState().matches("ACT|NSW|NT|QLD|SA|TAS|VIC|WA");
	}

	/**
	 * Updates a staff member record in the database.
	 *
	 * @param staff
	 *            the staff
	 * @throws Exception
	 *             the exception
	 */
	public static void updateStaff(Staff staff) throws Exception {

		if (!isStaffValid(staff)) {
			throw new Exception(
			        "Failed to update. Cannot update staff member. Ensure all fields (e.g., Last name, first name, etc.) are not left blank, and that the telephone number contains only digits. Ensure the state is valid. (ACT, NSW, NT, QLD, SA, TAS, VIC, WA)");
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE Staff SET lastName = ?, firstName = ?, middleInitial = ?, address = ?, city = ?, state = ?, telephoneNumber = ? WHERE id = ?;";

		try {
			conn = DriverManager.getConnection(
			        "jdbc:hsqldb:file:" + tempDirectory + "db ", "SA", "");
			pstmt = conn.prepareStatement(sql);
			int count = 1;
			pstmt.setString(count++, staff.getLastName());
			pstmt.setString(count++, staff.getFirstName());
			pstmt.setString(count++, staff.getMiddleInitial());
			pstmt.setString(count++, staff.getAddress());
			pstmt.setString(count++, staff.getCity());
			pstmt.setString(count++, staff.getState());
			pstmt.setString(count++, staff.getTelephoneNumber());

			// WHERE clause
			pstmt.setInt(count++, staff.getId());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		createTable();
		stage = primaryStage;
		stage.setResizable(false);

		primaryStage.setTitle("Java Database Program");

		// create the text for display / testing
		final String defaultMessage = "Click the buttons View or Insert a new record.";
		final Label messageText = new Label(defaultMessage);

		// labels
		final Label idLabel = new Label("ID");
		final Label lastNameLabel = new Label("Last Name");
		final Label firstNameLabel = new Label("First Name");
		final Label middleInitialLabel = new Label("MI");
		final Label addressLabel = new Label("Address");
		final Label cityLabel = new Label("City");
		final Label stateLabel = new Label("State");
		final Label telephoneLabel = new Label("Telephone");

		// Australian states
		final ObservableList<String> stateOptions = FXCollections
		        .observableArrayList();
		stateOptions.add("");
		stateOptions.add("ACT");
		stateOptions.add("NSW");
		stateOptions.add("NT");
		stateOptions.add("QLD");
		stateOptions.add("SA");
		stateOptions.add("TAS");
		stateOptions.add("VIC");
		stateOptions.add("WA");

		// text fields / combo box
		// if any are filled in other than id it will disable editing id
		final TextField idTextField = new TextField();
		final TextField lastNameTextField = new TextField();
		lastNameTextField.textProperty().addListener(listener -> {
			if (!isBlank(lastNameTextField.getText())) {
				idTextField.setDisable(true);
			} else {
				idTextField.setDisable(false);
			}
		});
		final TextField firstNameTextField = new TextField();
		firstNameTextField.textProperty().addListener(listener -> {
			if (!isBlank(firstNameTextField.getText())) {
				idTextField.setDisable(true);
			} else {
				idTextField.setDisable(false);
			}
		});
		final TextField middleInitialTextField = new TextField();
		middleInitialTextField.setMaxWidth(40);
		middleInitialTextField.textProperty().addListener(listener -> {
			if (!isBlank(middleInitialTextField.getText())) {
				idTextField.setDisable(true);
			} else {
				idTextField.setDisable(false);
			}
		});
		final TextField addressTextField = new TextField();
		addressTextField.textProperty().addListener(listener -> {
			if (!isBlank(addressTextField.getText())) {
				idTextField.setDisable(true);
			} else {
				idTextField.setDisable(false);
			}
		});
		final TextField cityTextField = new TextField();
		cityTextField.textProperty().addListener(listener -> {
			if (!isBlank(cityTextField.getText())) {
				idTextField.setDisable(true);
			} else {
				idTextField.setDisable(false);
			}
		});
		final ComboBox<String> stateComboBox = new ComboBox<String>(
		        stateOptions);
		stateComboBox.getSelectionModel().selectedItemProperty()
		        .addListener((options, oldValue, newValue) -> {
			        if (!isBlank(stateComboBox.getValue())) {
				        idTextField.setDisable(true);
			        } else {
				        idTextField.setDisable(false);
			        }
		        });
		final TextField telephoneTextField = new TextField();
		telephoneTextField.textProperty().addListener(listener -> {
			if (!isBlank(telephoneTextField.getText())) {
				idTextField.setDisable(true);
			} else {
				idTextField.setDisable(false);
			}
		});

		// buttons
		final Button viewButton = new Button("View");
		viewButton.setOnAction(actionEvent -> {
			try {
				Staff staff = viewStaff(idTextField.getText());
				idTextField.setText(String.valueOf(staff.getId()));
				lastNameTextField.setText(staff.getLastName());
				firstNameTextField.setText(staff.getFirstName());
				middleInitialTextField.setText(staff.getMiddleInitial());
				addressTextField.setText(staff.getAddress());
				cityTextField.setText(staff.getCity());
				stateComboBox.setValue(staff.getState());
				telephoneTextField.setText(staff.getTelephoneNumber());
				messageText.setText("Record found.");
			} catch (Exception e) {
				messageText.setText("Record not found.");
			}
		});

		final Button insertButton = new Button("Insert");
		insertButton.setOnAction(actionEvent -> {
			try {
				Staff staff = new Staff();
				staff.setLastName(lastNameTextField.getText());
				staff.setFirstName(firstNameTextField.getText());
				staff.setMiddleInitial(middleInitialTextField.getText());
				staff.setAddress(addressTextField.getText());
				staff.setCity(cityTextField.getText());
				staff.setState(stateComboBox.getValue());
				staff.setTelephoneNumber(telephoneTextField.getText());
				int id = insertStaff(staff);
				messageText.setText("Record inserted.");
				idTextField.setText(String.valueOf(id));
			} catch (Exception e) {
				messageText.setText("Failed to insert. " + e.getMessage());
			}
		});

		final Button updateButton = new Button("Update");
		updateButton.setOnAction(actionEvent -> {
			try {
				Staff staff = new Staff();
				staff.setId(Integer.parseInt(idTextField.getText()));
				staff.setLastName(lastNameTextField.getText());
				staff.setFirstName(firstNameTextField.getText());
				staff.setMiddleInitial(middleInitialTextField.getText());
				staff.setAddress(addressTextField.getText());
				staff.setCity(cityTextField.getText());
				staff.setState(stateComboBox.getValue());
				staff.setTelephoneNumber(telephoneTextField.getText());
				updateStaff(staff);
				messageText.setText("Record updated.");
			} catch (Exception e) {
				messageText.setText("Failed to update. " + e.getMessage());
			}
		});

		final Button clearButton = new Button("Clear");
		clearButton.setOnAction(actionEvent -> {
			idTextField.setText("");
			lastNameTextField.setText("");
			firstNameTextField.setText("");
			middleInitialTextField.setText("");
			addressTextField.setText("");
			cityTextField.setText("");
			stateComboBox.setValue("");
			telephoneTextField.setText("");
			messageText.setText(defaultMessage);
		});

		final HBox buttonHBox = new HBox();
		buttonHBox.getChildren().addAll(viewButton, insertButton, updateButton,
		        clearButton);
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.setSpacing(15);

		// add them all to a layout pane
		final GridPane root = new GridPane();

		// place a gap between nodes
		root.setHgap(15);

		root.addRow(0, messageText);
		GridPane.setColumnSpan(messageText, GridPane.REMAINING);

		root.addRow(1, idLabel, idTextField);

		root.addRow(2, lastNameLabel, lastNameTextField, firstNameLabel,
		        firstNameTextField, middleInitialLabel, middleInitialTextField);

		root.addRow(3, addressLabel, addressTextField);

		root.addRow(4, cityLabel, cityTextField, stateLabel, stateComboBox);

		root.addRow(5, telephoneLabel, telephoneTextField);

		root.addRow(6, buttonHBox);
		GridPane.setColumnSpan(buttonHBox, GridPane.REMAINING);

		// root.setGridLinesVisible(true);

		// use a white background
		root.setStyle("-fx-background-color: white");

		// align the root to the center
		root.setAlignment(Pos.BASELINE_CENTER);

		// place a vertical gap between nodes
		root.setVgap(15);

		// and add them to the scene, also setting background colour
		final Scene scene = new Scene(root, Color.WHITE);

		// draw the scene to the stage
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}

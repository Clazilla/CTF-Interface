package ctf_interface;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.*;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Interface extends Application {

	private ListView<String> listview; // making this one private global for the update function
	private static Path save_dir;
	private static Path match_dir;

	public static void main(String[] args) {
		// Doing this before platform launch to ensure 'save_dir' 
		// is initialized and created at any point in the program
		
		// Getting the path to the save folder (relative to the folder the program runs in)
		save_dir = Paths.get("team");
		match_dir = Paths.get("match");
		if(!Files.exists(save_dir)) {
			try {
				Files.createDirectory(save_dir); // Create the directory if it doesn't exist
			} catch (IOException e) {
				e.printStackTrace(); // Catch eventual IO Errors
				// IO = In and Output (Creating a file / disk writing [Output])
			}
		}
		if(!Files.exists(match_dir)) {
			try {
				Files.createDirectory(match_dir); // Create the directory if it doesn't exist
			} catch (IOException e) {
				e.printStackTrace(); // Catch eventual IO Errors
				// IO = In and Output (Creating a file / disk writing [Output])
			}
		}
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane gridpane = new GridPane();
		Scene scene = new Scene(gridpane, 800, 500);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Capture the Flag");
		gridpane.setAlignment(Pos.CENTER);

		// FIRST COLUMN
		GridPane gridpane1 = new GridPane();
		final TextField[] player = new TextField[6];
		for (int j = 1; j < 7; j++) {
			TextField players = new TextField("");
			players.setPromptText("Player");
			gridpane1.add(players, 0, j);
			player[j - 1] = players;
		}

		Button btn = new Button("Create");
		gridpane1.add(btn, 0, 8);

		TextField teamname = new TextField();
		teamname.setPromptText("Team");
		teamname.setPrefHeight(50);
		
		gridpane1.add(teamname, 0, 0);
		gridpane1.setVgap(10);
		gridpane1.setHgap(10);

		btn.setOnAction(event -> {
			// Get the text of the textfield
			String team_name = teamname.getText();
			if(team_name.isEmpty()) { // Check if team name is empty
				// Show an error dialog if it's empty
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Failed");
				alert.setHeaderText("Team name empty!");
				alert.setContentText("No team created!");
				alert.show();
				return; // returning because of error
			}

			// Creating a confirmation dialog/alert
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Create team?");
			alert.setHeaderText("Do you want to create a team!");
			alert.setContentText("Team: " + team_name);
			// Getting the result. Maybe it was a miss click? 
			Optional<ButtonType> result = alert.showAndWait();	
			if(!result.isPresent() || result.get().equals(ButtonType.CANCEL))
				return; // going back if the dialog was skipped or the button pressed is ButtonType.CANCEL
			
			try {
				// Getting the path (using save_dir as base path)
				Path path_to_team_file = Paths.get(save_dir.toString(), team_name + ".json");
				// creating the file if it doesn't exist
				if(!Files.exists(path_to_team_file))Files.createFile(path_to_team_file);
				else {
					// otherwise show an error dialog
					alert = new Alert(AlertType.WARNING);
					alert.setTitle("Failed");
					alert.setHeaderText("Team already exists!");
					alert.setContentText("No team created!");
					alert.show();
					return; // returning because of error
				}
				
				// Added the players to the json array
				JSONArray player_in_team_json_array = new JSONArray(); // This is not really an array
				for (TextField player_name_field : player) {
					String player_name = player_name_field.getText();
					if(!player_name.isEmpty()) // check if the player name is empty
						player_in_team_json_array.put(player_name); // adds if the player is named
				}
				
				// Holder object for the json array and just put the name
				// in it for additional safety
				JSONObject json_holder_obj = new JSONObject();
				json_holder_obj.put("name", team_name); // put team name
				json_holder_obj.put("players", player_in_team_json_array); // put player list
				
				// Create buffered writer and write to disk
				BufferedWriter json_writer = Files.newBufferedWriter(path_to_team_file);
				json_holder_obj.write(json_writer); // Writes into buffer! (Memory)
				json_writer.close(); // Flushes output stream! (flushes to disk and frees resources)
				
				// Show an information dialog to confirm to the user that the team was created
				alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Success");
				alert.setHeaderText("Team has been created!");
				alert.setContentText("Team: " + team_name);
				alert.show();
				
				update(); // Triggering the update for the team list
			} catch (IOException e) {
				e.printStackTrace(); // Catch eventual IO Errors
				// IO = In and Output (Writing to disk is done via Outputstream)
			}
		});

		gridpane.add(gridpane1, 0, 0);

		// SECOND COLUMN
		listview = new ListView<String>();
		update();
		
		gridpane.add(listview, 1, 0);
		
		// THIRD COLUMN
		GridPane gridpane3 = new GridPane();

		// Creating both earlier because we need them simultaneously
		Button team1 = new Button("TEAM1"); // Button is better I guess?
		Button team2 = new Button("TEAM2"); // Button is better I guess?

		team1.setOnAction(event -> {
			// set the name to the selected team name from the list
			
			// Check if it is the first item (----- Team -----) [we don't want that, it isn't a team]
			if(listview.getSelectionModel().getSelectedIndex() < 1) return; // so return in that case
			String selected_name = listview.getSelectionModel().getSelectedItem(); // get the selected text
			
			// The same team can not play against each other
			if(selected_name.equals(team2.getText())) return; // In case both team names are the same, return
			
			if(!selected_name.isEmpty()) // Check if it is empty
				team1.setText(selected_name); // If it is not empty the text will be set
		});
		gridpane3.add(team1, 0, 0);

		Label vs = new Label("VS");
		gridpane3.add(vs, 1, 0);
		vs.setAlignment(Pos.CENTER);
		vs.setPrefWidth(40);
		
		gridpane3.add(team2, 2, 0);
		team2.setOnAction(event -> {
			// set the name to the selected team name from the list
			
			// Check if it is the first item (----- Team -----) [we don't want that, it isn't a team]
			if(listview.getSelectionModel().getSelectedIndex() < 1) return; // so return in that case
			String selected_name = listview.getSelectionModel().getSelectedItem(); // get the selected text
			
			// The same team can not play against each other
			if(selected_name.equals(team1.getText())) return; // In case both team names are the same, return
			
			if(!selected_name.isEmpty()) // Check if it is empty
				team2.setText(selected_name); // If it is not empty the text will be set
		});

		Button match_create = new Button("Create match");
		match_create.setOnAction(event -> {
			try {
				// Getting the file path
				Path team1_path = Paths.get(save_dir.toString(), team1.getText() + ".json");
				if(!Files.exists(team1_path)) return; // return if file doesn't exist
				byte[] team1_data = Files.readAllBytes(team1_path); // Reading the bytes of the file
				JSONObject team1_json = new JSONObject(new String(team1_data)); // Making a json object of the raw data
				
				// Same thing for team 2
				Path team2_path = Paths.get(save_dir.toString(), team2.getText() + ".json");
				if(!Files.exists(team2_path)) return; // return if file doesn't exist
				byte[] team2_data = Files.readAllBytes(team2_path); // Reading the bytes of the file
				JSONObject team2_json = new JSONObject(new String(team2_data)); // Making a json object of the raw data

				JSONObject holder_obj = new JSONObject(); // creating a holder object
				holder_obj.put("1", team1_json); // Put the first team on place 1
				holder_obj.put("2", team2_json); // Put the second team on place 2
				
				// Get the path to the match file
				Path path_to_match_file = Paths.get(match_dir.toString(), team1.getText() + " vs " + team2.getText() + ".json");
				if(Files.exists(path_to_match_file)) { // Check if file exists
					// show an error dialog if so
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Failed");
					alert.setHeaderText("Match already exists!");
					alert.setContentText("No match created!");
					alert.show();
					return; // returning because of error
				}
				// Creating file
				Files.createFile(path_to_match_file);
				
				// Create buffered writer and write to disk
				BufferedWriter match_file_writer = Files.newBufferedWriter(path_to_match_file);
				holder_obj.write(match_file_writer); // Writes into buffer! (Memory)
				match_file_writer.close(); // Flushes output stream! (flushes to disk and frees resources)
				
				// Show an information dialog to confirm to the user that the match was created
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Success");
				alert.setHeaderText("Match has been created!");
				alert.setContentText("Match: " + path_to_match_file.getFileName().toString());
				alert.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		gridpane3.add(match_create, 0, 1, 2, 1);
		gridpane3.setVgap(15);
		gridpane3.setHgap(15);
		
		gridpane.add(gridpane3, 2, 0);
		gridpane.setVgap(15);
		gridpane.setHgap(15);
		primaryStage.show();

	}
	
	private void update() {
		ObservableList<String> list_items = listview.getItems(); // getting list of items in the table
		list_items.clear(); // Clearing the list
		list_items.add("----- TEAMS -----"); // adding teams banner
		
		try {
			Stream<Path> files_in_folder = Files.list(save_dir);
			files_in_folder.filter(file -> { // filters the list with the following check
				// Checked for each element in the stream
				// returns whether the path is a json file (ends with '.json')
				return file.toString().endsWith(".json");
			}).forEach(file -> { // for each loop in lambda over the filtered list
				// This code will be executed for each element
				String file_name = file.getFileName().toString(); // getting the file name
				list_items.add(file_name.replace(".json", "")); 
				// adding the team name to the table view
				// while removing '.json' suffix 
			});
			files_in_folder.close();
		} catch (IOException e) {
			e.printStackTrace(); // Catch eventual IO Errors
			// IO = In and Output (Reading from the directory [Input])
		}
	}
}

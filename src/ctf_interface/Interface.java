package ctf_interface;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;

import org.json.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Interface extends Application {

	public static void main(String[] args) {
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

		TextField teamname = new TextField("");
		teamname.setPromptText("Team");
		gridpane1.add(teamname, 0, 0);

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
				// Getting the path
				Path path_to_team_file = Paths.get(team_name + ".json");
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
				json_holder_obj.put("team_name", team_name); // put team name
				json_holder_obj.put("player", player_in_team_json_array); // put player list
				
				// Create buffered writer and write to disk
				BufferedWriter json_writer = Files.newBufferedWriter(path_to_team_file);
				json_holder_obj.write(json_writer); // Writes into buffer! (Memory)
				json_writer.close(); // Flushes output stream! (flushes to disk and frees resources)
				
				// Show and information dialog to confirm to the user that the team was created
				alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Success");
				alert.setHeaderText("Team has been created!");
				alert.setContentText("Team: " + team_name);
				alert.show();
				
			} catch (IOException e) {
				e.printStackTrace(); // Catch eventual IO Errors
				// IO = In and Output (Writing to disk is done via Outputstream)
			}
		});

		gridpane.add(gridpane1, 0, 0);

		// SECOND COLUMN
		TableView<String> tableview = new TableView<String>();
		GridPane gridpane2 = new GridPane();
		gridpane2.add(tableview, 1, 0);
		gridpane.add(gridpane2, 1, 0);

		// THIRD COLUMN
		Label team1 = new Label("TEAM1");
		GridPane gridpane3 = new GridPane();
		gridpane3.add(team1, 2, 0);

		Label vs = new Label("VS");
		gridpane3.add(vs, 3, 0);
		vs.setAlignment(Pos.CENTER);
		vs.setPrefWidth(40);
		
		Label team2 = new Label("TEAM2");
		gridpane3.add(team2, 4, 0);
		team2.setPrefSize(80, 20);

		gridpane.add(gridpane3, 2, 0);
		primaryStage.show();

	}
}

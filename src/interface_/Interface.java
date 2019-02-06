package interface_;

import java.nio.file.*;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
		TextField[] player = new TextField[6];
		for (int j = 1; j < 7; j++) {
			TextField players = new TextField("");
			players.setPromptText("player");
			gridpane1.add(players, 0, j);
			player[j - 1] = players;
		}

		Button btn = new Button("create a team");
		gridpane1.add(btn, 0, 8);

		TextField teamname = new TextField("");
		teamname.setPromptText("team name");
		gridpane1.add(teamname, 0, 0);

		btn.setOnAction(event -> {
			String teams = teamname.getText();
			Path pteam = Paths.get(teams + ".json");
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
		team1.setPrefSize(80, 20);

		Label vs = new Label("VS");
		gridpane3.add(vs, 3, 0);
		vs.setPrefSize(80, 20);
		vs.setCenterShape(true);

		Label team2 = new Label("TEAM2");
		gridpane3.add(team2, 4, 0);
		team2.setPrefSize(80, 20);

		gridpane.add(gridpane3, 2, 0);
		primaryStage.show();

	}
}

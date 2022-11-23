package application.controller;

import java.util.Optional;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



public class Main3 extends Application {
  @Override
    public void start(Stage primaryStage) {
    try {
      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
      gridPane.setHgap(5.5);
      gridPane.setVgap(5.5);
      gridPane.add(new Label("Choose Client"), 0, 0);
      ObservableList<String> clients = FXCollections.observableArrayList();
      ListView<String> clientListView = new ListView<>(clients);
      clients.addAll("Player1", "Player2", "Player3");
      clientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      clientListView.setMaxHeight(80);
      clientListView.setMaxWidth(270);
      gridPane.add(clientListView, 0, 1);
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
      VBox vBox = new VBox();
      Pane root = fxmlLoader.load();
      vBox.getChildren().addAll(gridPane, root);

      primaryStage.setTitle("Tic Tac Toe");
      primaryStage.setScene(new Scene(vBox));
      primaryStage.setResizable(false);

      primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
    public void handle(WindowEvent event) {
          Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
          alert2.setTitle("Exit");
          alert2.setHeaderText("Are you sure to exit——1");
          Optional<ButtonType> result = alert2.showAndWait();
          if (result.get() == ButtonType.OK) {
              primaryStage.close();
          } else {
            event.consume();
          }
          }
          });
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}

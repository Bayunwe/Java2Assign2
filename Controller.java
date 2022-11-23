package application.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable {
  public Socket socket;
  public DataInputStream dataInputStream;
  public DataOutputStream dataOutputStream;
  public String message = "";

  private static final int PLAY_1 = 1;
  private static final int PLAY_2 = 2;
  private static final int EMPTY = 0;
  private static final int BOUND = 90;
  private static final int OFFSET = 15;

  @FXML
  private Pane base_square;
  @FXML
  private Rectangle game_panel;
  @FXML
  private TextArea turn;
  @FXML
  private ObservableList<String> clients;
  private ListView<String> clientListView;

  private static boolean TURN = false;

  private static final int[][] chessBoard = new int[3][3];
  private static final boolean[][] flag = new boolean[3][3];


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      socket = new Socket("localhost", 9000);
      dataOutputStream = new DataOutputStream(socket.getOutputStream());
      dataInputStream = new DataInputStream(socket.getInputStream());


      System.out.println(dataInputStream.readUTF());

      message = dataInputStream.readUTF();
      System.out.println(message);
      if (message.equals("You are player 1, matching successful")){
        TURN = true;}

      Runnable runnable =new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              message = dataInputStream.readUTF();
              System.out.println(message);
              String[] split = message.split(" ");
              int x = Integer.parseInt(split[0]);
              int y = Integer.parseInt(split[1]);
              Platform.runLater(() -> {
                if (!TURN)
                  turn.setText("My turn");

                else
                  turn.setText("Opposite's turn");
                if (!TURN && refreshBoard(x,y)){
                  TURN = !TURN;
                }
                if (Win()) {
                  Alert alert = new Alert(Alert.AlertType.INFORMATION);
                  alert.setTitle("Game Over");
                  alert.setHeaderText(null);
                  alert.setContentText("You lose");
                  alert.showAndWait();
                  System.exit(0);
                }
                if (Draw()) {
                  Alert alert = new Alert(Alert.AlertType.INFORMATION);
                  alert.setTitle("Game Over");
                  alert.setHeaderText(null);
                  alert.setContentText("Draw");
                  alert.showAndWait();
                  System.exit(0);
                }
                if (Quit()){
                  Alert alert = new Alert(Alert.AlertType.INFORMATION);
                  alert.setTitle("Error");
                  alert.setHeaderText(null);
                  alert.setContentText("Player Quit");
                  alert.showAndWait();
                  System.exit(0);
                }
              });



            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      };
      Thread thread = new Thread(runnable);
      thread.start();

    } catch (IOException e) {
      e.printStackTrace();
    }

    game_panel.setOnMouseClicked(event -> {
      int x = (int) (event.getX() / BOUND);
      int y = (int) (event.getY() / BOUND);
      if (TURN && refreshBoard(x,y)) {
        if (!TURN){
          turn.setText("My turn");}
        else
          turn.setText("Opposite's turn");
        TURN = !TURN;
        message = x + " " + y;
        try {
          dataOutputStream.writeUTF(message);
          dataOutputStream.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (Win()) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Game Over");
          alert.setHeaderText(null);
          alert.setContentText("You win");
          alert.showAndWait();
          System.exit(0);
        }
        if (Draw()) {
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Game Over");
          alert.setHeaderText(null);
          alert.setContentText("Draw");
          alert.showAndWait();
          System.exit(0);
        }
        if (Quit()){
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Error");
          alert.setHeaderText(null);
          alert.setContentText("Player Quit");
          alert.showAndWait();
          System.exit(0);
        }
      }
    });
        }



  private boolean Draw() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (chessBoard[i][j] == EMPTY)
          return false;
      }
    }
    return true;
  }
  private boolean Quit(){
    if (message.equals("stop")){
      return true;
  }return false;
  }

  private boolean Win() {
    int horizonCheck =0;
    int verticalCheck=0;
    int diagonalCheck1 =0 ;
    int diagonalCheck2 = 0;
    for (int i = 0; i < 3; i++) {
      if (chessBoard[i][0] == chessBoard[i][1] && chessBoard[i][1] == chessBoard[i][2] && chessBoard[i][0] != EMPTY) {
        horizonCheck=1;
      }
    }
    for (int i = 0; i < 3; i++) {
      if (chessBoard[0][i] == chessBoard[1][i] && chessBoard[1][i] == chessBoard[2][i] && chessBoard[0][i] != EMPTY) {
        verticalCheck=1;
      }
    }
    if (chessBoard[0][0] == chessBoard[1][1] && chessBoard[1][1] == chessBoard[2][2] && chessBoard[0][0] != EMPTY) {
      diagonalCheck1=1;
    }
    if (chessBoard[0][2] == chessBoard[1][1] && chessBoard[1][1] == chessBoard[2][0] && chessBoard[0][2] != EMPTY) {
      diagonalCheck2=1;
    }
    if (((horizonCheck==1||verticalCheck==1)||diagonalCheck1==1)||diagonalCheck2==1){
      return true;
    }
    return false;
  }

  private boolean refreshBoard(int x, int y) {
    if (chessBoard[x][y] == EMPTY) {
      chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
      drawChess();
      return true;
    }
    return false;
  }

  private void drawChess() {
    for (int i = 0; i < chessBoard.length; i++) {
      for (int j = 0; j < chessBoard[0].length; j++) {
        if (flag[i][j]) {
          // This square has been drawing, ignore.
          continue;
        }
        switch (chessBoard[i][j]) {
          case PLAY_1:
            drawCircle(i, j);
            break;
          case PLAY_2:
            drawLine(i, j);
            break;
          case EMPTY:
            // do nothing
            break;
          default:
            System.err.println("Invalid value!");
        }
      }
    }
  }

  private void drawCircle(int i, int j) {
    Circle circle = new Circle();
    base_square.getChildren().add(circle);
    circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
    circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
    circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
    circle.setStroke(Color.RED);
    circle.setFill(Color.TRANSPARENT);
    flag[i][j] = true;
  }

  private void drawLine(int i, int j) {
    Line line_a = new Line();
    Line line_b = new Line();
    base_square.getChildren().add(line_a);
    base_square.getChildren().add(line_b);
    line_a.setStartX(i * BOUND + OFFSET * 1.5);
    line_a.setStartY(j * BOUND + OFFSET * 1.5);
    line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
    line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
    line_a.setStroke(Color.BLUE);

    line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
    line_b.setStartY(j * BOUND + OFFSET * 1.5);
    line_b.setEndX(i * BOUND + OFFSET * 1.5);
    line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
    line_b.setStroke(Color.BLUE);
    flag[i][j] = true;
  }

}



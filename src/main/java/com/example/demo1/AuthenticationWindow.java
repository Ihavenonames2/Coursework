package com.example.demo1;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.application.Application;
import static javafx.application.Application.launch;

public class AuthenticationWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Authentication Window");

        // Створення GridPane для організації елементів у вигляді таблиці
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Додавання елементів на форму автентифікації
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);

        // Додавання обробника подій для кнопки "Login"
        loginButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();

            // Логіка перевірки ідентифікаційних даних
            if (authenticate(username, password)) {
                showAlert("Authentication Successful", "Welcome, " + username + "!");
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
            }
        });

        // Додавання всіх елементів на GridPane
        grid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);

        // Створення сцени
        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setScene(scene);

        // Показ сцени
        primaryStage.show();
    }

    private boolean authenticate(String username, String password) {
        // Ваша логіка перевірки ідентифікаційних даних тут
        // Наприклад, ви можете порівнювати їх з фіксованими значеннями чи виконувати запит до бази даних
        return username.equals("user") && password.equals("pass");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
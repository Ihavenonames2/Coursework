package com.example.demo1;

import ConnectToPostgreSq.Student;
import ConnectToPostgreSq.WorkWithPostgreSql;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Tester extends Application {

    private WorkWithPostgreSql postgresql;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        postgresql = new WorkWithPostgreSql();

        primaryStage.setTitle("PostgreSQL GUI");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Додайте текстові поля для введення даних про студента
        TextField fullNameField = new TextField();
        TextField courseField = new TextField();
        TextField specializationCodeField = new TextField();
        TextField averageGradeField = new TextField();
        TextField facultyField = new TextField();
        TextField idField= new TextField();
        CheckBox communityWorkCheckBox = new CheckBox("Community Work");

        // Додайте елемент Label для повідомлень про помилки або успіх
        Label messageLabel = new Label();

        Button insertStudentButton = new Button("Insert Student");
        insertStudentButton.setOnAction(e -> {
            try {
                // Отримайте дані з текстових полів
                String fullName = fullNameField.getText();
                int course = (int) Double.parseDouble(courseField.getText());
                String specializationCode = specializationCodeField.getText();
                double averageGrade = Double.parseDouble(averageGradeField.getText());
                boolean communityWork = communityWorkCheckBox.isSelected();
                String faculty = facultyField.getText();
                int id=Integer.parseInt(idField.getText());
                Student student = new Student();
                student.setFullName(fullName);
                student.setCourse(course);
                student.setSpecializationCode(specializationCode);
                student.setAverageGrade(averageGrade);
                student.setCommunityWork(communityWork);
                student.setFaculty(faculty);
                student.setGroup_id(id);

                postgresql.insertStudent(student);

                // Виведіть повідомлення про успіх
                messageLabel.setText("Студента додано успішно.");
            } catch (NumberFormatException ex) {
                // Виведіть повідомлення про помилку, якщо введено некоректні дані
                messageLabel.setText("Помилка: Введено некоректні дані.");
            }
        });

        layout.getChildren().addAll(
                new Label("Full Name:"), fullNameField,
                new Label("Course:"), courseField,
                new Label("Specialization Code:"), specializationCodeField,
                new Label("Average Grade:"), averageGradeField,
                new Label("Faculty:"), facultyField,
                new Label("Id:"),idField,
                communityWorkCheckBox,
                insertStudentButton,
                messageLabel
        );

        Scene scene = new Scene(layout, 300, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
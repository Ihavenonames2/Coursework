package com.example.demo1;

import ConnectToPostgreSq.Student;
import ConnectToPostgreSq.WorkWithPostgreSql;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI extends Application {


    private WorkWithPostgreSql postgresql;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Database Operations");

        WorkWithPostgreSql.addFacultyColum();

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));

        Button createGroupsTableButton = new Button("Create Groups Table");
        Button createStudentsTableButton = new Button("Create Students Table");
        Button addGroupButton = new Button("Add Group");
        Button getGroupsButton = new Button("Get Groups");
        Button getStudentsButton = new Button("Get Students");
        Button delStudentsButton = new Button("Delete Students");
        Button getAvgStudentsButton = new Button("Get Average mark In Groups");
        Button getExcludedList = new Button("Get excluded list");
        Button updateInTable = new Button("Update data");
        Button getMarks = new Button("Get marks");
        Button schoolarship = new Button("Schoolarship");

        createGroupsTableButton.setOnAction(e -> WorkWithPostgreSql.createGroupsTable());
        createStudentsTableButton.setOnAction(e -> WorkWithPostgreSql.createStudentsTable());
        addGroupButton.setOnAction(e -> showAddGroupDialog());
        getGroupsButton.setOnAction(e -> WorkWithPostgreSql.selectIntoGroups());
        getStudentsButton.setOnAction(e -> WorkWithPostgreSql.selectIntoTable());
        delStudentsButton.setOnAction(e -> delStudents());
        getAvgStudentsButton.setOnAction(e -> WorkWithPostgreSql.getAvg());
        getExcludedList.setOnAction(e -> WorkWithPostgreSql.excluding());
        updateInTable.setOnAction(e -> WorkWithPostgreSql.updateInTable());
        getMarks.setOnAction(e -> WorkWithPostgreSql.getMarks());
        schoolarship.setOnAction(e -> SchoolarShip());

        vbox.getChildren().addAll(
                createGroupsTableButton,
                createStudentsTableButton,
                addGroupButton,
                getGroupsButton,
                getStudentsButton,
                delStudentsButton,
                getAvgStudentsButton,
                getExcludedList,
                updateInTable,
                getMarks,
                schoolarship
        );

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void SchoolarShip() {
        WorkWithPostgreSql.addScholarshipTypeColumn();
        WorkWithPostgreSql.awardScholarships();
    }

    private void delStudents() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dell student");
        dialog.setHeaderText("Enter Student ID:");
        dialog.setContentText("Student ID:");
        dialog.showAndWait().ifPresent(x -> WorkWithPostgreSql.DelbyId(Integer.parseInt(x)));
    }

    private void showAddGroupDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Group");
        dialog.setHeaderText("Enter Group Name:");
        dialog.setContentText("Group Name:");
        dialog.showAndWait().ifPresent(groupName -> WorkWithPostgreSql.insertGroup(groupName));
    }
}
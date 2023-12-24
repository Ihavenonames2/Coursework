package com.example.demo1;

import ConnectToPostgreSq.ConnectToPostgreSql;
import ConnectToPostgreSq.WorkWithPostgreSql;
import ConnectToPostgreSq.Student;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;



public class Main{
    public  static Scanner scanner= new Scanner(System.in);
    public static void main(String[] args) {
        try {
            Connection connection = ConnectToPostgreSql.connect();
            System.out.println("Opened database successfully");
            connection.close();

            WorkWithPostgreSql.createGroupsTable();
            WorkWithPostgreSql.createStudentsTable();

            Scanner scanner = new Scanner(System.in);
            WorkWithPostgreSql.menu();
            while (true) {


                // Отримання введення користувача
                System.out.print("Введіть номер опції (або 0 для виходу): ");
                int option = scanner.nextInt();

                if (option == 0) {
                    System.out.println("Дякую за використання програми. До побачення!");
                    break;
                }

                // Обробка введеної опції
                switch (option) {
                    case 1:
                        WorkWithPostgreSql.createGroupsTable();
                        break;
                    case 2:
                        WorkWithPostgreSql.createStudentsTable();
                        break;
                    case 3:
                        scanner.nextLine(); // Очистка буфера після nextInt()
                        System.out.print("Введіть назву групи: ");
                        String groupName = scanner.nextLine();
                        WorkWithPostgreSql.insertGroup(groupName);
                        break;
                    case 4:
                        scanner.nextLine(); // Очистка буфера після nextInt()
                        //Student student = new Student(fullName, course, specCode, average, isCommunityWork, groupId);
                        System.out.print("Введіть повне ім'я студента: ");
                        Student student= new Student();
                        student.setFullName(scanner.nextLine());
                        System.out.print("Введіть курс: ");
                        student.setCourse(scanner.nextInt());
                        scanner.nextLine(); // Очистка буфера після nextInt()
                        System.out.print("Введіть код спеціалізації: ");
                        student.setSpecializationCode(scanner.nextLine());
                        System.out.print("Введіть середній бал: ");

                        // Встановлення американського формату для чисел з плаваючою комою
                        scanner.useLocale(Locale.US);

                        student.setAverageGrade(scanner.nextDouble());
                        scanner.nextLine(); // Очистка буфера після nextDouble()
                        System.out.print("Чи є студент учасником громадських робіт (true/false): ");
                        student.setCommunityWork(scanner.nextBoolean());
                        scanner.nextLine(); // Очистка буфера після nextBoolean()
                        System.out.print("Введіть ID групи: ");
                        student.setGroup_id(scanner.nextInt());
                        scanner.nextLine(); // Очистка буфера після nextBoolean()
                        System.out.println("Введіть факультет: ");
                        student.setFaculty(scanner.nextLine());
                        WorkWithPostgreSql.insertStudent(student);
                        break;
                    case 5:
                        WorkWithPostgreSql.selectIntoGroups();
                        break;
                    case 6:
                        WorkWithPostgreSql.selectIntoTable();
                        break;
                    case 7:
                        WorkWithPostgreSql.updateInTable();
                        break;
                    case 8:
                        System.out.print("Введіть ID студента для видалення: ");
                        int studentId = scanner.nextInt();
                        WorkWithPostgreSql.DelbyId(studentId);
                        break;
                    case 9:
                        WorkWithPostgreSql.getAvg();
                        break;
                    case 10:
                        WorkWithPostgreSql.excluding();
                        break;
                    case 11:
                        WorkWithPostgreSql.getMarks();
                        break;
                    default:
                        System.out.println("Невідома опція. Будь ласка, введіть правильний номер опції.");
                }
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }




}

package ConnectToPostgreSq;

import com.example.demo1.MainApplication;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MongoCRUD extends MainApplication {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {

            MongoDatabase groupDatabase = mongoClient.getDatabase("group");
            MongoCollection<Document> groupCollection = groupDatabase.getCollection("groupCollection");

            MongoDatabase facultyDatabase = mongoClient.getDatabase("faculties");
            MongoCollection<Document> facultyCollection = facultyDatabase.getCollection("facultyCollection");

            // Example: Read all documents from the collections
            System.out.println("Group Collection:");
            readAllDocuments(groupCollection);
            System.out.println("\nFaculty Collection:");
            readAllDocuments(facultyCollection);
            Student student = new Student();
            // Check if the user wants to add, delete a student from the groupCollection, or add/delete a group from facultyCollection
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введіть повне ім'я студента: ");
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
            System.out.print("Enter 1 to add student into group, 2 to delete student from group, 3 to add new group to facultyCollection, 4 to delete group from facultyCollection: ");
            int userInput = scanner.nextInt();

            if (userInput == 1) {
                // Create a new element in groupCollection
                createNewGroupElement(groupCollection, student);
            } else if (userInput == 2) {
                // Delete a student from the groupCollection
                deleteStudentFromGroup(groupCollection, scanner);
            } else if (userInput == 3) {
                // Add a new group to facultyCollection
                addGroupToFaculty(facultyCollection, student);
            } else if (userInput == 4) {
                // Delete a group from the facultyCollection
                deleteGroupFromFaculty(facultyCollection, scanner);
            }

            // Read all documents again to verify the update or decrease
            System.out.println("\nGroup Collection After Update:");
            readAllDocuments(groupCollection);
            System.out.println("\nFaculty Collection After Update:");
            readAllDocuments(facultyCollection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readAllDocuments(MongoCollection<Document> collection) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            System.out.println(document.toJson());
        }
    }

    public  static void createNewGroupElement(MongoCollection<Document> groupCollection, Student student) {
        // Get the _id from the user
        System.out.print("Enter the _id for the new group: ");


        // Check if the group _id already exists
        Document existingGroup = groupCollection.find(Filters.eq("_id", student.getSpecializationCode())).first();

        if (existingGroup != null) {
            // If the group _id already exists, increment the number of People
            groupCollection.updateOne(Filters.eq("_id", student.getSpecializationCode()), new Document("$inc", new Document("People", 1)));
            System.out.println("Group with _id " + student.getSpecializationCode() + " already exists. Incremented the number of People.");
        } else {
            // If the group _id does not exist, create a new group with the specified _id and number of People
            groupCollection.insertOne(new Document("_id", student.getSpecializationCode()).append("People", 1));
            System.out.println("Created a new group with _id " + student.getSpecializationCode() + " and number of People 1.");
        }
    }

    public  static void deleteStudentFromGroup(MongoCollection<Document> groupCollection, Scanner scanner) {
        // Get the _id from the user
        System.out.print("Enter the _id of the group to delete a student: ");
        String groupId = scanner.next();

        // Check if the group _id exists
        Document existingGroup = groupCollection.find(Filters.eq("_id", groupId)).first();

        if (existingGroup != null) {
            // If the group _id exists, decrement the number of People
            groupCollection.updateOne(Filters.eq("_id", groupId), new Document("$inc", new Document("People", -1)));
            System.out.println("Decreased the number of People for group with _id " + groupId + ".");
        } else {
            System.out.println("Group with _id " + groupId + " does not exist. Cannot delete a student.");
        }
    }

    public  static void addGroupToFaculty(MongoCollection<Document> facultyCollection, Student student) {
        // Get the faculty ID from the user
        System.out.print("Enter the faculty ID to add a group: ");
        //String facultyId = scanner.next(); [Abduction]

        // Check if the faculty ID exists
        Document existingFaculty = facultyCollection.find(Filters.eq("_id", student.getFaculty())).first();

        if (existingFaculty != null) {
            // If the faculty ID exists, add the group to the faculty
            addGroupToExistingFaculty(existingFaculty, facultyCollection,student );
        } else {
            System.out.println("Faculty with ID " + student.getFaculty() + " does not exist. Cannot add a group.");
        }
    }

    public  static void addGroupToExistingFaculty(Document faculty, MongoCollection<Document> facultyCollection, Student student) {
        // Get the group name from the user
        System.out.print("Enter the group name to add to the faculty: ");

        // Update the faculty document to add the group
        facultyCollection.updateOne(
                Filters.eq("_id", faculty.get("_id")),
                new Document("$addToSet", new Document("group", student.getSpecializationCode()))
        );
        System.out.println("Added group " + student.getSpecializationCode() + " to faculty with ID " + faculty.get("_id"));
    }

    public  static void deleteGroupFromFaculty(MongoCollection<Document> facultyCollection, Scanner scanner) {
        // Get the faculty ID from the user
        System.out.print("Enter the faculty ID to delete a group: ");
        Student student = new Student();
        String facultyId = scanner.next();
        student.setFaculty(facultyId);

        // Check if the faculty ID exists
        Document existingFaculty = facultyCollection.find(Filters.eq("_id", student.getFaculty())).first();

        if (existingFaculty != null) {
            // If the faculty ID exists, delete the group from the faculty
            deleteGroupFromExistingFaculty(existingFaculty, facultyCollection, scanner);
        } else {
            System.out.println("Faculty with ID " + student.getFaculty() + " does not exist. Cannot delete a group.");
        }
    }

    public  static void deleteGroupFromExistingFaculty(Document faculty, MongoCollection<Document> facultyCollection, Scanner scanner) {
        // Get the group name from the user
        System.out.print("Enter the group name to delete from faculty: ");
        Student student = new Student();
        String groupName = scanner.next();
        student.setGroup(groupName);

        // Get the list of groups from the faculty
        List<String> groups = (List<String>) faculty.get("group");

        if (groups != null && groups.contains(student.getGroup())) {
            // If the group exists in the faculty, remove it
            groups.remove(groupName);

            // Update the faculty document with the modified list of groups
            facultyCollection.replaceOne(Filters.eq("_id", faculty.getString("_id")), faculty);

            System.out.println("Deleted group " + student.getGroup() + " from faculty with ID " + faculty.getString("_id"));
        } else {
            System.out.println("Group " + student.getGroup() + " does not exist in faculty with ID " + faculty.getString("_id"));
        }
    }
}

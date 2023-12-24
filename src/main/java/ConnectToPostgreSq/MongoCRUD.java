package ConnectToPostgreSq;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.List;
import java.util.Scanner;

public class MongoCRUD {

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

            // Check if the user wants to add, delete a student from the groupCollection, or add/delete a group from facultyCollection
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter 1 to add student into group, 2 to delete student from group, 3 to add new group to facultyCollection, 4 to delete group from facultyCollection: ");
            int userInput = scanner.nextInt();

            if (userInput == 1) {
                // Create a new element in groupCollection
                createNewGroupElement(groupCollection, scanner);
            } else if (userInput == 2) {
                // Delete a student from the groupCollection
                deleteStudentFromGroup(groupCollection, scanner);
            } else if (userInput == 3) {
                // Add a new group to facultyCollection
                addGroupToFaculty(facultyCollection, scanner);
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

    private static void readAllDocuments(MongoCollection<Document> collection) {
        MongoCursor<Document> cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            System.out.println(document.toJson());
        }
    }

    private static void createNewGroupElement(MongoCollection<Document> groupCollection, Scanner scanner) {
        // Get the _id from the user
        System.out.print("Enter the _id for the new group: ");
        String groupId = scanner.next();

        // Check if the group _id already exists
        Document existingGroup = groupCollection.find(Filters.eq("_id", groupId)).first();

        if (existingGroup != null) {
            // If the group _id already exists, increment the number of People
            groupCollection.updateOne(Filters.eq("_id", groupId), new Document("$inc", new Document("People", 1)));
            System.out.println("Group with _id " + groupId + " already exists. Incremented the number of People.");
        } else {
            // If the group _id does not exist, create a new group with the specified _id and number of People
            groupCollection.insertOne(new Document("_id", groupId).append("People", 1));
            System.out.println("Created a new group with _id " + groupId + " and number of People 1.");
        }
    }

    private static void deleteStudentFromGroup(MongoCollection<Document> groupCollection, Scanner scanner) {
        System.out.print("Enter the _id of the group to delete a student: ");
        String groupId = scanner.next();
        Document existingGroup = groupCollection.find(Filters.eq("_id", groupId)).first();
        if (existingGroup != null) {
            groupCollection.updateOne(Filters.eq("_id", groupId), new Document("$inc", new Document("People", -1)));
            System.out.println("Decreased the number of People for group with _id " + groupId + ".");
        }
        else {
            System.out.println("Group with _id " + groupId + " does not exist. Cannot delete a student.");
        }
    }

    private static void addGroupToFaculty(MongoCollection<Document> facultyCollection, Scanner scanner) {
        System.out.print("Enter the faculty ID to add a group: ");
        String facultyId = scanner.next();
        Document existingFaculty = facultyCollection.find(Filters.eq("_id", facultyId)).first();
        if (existingFaculty != null) {
            addGroupToExistingFaculty(existingFaculty, facultyCollection, scanner);
        } else {
            System.out.println("Faculty with ID " + facultyId + " does not exist. Cannot add a group.");
        }
    }

    private static void addGroupToExistingFaculty(Document faculty, MongoCollection<Document> facultyCollection, Scanner scanner) {
        // Get the group name from the user
        System.out.print("Enter the group name to add to the faculty: ");
        String groupName = scanner.next();

        // Update the faculty document to add the group
        facultyCollection.updateOne(
                Filters.eq("_id", faculty.get("_id")),
                new Document("$addToSet", new Document("group", groupName))
        );
        System.out.println("Added group " + groupName + " to faculty with ID " + faculty.get("_id"));
    }

    private static void deleteGroupFromFaculty(MongoCollection<Document> facultyCollection, Scanner scanner) {
        // Get the faculty ID from the user
        System.out.print("Enter the faculty ID to delete a group: ");
        String facultyId = scanner.next();

        // Check if the faculty ID exists
        Document existingFaculty = facultyCollection.find(Filters.eq("_id", facultyId)).first();

        if (existingFaculty != null) {
            // If the faculty ID exists, delete the group from the faculty
            deleteGroupFromExistingFaculty(existingFaculty, facultyCollection, scanner);
        } else {
            System.out.println("Faculty with ID " + facultyId + " does not exist. Cannot delete a group.");
        }
    }

    private static void deleteGroupFromExistingFaculty(Document faculty, MongoCollection<Document> facultyCollection, Scanner scanner) {
        System.out.print("Enter the group name to delete from faculty: ");
        String groupName = scanner.next();
        List<String> groups = (List<String>) faculty.get("group");
        if (groups != null && groups.contains(groupName)) {
            // If the group exists in the faculty, remove it
            groups.remove(groupName);
            facultyCollection.replaceOne(Filters.eq("_id", faculty.getString("_id")), faculty);
            System.out.println("Deleted group " + groupName + " from faculty with ID " + faculty.getString("_id"));
        } else {
            System.out.println("Group " + groupName + " does not exist in faculty with ID " + faculty.getString("_id"));
        }
    }
}




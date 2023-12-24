package ConnectToPostgreSq;

import redis.clients.jedis.Jedis;
import java.time.LocalDate;
import java.time.LocalTime;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class WorkWithPostgreSql {
    static Jedis jedis = new Jedis("http://localhost:6379/");
    static LocalDate currentDate = LocalDate.now();
    static LocalTime currentTime = LocalTime.now();

    public static void createGroupsTable() {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS GroupsDA (" +
                    "GroupID SERIAL PRIMARY KEY," +
                    "GroupName VARCHAR(255) NOT NULL);";

            statement.executeUpdate(sql);
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("createdGroupstable " + currentTime));
            System.out.println("Groups table created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertGroup(String groupName) {
        try (Connection connection = ConnectToPostgreSql.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO GroupsDA (GroupName) VALUES (?)")) {

            preparedStatement.setString(1, groupName);
            preparedStatement.executeUpdate();
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("Addedgroup " + groupName + " "+ currentTime));
            System.out.println("Group added successfully.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void createStudentsTable() {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS StudentsFac (" +
                    "StudentID SERIAL PRIMARY KEY," +
                    "FullName VARCHAR(255) NOT NULL," +
                    "COURSE REAL," +
                    "specializationCode CHAR(50)," +
                    "AVERAGE_MARK REAL," +
                    "communityWork BOOLEAN," +
                    "GroupID INT," +
                    "Faculty  VARCHAR(255) NOT NULL,"+
                    "FOREIGN KEY (GroupID) REFERENCES Groups(GroupID));";

            statement.executeUpdate(sql);
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("createdStudentstable " + currentTime));
            System.out.println("Students table created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void awardScholarships() {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "UPDATE StudentsFac SET ScholarshipType = " +
                    "CASE " +
                    "   WHEN AVERAGE_MARK = 5.0 THEN 'Підвищена стипендія' " +
                    "   WHEN AVERAGE_MARK >= 4.5 THEN 'Звичайна стипендія' " +
                    "   WHEN AVERAGE_MARK = 4.0 AND communityWork = true THEN 'Звичайна стипендія' " +
                    "   ELSE 'Без стипендії' " +
                    "END";

            statement.executeUpdate(sql);
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("Scholarships awarded " + currentTime));
            System.out.println("Scholarships awarded successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addFacultyColum() {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "ALTER TABLE StudentsFac ADD COLUMN Faculty VARCHAR(50);";

            statement.executeUpdate(sql);
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("Faculty column added " + currentTime));
            System.out.println("Faculty column added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addScholarshipTypeColumn() {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "ALTER TABLE StudentsFac ADD COLUMN ScholarshipType VARCHAR(50);";

            statement.executeUpdate(sql);
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("ScholarshipType column " + currentTime));
            System.out.println("ScholarshipType column added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertStudent(Student student) {
        try (Connection connection = ConnectToPostgreSql.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO StudentsFac (FullName, COURSE, specializationCode, AVERAGE_MARK, communityWork, GroupID,Faculty) " +
                             "VALUES (?, ?, ?, ?, ?, ?,?)")) {

            // Перевірка на null перед встановленням значення для FullName
            if (student.getFullName() != null) {
                preparedStatement.setString(1, student.getFullName());
            } else {
                throw new IllegalArgumentException("Помилка: Пусте ім'я студента.");
            }
            preparedStatement.setDouble(2, student.getCourse());
            preparedStatement.setString(3, student.getSpecializationCode());
            preparedStatement.setDouble(4, student.getAverageGrade());
            preparedStatement.setBoolean(5, student.isCommunityWork());
            preparedStatement.setInt(6, student.getGroup_id());
            preparedStatement.setString(7,student.getFaculty());

            preparedStatement.executeUpdate();
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("Added student " + student.getFullName() + " "+ currentTime));
            System.out.println("Студента додано успішно.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //    public static void createTable() {
//        try (Connection connection = ConnectToPostgreSql.connect();
//             Statement statement = connection.createStatement()) {
//
//            // Спочатку створимо базу даних "studentss", якщо її ще немає
//
//
//            String sql = "CREATE TABLE temp " +
//                    "(STUDENT_ID INT PRIMARY KEY," +
//                    " NAME TEXT NOT NULL, " +
//                    " COURSE REAL, " +
//                    " specializationCode CHAR(50), " +
//                    " AVERAGE_MARK REAL," +
//                    " communityWork BOOLEAN)";
//
//            statement.executeUpdate(sql);
//            System.out.println("Table created successfully");
//
//        } catch (SQLException e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//        }
//    }
    public static void addColumnsToTable(String tableName)
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement())
        {
            String sql = "ALTER TABLE " + tableName +
                    " ADD COLUMN successRate REAL, " +
                    "ADD COLUMN numberOfExams INT, " +
                    "ADD COLUMN fullName TEXT, " +
                    "ADD COLUMN dormitoryResidence BOOLEAN;";

            statement.executeUpdate(sql);
            currentTime = LocalTime.now();
            jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("Columns added to " + tableName + " "+ currentTime));
            System.out.println("Columns added successfully to the table " + tableName);

        }
        catch (SQLException e) {
            System.err.println("Error adding columns: " + e.getMessage());
        }
    }

//    public static void insertIntoTable(Student student) {
//        try (Connection connection = ConnectToPostgreSql.connect();
//             Statement statement = connection.createStatement()) {
//
//            // Використовуйте PreparedStatement для безпечного вставлення значень
//            String sql = "INSERT INTO temp (STUDENT_ID, NAME, COURSE, specializationCode, AVERAGE_MARK, communityWork) "
//                    + "VALUES (?, ?, ?, ?, ?, ?)";
//
//            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//                preparedStatement.setInt(1, student.getStudentId());
//                preparedStatement.setString(2, student.getFullName());
//                preparedStatement.setInt(3, student.getCourse());
//                preparedStatement.setString(4, student.getSpecializationCode());
//                preparedStatement.setDouble(5, student.getAverageGrade());
//                preparedStatement.setBoolean(6, student.isCommunityWork());
//
//                preparedStatement.executeUpdate();
//            }
//        } catch (SQLException e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            System.exit(0);
//        }
//        System.out.println("Records created successfully");
//    }


    public static void selectIntoGroups()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet =statement.executeQuery("SELECT * FROM GroupsDA "))
        {

            while (resultSet.next())
            {
                int id = resultSet.getInt("groupid");
                String fullName= resultSet.getString("groupname");
                System.out.println("STUDENT_ID = " + id);
                System.out.println("Name = " + fullName );
                System.out.println();
            }
        }
        catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }
    public static void selectIntoTable()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet =statement.executeQuery("SELECT * FROM StudentsFac "))
        {

            while (resultSet.next())
            {
                int id = resultSet.getInt("studentid");
                String fullName= resultSet.getString("fullname");
                int course = resultSet.getInt("COURSE");
                String specCode= resultSet.getString("specializationCode");
                double average= resultSet.getDouble("AVERAGE_MARK");
                boolean isWork=resultSet.getBoolean("communityWork");
                int getId=resultSet.getInt("groupid");
                System.out.println("STUDENT_ID = " + id);
                System.out.println("Name = " + fullName );
                System.out.println("Course = " + course);
                System.out.println("SpecializationCode = " + specCode);
                System.out.println("AVERAGE_MARK = " + average);
                System.out.println("isCommunityWork = " + isWork);
                System.out.println("GroupID = " + getId);
                System.out.println();
            }
        }
        catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }
    public static void updateInTable() {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Введіть SQL-запит для оновлення: ");
            String updateSql = scanner.nextLine();

            statement.executeUpdate(updateSql);

            System.out.println("Результати після оновлення:");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM  StudentsFac;");
            while (resultSet.next()) {
                // Виведення результатів після оновлення
                int id = resultSet.getInt("studentid");
                String fullName= resultSet.getString("fullname");
                int course = resultSet.getInt("COURSE");
                String specCode= resultSet.getString("specializationCode");
                double average= resultSet.getDouble("AVERAGE_MARK");
                boolean isWork=resultSet.getBoolean("communityWork");
                int getId=resultSet.getInt("groupid");
                System.out.println("STUDENT_ID = " + id);
                System.out.println("Name = " + fullName );
                System.out.println("Course = " + course);
                System.out.println("SpecializationCode = " + specCode);
                System.out.println("AVERAGE_MARK = " + average);
                System.out.println("isCommunityWork = " + isWork);
                System.out.println("GroupID = " + getId);
                System.out.println();
            }
            resultSet.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Операція виконана успішно");
    }
    public static void DelbyId(int studentId)
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement();
        )
        {
            Scanner scanner = new Scanner(System.in);
            String deleteSql = "DELETE FROM StudentsFac WHERE studentid = " + studentId;
            statement.executeUpdate(deleteSql);
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM  StudentsFac;");
            while (resultSet.next()) {
                int id = resultSet.getInt("studentid");
                String fullName= resultSet.getString("fullname");
                int course = resultSet.getInt("COURSE");
                String specCode= resultSet.getString("specializationCode");
                double average= resultSet.getDouble("AVERAGE_MARK");
                boolean isWork=resultSet.getBoolean("communityWork");
                int getId=resultSet.getInt("groupid");
                System.out.println("STUDENT_ID = " + id);
                System.out.println("Name = " + fullName );
                System.out.println("Course = " + course);
                System.out.println("SpecializationCode = " + specCode);
                System.out.println("AVERAGE_MARK = " + average);
                System.out.println("isCommunityWork = " + isWork);
                System.out.println("GroupID = " + getId);
                System.out.println();
            }
            resultSet.close();


        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        currentTime = LocalTime.now();
        jedis.rpush(String.valueOf(currentDate.getDayOfYear()), String.valueOf("deleted student" + studentId + " "+ currentTime));
        System.out.println("Операція виконана успішно");
    }
    public static void DelOperations()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement();
        )
        {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введіть SQL-запит для видалення: ");
            String updateSql = scanner.nextLine();

            statement.executeUpdate(updateSql);
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM  StudentsFac;");
            while (resultSet.next()) {
                int id = resultSet.getInt("studentid");
                String fullName= resultSet.getString("fullname");
                int course = resultSet.getInt("COURSE");
                String specCode= resultSet.getString("specializationCode");
                double average= resultSet.getDouble("AVERAGE_MARK");
                boolean isWork=resultSet.getBoolean("communityWork");
                int getId=resultSet.getInt("groupid");
                System.out.println("STUDENT_ID = " + id);
                System.out.println("Name = " + fullName );
                System.out.println("Course = " + course);
                System.out.println("SpecializationCode = " + specCode);
                System.out.println("AVERAGE_MARK = " + average);
                System.out.println("isCommunityWork = " + isWork);
                System.out.println("GroupID = " + getId);
                System.out.println();
            }
            resultSet.close();


        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Операція виконана успішно");
    }
    public static void getAvg()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "SELECT g.GroupName, AVG(s.AVERAGE_MARK) AS AverageGradeInGroup " +
                    "FROM Groups g JOIN  StudentsFac s ON g.GroupID = s.GroupID " +
                    "GROUP BY g.GroupName";

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String groupName = resultSet.getString("GroupName");
                double averageGrade = resultSet.getDouble("AverageGradeInGroup");

                System.out.println("Group: " + groupName + ", Average Grade: " + averageGrade);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void excluding()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "SELECT g.GroupName, s.StudentID, s.FullName " +
                    "FROM Groups g JOIN  StudentsFac s ON g.GroupID = s.GroupID " +
                    "WHERE s.AVERAGE_MARK = 2.0 " +
                    "ORDER BY g.GroupName, s.FullName";

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String groupName = resultSet.getString("GroupName");
                int studentID = resultSet.getInt("StudentID");
                String fullName = resultSet.getString("FullName");

                System.out.println("Group: " + groupName + ", StudentID: " + studentID + ", FullName: " + fullName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void getList()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "SELECT g.GroupName, s.StudentID, s.FullName, s.AVERAGE_MARK " +
                    "FROM Groups g JOIN  StudentsFac s ON g.GroupID = s.GroupID " +
                    "ORDER BY g.GroupName, s.FullName";

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String groupName = resultSet.getString("GroupName");
                int studentID = resultSet.getInt("StudentID");
                String fullName = resultSet.getString("FullName");
                double averageMark = resultSet.getDouble("AVERAGE_MARK");

                System.out.println("Group: " + groupName + ", StudentID: " + studentID +
                        ", FullName: " + fullName + ", Average Mark: " + averageMark);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void getMarks()
    {
        try (Connection connection = ConnectToPostgreSql.connect();
             Statement statement = connection.createStatement()) {

            String sql = "SELECT g.GroupName, s.FullName, s.AVERAGE_MARK " +
                    "FROM Groups g JOIN  StudentsFac s ON g.GroupID = s.GroupID " +
                    "ORDER BY g.GroupName, s.FullName";

            ResultSet resultSet = statement.executeQuery(sql);

            // Створюємо мапу для збереження розподілу оцінок за групами
            Map<String, Map<String, Integer>> gradeDistribution = new TreeMap<>();

            while (resultSet.next()) {
                String groupName = resultSet.getString("GroupName");
                String fullName = resultSet.getString("FullName");
                double averageMark = resultSet.getDouble("AVERAGE_MARK");

                // Визначаємо категорію оцінки (наприклад, "A", "B", "C", "D", "F") на основі середнього балу
                String gradeCategory = determineGradeCategory(averageMark);

                // Додаємо запис до мапи
                gradeDistribution
                        .computeIfAbsent(groupName, k -> new HashMap<>())
                        .merge(gradeCategory, 1, Integer::sum);
            }

            // Виводимо результат
            for (Map.Entry<String, Map<String, Integer>> entry : gradeDistribution.entrySet()) {
                String groupName = entry.getKey();
                Map<String, Integer> groupGradeDistribution = entry.getValue();

                System.out.println("Group: " + groupName);
                for (Map.Entry<String, Integer> gradeEntry : groupGradeDistribution.entrySet()) {
                    String gradeCategory = gradeEntry.getKey();
                    int count = gradeEntry.getValue();

                    System.out.println("  Grade " + gradeCategory + ": " + count + " students");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String determineGradeCategory(double averageMark) {
        if (averageMark >= 4.5) {
            return "A";
        } else if (averageMark >= 3.9) {
            return "B";
        } else if (averageMark >= 3.5) {
            return "C";
        } else if (averageMark >= 3) {
            return "D";
        } else {
            return "F";
        }
    }
    public static void tester()
    {
        try (Connection connection = ConnectToPostgreSql.connect()) {

            // Вибір стовпців (замініть це на ваші потреби)
            String selectedColumns = "studentid, fullname, AVERAGE_MARK";

            // Виконання запиту до бази даних
            String query = "SELECT " + selectedColumns + " FROM  StudentsFac;";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Виведення заголовків стовпців
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t");
                }
                System.out.println("\n------------------------");

                // Отримання даних з бази даних
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(resultSet.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void menu()
    {
        System.out.println("Меню");
        System.out.println("Натисніть -1,якщо ви хочете створити таблицю груп");
        System.out.println("Натисніть -2,якщо ви хочете створити таблицю студентів ");
        System.out.println("Натисніть -3,якщо ви хочете добавити групу в таблицю  ");
        System.out.println("Натисніть -4,якщо ви хочете добавити студента в таблицю ");
        System.out.println("Натисніть -5,якщо ви хочете отримати таблицю груп ");
        System.out.println("Натисніть -6,якщо ви хочете отримати таблицю студентів ");
        System.out.println("Натисніть -7,якщо ви хочете зробити оновлення в таблиці студентів");
        System.out.println("Натисніть -8,якщо ви хочете видалити студента з таблиці за ID");
        System.out.println("Натисніть -8,якщо ви хочете видали студента SQL запитом");
        System.out.println("Натисніть -9,якщо ви хочете отримати сердній бал груп");
        System.out.println("Натисніть -10,якщо ви хочете отримати список на відрахування");
        System.out.println("Натисніть -11,якщо ви хочете отримати список студента та групи");
    }


}
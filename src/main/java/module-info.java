module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires redis.clients.jedis;


    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}
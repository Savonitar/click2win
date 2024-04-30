import ballerina/io;
import ballerina/os;
import ballerinax/java.jdbc;

public function main() returns error? {
    string? jdbcUrl = os:getEnv("postgress_url");
    string? username = os:getEnv("postgress_user");
    string? password = os:getEnv("postgress_pass");

    if (jdbcUrl == null || username == null || password == null) {
        io:println("One or more required environment variables are not set.");
        return error("Missing environment variables");
    }

    jdbc:Client dbClient = check new ({
        url: jdbcUrl,
        username: username,
        password: password,
        dbOptions: { ssl: true }
    });

    string selectQuery = "SELECT user_id, playername FROM players";
    string updateQuery = "UPDATE players SET playername = ? WHERE user_id = ?";

    var result = dbClient->select(selectQuery);
    error? resultError = result.error();

    while (result.hasNext()) {
        var row = result.getNext();
        int userId = check <int>row["user_id"];
        string playerName = check <string>row["playername"];

        if (playerName.contains("shit")) {
            string newPlayerName = playerName.replace("shit", "Ball");

            var updateParams = [newPlayerName, userId];
            var updateResult = dbClient->update(updateQuery, updateParams);
            error? updateError = updateResult.error();

            if (updateError != null) {
                io:println("Error updating player with ID: " + userId + ", Error: " + updateError);
            } else {
                io:println("Player with ID " + userId + " updated successfully.");
            }
        }
    }

    error? closeResult = result.close();
    if (closeResult != null) {
        return closeResult;
    }

    return ();
}
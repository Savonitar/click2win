import ballerina/io;
import ballerinax/java.jdbc;
import ballerina/sql;
import ballerina/os;

public function main() returns error? {
    string jdbcUrl = os:getEnv("postgress_url");
    string username = os:getEnv("postgress_user");
    string password = os:getEnv("postgress_pass");

    jdbc:Client dbClient = check new(jdbcUrl, username, password);

    io:println(dbClient);

    sql:ExecutionResult result =
        check dbClient->execute(`UPDATE players SET playername = fixed WHERE playername like '%shit'`);
}
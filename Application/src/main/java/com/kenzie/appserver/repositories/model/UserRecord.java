package com.kenzie.appserver.repositories.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;
import java.util.UUID;

@DynamoDBTable(tableName = "Users")
public class UserRecord {
    private String username;
    private String name;
    private String hashedPassword;

    @DynamoDBHashKey(attributeName = "Username")
    public String getUsername() {
        return this.username;
    }

    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return this.name;
    }

    @DynamoDBAttribute(attributeName = "HashedPassword")
    public String getHashedPassword() {
        return this.hashedPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRecord that = (UserRecord) o;
        return Objects.equals(username, that.username) && Objects.equals(name, that.name) && Objects.equals(hashedPassword, that.hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, hashedPassword);
    }
}

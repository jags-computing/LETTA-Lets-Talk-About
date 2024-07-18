package com.letta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.letta.entities.Group;

public class TopicsDAO extends DAO{

    private final GroupsDAO GroupsDAO = new GroupsDAO();


    public List<Group> get(String topicname) throws DAOException {
        List<Group> groupsList = new ArrayList<>();
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT g.* FROM groups g INNER JOIN group_topics t ON g.group_id = t.group_id WHERE t.topic_name = ?";
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, topicname);
                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        groupsList.add(new Group(
                                result.getString("group_name"),
                                result.getString("description"),
                                result.getInt("owner_id"),
                                result.getInt("group_id")
                        ));
                    }
                }
            }
            return groupsList;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }


    public List<String> getTopicnames()throws DAOException{
        List<String> topicnames = new ArrayList<>();
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT topic_name FROM topics";
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        topicnames.add(result.getString("topic_name"));  
                    }
                }
            }
            return topicnames;
        }
        catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Insert in the table a topic to a group
     *
     * @param groupName the name of the group to be retrieved.
     * @param topics the topics to be inserted in the table.s
     * @throws DAOException if an error happens while retrieving the user.
     * @throws IllegalArgumentException if the provided login does not
     */
    public void setTopics(String groupName, List<String> topics) throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final int group_id = GroupsDAO.getGroupId(groupName);
            final String query = "INSERT INTO group_topics (group_id, topic_name) VALUES (?, ?)";
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                for (String topic : topics) {
                    statement.setInt(1, group_id);
                    statement.setString(2, topic);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    

    
}

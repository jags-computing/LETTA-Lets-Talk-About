package com.letta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.letta.entities.EventReport;
import com.letta.entities.Group;

public class GroupsDAO extends DAO {

    private final static Logger LOG = Logger.getLogger(UsersDAO.class.getName());


    public List<String> getbytopic(int group_id) throws DAOException {
        List<String> topicNames = new ArrayList<>();
        try (final Connection conn = this.getConnection()) {

            final String query = "SELECT group_topics.topic_name FROM group_topics WHERE group_id=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, group_id);

                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        topicNames.add(result.getString("topic_name"));
                    }
                }
            }

            return topicNames;

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public Group getByName(String group_name) throws DAOException {
        try (final Connection conn = this.getConnection()) {

            final String query = "SELECT * FROM groups WHERE group_name=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, group_name);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid group name");
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private Group rowToEntity(ResultSet result) throws SQLException {
        return new Group(
                result.getString("group_name"),
                result.getString("description"),
                result.getInt("owner_id")
        );
    }

    /**
     * Registers a new group in the system.
     *
     * @param groupName   the name/login of the user to be registered
     * @param description the email of the user to be registered.
     * @param ownerId     the password in plain text of news user
     * @throws DAOException if an error happens while registering the user.
     */
    public Group addGroup(String groupName, String description, int ownerId) throws DAOException {
        try (final Connection conn = this.getConnection()) {

            final String query = "INSERT INTO groups (group_name, description, owner_id) VALUES (?, ?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, groupName);
                statement.setString(2, description);
                statement.setInt(3, ownerId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error group user", e);
            throw new DAOException(e);
        }

        return new Group(groupName, description, ownerId);

    }


    public void addUserToGroup(int userID, int groupID) throws DAOException {
        try (final Connection conn = this.getConnection()) {

            final String query = "INSERT INTO user_groups (user_id, group_id) VALUES (?, ?)";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, userID);
                statement.setInt(2, groupID);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error group user", e);
            throw new DAOException(e);
        }

    }
    
    /**
     * Return a group_id of a group existing in the system.
     *
     * @param groupName the name of the group to be retrieved.
     * @return a group_id with the provided name.
     * @throws DAOException if an error happens while retrieving the group.
     */
    public int getGroupId(String groupName) throws DAOException {
        try (final Connection conn = this.getConnection()) {
            final String query = "SELECT group_id FROM groups WHERE group_name=?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, groupName);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getInt("group_id");
                    } else {
                        throw new IllegalArgumentException("Invalid group name");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting the groupId", e);
            throw new DAOException(e);
        }

    }


    public List<EventReport> getEventReportsByGroup(int user_id, int group_id) throws DAOException{
		List<EventReport> eventReports = new ArrayList<>();

		try (final Connection conn = this.getConnection()){
			if (!isUserModerator(conn, user_id) || !isUserModeratorOfThisGroup(conn, user_id, group_id)){
				throw new DAOException("User is not a moderator for this group");
			}

			List<Integer> eventIds = getEventIdsForGroup(conn, group_id);

			for (int eventId: eventIds){
				List<EventReport> reports = getReportsForEvent(conn, eventId);
				eventReports.addAll(reports);
			}


		}catch (SQLException e) {
            throw new DAOException(e);
        }


		return eventReports;
	}

	public boolean isUserModerator (Connection conn, int user_id) throws SQLException{
		String query = "SELECT `role` FROM `users` WHERE `user_id` = ? AND (`role` = 'MODERATOR' OR `role` = 'ADMIN');";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, user_id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		}
	}

    public static boolean isUserModeratorOfThisGroup(Connection conn, int user_id, int group_id)throws SQLException{
        String query = "SELECT * FROM moderations WHERE group_id = ? AND user_id = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, group_id);
            stmt.setInt(2, user_id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

	private List<Integer> getEventIdsForGroup(Connection conn, int group_id) throws SQLException{
		List<Integer> eventsIds = new ArrayList<>();

		String query = "SELECT `event_id` FROM `events` WHERE `group_id` = ?";

		try(PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1, group_id);
			try(ResultSet rs = stmt.executeQuery()){
				while (rs.next()){
					eventsIds.add(rs.getInt("event_id"));
				}
			}
		}

		return eventsIds;
	}
    public List<Integer> getEventIds(int group_id) throws SQLException {
        try (Connection conn = this.getConnection()) {
            return getEventIdsForGroup(conn, group_id);
        }
    }

	private List<EventReport> getReportsForEvent(Connection conn, int event_id) throws SQLException{
		List<EventReport> reports = new ArrayList<>();

		String query = "SELECT * FROM event_reports WHERE event_id=?";

		try(final PreparedStatement stmt = conn.prepareStatement(query)){
			stmt.setInt(1, event_id);

			try (final ResultSet result = stmt.executeQuery()){
				while (result.next()){
					String reportReason = result.getString("report_reason");
                    int owner = result.getInt("owner_id");
					reports.add(new EventReport(event_id, reportReason, owner));
				}
			}

		}

		return reports;
	}
}





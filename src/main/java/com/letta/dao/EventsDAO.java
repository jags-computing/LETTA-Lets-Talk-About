package com.letta.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.letta.entities.Comment;
import com.letta.entities.CommentReport;
import com.letta.entities.Event;
import com.letta.entities.EventReport;



public class EventsDAO extends DAO{

    private final static Logger LOG = Logger.getLogger(PeopleDAO.class.getName());

    public Event add(String event_name, String event_date, int groupid, String event_description, String media_file, int ownerid)
	throws DAOException, IllegalArgumentException {
		
		if (event_name.isEmpty() || event_date==null || event_description==null) {
			throw new IllegalArgumentException("Invalid arguments!.");
		}
		try (Connection conn = this.getConnection()) {
			final String query = "INSERT INTO events (event_name, event_date, group_id, event_description, media_file, owner_id) VALUES (?, ?, ?, ?, ?, ?)";
			
			try (PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, event_name);
                java.sql.Date sqlDate = java.sql.Date.valueOf(event_date);
				statement.setDate(2, sqlDate);
                statement.setInt(3, groupid);
                statement.setString(4, event_description);
                statement.setString(5, media_file);
				statement.setInt(6, ownerid);
				
				if (statement.executeUpdate() == 1) { 
						return new Event(event_name, event_date, groupid, event_description, media_file, ownerid);
				} else {
					throw new SQLException("Error inserting value");
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error adding a event", e);
			throw new DAOException(e);
		}
	}


	public void addComment(int eventId, int userId, String commentText) throws DAOException {
		try (Connection conn = this.getConnection()) {
			final String query = "INSERT INTO event_comments (event_id, user_id, comment_text) VALUES (?, ?, ?)";

			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, eventId);
				statement.setInt(2, userId);
				statement.setString(3, commentText);

				statement.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}
	public List<Comment> getCommentsByEventId(int eventId) throws DAOException {
		List<Comment> comments = new ArrayList<>();
		try (Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM event_comments WHERE event_id=?";
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, eventId);
				try (ResultSet result = statement.executeQuery()) {
					while (result.next()) {
						comments.add(rowToComment(result));
					}
				}
			}
			return comments;
		} catch (SQLException e) {
			throw new DAOException(e);
		}
	}

	public EventReport addReport(int eventid, String reportReason, int owner_id)
		throws DAOException, IllegalArgumentException{

		if(reportReason.isEmpty() || owner_id==0){
			throw new IllegalArgumentException("Invalid arguments!.");
		}
		try(Connection conn = this.getConnection()){
			final String query = "INSERT INTO event_reports (event_id, report_reason, owner_id) VALUES (?, ?, ?)";
			try(PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
				statement.setInt(1, eventid);
				statement.setString(2, reportReason);
				statement.setInt(3, owner_id);
				if (statement.executeUpdate() == 1) { 
					return new EventReport(eventid, reportReason, owner_id);
				} else {
					throw new SQLException("Error inserting report");
				}
			}
		}	catch(SQLException e){
			LOG.log(Level.SEVERE, "Error adding a report", e);
			throw new DAOException(e);
		}
	}


	public CommentReport addCommentReport(int comment_id, int user_id, String reportReason)
		throws DAOException, IllegalArgumentException{

		if(reportReason.isEmpty()){
			throw new IllegalArgumentException("Invalid arguments!.");
		}
		try(Connection conn = this.getConnection()){
			final String query = "INSERT INTO comment_reports (comment_id, user_id, report_reason) VALUES (?, ?, ?)";
			try(PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
				statement.setInt(1, comment_id);
				statement.setInt(2, user_id);
				statement.setString(3, reportReason);
				if (statement.executeUpdate() == 1) { 
					return new CommentReport(comment_id, user_id, reportReason);
				} else {
					throw new SQLException("Error inserting comment report");
				}
			}
		}	catch(SQLException e){
			LOG.log(Level.SEVERE, "Error adding a comment report", e);
			throw new DAOException(e);
		}
	}


	/**
	 * Return an event stored persisted in the system associated to a specific group
	 *
	 * @param groupId the id of the group to be retrieved.
	 * @return a Event with the provided group id.
	 * @throws DAOException if an error happens while retrieving the event.
	 * @throws IllegalArgumentException if the provided groupId does not
	 * corresponds with any persisted event.
	 */
	public List<Event> getEventsOfAGroup(String groupId) throws DAOException {
		List<Event> events = new LinkedList<>();
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT * FROM events WHERE group_id=?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, Integer.parseInt(groupId));

				try (final ResultSet result = statement.executeQuery()) {

					// insert event by event in the list of events
					while (result.next()) {
						events.add(rowToEntity(result));
					}
				}
			}

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error finding events for this group", e);
			throw new DAOException(e);
		}

		// return the list of events
		return events;
	}

	public int getIdByName(String eventName) throws DAOException{
		int id_toRet = -1; 
		
		try (final Connection conn = this.getConnection()) {
			final String query = "SELECT events.event_id FROM events WHERE event_name=?";
			
			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, eventName);
				
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						id_toRet = result.getInt("event_id");
					}
				}
			}
			
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error finding events for this group", e);
			throw new DAOException(e);
		}
		
		return id_toRet;  
	}

	public String getNameById(int eventid) throws DAOException{
		String eventName = ""; 

		try (final Connection conn = this.getConnection()){
			final String query = "SELECT events.event_name FROM events WHERE event_id=?";

			try(final PreparedStatement statement = conn.prepareStatement(query)){
				statement.setInt(1, eventid);
				try (final ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						eventName = result.getString("event_name");
					}
				}
			}

		}catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error finding events for this group", e);
			throw new DAOException(e);
		}

		return eventName;
	}

	
	public List<CommentReport> getCommentReport(int commentId, int user_id) throws DAOException {


		List<CommentReport> commentReports = new ArrayList<>();

		try (final Connection conn = this.getConnection()) {
			if (!isUserModeratorOfThisComment(conn, user_id, commentId)){
				throw new DAOException("User is not a moderator for this group");
			}
			final String query = "SELECT * FROM comment_reports WHERE comment_id=?";

			try (final PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setInt(1, commentId);

				try (final ResultSet result = statement.executeQuery()) {

					while(result.next()){
						commentReports.add(new CommentReport(result.getInt("comment_id"), result.getInt("user_id"), result.getString("report_reason")));
					}
				}
			}

			return commentReports;

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error finding comment reports for this comment", e);
			throw new DAOException(e);
		}
	}

	private boolean isUserModeratorOfThisComment(Connection conn, int user_id, int comment_id)throws SQLException{

		int eventID = getEventIdByCommentId(conn, comment_id);
		int groupId = getGroupIdByEventId(conn, eventID);

		return GroupsDAO.isUserModeratorOfThisGroup(conn, user_id, groupId);
	}

	private int getEventIdByCommentId(Connection conn, int comment_id)throws SQLException{

		String query = "SELECT `event_id` FROM event_comments WHERE comment_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)){

			stmt.setInt(1, comment_id);
			try(ResultSet rs = stmt.executeQuery()){
				if (rs.next()) { 
                	return rs.getInt("event_id");
				} else {
					throw new SQLException("No event found for comment_id: " + comment_id);
				}
			}
		}
	}

	private int getGroupIdByEventId(Connection conn, int eventID)throws SQLException{

		String query = "SELECT `group_id` FROM events WHERE event_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)){

			stmt.setInt(1, eventID);
			try(ResultSet rs = stmt.executeQuery()){
				if (rs.next()) { 
                	return rs.getInt("group_id");
				} else {
					throw new SQLException("No group found for eventID: " + eventID);
				}
				
			}
		}
	}


	private Event rowToEntity(ResultSet result) throws SQLException {
		return new Event(
				result.getString("event_name"),
				result.getString("event_date"),
				result.getInt("group_id"),
				result.getString("event_description"),
				result.getString("media_file"),
				result.getInt("owner_id")
		);
	}

	private Comment rowToComment(ResultSet result) throws SQLException {
		int commentId = result.getInt("comment_id");
		int eventId = result.getInt("event_id");
		int userId = result.getInt("user_id");
		String commentText = result.getString("comment_text");

		return new Comment(commentId, eventId, userId, commentText);
	}

}

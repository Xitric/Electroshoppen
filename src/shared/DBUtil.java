package shared;

import java.sql.*;

/**
 * Static utility methods for working with JDBC.
 *
 * @author Kasper
 */
public class DBUtil {

	/**
	 * Safely close the specified statement.
	 *
	 * @param st the statement to close
	 */
	public static void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Safely close the specified prepared statement.
	 *
	 * @param pst the prepared statement to close
	 */
	public static void close(PreparedStatement pst) {
		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Safely close the specified result set.
	 *
	 * @param rs the result set to close
	 */
	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Safely close the specified connection.
	 *
	 * @param conn the connection to close
	 */
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

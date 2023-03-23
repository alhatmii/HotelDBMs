import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HotelDBMS {

	public static void main(String[] args) {

		// Create tables
		String createTablesQuery = "CREATE TABLE Hotels (" + "id INT PRIMARY KEY," + "name VARCHAR(255),"
				+ "location VARCHAR(255)," + "rating DECIMAL(3,2)," + "is_active BIT" + ")"

				+ "CREATE TABLE Rooms (" + "id INT PRIMARY KEY," + "hotel_id INT," + "room_number INT,"
				+ "capacity INT," + "price DECIMAL(10,2)," + "is_available BIT,"
				+ "FOREIGN KEY (hotel_id) REFERENCES Hotels(id)" + ");\n"

				+ "CREATE TABLE Bookings (" + "id INT PRIMARY KEY," + "hotel_id INT," + "room_id INT,"
				+ "start_date DATE," + "end_date DATE," + "FOREIGN KEY (hotel_id) REFERENCES Hotels(id),"
				+ "FOREIGN KEY (room_id) REFERENCES Rooms(id)" + ");";

		String dbUrl = null;

		try (Connection conn = DriverManager.getConnection(dbUrl); 
			Statement stmt = conn.createStatement()) {
			stmt.execute(createTablesQuery);
		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		// Insert 10,000 hotels
		int numHotels = 10000;
		String insertQuery = "INSERT INTO Hotels (id, name, location, rating, is_active) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DriverManager.getConnection(dbUrl);
				PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
			for (int i = 1; i <= numHotels; i++) {
				pstmt.setInt(1, i);
				pstmt.setString(2, "Hotel " + i);
				pstmt.setString(3, "Location " + i);
				pstmt.setDouble(4, Math.floor(Math.random() * 5) + 1); // Random rating between 1 and 5
				pstmt.setBoolean(5, true);
				pstmt.addBatch();
				if (i % 1000 == 0) {
					pstmt.executeBatch();
				}
			}
			pstmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Print 10 hotels
		String selectQuery = "SELECT * FROM Hotels WHERE is_active = 1 ORDER BY id LIMIT 10";
		try (Connection conn = DriverManager.getConnection(dbUrl);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(selectQuery)) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String location = rs.getString("location");
				double rating = rs.getDouble("rating");
				boolean isActive = rs.getBoolean("is_active");
				System.out.println(id + " | " + name + " | " + location + " | " + rating + " | " + isActive);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Make first 10 hotels 'is_active' = false
		String updateQuery = "UPDATE Hotels SET is_active = ? WHERE id <= 10";
		try (Connection conn = DriverManager.getConnection(dbUrl);
				PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
			pstmt.setBoolean(1, false);
			int rowsUpdated = pstmt.executeUpdate();
			System.out.println(rowsUpdated + " rows updated.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
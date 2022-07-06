package com.auction.virtualauctionserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import com.auction.virtualauctionserver.functions.Constants;
import com.auction.virtualauctionserver.functions.FileUtil;
import com.auction.virtualauctionserver.functions.Functions;
import com.auction.virtualauctionserver.model.AuctionParams;
import com.auction.virtualauctionserver.model.Login;
import com.auction.virtualauctionserver.model.NamesList;
import com.auction.virtualauctionserver.model.PlayerInfo;
import com.auction.virtualauctionserver.model.PlayerInfoList;
import com.auction.virtualauctionserver.model.PlayerStatus;
import com.auction.virtualauctionserver.model.Register;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.model.UpdateDetails;
import com.auction.virtualauctionserver.model.Username;
import com.auction.virtualauctionserver.service.AuctionInterface;

public class AuctionDAO implements AuctionInterface {

	public Connection getConnection(boolean autoCommitValue) throws SQLException, ClassNotFoundException {

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/virtualauction", "root", "123456");
		conn.setAutoCommit(autoCommitValue);

		return conn;
	}

	public void close(Connection con) throws SQLException {

		con.close();
	}

	public void createRoomTable(Connection con, String roomId) throws SQLException {

		String query = "create table IF NOT EXISTS " + roomId
				+ "_room (Username Varchar(100) NOT NULL PRIMARY KEY, Team Varchar(100), Host Varchar(100), ReJoinRoom Varchar(100), LastOnlineDateTime TIMESTAMP);";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}
	}

	public void createUnsoldListTable(Connection con, String roomId, String round) throws SQLException {

		String listName = "";
		if (round.equals(Constants.I_SHORT_LIST)) {
			listName = "round2_unsoldlistshort";
		} else {
			listName = round + "_unsoldlist";
		}

		String query = "create table IF NOT EXISTS " + roomId + "_" + listName
				+ " (SrNo Int NOT NULL PRIMARY KEY AUTO_INCREMENT, PlayerId Int NOT NULL, PlayerName Varchar(200) NOT NULL, BasePrice_in_lakh Int NOT NULL, BasePrice_in_crore Decimal(10,2));";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void createAuctionListTable(Connection con, String roomId, String round) throws SQLException {

		String query = "create table IF NOT EXISTS " + roomId + "_" + round
				+ "_auctionlist (SrNo Int NOT NULL PRIMARY KEY AUTO_INCREMENT, PlayerId Int NOT NULL, PlayerName Varchar(200) NOT NULL, TotalPrice_in_lakh Int NOT NULL, TotalPrice_in_crore Decimal(10,2), PlayerStatus Varchar(100), Team Varchar(100), SetName Varchar(100), Time Int);";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void createPlayerCountTable(Connection con, String roomId) throws SQLException {

		String query = "create table IF NOT EXISTS " + roomId
				+ "_playercount (Team Varchar(100) NOT NULL PRIMARY KEY, Batsman Int NOT NULL, WicketKeepers Int NOT NULL, AllRounders Int NOT NULL, FastBowlers Int NOT NULL, SpinBowlers Int NOT NULL, Foreigners Int NOT NULL, Total Int NOT NULL, Budget_in_lakh Int NOT NULL, Budget_in_crore Decimal(10, 2) NOT NULL);";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void createTeamTable(Connection con, String roomId, String team) throws SQLException {

		String query = "create table IF NOT EXISTS " + roomId + "_" + team
				+ " (SrNo Int NOT NULL PRIMARY KEY AUTO_INCREMENT, PlayerId Int NOT NULL, PlayerName Varchar(200) NOT NULL, TotalPrice_in_lakh Int NOT NULL, TotalPrice_in_crore Decimal(10,2));";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void deleteRoomTable(Connection con, String roomId) throws SQLException {

		String query = "Drop Table IF EXISTS " + roomId + "_room";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void deleteUnsoldListTable(Connection con, String roomId, String round) throws SQLException {

		String listName = "";
		if (round.equals(Constants.I_SHORT_LIST)) {
			listName = "round2_unsoldlistshort";
		} else {
			listName = round + "_unsoldlist";
		}

		String query = "Drop Table IF EXISTS " + roomId + "_" + listName;
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void deleteAuctionListTable(Connection con, String roomId, String round) throws SQLException {

		String query = "Drop Table IF EXISTS " + roomId + "_" + round + "_auctionlist";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void deletePlayerCountTable(Connection con, String roomId) throws SQLException {

		String query = "Drop Table IF EXISTS " + roomId + "_playercount";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void deleteTeamTable(Connection con, String roomId, String team) throws SQLException {

		String query = "Drop Table IF EXISTS " + roomId + "_" + team;
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void insertIntoPlayerCount(Connection con, String roomId, String team, int budget) throws SQLException {

		String query_room = (AuctionInterface.Q_INSERT_INTO_PLAYER_COUNT).replaceAll("\\{Table\\}",
				roomId + "_playerCount");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setString(1, team);
			stmt_room.setInt(2, budget);
			double budgetDouble = budget;
			budgetDouble = budgetDouble / 100.00;
			stmt_room.setDouble(3, budgetDouble);

			stmt_room.execute();

		}
	}

	public void updateAuctionParams(Connection con, String roomId, AuctionParams auctionParams) throws SQLException {

		String query_room = (AuctionInterface.Q_UPDATE_AUCTION_PARAMS + roomId + "'");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setInt(1, auctionParams.getMaxForeigners());
			stmt_room.setInt(2, auctionParams.getMinTotal());
			stmt_room.setInt(3, auctionParams.getMaxTotal());
			stmt_room.setInt(4, auctionParams.getMaxBudget());

			stmt_room.execute();

		}

	}

	public String getUserNameByPhoneNumber(Connection con, String emailId) throws SQLException {

		String username = "";
		String query_no = (AuctionInterface.Q_CHECK_EMAIL_ID + emailId + "'");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				username = rs.getString(Constants.I_USERNAME);

			}

		}

		return username;
	}

	public String checkUserAndPhoneNumber(Connection con, Register register) throws SQLException {

		String check = "";
		String query_no = (AuctionInterface.Q_CHECK_USER + register.getUsername() + "'");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (register.getUsername() == null) {
					check = Constants.USERNAME_NOT_EXIST_MESSAGE;
				} else if (register.getUsername() != null
						&& !register.getEmailId().equals(rs.getString(Constants.I_EMAIL_ID))) {
					check = Constants.EMAIL_ID_INCORRECT_MESSAGE;
				} else {
					check = Constants.OK_MESSAGE;
				}

			}
		}

		return check;
	}

	public void updateOtp(Connection con, String username, int otp) throws SQLException {

		String query = (AuctionInterface.Q_UPDATE_OTP);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, otp);
			stmt.setString(2, username);

			stmt.execute();

		}
	}

	public boolean otpExistsCheck(Connection con, String username, int otp, String otpOperationType)
			throws SQLException {

		boolean exist = false;
		String query_no = "";
		if (otpOperationType.equals(Constants.I_REGISTER)) {
			username = username + "&&";
		}
		query_no = (AuctionInterface.Q_CHECK_USER + username + "'");

		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (rs.getInt(Constants.I_OTP) == otp) {
					exist = true;
				}

			}
		}

		return exist;

	}

	public void updatePassword(Connection con, String username, String password) throws SQLException {

		String query = (AuctionInterface.Q_UPDATE_PASSWORD);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, password);
			stmt.setString(2, username);

			stmt.execute();

		}
	}

	public boolean userExistsCheck(Connection con, String data, String input) throws SQLException {

		boolean exist = false;
		String query_no = "";
		if (input.equals(Constants.I_USERNAME)) {
			query_no = (AuctionInterface.Q_CHECK_USER + data + "'");
		} else {
			query_no = (AuctionInterface.Q_CHECK_EMAIL_ID + data + "'");
		}
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (rs.getString(input) != null) {
					exist = true;
				}

			}
		}

		return exist;

	}

	public Register getUserDetails(Connection con, String username) throws SQLException {

		Register register = new Register();
		String query_no = (AuctionInterface.Q_CHECK_USER + username + "'");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				register.setUsername(rs.getString(Constants.I_USERNAME));
				register.setPassword(rs.getString(Constants.I_PASSWORD));
				if (rs.getString(Constants.I_EMAIL_ID) == null) {
					register.setEmailId("");
				} else {
					register.setEmailId(rs.getString(Constants.I_EMAIL_ID));
				}
			}

		}
		return register;
	}

	public String login(Connection con, Login login) throws SQLException {

		String check = Constants.USERNAME_NOT_EXIST_MESSAGE;
		String query_no = (AuctionInterface.Q_CHECK_USER + login.getUsername() + "'");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (login.getUsername() == null) {
					check = Constants.USERNAME_NOT_EXIST_MESSAGE;
				} else if (login.getUsername() != null
						&& !login.getPassword().equals(rs.getString(Constants.I_PASSWORD))) {
					check = Constants.PASSWORD_INCORRECT_MESSAGE;
				} else {
					check = Constants.OK_MESSAGE;
				}

			}
		}

		return check;
	}

	public void register(Connection con, Register register) throws SQLException {

		String query = (AuctionInterface.Q_INSERT_USER);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, register.getUsername());
			stmt.setString(2, register.getPassword());
			stmt.setString(3, register.getEmailId());

			stmt.execute();

		}
	}

	public void insertTempOtp(Connection con, String username, int otp) throws SQLException {

		String query = (AuctionInterface.Q_INSERT_TEMP);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, username + "&&");
			stmt.setString(2, "111");
			stmt.setInt(3, otp);

			stmt.execute();

		}
	}

	public void deleteTempOtp(Connection con, String username) throws SQLException {

		String query = (AuctionInterface.Q_DELETE_TEMP) + username + "&&'";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}
	}

	public void updateUserDetails(Connection con, UpdateDetails updateDetails) throws SQLException {

		String query = (AuctionInterface.Q_UPDATE_USER);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, updateDetails.getUsername());
			stmt.setString(2, updateDetails.getPassword());
			stmt.setString(3, updateDetails.getEmailId());
			stmt.setString(4, updateDetails.getOldUsername());
			stmt.setString(5, updateDetails.getOldPassword());
			stmt.setString(6, updateDetails.getOldEmailId());

			stmt.execute();

		}
	}

	public void deleteUser(Connection con, String username) throws SQLException {

		String query = (AuctionInterface.Q_DELETE_USER);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, username);

			stmt.execute();

		}
	}

	public String getRandomRoomId(Connection con) throws SQLException {

		String roomOutput = "";
		String query_no = (AuctionInterface.Q_SELECT_RANDOM_ROOM_ID);
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (rs.getString("RoomId") == null) {
					roomOutput = "";
				} else {
					roomOutput = rs.getString("RoomId");
				}
			}
		}

		return roomOutput;
	}

	public String getRoomResult(Connection con, String roomIdCreate, String input) throws SQLException {

		String roomOutput = "";
		String query_no = (AuctionInterface.Q_GET_USERS + roomIdCreate + "'");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (input.equals(Constants.I_USERS) || input.equals(Constants.I_MAX_FOREIGNERS)
						|| input.equals(Constants.I_MIN_TOTAL) || input.equals(Constants.I_MAX_TOTAL)
						|| input.equals(Constants.I_MAX_BUDGET) || input.equals("MinBreakTime")) {
					roomOutput = String.valueOf(rs.getInt(input));
				} else {
					if (rs.getString(input) == null) {
						roomOutput = "";
					} else {
						roomOutput = rs.getString(input);
					}
				}
			}
		}

		return roomOutput;
	}

	public void insertIntoRoomList(Connection con, String roomId, String visibility, String roomPassword)
			throws SQLException {

		String query = (AuctionInterface.Q_INSERT_INTO_ROOMS_LIST);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, roomId);
			stmt.setString(2, Constants.I_START_STATUS);
			stmt.setInt(3, 1);
			stmt.setString(4, visibility);
			stmt.setString(5, roomPassword);

			stmt.execute();

		}

	}

	public void insertPlayerIntoRoom(Connection con, String roomId, String username, String host) throws SQLException {

		String query_room = (AuctionInterface.Q_INSERT_INTO_ROOM_HOST).replaceAll("\\{Table\\}", roomId + "_room");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setString(1, username);
			stmt_room.setString(2, host);
			stmt_room.setString(3, Constants.I_YES);

			stmt_room.execute();

		}
	}

	public void updateUser(Connection con, String roomId, int users) throws SQLException {

		String query_upd = (AuctionInterface.Q_UPDATE_USERS + roomId + "'");
		try (PreparedStatement stmt_upd = con.prepareStatement(query_upd)) {
			stmt_upd.setInt(1, users);

			stmt_upd.execute();

		}
	}

	public String getUserbyUsername(Connection con, String roomId, String username, String input) throws SQLException {

		String uName = "";
		String query_room = (AuctionInterface.Q_GET_BY_USERNAME + username + "'").replaceAll("\\{Table\\}",
				roomId + "_room");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (rs.getString(input) != null) {
					uName = rs.getString(input);
				}
			}
		}

		return uName;
	}

	public String getUserbyHost(Connection con, String roomId, String input) throws SQLException {

		String uName = "";
		String query_room = (AuctionInterface.Q_SELECT_HOST).replaceAll("\\{Table\\}", roomId + "_room");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				uName = rs.getString(input);

			}
		}

		return uName;
	}

	public boolean searchTeam(Connection con, String roomId, String username, String team) throws SQLException {

		boolean teamExist = false;
		String query_room = (AuctionInterface.Q_ROOM_STATUS + AuctionInterface.Q_WHERE_TEAM + team + "'"
				+ AuctionInterface.Q_WHERE_TEAM_AND + username + "'").replaceAll("\\{Table\\}", roomId + "_room");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				String teamName = rs.getString(Constants.I_TEAM);
				if (!rs.equals("") || !rs.equals(null)) {
					teamExist = true;
				}

			}
		}

		return teamExist;
	}

	public String getRandomUser(Connection con, String roomId) throws SQLException {

		String randomUser = "";
		String query_random = (AuctionInterface.Q_SELECT_RANDOM_USER).replaceAll("\\{Table\\}", roomId + "_room");
		try (PreparedStatement sltmt_host = con.prepareStatement(query_random)) {
			ResultSet rs = sltmt_host.executeQuery();

			while (rs.next()) {

				randomUser = rs.getString(Constants.I_USERNAME);

			}

		}
		return randomUser;
	}

	public void updateUserForRejoin(Connection con, String roomId, String username, String input) throws SQLException {

		String query_updroom = (AuctionInterface.Q_UPDATE_INTO_ROOM + username + "'").replaceAll("\\{Table\\}",
				roomId + "_room");
		try (PreparedStatement stmt_updroom = con.prepareStatement(query_updroom)) {
			stmt_updroom.setString(1, input);

			stmt_updroom.execute();

		}
	}

	public void updateRoomStatus(Connection con, String roomId, String input) throws SQLException {

		String query_room = (AuctionInterface.Q_UPDATE_ROOM_STATUS + roomId + "'");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setString(1, input);

			stmt_room.execute();

		}

	}

	public void updateRound(Connection con, String roomId, String input) throws SQLException {

		String query_room = (AuctionInterface.Q_UPDATE_ROUND + roomId + "'");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setString(1, input);

			stmt_room.execute();

		}

	}

	public void updateHost(Connection con, String roomId, String username, String input) throws SQLException {

		String query_updhost = (AuctionInterface.Q_UPDATE_HOST + username + "'").replaceAll("\\{Table\\}",
				roomId + "_room");
		try (PreparedStatement stmt_updhost = con.prepareStatement(query_updhost)) {
			stmt_updhost.setString(1, input);

			stmt_updhost.execute();

		}
	}

	public void deleteUserFromRoom(Connection con, String roomId, String username) throws SQLException {

		String query_room = (AuctionInterface.Q_DELETE_FROM_ROOM + username + "'").replaceAll("\\{Table\\}",
				roomId + "_room");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {

			stmt_room.execute();

		}
	}

	public void deleteRoomIdFromRoom(Connection con, String roomId) throws SQLException {

		String query_room = (AuctionInterface.Q_DELETE_ROOMID_FROM_ROOM + roomId + "'");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {

			stmt_room.execute();

		}
	}

	public String getUnSelectedTeams(Connection con, String roomId) throws SQLException {

		StringBuilder builder = new StringBuilder();
		String query = (AuctionInterface.Q_GET_RANDOM_TEAM).replaceAll("\\{RoomId\\}", roomId);

		try (PreparedStatement sltmt = con.prepareStatement(query)) {
			ResultSet rs = sltmt.executeQuery();

			while (rs.next()) {

				builder.append(rs.getString(Constants.I_TEAM) + ",");

			}
		}
		if (builder.length() != 0) {
			builder.deleteCharAt(builder.length() - 1).toString();
		}

		return builder.toString();
	}

	public String getUserOnlineDetails(Connection con, String roomId) throws SQLException {

		StringBuilder builder = new StringBuilder();
		String query = (AuctionInterface.Q_SELECT_USER_ONLINE_DETAILS).replaceAll("\\{RoomId\\}", roomId);

		try (PreparedStatement sltmt = con.prepareStatement(query)) {
			ResultSet rs = sltmt.executeQuery();

			while (rs.next()) {

				builder.append(rs.getString(Constants.I_USERNAME) + ","
						+ rs.getTimestamp("LastOnlineDateTime").toLocalDateTime() + "|");

			}
		}

		return builder.deleteCharAt(builder.length() - 1).toString();

	}

	public void setUserDateTime(Connection con, String roomId, String username) throws SQLException {

		String query = (AuctionInterface.Q_UPDATE_DATE_TIME).replaceAll("\\{RoomId\\}", roomId);
		Timestamp ts = Timestamp.valueOf(LocalDateTime.now());

		try (PreparedStatement sltmt = con.prepareStatement(query)) {
			sltmt.setTimestamp(1, ts);
			sltmt.setString(2, username);

			sltmt.execute();

		}

	}

	public void updateAuctionBreakTime(Connection con, String roomId, int time) throws SQLException {

		String query_room = (AuctionInterface.Q_UPDATE_AUCTION_BREAK_TIME + roomId + "'");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setInt(1, time);

			stmt_room.execute();

		}

	}

	public RoomStatusResponse getRoomStatus(Connection con, String roomId, String roomStatus) throws SQLException {
		RoomStatusResponse roomStatusResponse = new RoomStatusResponse();
		StringBuilder usernameStringBuilder = new StringBuilder();
		StringBuilder teamStringBuilder = new StringBuilder();

		String query_room = "";
		if (roomStatus.equals(Constants.I_START_STATUS)) {

			query_room = (AuctionInterface.Q_ROOM_STATUS).replaceAll("\\{Table\\}", roomId + "_room");
		} else if (roomStatus.equals("Paused")) {
			query_room = (AuctionInterface.Q_ROOM_STATUS + AuctionInterface.Q_ROOM_STATUS_AND).replaceAll("\\{Table\\}",
					roomId + "_room");
		}
		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				usernameStringBuilder.append(rs.getString(Constants.I_USERNAME));
				usernameStringBuilder.append(",");
				if (rs.getString(Constants.I_TEAM) == null) {
					teamStringBuilder.append("NA");
				} else {
					teamStringBuilder.append(rs.getString(Constants.I_TEAM));
				}
				teamStringBuilder.append(",");

			}
		}

		if (!usernameStringBuilder.toString().equals("")) {
			roomStatusResponse.setUsernameslist(
					usernameStringBuilder.deleteCharAt(usernameStringBuilder.length() - 1).toString());
		}
		if (!teamStringBuilder.toString().equals("")) {
			roomStatusResponse.setTeamslist(teamStringBuilder.deleteCharAt(teamStringBuilder.length() - 1).toString());
		}

		return roomStatusResponse;

	}

	public void updateTeam(Connection con, String roomId, String username, String team) throws SQLException {

		String query_room = (AuctionInterface.Q_UPDATE_TEAM_NAME + username + "'").replaceAll("\\{Table\\}",
				roomId + "_room");
		try (PreparedStatement stmt_room = con.prepareStatement(query_room)) {
			stmt_room.setString(1, team);

			stmt_room.execute();

		}
	}

	public String sets(Connection con) throws SQLException {

		StringBuilder stringBuilder = new StringBuilder();
		try (PreparedStatement sltmt_no = con.prepareStatement(AuctionInterface.Q_SELECT_SETS)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				stringBuilder.append(rs.getString("SetName"));
				stringBuilder.append(",");

			}

		}

		return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
	}

	public String getPlayerPrice(Connection con, String roomId, String round, String playerName) throws SQLException {

		String totalPriceTeam = "";

		try (PreparedStatement stmt = con.prepareStatement(AuctionInterface.Q_PLAYER_INFO + roomId + "_" + round
				+ "_auctionlist" + AuctionInterface.Q_WHERE + playerName + "'")) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				if (rs.getString(Constants.I_TEAM) == null) {
					totalPriceTeam = String.valueOf(rs.getInt("TotalPrice_in_lakh")) + ",NA";
				} else {
					totalPriceTeam = String.valueOf(rs.getInt("TotalPrice_in_lakh")) + ","
							+ rs.getString(Constants.I_TEAM);
				}
			}
		}

		return totalPriceTeam;
	}

	public void updatePrice(Connection con, String roomId, String round, String playerName, String team, int totalPrice)
			throws SQLException {

		String query = (AuctionInterface.Q_UPDATE_PRICE + playerName + "'").replaceAll("\\{Table\\}",
				roomId + "_" + round + "_auctionlist");
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, team);
			stmt.setInt(2, totalPrice);
			double doublePrice = totalPrice;
			doublePrice = doublePrice / 100.00;
			stmt.setDouble(3, doublePrice);
			stmt.setInt(4, 5);

			stmt.execute();

		}
	}

	public String getCurrentSet(Connection con, String roomId, String round) throws SQLException {

		String set = "";

		String query = (AuctionInterface.Q_STATUS).replaceAll("\\{Table\\}", roomId + "_" + round + "_auctionlist");
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				set = rs.getString("SetName");

			}
		}

		return set;

	}

	public String getTime(Connection con, String roomId, String round) throws SQLException {

		String playerNameTime = "";

		String query = (AuctionInterface.Q_STATUS).replaceAll("\\{Table\\}", roomId + "_" + round + "_auctionlist");
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				playerNameTime = rs.getInt("Time") + "," + rs.getString(Constants.I_PLAYER_NAME);

			}
		}

		return playerNameTime;
	}

	public int updateTime(Connection con, String roomId, String round, String playerName, int time)
			throws SQLException {

		String query_upd = (AuctionInterface.Q_UPDATE_TIME + playerName + "'").replaceAll("\\{Table\\}",
				roomId + "_" + round + "_auctionlist");
		try (PreparedStatement stmt = con.prepareStatement(query_upd)) {

			stmt.setInt(1, time);

			stmt.execute();

		}

		return time;
	}

	public PlayerStatus playerStatus(Connection con, String roomId, String round) throws SQLException {
		PlayerStatus playerStatus = new PlayerStatus();

		String query = (AuctionInterface.Q_STATUS).replaceAll("\\{Table\\}", roomId + "_" + round + "_auctionlist");
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				playerStatus.setRoomId(roomId);
				playerStatus.setPlayerId(rs.getInt("PlayerId"));
				playerStatus.setPlayerName(rs.getString(Constants.I_PLAYER_NAME));
				playerStatus.setTotalPriceinLakhs(rs.getInt("TotalPrice_in_lakh"));
				playerStatus.setTotalPriceInCrores(rs.getDouble("TotalPrice_in_crore"));
				playerStatus.setSet(rs.getString("SetName"));
				if (rs.getString(Constants.I_TEAM) == null) {
					playerStatus.setTeam("");
				} else {
					playerStatus.setTeam(rs.getString(Constants.I_TEAM));
				}
				playerStatus.setTime(rs.getInt("Time"));

			}
		}
		return playerStatus;

	}

	public PlayerStatus playerStatusFromInformation(Connection con, String roomId, PlayerStatus playerStatus)
			throws SQLException {
		// PlayerStatus playerStatus = new PlayerStatus();

		String query = (AuctionInterface.Q_PLAYER_INFO + "player_information" + AuctionInterface.Q_WHERE
				+ playerStatus.getPlayerName() + "'");
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				// playerStatus.setRoomId(roomId);
				// playerStatus.setPlayerId(rs.getInt("PlayerId"));
				// playerStatus.setPlayerName(rs.getString(Constants.I_PLAYER_NAME));
				playerStatus.setPlayerCountry(rs.getString("PlayerCountry"));
				playerStatus.setPlayerRole(rs.getString("PlayerRole"));
				playerStatus.setBattingStyle(rs.getString("BattingStyle"));
				playerStatus.setBowlingStyle(rs.getString("BowlingStyle"));
				playerStatus.setBattingPosition(rs.getString("BattingPosition"));
				if (rs.getString(Constants.I_PLAYER_IMAGE1) == null) {
					playerStatus.setPlayerImage1Uri("");
				} else {
					playerStatus.setPlayerImage1Uri(rs.getString(Constants.I_PLAYER_IMAGE1));
				}
				if (rs.getString(Constants.I_PLAYER_IMAGE2) == null) {
					playerStatus.setPlayerImage2Uri("");
				} else {
					playerStatus.setPlayerImage2Uri(rs.getString(Constants.I_PLAYER_IMAGE2));
				}

			}
		}
		return playerStatus;

	}

	public PlayerStatus teamStatus(Connection con, String roomId, String team, PlayerStatus playerStatus)
			throws SQLException {

		try (PreparedStatement stemt = con.prepareStatement(AuctionInterface.Q_PLAYER_INFO + roomId + "_playercount"
				+ AuctionInterface.Q_SELECT_PLAYER_NUMBER + team + "'")) {
			ResultSet rs = stemt.executeQuery();

			while (rs.next()) {

				playerStatus.setBatsman(rs.getInt("Batsman"));
				playerStatus.setWicketKeepers(rs.getInt("WicketKeepers"));
				playerStatus.setAllRounders(rs.getInt("AllRounders"));
				playerStatus.setFastBowlers(rs.getInt("FastBowlers"));
				playerStatus.setSpinBowlers(rs.getInt("SpinBowlers"));
				playerStatus.setForeigners(rs.getInt("Foreigners"));
				playerStatus.setTotal(rs.getInt("Total"));
				playerStatus.setBudgetInLakh(rs.getInt("Budget_in_lakh"));
				playerStatus.setBudgetInCrores(rs.getDouble("Budget_in_crore"));

			}
		}

		return playerStatus;
	}

	public String checkPlayerName(Connection con, String roomId, String round, String set) throws SQLException {

		String playerName = "";

		String query = "";
		if (round.contains("round")) {

			if (set.contains("unsold")) {

				query = AuctionInterface.Q_PLAYER_INFO + set + AuctionInterface.Q_JOIN + roomId + "_" + round
						+ AuctionInterface.Q_NON_RANDOM;
				;

			} else {

				query = (AuctionInterface.Q_PLAYER_INFO + "ipl_auction_set_list" + AuctionInterface.Q_JOIN + roomId
						+ "_" + round + AuctionInterface.Q_RANDOM).replaceAll("\\{Set\\}", "'" + set + "'");

			}

		} else {

			query = AuctionInterface.Q_PLAYER_INFO + roomId + "_" + set + AuctionInterface.Q_WHERE_PLAYER_NAME + round
					+ "'";

		}

		try (PreparedStatement sltmt = con.prepareStatement(query)) {
			ResultSet rs = sltmt.executeQuery();

			while (rs.next()) {

				if (rs.getString(Constants.I_PLAYER_NAME) == null) {
					playerName = "";
				} else {
					playerName = rs.getString(Constants.I_PLAYER_NAME);
				}
			}
		}
		return playerName;
	}

	public PlayerInfo getPlayerInfoToAdd(Connection con, String name, String set) throws SQLException {
		PlayerInfo playerInfo = new PlayerInfo();
		try (PreparedStatement stmt = con
				.prepareStatement(AuctionInterface.Q_PLAYER_INFO + set + AuctionInterface.Q_WHERE + name + "'")) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				playerInfo.setPlayerId(rs.getInt("PlayerId"));
				playerInfo.setPlayerName(rs.getString(Constants.I_PLAYER_NAME));
				playerInfo.setTotalPriceinLakhs(rs.getInt("BasePrice_in_lakh"));
				playerInfo.setTotalPriceInCrores(rs.getDouble("BasePrice_in_crore"));

			}
		}
		return playerInfo;
	}

	public PlayerInfo getPlayerInfoToAddFromInformation(Connection con, String name, PlayerInfo playerInfo)
			throws SQLException {
		// PlayerInfo playerInfo = new PlayerInfo();
		try (PreparedStatement stmt = con.prepareStatement(
				AuctionInterface.Q_PLAYER_INFO + "player_information" + AuctionInterface.Q_WHERE + name + "'")) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				// playerInfo.setPlayerId(rs.getInt("PlayerId"));
				// playerInfo.setPlayerName(rs.getString(Constants.I_PLAYER_NAME));
				playerInfo.setPlayerCountry(rs.getString("PlayerCountry"));
				playerInfo.setPlayerRole(rs.getString("PlayerRole"));
				playerInfo.setBattingStyle(rs.getString("BattingStyle"));
				playerInfo.setBowlingStyle(rs.getString("BowlingStyle"));
				playerInfo.setBattingPosition(rs.getString("BattingPosition"));
				playerInfo.setPlayerImage1Uri(rs.getString(Constants.I_PLAYER_IMAGE1));
				playerInfo.setPlayerImage2Uri(rs.getString(Constants.I_PLAYER_IMAGE2));

			}
		}
		return playerInfo;
	}

	public void addPlayerInfo(Connection con, String roomId, String round, String set, int initialTime,
			PlayerInfo playerInfo) throws SQLException {

		String query = "";
		if (!set.equals(Constants.I_SHORT_LIST)) {
			query = (AuctionInterface.Q_INSERT_PLAYER_IN_LIST).replaceAll("\\{Table\\}",
					roomId + "_" + round + "_auctionlist");
		} else {
			query = (AuctionInterface.Q_INSERT_UNSOLD_PLAYER).replaceAll("\\{Table\\}",
					roomId + "_" + round + "_unsoldlistshort");
		}

		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, playerInfo.getPlayerId());
			stmt.setString(2, playerInfo.getPlayerName());
			stmt.setInt(3, playerInfo.getTotalPriceinLakhs());
			stmt.setDouble(4, playerInfo.getTotalPriceInCrores());

			if (!set.equals(Constants.I_SHORT_LIST)) {
				stmt.setString(5, set);
				stmt.setInt(6, initialTime);
			}

			stmt.execute();

		}

	}

	public void addPlayerInfoAfterBid(Connection con, String roomId, String round, String set,
			PlayerStatus playerStatus, String team) throws SQLException {

		String query = "";

		if (team.equals("")) {

			query = (AuctionInterface.Q_INSERT_UNSOLD_PLAYER).replaceAll("\\{Table\\}",
					roomId + "_" + round + "_unsoldlist");

		} else {

			query = (AuctionInterface.Q_INSERT_PLAYER_INTO_TEAM).replaceAll("\\{Table\\}", roomId + "_" + team);
		}

		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, playerStatus.getPlayerId());
			stmt.setString(2, playerStatus.getPlayerName());
			stmt.setInt(3, playerStatus.getTotalPriceinLakhs());
			stmt.setDouble(4, playerStatus.getTotalPriceInCrores());

			stmt.execute();

		}

	}

	public void addTeamInfo(Connection con, String roomId, String round, String team, int number, String action)
			throws SQLException {

		String query = "";

		query = (AuctionInterface.Q_UPDATE_PLAYER_NUMBER + team + "'").replaceAll("\\{Table\\}",
				roomId + "_playercount");
		query = query.replaceAll("\\{Count\\}", action);

		try (PreparedStatement stmt = con.prepareStatement(query)) {

			if (action.equals("Budget_in_crore")) {
				double doublePrice = number;
				doublePrice = doublePrice / 100.00;
				stmt.setDouble(1, doublePrice);
			} else {
				stmt.setInt(1, number);
			}

			stmt.execute();

		}
	}

	public void updatePlayerInfo(Connection con, String roomId, String round, String playerName, String team,
			int totalPrice) throws SQLException {

		String query_upd = (AuctionInterface.Q_UPDATE_PLAYER_LIST + playerName + "'").replaceAll("\\{Table\\}",
				roomId + "_" + round + "_auctionlist");
		try (PreparedStatement stmt = con.prepareStatement(query_upd)) {

			if (team == null || (team.equals(""))) {
				stmt.setString(1, "Unsold");
			} else {
				stmt.setString(1, "Sold to " + team);
			}
			// stmt.setInt(2, totalPrice);

			// double doublePrice = totalPrice;
			// doublePrice = doublePrice / 100.00;
			// stmt.setDouble(3, doublePrice);

			stmt.execute();

		}
	}

	public void updateSkip(Connection con, String roomId, String input) throws SQLException {

		String query_upd = (AuctionInterface.Q_UPDATE_SKIP + roomId + "'");
		try (PreparedStatement stmt_upd = con.prepareStatement(query_upd)) {
			stmt_upd.setString(1, input);

			stmt_upd.execute();

		}
	}

	public NamesList getUsernamesList(Connection con, String roomId, String roomStatus, String username)
			throws SQLException {
		NamesList list = new NamesList();
		ArrayList<String> arrayList = new ArrayList<>();

		String query_room = "";
		if (roomStatus.equals(Constants.I_START_STATUS)) {

			query_room = (AuctionInterface.Q_ROOM_STATUS).replaceAll("\\{Table\\}", roomId + "_room");
		} else {
			query_room = (AuctionInterface.Q_ROOM_STATUS + AuctionInterface.Q_ROOM_STATUS_AND
					+ AuctionInterface.Q_ROOM_HOST).replaceAll("\\{Table\\}", roomId + "_room");
		}
		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				arrayList.add(rs.getString(Constants.I_USERNAME));

			}
		}

		list.setNamesList(arrayList);
		return list;

	}

	public NamesList getUnsoldPlayersList(Connection con, String roomId, String type, int budget) throws SQLException {
		NamesList list = new NamesList();
		ArrayList<String> arrayList = new ArrayList<>();
		ArrayList<String> arrayListPrice = new ArrayList<>();

		String query_room = "";
		if (type.equals("ForiegnersExceed")) {

			query_room = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST_FOR_OMIT_FORIEGN).replaceAll("\\{RoomId\\}", roomId);

		} else if (type.equals("Normal")) {

			query_room = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST_FOR_ADD).replaceAll("\\{RoomId\\}", roomId);

		} else {

			query_room = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST).replaceAll("\\{Table\\}",
					roomId + "_round2_unsoldlist");

		}

		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			if (!type.equals("")) {
				sltmt_no.setInt(1, budget);
			}
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				arrayList.add(rs.getString(Constants.I_PLAYER_NAME));
				arrayListPrice.add(String.valueOf(rs.getInt(Constants.I_BASE_PRICE_IN_LAKHS)) + " lakhs("
						+ String.format("%.2f", rs.getDouble(Constants.I_BASE_PRICE_IN_CRORES)) + " crores)");

			}
		}

		list.setNamesList(arrayList);
		list.setPriceList(arrayListPrice);
		return list;

	}

	public NamesList getCurrentSetPlayersList(Connection con, String set) throws SQLException { // **
		NamesList list = new NamesList();
		ArrayList<String> arrayList = new ArrayList<>();

		String query_room = (AuctionInterface.Q_PLAYER_INFO_WHERE + set + "'");

		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				arrayList.add(rs.getString(Constants.I_PLAYER_NAME));
			}
		}

		list.setNamesList(arrayList);
		return list;

	}

	public PlayerInfoList playerInfoList(Connection con, String roomId, String team) throws SQLException {
		PlayerInfoList playerInfoList = new PlayerInfoList();
		ArrayList<String> playerNameList = new ArrayList<>();
		ArrayList<String> priceInLakhsList = new ArrayList<>();
		ArrayList<String> priceInCroresList = new ArrayList<>();

		String query = "";
		if (team.equals("all")) {

			query = AuctionInterface.Q_PLAYER_INFO + "ipl_auction_set_list";

		} else if (team.equals("unsold1")) {

			query = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST).replaceAll("\\{Table\\}", roomId + "_round1_unsoldlist");

		} else if (team.equals("unsold2")) {

			query = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST).replaceAll("\\{Table\\}", roomId + "_round2_unsoldlist");

		} else if (team.equals("unsold3")) {

			query = (AuctionInterface.Q_GET_TOTAL_UNSOLD_PLAYERS).replaceAll("\\{RoomId\\}", roomId);
		} else if (team.equals("unsold2short")) {

			query = (AuctionInterface.Q_GET_TOTAL_UNSOLD_PLAYERS).replaceAll("\\{RoomId\\}",
					roomId + "_round2_unsoldlistshort");
		} else if (team.contains("set")) {

			query = (AuctionInterface.Q_PLAYER_INFO + team);
		} else {

			query = (AuctionInterface.Q_ROOM_STATUS).replaceAll("\\{Table\\}", roomId + "_" + team);

		}
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				playerNameList.add(rs.getString(Constants.I_PLAYER_NAME));
				if (team.contains("unsold") || team.contains("set") || team.equals("all")) {
					priceInLakhsList.add(String.valueOf(rs.getInt("BasePrice_in_lakh")));
					priceInCroresList.add(String.format("%.2f", rs.getDouble("BasePrice_in_crore")));
				} else {
					priceInLakhsList.add(String.valueOf(rs.getInt("TotalPrice_in_lakh")));
					priceInCroresList.add(String.format("%.2f", rs.getDouble("TotalPrice_in_crore")));
				}

			}
		}

		playerInfoList.setPlayerNameList(playerNameList);
		playerInfoList.setPriceinLakhsList(priceInLakhsList);
		playerInfoList.setPriceinCroresList(priceInCroresList);
		return playerInfoList;

	}

	public PlayerInfoList playerInfoListFromInformation(Connection con, String roomId, String playerListForDB,
			String playerListForOrder, PlayerInfoList playerInfoList) throws SQLException {
		// PlayerInfoList playerInfoList = new PlayerInfoList();
		// ArrayList<String> playerNameList = new ArrayList<>();
		ArrayList<String> playerCountryList = new ArrayList<>();
		ArrayList<String> playerRoleList = new ArrayList<>();
		ArrayList<String> battingStyleList = new ArrayList<>();
		ArrayList<String> bowlingStyleList = new ArrayList<>();
		ArrayList<String> battingPositionList = new ArrayList<>();

		String query = (AuctionInterface.Q_PLAYER_INFO + "player_information" + AuctionInterface.Q_WHERE_IN
				+ playerListForOrder + AuctionInterface.Q_ORDER_BY_LIST + playerListForDB + "') asc");

		try (PreparedStatement stmt = con.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				// playerNameList.add(rs.getString(Constants.I_PLAYER_NAME));
				playerCountryList.add(rs.getString("PlayerCountry"));
				playerRoleList.add(rs.getString("PlayerRole"));
				battingStyleList.add(rs.getString("BattingStyle"));
				if (rs.getString("BowlingStyle") == null) {
					bowlingStyleList.add("");
				} else {
					bowlingStyleList.add(rs.getString("BowlingStyle"));
				}
				battingPositionList.add(rs.getString("BattingPosition"));

			}
		}

		playerInfoList.setPlayerCountryList(playerCountryList);
		playerInfoList.setPlayerRoleList(playerRoleList);
		playerInfoList.setBattingStyleList(battingStyleList);
		playerInfoList.setBowlingStyleList(bowlingStyleList);
		playerInfoList.setBattingPositionList(battingPositionList);
		return playerInfoList;

	}

}

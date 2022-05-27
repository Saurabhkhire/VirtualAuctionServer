package com.auction.virtualauctionserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.auction.virtualauctionserver.functions.Constants;
import com.auction.virtualauctionserver.functions.Functions;
import com.auction.virtualauctionserver.model.AuctionInfo;
import com.auction.virtualauctionserver.model.AuctionParams;
import com.auction.virtualauctionserver.model.Bid;
import com.auction.virtualauctionserver.model.Login;
import com.auction.virtualauctionserver.model.NamesList;
import com.auction.virtualauctionserver.model.PlayerInfo;
import com.auction.virtualauctionserver.model.PlayerInfoList;
import com.auction.virtualauctionserver.model.PlayerName;
import com.auction.virtualauctionserver.model.PlayerStatus;
import com.auction.virtualauctionserver.model.Register;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.model.Team;
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
				+ "_room (Username Varchar(100) NOT NULL PRIMARY KEY, Team Varchar(100), Host Varchar(100), ReJoinRoom Varchar(100));";
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
				+ " (SrNo Int NOT NULL PRIMARY KEY AUTO_INCREMENT, PlayerId Int NOT NULL, PlayerName Varchar(200) NOT NULL, PlayerCountry Varchar(100) NOT NULL, PlayerRole Varchar(100) NOT NULL, BattingStyle Varchar(100) NOT NULL, BowlingStyle Varchar(100) , BattingPosition Varchar(100) NOT NULL, BasePrice_in_lakh Int NOT NULL, BasePrice_in_crore Decimal(10,2));";
		try (PreparedStatement stmt = con.prepareStatement(query)) {

			stmt.execute();

		}

	}

	public void createAuctionListTable(Connection con, String roomId, String round) throws SQLException {

		String query = "create table IF NOT EXISTS " + roomId + "_" + round
				+ "_auctionlist (SrNo Int NOT NULL PRIMARY KEY AUTO_INCREMENT, PlayerId Int NOT NULL, PlayerName Varchar(200) NOT NULL, PlayerCountry Varchar(100) NOT NULL, PlayerRole Varchar(100) NOT NULL, BattingStyle Varchar(100) NOT NULL, BowlingStyle Varchar(100) , BattingPosition Varchar(100) NOT NULL, TotalPrice_in_lakh Int NOT NULL, TotalPrice_in_crore Decimal(10,2), PlayerStatus Varchar(100), Team Varchar(100), SetName Varchar(100), Time Int);";
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
				+ " (SrNo Int NOT NULL PRIMARY KEY AUTO_INCREMENT, PlayerId Int NOT NULL, PlayerName Varchar(200) NOT NULL, PlayerCountry Varchar(100) NOT NULL, PlayerRole Varchar(100) NOT NULL, BattingStyle Varchar(100) NOT NULL, BowlingStyle Varchar(100) , BattingPosition Varchar(100) NOT NULL, TotalPrice_in_lakh Int NOT NULL, TotalPrice_in_crore Decimal(10,2));";
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
			stmt_room.setFloat(3, budget);

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

	public boolean login(Connection con, Login login) throws SQLException {

		boolean userExist = false;
		String query_no = (AuctionInterface.Q_CHECK_USER + login.getUsername() + "'");
		try (PreparedStatement sltmt_no = con.prepareStatement(query_no)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				if (login.getUsername().equals(rs.getString(Constants.I_USERNAME))
						&& login.getPassword().equals(rs.getString("Password"))) {
					userExist = true;
				}

			}
		}

		return userExist;
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
						|| input.equals(Constants.I_MAX_BUDGET)) {
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

	public void insertIntoRoomList(Connection con, String roomId) throws SQLException {

		String query = (AuctionInterface.Q_INSERT_INTO_ROOMS_LIST);
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setString(1, roomId);
			stmt.setString(2, Constants.I_START_STATUS);
			stmt.setInt(3, 1);

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

	public String getRandomTeam(Connection con, String roomId) throws SQLException {

		String randomTeam = "";
		String query = (AuctionInterface.Q_GET_RANDOM_TEAM).replaceAll("\\{RoomId\\}", roomId);

		try (PreparedStatement sltmt = con.prepareStatement(query)) {
			ResultSet rs = sltmt.executeQuery();

			while (rs.next()) {

				randomTeam = rs.getString(Constants.I_TEAM);

			}
		}
		return randomTeam;
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
			stmt.setInt(3, 5);

			stmt.execute();

		}
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
				playerStatus.setPlayerCountry(rs.getString("PlayerCountry"));
				playerStatus.setPlayerRole(rs.getString("PlayerRole"));
				playerStatus.setBattingStyle(rs.getString("BattingStyle"));
				playerStatus.setBowlingStyle(rs.getString("BowlingStyle"));
				playerStatus.setBattingPosition(rs.getString("BattingPosition"));
				playerStatus.setTotalPriceinLakhs(rs.getInt("TotalPrice_in_lakh"));
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
				playerStatus.setBudget(rs.getInt("Budget_in_lakh"));

			}
		}

		return playerStatus;
	}

	public String checkPlayerName(Connection con, String roomId, String round, String set) throws SQLException {

		String playerName = "";
		
		String query = "";
		if (round.contains("round")) {
			
			query = AuctionInterface.Q_PLAYER_INFO + set + AuctionInterface.Q_JOIN + roomId + "_" + round
					+ AuctionInterface.Q_RANDOM;

		} else {

			query = AuctionInterface.Q_PLAYER_INFO + roomId + "_" + set + AuctionInterface.Q_WHERE_PLAYER_NAME + round + "'";
			
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
				playerInfo.setPlayerCountry(rs.getString("PlayerCountry"));
				playerInfo.setPlayerRole(rs.getString("PlayerRole"));
				playerInfo.setBattingStyle(rs.getString("BattingStyle"));
				playerInfo.setBowlingStyle(rs.getString("BowlingStyle"));
				playerInfo.setBattingPosition(rs.getString("BattingPosition"));
				playerInfo.setTotalPriceinLakhs(rs.getInt("BasePrice_in_lakh"));

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
			stmt.setString(3, playerInfo.getPlayerCountry());
			stmt.setString(4, playerInfo.getPlayerRole());
			stmt.setString(5, playerInfo.getBattingStyle());
			stmt.setString(6, playerInfo.getBowlingStyle());
			stmt.setString(7, playerInfo.getBattingPosition());
			stmt.setInt(8, playerInfo.getTotalPriceinLakhs());
			stmt.setDouble(9, 0);

			if (!set.equals(Constants.I_SHORT_LIST)) {
				stmt.setString(10, set);
				stmt.setInt(11, initialTime);
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
			stmt.setString(3, playerStatus.getPlayerCountry());
			stmt.setString(4, playerStatus.getPlayerRole());
			stmt.setString(5, playerStatus.getBattingStyle());
			stmt.setString(6, playerStatus.getBowlingStyle());
			stmt.setString(7, playerStatus.getBattingPosition());
			stmt.setInt(8, playerStatus.getTotalPriceinLakhs());
			stmt.setDouble(9, 0);

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
			stmt.setInt(1, number);

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
			stmt.setInt(2, totalPrice);

			double doublePrice = totalPrice / 10;
			stmt.setDouble(3, doublePrice);

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

	public NamesList getUnsoldPlayersList(Connection con, String roomId) throws SQLException {
		NamesList list = new NamesList();
		ArrayList<String> arrayList = new ArrayList<>();

		String query_room = (AuctionInterface.Q_ROOM_STATUS).replaceAll("\\{Table\\}", roomId + "_round2_unsoldlist");

		try (PreparedStatement sltmt_no = con.prepareStatement(query_room)) {
			ResultSet rs = sltmt_no.executeQuery();

			while (rs.next()) {

				arrayList.add(rs.getString(Constants.I_PLAYER_NAME));

			}
		}

		list.setNamesList(arrayList);
		return list;

	}

	public NamesList getCurrentSetPlayersList(Connection con, String set) throws SQLException {
		NamesList list = new NamesList();
		ArrayList<String> arrayList = new ArrayList<>();

		String query_room = (AuctionInterface.Q_PLAYER_INFO + set);

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
		ArrayList<String> playerCountryList = new ArrayList<>();
		ArrayList<String> playerRoleList = new ArrayList<>();
		ArrayList<String> battingStyleList = new ArrayList<>();
		ArrayList<String> bowlingStyleList = new ArrayList<>();
		ArrayList<String> battingPositionList = new ArrayList<>();
		ArrayList<String> priceInLakhsList = new ArrayList<>();
		ArrayList<String> priceInCroresList = new ArrayList<>();
		playerNameList.add(Constants.I_PLAYER_NAME_HEADER);
		playerCountryList.add(Constants.I_PLAYER_COUNTRY_HEADER);
		playerRoleList.add(Constants.I_PLAYER_ROLE_HEADER);
		battingStyleList.add(Constants.I_BATTING_STYLE_HEADER);
		bowlingStyleList.add(Constants.I_BOWLING_STYLE_HEADER);
		battingPositionList.add(Constants.I_BATTING_POSITION_HEADER);
		priceInLakhsList.add(Constants.I_PRICE_IN_LAKHS_HEADER);
		priceInCroresList.add(Constants.I_PRICE_IN_CRORES_HEADER);

		String query = "";
		if (team.equals("unsold1")) {

			query = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST).replaceAll("\\{Table\\}", roomId + "round1_unsoldlist");

		}
		if (team.equals("unsold2")) {

			query = (AuctionInterface.Q_UNSOLD_PLAYERS_LIST).replaceAll("\\{Table\\}", roomId + "round2_unsoldlist");

		}
		if (team.equals("unsold3")) {

			query = (AuctionInterface.Q_GET_TOTAL_UNSOLD_PLAYERS).replaceAll("\\{RoomId\\}", roomId);
		} else {

			query = (AuctionInterface.Q_ROOM_STATUS).replaceAll("\\{Table\\}", roomId + "_" + team);

		}
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				playerNameList.add(rs.getString(Constants.I_PLAYER_NAME));
				playerCountryList.add(rs.getString("PlayerCountry"));
				playerRoleList.add(rs.getString("PlayerRole"));
				battingStyleList.add(rs.getString("BattingStyle"));
				if (rs.getString("BowlingStyle") == null) {
					bowlingStyleList.add("");
				} else {
					bowlingStyleList.add(rs.getString("BowlingStyle"));
				}
				battingPositionList.add(rs.getString("BattingPosition"));
				if (team.contains("unsold")) {
					priceInLakhsList.add(String.valueOf(rs.getInt("BasePrice_in_lakh")));
					priceInCroresList.add(String.valueOf(rs.getInt("BasePrice_in_crore")));
				} else {
					priceInLakhsList.add(String.valueOf(rs.getInt("TotalPrice_in_lakh")));
					priceInCroresList.add(String.valueOf(rs.getInt("TotalPrice_in_crore")));
				}

			}
		}

		playerInfoList.setPlayerNameList(playerNameList);
		playerInfoList.setPlayerCountryList(playerCountryList);
		playerInfoList.setPlayerRoleList(playerRoleList);
		playerInfoList.setBattingStyleList(battingStyleList);
		playerInfoList.setBowlingStyleList(bowlingStyleList);
		playerInfoList.setBattingPositionList(battingPositionList);
		playerInfoList.setPriceinLakhsList(priceInLakhsList);
		playerInfoList.setPriceinCroresList(priceInCroresList);
		return playerInfoList;

	}

}

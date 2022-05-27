package com.auction.virtualauctionserver.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.auction.virtualauctionserver.model.AuctionInfo;
import com.auction.virtualauctionserver.model.AuctionParams;
import com.auction.virtualauctionserver.model.Bid;
import com.auction.virtualauctionserver.model.Login;
import com.auction.virtualauctionserver.model.NamesList;
import com.auction.virtualauctionserver.model.PlayerInfo;
import com.auction.virtualauctionserver.model.PlayerInfoList;
import com.auction.virtualauctionserver.model.PlayerStatus;
import com.auction.virtualauctionserver.model.Register;
import com.auction.virtualauctionserver.model.RoomStatusResponse;

public interface AuctionInterface {

	String Q_SELECT_RANDOM_ROOM_ID = "select * from roomslist where RoomStatus='Start' ORDER BY RAND() LIMIT 1;";
	String Q_INSERT_USER = "insert into users (Username, Password, EmailId) values (?,?,?);";
	String Q_CHECK_USER = "select * from users where Username = '";
	String Q_INSERT_INTO_ROOMS_LIST = "insert into roomslist (RoomId, RoomStatus, Users) values (?,?,?);";
	String Q_UPDATE_AUCTION_PARAMS = "update roomslist set MaxForeigners=?, MinTotal=?, MaxTotal=?, MaxBudget=? where RoomId = '";
	String Q_GET_USERS = "select * from roomslist where RoomId = '";
	String Q_UPDATE_USERS = "update roomslist set Users=? where RoomId = '";
	String Q_INSERT_INTO_ROOM_HOST = "insert into {Table} (Username, Host, ReJoinRoom) values (?,?,?)";
	String Q_INSERT_INTO_ROOM = "insert into {Table} (Username) values (?)";
	String Q_SELECT_HOST = "select * from {Table} where Host='Yes'";
	String Q_GET_BY_USERNAME = "select * from {Table} where Username='";
	String Q_ROOM_STATUS = "select * from {Table}";
	String Q_WHERE_TEAM = " where Team = '";
	String Q_WHERE_TEAM_AND = " and Username != '";
	String Q_ROOM_STATUS_AND = " where ReJoinRoom='Yes'";
	String Q_ROOM_HOST = " and Host!='Yes'";
	String Q_UPDATE_TEAM_NAME = "update {Table} set Team=? where Username='";
	String Q_UPDATE_ROOM_STATUS = "update roomslist set RoomStatus=? where RoomId= '";
	String Q_UPDATE_ROUND = "update roomslist set CurrentRound=? where RoomId= '";
	String Q_UPDATE_INTO_ROOM = "update {Table} set ReJoinRoom=? where Username='";
	String Q_DELETE_FROM_ROOM = "delete from {Table} where Username='";
	String Q_DELETE_ROOMID_FROM_ROOM = "delete from roomslist where RoomId='";
	String Q_UPDATE_HOST = "update {Table} set Host=? where Username='";
	String Q_SELECT_RANDOM_USER = "select * from {Table} where ReJoinRoom='Yes' ORDER BY RAND() LIMIT 1;";
	String Q_UPDATE_SKIP = "update roomslist set Skip=? where RoomId = '";
	String Q_GET_RANDOM_TEAM = "select * from {RoomId}_playercount a left outer join {RoomId}_room b on a.team=b.team where b.team is null ORDER BY RAND() LIMIT 1;";
	String Q_INSERT_INTO_PLAYER_COUNT = "insert into {Table} (Team, Batsman, WicketKeepers,AllRounders ,FastBowlers ,SpinBowlers ,Foreigners ,Total, Budget_in_lakh, Budget_in_crore) values (?,0,0,0,0,0,0,0,?,?)";

	String Q_WHERE_PLAYER_NAME = " where PlayerName = '";
	String Q_PLAYER_INFO = "select * from ";
	String Q_JOIN = " a left outer join ";
	String Q_RANDOM = "_auctionlist b on a.playername=b.playername where b.playername is null ORDER BY RAND() LIMIT 1;";
	String Q_WHERE = " where PlayerName = '";

	String Q_STATUS = "select * from {Table} order by SrNo desc LIMIT 1;";
	String Q_SELECT_SETS = "select * from sets";

	String Q_SELECT_PLAYER_NUMBER = " where Team= '";
	String Q_UPDATE_PLAYER_NUMBER = "update {Table} set {Count} = ? where Team= '";
	String Q_UPDATE_PRICE = "update {Table} set Team=?,TotalPrice_in_lakh = ?,Time=? where PlayerName= '";
	String Q_INSERT_PLAYER_IN_LIST = "insert into {Table} (PlayerId, PlayerName, PlayerCountry, PlayerRole, BattingStyle, BowlingStyle, BattingPosition, TotalPrice_in_lakh, TotalPrice_in_crore, SetName, Time) values (?,?,?,?,?,?,?,?,?,?,?)";
	String Q_INSERT_PLAYER_INTO_TEAM = "insert into {Table} (PlayerId, PlayerName, PlayerCountry, PlayerRole, BattingStyle, BowlingStyle, BattingPosition, TotalPrice_in_lakh, TotalPrice_in_crore) values (?,?,?,?,?,?,?,?,?)";
	String Q_UPDATE_PLAYER_LIST = "update {Table} set PlayerStatus=?, TotalPrice_in_lakh = ?, TotalPrice_in_crore = ? where PlayerName = '";
	String Q_UPDATE_TIME = "update {Table} set Time=? where PlayerName = '";
	String Q_INSERT_UNSOLD_PLAYER = "insert into {Table} (PlayerId, PlayerName, PlayerCountry, PlayerRole, BattingStyle, BowlingStyle, BattingPosition, BasePrice_in_lakh, BasePrice_in_crore) values (?,?,?,?,?,?,?,?,?)";
	String Q_UNSOLD_PLAYERS_LIST = "select * from {Table}";
	String Q_GET_TOTAL_UNSOLD_PLAYERS = "select * from {RoomId}_round2_unsoldlist a left outer join {RoomId}_round3_auctionlist b on a.playername=b.playername where b.playername is null";

	Connection getConnection(boolean autoCommitValue) throws SQLException, ClassNotFoundException;

	void close(Connection con) throws SQLException;

	public void createRoomTable(Connection con, String roomId) throws SQLException;

	public void createUnsoldListTable(Connection con, String roomId, String round) throws SQLException;

	public void createAuctionListTable(Connection con, String roomId, String round) throws SQLException;

	public void createPlayerCountTable(Connection con, String roomId) throws SQLException;

	public void createTeamTable(Connection con, String roomId, String team) throws SQLException;

	public void deleteRoomTable(Connection con, String roomId) throws SQLException;

	public void deleteUnsoldListTable(Connection con, String roomId, String round) throws SQLException;

	public void deleteAuctionListTable(Connection con, String roomId, String round) throws SQLException;

	public void deletePlayerCountTable(Connection con, String roomId) throws SQLException;

	public void deleteTeamTable(Connection con, String roomId, String team) throws SQLException;

	public void insertIntoPlayerCount(Connection con, String roomId, String team, int budget) throws SQLException;

	public void updateAuctionParams(Connection con, String roomId, AuctionParams auctionParams) throws SQLException;

	boolean login(Connection con, Login login) throws SQLException;

	void register(Connection con, Register register) throws SQLException;

	String getRoomResult(Connection con, String roomIdCreate, String input) throws SQLException;
	
	String getRandomRoomId(Connection con) throws SQLException;

	void insertIntoRoomList(Connection con, String roomId) throws SQLException;

	void insertPlayerIntoRoom(Connection con, String roomId, String username, String host) throws SQLException;

	void updateUser(Connection con, String roomId, int users) throws SQLException;

	String getUserbyUsername(Connection con, String roomId, String username, String input) throws SQLException;

	String getUserbyHost(Connection con, String roomId, String input) throws SQLException;

	boolean searchTeam(Connection con, String roomId, String username, String team) throws SQLException;

	void updateUserForRejoin(Connection con, String roomId, String username, String input) throws SQLException;

	void updateHost(Connection con, String roomId, String username, String input) throws SQLException;

	String getRandomUser(Connection con, String roomId) throws SQLException;

	void updateRoomStatus(Connection con, String roomId, String input) throws SQLException;

	void updateRound(Connection con, String roomId, String input) throws SQLException;

	void deleteUserFromRoom(Connection con, String roomId, String username) throws SQLException;

	void deleteRoomIdFromRoom(Connection con, String roomId) throws SQLException;

	RoomStatusResponse getRoomStatus(Connection con, String roomId, String roomStatus) throws SQLException;
	
	String getRandomTeam(Connection con, String roomId) throws SQLException;

	void updateTeam(Connection con, String roomId, String username, String team) throws SQLException;

	String sets(Connection con) throws SQLException;

	String getPlayerPrice(Connection con, String roomId, String round, String playerName) throws SQLException;

	void updatePrice(Connection con, String roomId, String round, String playerName, String team, int totalPrice)
			throws SQLException;

	String getTime(Connection con, String roomId, String round) throws SQLException;

	int updateTime(Connection con, String roomId, String round, String playerName, int time) throws SQLException;

	PlayerStatus playerStatus(Connection con, String roomId, String round) throws SQLException;

	PlayerStatus teamStatus(Connection con, String roomId, String team, PlayerStatus playerStatus) throws SQLException;

	String checkPlayerName(Connection con, String roomId, String round, String set) throws SQLException;

	PlayerInfo getPlayerInfoToAdd(Connection con, String name, String set) throws SQLException;

	void addPlayerInfo(Connection con, String roomId, String round, String set, int initialTime, PlayerInfo playerInfo)
			throws SQLException;

	void addPlayerInfoAfterBid(Connection con, String roomId, String round, String set, PlayerStatus playerStatus,
			String team) throws SQLException;

	void addTeamInfo(Connection con, String roomId, String round, String team, int number, String action)
			throws SQLException;

	void updatePlayerInfo(Connection con, String roomId, String round, String playerName, String team, int totalPrice)
			throws SQLException;

	void updateSkip(Connection con, String roomId, String input) throws SQLException;

	NamesList getUsernamesList(Connection con, String roomId, String roomStatus, String username) throws SQLException;

	NamesList getUnsoldPlayersList(Connection con, String roomId) throws SQLException;

	NamesList getCurrentSetPlayersList(Connection con, String set) throws SQLException;

	PlayerInfoList playerInfoList(Connection con, String roomId, String team) throws SQLException;

}

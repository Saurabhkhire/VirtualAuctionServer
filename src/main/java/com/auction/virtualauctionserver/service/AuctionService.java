package com.auction.virtualauctionserver.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.auction.virtualauctionserver.dao.AuctionDAO;
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

public class AuctionService implements AuctionInterface {

	private final AuctionInterface auctionInterface;

	public AuctionService() {
		auctionInterface = new AuctionDAO();
	}

	// @Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		return auctionInterface.getConnection();
	}

	// @Override
	public void close(Connection con) throws SQLException {
		auctionInterface.close(con);
	}

	public void createRoomTable(Connection con, String roomId) throws SQLException {
		auctionInterface.createRoomTable(con, roomId);
	}

	public void createUnsoldListTable(Connection con, String roomId, String round) throws SQLException {
		auctionInterface.createUnsoldListTable(con, roomId, round);
	}

	public void createAuctionListTable(Connection con, String roomId, String round) throws SQLException {
		auctionInterface.createAuctionListTable(con, roomId, round);
	}

	public void createPlayerCountTable(Connection con, String roomId) throws SQLException {
		auctionInterface.createPlayerCountTable(con, roomId);
	}

	public void createTeamTable(Connection con, String roomId, String team) throws SQLException {
		auctionInterface.createTeamTable(con, roomId, team);
	}

	public void deleteRoomTable(Connection con, String roomId) throws SQLException {
		auctionInterface.deleteRoomTable(con, roomId);
	}

	public void deleteUnsoldListTable(Connection con, String roomId, String round) throws SQLException {
		auctionInterface.deleteUnsoldListTable(con, roomId, round);
	}

	public void deleteAuctionListTable(Connection con, String roomId, String round) throws SQLException {
		auctionInterface.deleteAuctionListTable(con, roomId, round);
	}

	public void deletePlayerCountTable(Connection con, String roomId) throws SQLException {
		auctionInterface.deletePlayerCountTable(con, roomId);
	}

	public void deleteTeamTable(Connection con, String roomId, String team) throws SQLException {
		auctionInterface.deleteTeamTable(con, roomId, team);
	}

	public void insertIntoPlayerCount(Connection con, String roomId, String team, int budget) throws SQLException {
		auctionInterface.insertIntoPlayerCount(con, roomId, team, budget);
	}

	public void updateAuctionParams(Connection con, String roomId, AuctionParams auctionParams) throws SQLException {
		auctionInterface.updateAuctionParams(con, roomId, auctionParams);
	}

	public boolean login(Connection con, Login login) throws SQLException {
		return auctionInterface.login(con, login);
	}

	public void register(Connection con, Register register) throws SQLException {
		auctionInterface.register(con, register);
	}

	public String getUsers(Connection con, String roomIdCreate, String input) throws SQLException {
		return auctionInterface.getUsers(con, roomIdCreate, input);
	}

	public void insertIntoRoomList(Connection con, String roomId) throws SQLException {
		auctionInterface.insertIntoRoomList(con, roomId);
	}

	public void insertPlayerIntoRoom(Connection con, String roomId, String username, String host) throws SQLException {
		auctionInterface.insertPlayerIntoRoom(con, roomId, username, host);
	}

	public void updateUser(Connection con, String roomId, int users) throws SQLException {
		auctionInterface.updateUser(con, roomId, users);
	}

	public String getUserbyUsername(Connection con, String roomId, String username, String input) throws SQLException {
		return auctionInterface.getUserbyUsername(con, roomId, username, input);
	}

	public boolean searchTeam(Connection con, String roomId, String username, String team) throws SQLException {
		return auctionInterface.searchTeam(con, roomId, username, team);
	}

	public String getUserbyHost(Connection con, String roomId, String input) throws SQLException {
		return auctionInterface.getUserbyHost(con, roomId, input);
	}

	public void updateUserForRejoin(Connection con, String roomId, String username, String input) throws SQLException {
		auctionInterface.updateUserForRejoin(con, roomId, username, input);
	}

	public void updateHost(Connection con, String roomId, String username, String input) throws SQLException {
		auctionInterface.updateHost(con, roomId, username, input);
	}

	public String getRandomUser(Connection con, String roomId) throws SQLException {
		return auctionInterface.getRandomUser(con, roomId);
	}

	public void updateRoomStatus(Connection con, String roomId, String input) throws SQLException {
		auctionInterface.updateRoomStatus(con, roomId, input);
	}

	public void updateRound(Connection con, String roomId, String input) throws SQLException {
		auctionInterface.updateRound(con, roomId, input);
	}

	public void deleteUserFromRoom(Connection con, String roomId, String username) throws SQLException {
		auctionInterface.deleteUserFromRoom(con, roomId, username);
	}

	public void deleteRoomIdFromRoom(Connection con, String roomId) throws SQLException {
		auctionInterface.deleteRoomIdFromRoom(con, roomId);
	}

	public RoomStatusResponse getRoomStatus(Connection con, String roomId, String roomStatus) throws SQLException {
		return auctionInterface.getRoomStatus(con, roomId, roomStatus);
	}
	
	public String getRandomTeam(Connection con, String roomId) throws SQLException {
		return auctionInterface.getRandomTeam(con, roomId);
	}

	public void updateTeam(Connection con, String roomId, String username, String team) throws SQLException {
		auctionInterface.updateTeam(con, roomId, username, team);
	}

	public String sets(Connection con) throws SQLException {
		return auctionInterface.sets(con);
	}

	public String getPlayerPrice(Connection con, String roomId, String round, String playerName) throws SQLException {
		return auctionInterface.getPlayerPrice(con, roomId, round, playerName);
	}

	public void updatePrice(Connection con, String roomId, String round, String playerName, String team, int totalPrice)
			throws SQLException {
		auctionInterface.updatePrice(con, roomId, round, playerName, team, totalPrice);
	}

	public String getTime(Connection con, String roomId, String round) throws SQLException {
		return auctionInterface.getTime(con, roomId, round);
	}

	public int updateTime(Connection con, String roomId, String round, String playerName, int time)
			throws SQLException {
		return auctionInterface.updateTime(con, roomId, round, playerName, time);
	}

	public PlayerStatus playerStatus(Connection con, String roomId, String round) throws SQLException {
		return auctionInterface.playerStatus(con, roomId, round);
	}

	public PlayerStatus teamStatus(Connection con, String roomId, String team, PlayerStatus playerStatus)
			throws SQLException {
		return auctionInterface.teamStatus(con, roomId, team, playerStatus);
	}

	public String checkPlayerName(Connection con, String roomId, String round, String set) throws SQLException {
		return auctionInterface.checkPlayerName(con, roomId, round, set);
	}

	public PlayerInfo getPlayerInfoToAdd(Connection con, String name, String set) throws SQLException {
		return auctionInterface.getPlayerInfoToAdd(con, name, set);
	}

	public void addPlayerInfo(Connection con, String roomId, String round, String set, int initialTime,
			PlayerInfo playerInfo) throws SQLException {
		auctionInterface.addPlayerInfo(con, roomId, round, set, initialTime, playerInfo);
	}

	public void addPlayerInfoAfterBid(Connection con, String roomId, String round, String set,
			PlayerStatus playerStatus, String team) throws SQLException {
		auctionInterface.addPlayerInfoAfterBid(con, roomId, round, set, playerStatus, team);
	}

	public void addTeamInfo(Connection con, String roomId, String round, String team, int number, String action)
			throws SQLException {
		auctionInterface.addTeamInfo(con, roomId, round, team, number, action);
	}

	public void updatePlayerInfo(Connection con, String roomId, String round, String playerName, String team,
			int totalPrice) throws SQLException {
		auctionInterface.updatePlayerInfo(con, roomId, round, playerName, team, totalPrice);
	}

	public void updateSkip(Connection con, String roomId, String input) throws SQLException {
		auctionInterface.updateSkip(con, roomId, input);
	}

	public NamesList getUsernamesList(Connection con, String roomId, String roomStatus, String username)
			throws SQLException {
		return auctionInterface.getUsernamesList(con, roomId, roomStatus, username);
	}

	public NamesList getUnsoldPlayersList(Connection con, String roomId) throws SQLException {
		return auctionInterface.getUnsoldPlayersList(con, roomId);
	}

	public NamesList getCurrentSetPlayersList(Connection con, String set) throws SQLException {
		return auctionInterface.getUnsoldPlayersList(con, set);
	}

	public PlayerInfoList playerInfoList(Connection con, String roomId, String team) throws SQLException {
		return auctionInterface.playerInfoList(con, roomId, team);
	}

}

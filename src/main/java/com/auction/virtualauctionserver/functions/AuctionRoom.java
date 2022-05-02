package com.auction.virtualauctionserver.functions;

import java.sql.SQLException;

import com.auction.virtualauctionserver.model.AuctionParams;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.model.RoomStatus;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.model.Team;
import com.auction.virtualauctionserver.model.Username;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

import java.sql.Connection;

public class AuctionRoom {

	public static ResponseMessage createRoom(Username username) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomInfo roomInfo = new RoomInfo();
		try {

			con = auctionService.getConnection();

			try {

				String roomIdCreate = "";
				boolean exist = true;

				while (exist) {

					roomIdCreate = "2";

					String roomIdCreated = auctionService.getUsers(con, roomIdCreate, "RoomId");

					if (!roomIdCreated.equals(null) || !roomIdCreated.equals("")) {
						exist = false;
					}

				}

				auctionService.createRoomTable(con, roomIdCreate);
				auctionService.createAuctionListTable(con, roomIdCreate, "round1");
				auctionService.createUnsoldListTable(con, roomIdCreate, "round1");
				auctionService.createAuctionListTable(con, roomIdCreate, "round2");
				auctionService.createUnsoldListTable(con, roomIdCreate, "round2");
				auctionService.createAuctionListTable(con, roomIdCreate, "round3");
				auctionService.createUnsoldListTable(con, roomIdCreate, "round3");
				auctionService.createUnsoldListTable(con, roomIdCreate, "ShortList");
				auctionService.createPlayerCountTable(con, roomIdCreate);
				auctionService.insertIntoRoomList(con, roomIdCreate);
				auctionService.insertPlayerIntoRoom(con, roomIdCreate, username.getUsername(), "Yes");

				AuctionParams auctionParams = new AuctionParams();
				auctionParams.setMaxForeigners(10);
				auctionParams.setMinTotal(20);
				auctionParams.setMaxTotal(25);
				auctionParams.setMaxBudget(10000);
				auctionService.updateAuctionParams(con, roomIdCreate, auctionParams);

				roomInfo.setMessage("Start");
				roomInfo.setRoomId(roomIdCreate);

			} catch (Exception ex) {
				roomInfo.setMessage("Error");
			} finally {

				con.close();
			}

		} catch (Exception ex) {
			roomInfo.setMessage("Error");
		}

		return roomInfo;

	}

	public static RoomStatus joinRoom(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomStatus roomStatusResponse = new RoomStatus();

		try {

			con = auctionService.getConnection();

			try {
				String roomStatus = "";
				roomStatus = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");

				int users = 0;
				users = Integer.parseInt(auctionService.getUsers(con, roomInfo.getRoomId(), "Users"));

				if (roomStatus.equals("Start")) {

					auctionService.insertPlayerIntoRoom(con, roomInfo.getRoomId(), roomInfo.getUsername(), "No");

					auctionService.updateUser(con, roomInfo.getRoomId(), users + 1);

				} else if (roomStatus.equals("Paused") || roomStatus.equals("Ongoing")
						|| roomStatus.equals("TempPaused")) {

					String uName = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(),
							"Username");

					if (!uName.equals(null) || !!uName.equals("")) {

						auctionService.updateUserForRejoin(con, roomInfo.getRoomId(), uName, "Yes");

						auctionService.updateUser(con, roomInfo.getRoomId(), users + 1);

						if (users == 0) {
							auctionService.updateHost(con, roomInfo.getRoomId(), uName, "Yes");
						}

					} else {

					}

				}

				roomStatusResponse.setRoomStatus(roomStatus);

			} catch (Exception ex) {
				ex.printStackTrace();
				roomStatusResponse.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			roomStatusResponse.setMessage("Error");
		}

		return roomStatusResponse;
	}

	public static ResponseMessage leaveRoom(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {
				String roomStatus = "";
				roomStatus = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");

				int users = 0;
				users = Integer.parseInt(auctionService.getUsers(con, roomInfo.getRoomId(), "Users"));

				String host = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(),
						"Host");

				if (roomStatus.equals("Start")) {

					if (host.equals("Yes")) {

						auctionService.deleteRoomTable(con, roomInfo.getRoomId());
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), "round1");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "round1");
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), "round2");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "round2");
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), "round3");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "round3");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "ShortList");
						auctionService.deletePlayerCountTable(con, roomInfo.getRoomId());
						auctionService.deleteRoomIdFromRoom(con, roomInfo.getRoomId());

					} else {

						auctionService.deleteUserFromRoom(con, roomInfo.getRoomId(), roomInfo.getUsername());

						auctionService.updateUser(con, roomInfo.getRoomId(), users - 1);
					}

				} else {

					auctionService.updateUser(con, roomInfo.getRoomId(), users - 1);

					auctionService.updateUserForRejoin(con, roomInfo.getRoomId(), roomInfo.getUsername(), "No");

					if (host.equals("Yes")) {

						auctionService.updateHost(con, roomInfo.getRoomId(), roomInfo.getUsername(), "No");

						String randomUser = auctionService.getRandomUser(con, roomInfo.getRoomId());

						if (!randomUser.equals("") || randomUser != null) {
							auctionService.updateHost(con, roomInfo.getRoomId(), randomUser, "Yes");
						}
					}

					if (users - 1 == 0 && (roomStatus.equals("Ongoing") || roomStatus.equals("TempPaused"))) {
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), "Paused");
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				responseMessage.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;
	}

	public static ResponseMessage quitRoom(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {
				// String roomStatus = "";
				// roomStatus = auctionService.getUsers(con, roomInfo.getRoomId(),
				// "RoomStatus");

				int users = 0;
				users = Integer.parseInt(auctionService.getUsers(con, roomInfo.getRoomId(), "Users"));

				String host = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(),
						"Host");

				if (host.equals("Yes")) {

					if (users - 1 == 0) {

						auctionService.deleteRoomTable(con, roomInfo.getRoomId());
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), "round1");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "round1");
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), "round2");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "round2");
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), "round3");
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), "round3");
						auctionService.deletePlayerCountTable(con, roomInfo.getRoomId());
						auctionService.deleteRoomIdFromRoom(con, roomInfo.getRoomId());

					} else {

						auctionService.updateHost(con, roomInfo.getRoomId(), roomInfo.getUsername(), "No");

						String randomUser = auctionService.getRandomUser(con, roomInfo.getRoomId());

						if (!randomUser.equals("") || randomUser != null) {
							auctionService.updateHost(con, roomInfo.getRoomId(), randomUser, "Yes");
						}

						auctionService.deleteUserFromRoom(con, roomInfo.getRoomId(), roomInfo.getUsername());
					}
				} else {

					auctionService.deleteUserFromRoom(con, roomInfo.getRoomId(), roomInfo.getUsername());
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				responseMessage.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;
	}

	public static RoomStatusResponse roomStatus(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomStatusResponse roomStatusResponse = new RoomStatusResponse();

		try {

			con = auctionService.getConnection();

			String team = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(), "Team");

			try {
				String roomStatus = "";
				roomStatus = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");

				if (roomStatus.equals("Start") || roomStatus.equals("Paused")) {
					roomStatusResponse = auctionService.getRoomStatus(con, roomInfo.getRoomId(), roomStatus);
				} else {

					if (team.equals("")) {
						team = "";
						auctionService.updateTeam(con, roomInfo.getRoomId(), roomInfo.getUsername(), team);
						auctionService.createTeamTable(con, roomInfo.getRoomId(), team);
					}
				}

				roomStatusResponse.setHost(auctionService.getUserbyHost(con, roomInfo.getRoomId(), "Username"));

				roomStatusResponse.setTeam(team);
				String maxForeigners = auctionService.getUsers(con, roomInfo.getRoomId(), "MaxForeigners");
				roomStatusResponse.setMaxForeigners(Integer.parseInt(maxForeigners));
				String minTotal = auctionService.getUsers(con, roomInfo.getRoomId(), "MinTotal");
				roomStatusResponse.setMinTotal(Integer.parseInt(minTotal));
				String maxTotal = auctionService.getUsers(con, roomInfo.getRoomId(), "MaxTotal");
				roomStatusResponse.setMaxTotal(Integer.parseInt(maxTotal));
				String maxBudget = auctionService.getUsers(con, roomInfo.getRoomId(), "MaxBudget");
				roomStatusResponse.setMaxBudget(Integer.parseInt(maxBudget));
				roomStatusResponse.setRoomStatus(roomStatus);

				con.close();

			} catch (Exception ex) {
				ex.printStackTrace();
				roomStatusResponse.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			roomStatusResponse.setMessage("Error");
		}

		return roomStatusResponse;
	}

	public static ResponseMessage updateAuctionParams(AuctionParams auctionParams) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				boolean teamExist = auctionService.searchTeam(con, auctionParams.getRoomId(),
						auctionParams.getUsername(), auctionParams.getTeam());
				if (!teamExist) {
					auctionService.updateTeam(con, auctionParams.getRoomId(), auctionParams.getUsername(),
							auctionParams.getTeam());
					auctionService.createTeamTable(con, auctionParams.getRoomId(), auctionParams.getTeam());

					String host = auctionService.getUserbyUsername(con, auctionParams.getRoomId(),
							auctionParams.getUsername(), "Host");

					if (host.equals("Yes")) {
						auctionService.updateAuctionParams(con, auctionParams.getRoomId(), auctionParams);
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				responseMessage.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;
	}

	public static ResponseMessage startAuction(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				String roomStatus = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");
				int budget = Integer.parseInt(auctionService.getUsers(con, roomInfo.getRoomId(), "MaxBudget"));
				if (roomStatus.equals("Start")) {
					String[] splitData = "csk|rcb|mi|gt|pbks|lsg|srh|rr|dc|kkr".split("\\|");
					for (int i = 0; i < splitData.length; i++) {
						auctionService.insertIntoPlayerCount(con, roomInfo.getRoomId(), splitData[i], budget);
					}
					auctionService.updateRound(con, roomInfo.getRoomId(), "round1");
				}

				auctionService.updateRoomStatus(con, roomInfo.getRoomId(), "Ongoing");


				AuctionRunning auctionRunning = new AuctionRunning(roomInfo);
				new Thread(auctionRunning).start();

				try {
					Thread.sleep(100);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;
	}
}

package com.auction.virtualauctionserver.functions;

import com.auction.virtualauctionserver.model.AuctionParams;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.model.RoomStatus;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.model.Username;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

import java.sql.Connection;

public class AuctionRoom {

	public static RoomInfo createRoom(Username username) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomInfo roomInfo = new RoomInfo();
		try {

			con = auctionService.getConnection(false);

			try {

				String roomIdCreate = "";
				boolean exceptionist = true;

				while (exceptionist) {

					roomIdCreate = "2";

					String roomIdCreated = auctionService.getRoomResult(con, roomIdCreate, Constants.I_ROOM_ID);

					if (!roomIdCreated.equals(null) || !roomIdCreated.equals("")) {
						exceptionist = false;
					}

				}

				auctionService.createRoomTable(con, roomIdCreate);
				auctionService.createAuctionListTable(con, roomIdCreate, Constants.I_ROUND1);
				auctionService.createUnsoldListTable(con, roomIdCreate, Constants.I_ROUND1);
				auctionService.createAuctionListTable(con, roomIdCreate, Constants.I_ROUND2);
				auctionService.createUnsoldListTable(con, roomIdCreate, Constants.I_ROUND2);
				auctionService.createAuctionListTable(con, roomIdCreate, Constants.I_ROUND3);
				auctionService.createUnsoldListTable(con, roomIdCreate, Constants.I_ROUND3);
				auctionService.createUnsoldListTable(con, roomIdCreate, Constants.I_SHORT_LIST);
				auctionService.createPlayerCountTable(con, roomIdCreate);
				auctionService.insertIntoRoomList(con, roomIdCreate);
				auctionService.insertPlayerIntoRoom(con, roomIdCreate, username.getUsername(), Constants.I_YES);

				AuctionParams auctionParams = new AuctionParams();
				auctionParams.setMaxForeigners(10);
				auctionParams.setMinTotal(20);
				auctionParams.setMaxTotal(25);
				auctionParams.setMaxBudget(10000);
				auctionService.updateAuctionParams(con, roomIdCreate, auctionParams);

				roomInfo.setMessage(Constants.OK_MESSAGE);
				roomInfo.setRoomId(roomIdCreate);
				con.commit();

			} catch (Exception exception) {
				roomInfo.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {

				con.close();
			}

		} catch (Exception exception) {
			roomInfo.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return roomInfo;

	}

	public static RoomInfo joinRandomRoom(Username username) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomInfo roomInfo = new RoomInfo();

		try {

			con = auctionService.getConnection(false);

			try {

				
				
				String roomId = auctionService.getRandomRoomId(con);
						
				if(roomId.equals("")) {
					
					int users = 0;
					users = Integer.parseInt(auctionService.getRoomResult(con, roomId, Constants.I_USERS));

				if (users < 10) {

					auctionService.insertPlayerIntoRoom(con, roomId, roomInfo.getUsername(),
							Constants.I_NO);

					auctionService.updateUser(con, roomId, users + 1);

					roomInfo.setMessage(Constants.OK_MESSAGE);

				} else {
					roomInfo.setMessage(Constants.ROOM_FULL_MESSAGE);
				}
				} else {
					roomInfo.setMessage(Constants.ROOM_NOT_FOUND_AVAILABLE);
				}

				con.commit();
			} catch (Exception exception) {
				roomInfo.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			roomInfo.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return roomInfo;
	}

	public static RoomStatus joinRoom(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomStatus roomStatusResponse = new RoomStatus();

		try {

			con = auctionService.getConnection(false);

			try {

				String roomId = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_ID);

				if (!roomId.equals("")) {

					String roomStatus = "";
					roomStatus = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);

					int users = 0;
					users = Integer
							.parseInt(auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_USERS));

					if (users < 10) {

						if (roomStatus.equals(Constants.I_START_STATUS)) {

							auctionService.insertPlayerIntoRoom(con, roomInfo.getRoomId(), roomInfo.getUsername(),
									Constants.I_NO);

							auctionService.updateUser(con, roomInfo.getRoomId(), users + 1);

							roomStatusResponse.setMessage(Constants.OK_MESSAGE);

						} else {

							String uName = auctionService.getUserbyUsername(con, roomInfo.getRoomId(),
									roomInfo.getUsername(), Constants.I_USERNAME);

							if (!uName.equals(null) || !!uName.equals("")) {

								auctionService.updateUserForRejoin(con, roomInfo.getRoomId(), uName, Constants.I_YES);

								auctionService.updateUser(con, roomInfo.getRoomId(), users + 1);

								if (users == 0) {
									auctionService.updateHost(con, roomInfo.getRoomId(), uName, Constants.I_YES);
								}

								roomStatusResponse.setMessage(Constants.OK_MESSAGE);

							} else {
								roomStatusResponse.setMessage(Constants.CANT_JOIN_ROOM_MESSAGE);
							}

						}

						roomStatusResponse.setRoomStatus(roomStatus);

					} else {
						roomStatusResponse.setMessage(Constants.ROOM_FULL_MESSAGE);
					}

				} else {
					roomStatusResponse.setMessage(Constants.ROOM_NOT_EXIST_MESSAGE);
				}

				con.commit();

			} catch (Exception exception) {
				roomStatusResponse.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			roomStatusResponse.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return roomStatusResponse;
	}

	public static ResponseMessage leaveRoom(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {
				String roomStatus = "";
				roomStatus = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);

				int users = 0;
				users = Integer.parseInt(auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_USERS));

				String host = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(),
						Constants.I_HOST);

				if (roomStatus.equals(Constants.I_START_STATUS)) {

					if (host.equals(Constants.I_YES)) {

						auctionService.deleteRoomTable(con, roomInfo.getRoomId());
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), Constants.I_ROUND1);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_ROUND1);
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), Constants.I_ROUND2);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_ROUND2);
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_SHORT_LIST);
						auctionService.deletePlayerCountTable(con, roomInfo.getRoomId());
						auctionService.deleteRoomIdFromRoom(con, roomInfo.getRoomId());
						
						String[] splitData = Constants.I_TEAMS_LIST.split("\\|");
						for (int i = 0; i < splitData.length; i++) {
							auctionService.deleteTeamTable(con, roomInfo.getRoomId(), splitData[i]);
						}
						

					} else {

						auctionService.deleteUserFromRoom(con, roomInfo.getRoomId(), roomInfo.getUsername());

						auctionService.updateUser(con, roomInfo.getRoomId(), users - 1);
					}

				} else {

					auctionService.updateUser(con, roomInfo.getRoomId(), users - 1);

					auctionService.updateUserForRejoin(con, roomInfo.getRoomId(), roomInfo.getUsername(),
							Constants.I_NO);

					if (host.equals(Constants.I_YES)) {

						auctionService.updateHost(con, roomInfo.getRoomId(), roomInfo.getUsername(), Constants.I_NO);

						String randomUser = auctionService.getRandomUser(con, roomInfo.getRoomId());

						if (!randomUser.equals("") || randomUser != null) {
							auctionService.updateHost(con, roomInfo.getRoomId(), randomUser, Constants.I_YES);
						}
					}

					if (users - 1 == 0 && (roomStatus.equals(Constants.I_ONGOING_STATUS)
							|| roomStatus.equals(Constants.I_PAUSED_STATUS))) {
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_HALT_STATUS);
					}
				}

				responseMessage.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return responseMessage;
	}

	public static ResponseMessage quitRoom(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				int users = 0;
				users = Integer.parseInt(auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_USERS));

				String host = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(),
						Constants.I_HOST);

				if (host.equals(Constants.I_YES)) {

					if (users - 1 == 0) {

						auctionService.deleteRoomTable(con, roomInfo.getRoomId());
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), Constants.I_ROUND1);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_ROUND1);
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), Constants.I_ROUND2);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_ROUND2);
						auctionService.deleteAuctionListTable(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.deleteUnsoldListTable(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.deletePlayerCountTable(con, roomInfo.getRoomId());
						auctionService.deleteRoomIdFromRoom(con, roomInfo.getRoomId());
						String[] splitData = Constants.I_TEAMS_LIST.split("\\|");
						for (int i = 0; i < splitData.length; i++) {
							auctionService.deleteTeamTable(con, roomInfo.getRoomId(), splitData[i]);
						}

					} else {

						auctionService.updateHost(con, roomInfo.getRoomId(), roomInfo.getUsername(), Constants.I_NO);

						String randomUser = auctionService.getRandomUser(con, roomInfo.getRoomId());

						if (!randomUser.equals("") || randomUser != null) {
							auctionService.updateHost(con, roomInfo.getRoomId(), randomUser, Constants.I_YES);
						}

						auctionService.deleteUserFromRoom(con, roomInfo.getRoomId(), roomInfo.getUsername());
					}
				} else {

					auctionService.deleteUserFromRoom(con, roomInfo.getRoomId(), roomInfo.getUsername());
				}

				responseMessage.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return responseMessage;
	}

	public static RoomStatusResponse roomStatus(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomStatusResponse roomStatusResponse = new RoomStatusResponse();

		try {

			con = auctionService.getConnection(false);

			String team = auctionService.getUserbyUsername(con, roomInfo.getRoomId(), roomInfo.getUsername(),
					Constants.I_TEAM);

			try {
				String roomStatus = "";
				roomStatus = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);

				if (roomStatus.equals(Constants.I_START_STATUS) || roomStatus.equals(Constants.I_HALT_STATUS)) {
					roomStatusResponse = auctionService.getRoomStatus(con, roomInfo.getRoomId(), roomStatus);
				} else {

					if (team.equals("")) {
						team = auctionService.getRandomTeam(con, roomInfo.getRoomId());
						auctionService.updateTeam(con, roomInfo.getRoomId(), roomInfo.getUsername(), team);
						auctionService.createTeamTable(con, roomInfo.getRoomId(), team);
					}
				}

				roomStatusResponse
						.setHost(auctionService.getUserbyHost(con, roomInfo.getRoomId(), Constants.I_USERNAME));

				roomStatusResponse.setTeam(team);
				String maxForeigners = auctionService.getRoomResult(con, roomInfo.getRoomId(),
						Constants.I_MAX_FOREIGNERS);
				roomStatusResponse.setMaxForeigners(Integer.parseInt(maxForeigners));
				String minTotal = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_MIN_TOTAL);
				roomStatusResponse.setMinTotal(Integer.parseInt(minTotal));
				String maxTotal = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_MAX_TOTAL);
				roomStatusResponse.setMaxTotal(Integer.parseInt(maxTotal));
				String maxBudget = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_MAX_BUDGET);
				roomStatusResponse.setMaxBudget(Integer.parseInt(maxBudget));
				roomStatusResponse.setRoomStatus(roomStatus);

				roomStatusResponse.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				roomStatusResponse.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			roomStatusResponse.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return roomStatusResponse;
	}

	public static ResponseMessage updateAuctionParams(AuctionParams auctionParams) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				boolean teamExist = auctionService.searchTeam(con, auctionParams.getRoomId(),
						auctionParams.getUsername(), auctionParams.getTeam());
				if (!teamExist) {
					auctionService.updateTeam(con, auctionParams.getRoomId(), auctionParams.getUsername(),
							auctionParams.getTeam());
					auctionService.createTeamTable(con, auctionParams.getRoomId(), auctionParams.getTeam());

					String host = auctionService.getUserbyUsername(con, auctionParams.getRoomId(),
							auctionParams.getUsername(), Constants.I_HOST);

					if (host.equals(Constants.I_YES)) {
						auctionService.updateAuctionParams(con, auctionParams.getRoomId(), auctionParams);
					}
				} else {
					responseMessage.setMessage(auctionParams.getTeam() + Constants.TEAM_ALREADY_SELECTED_MESSAGE);
				}

				responseMessage.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(auctionParams.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(auctionParams.getRoomId(), exception);
		}

		return responseMessage;
	}

	public static ResponseMessage startAuction(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				String roomStatus = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);
				int budget = Integer
						.parseInt(auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_MAX_BUDGET));
				if (roomStatus.equals(Constants.I_START_STATUS)) {
					String[] splitData = Constants.I_TEAMS_LIST.split("\\|");
					for (int i = 0; i < splitData.length; i++) {
						auctionService.insertIntoPlayerCount(con, roomInfo.getRoomId(), splitData[i], budget);
					}
					auctionService.updateRound(con, roomInfo.getRoomId(), Constants.I_ROUND1);
				}

				auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_ONGOING_STATUS);
				con.commit();
				
				AuctionRunning auctionRunning = new AuctionRunning(roomInfo);
				new Thread(auctionRunning).start();

				Thread.sleep(100);

				responseMessage.setMessage(Constants.OK_MESSAGE);
				
			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return responseMessage;
	}
}

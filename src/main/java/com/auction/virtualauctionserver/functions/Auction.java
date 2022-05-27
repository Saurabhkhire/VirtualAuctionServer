package com.auction.virtualauctionserver.functions;

import java.sql.Connection;

import com.auction.virtualauctionserver.model.AuctionInfo;
import com.auction.virtualauctionserver.model.Bid;
import com.auction.virtualauctionserver.model.NamesList;
import com.auction.virtualauctionserver.model.PlayerInfo;
import com.auction.virtualauctionserver.model.PlayerInfoList;
import com.auction.virtualauctionserver.model.PlayerName;
import com.auction.virtualauctionserver.model.PlayerStatus;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.model.RoomStatus;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.model.SkipInfo;
import com.auction.virtualauctionserver.model.Team;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class Auction {

	public static void auction(RoomInfo roomInfo) {

		boolean pause = false;
		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		// PlayerStatus playerStatus = new PlayerStatus();

		try {

			con = auctionService.getConnection(true);

			try {

				//Thread.sleep(1000);
				String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);
				System.out.println(round);
				
				String sets = "";
				if (round.equalsIgnoreCase(Constants.I_ROUND2)) {

					sets = roomInfo.getRoomId() + "_round1_unsoldlist";

				} else if (round.equalsIgnoreCase(Constants.I_ROUND3)) {

					sets = roomInfo.getRoomId() + "_round2_unsoldlistshort";

				} else {

					sets = auctionService.sets(con);
				}

				String[] setsSplit = sets.split(",");

				for (int i = 0; i < setsSplit.length; i++) {

					while (true) {

						String status = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);

						if (status.equals(Constants.I_PAUSED_STATUS) || status.equals(Constants.I_HALT_STATUS)) {

							pause = true;
							break;

						}

						String skip = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_SKIP);

						String playerName = auctionService.checkPlayerName(con, roomInfo.getRoomId(), round,
								setsSplit[i]);

						if (!playerName.equals("")) {

							PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName, setsSplit[i]);

							if (skip.equalsIgnoreCase(Constants.I_SKIP_ENTIRE_ROUND) || skip.equalsIgnoreCase(Constants.I_SKIP_CURRENT_SET)) {

								auctionService.addPlayerInfo(con, roomInfo.getRoomId(), round, setsSplit[i], 0,
										playerInfo);

							} else {

								auctionService.addPlayerInfo(con, roomInfo.getRoomId(), round, setsSplit[i], 6,
										playerInfo);
								
								

								while (true) {

							String playerNameTime = auctionService.getTime(con, roomInfo.getRoomId(), round);
									String[] split = playerNameTime.split(",");
									int time = Integer.parseInt(split[0]) - 1;
									playerName = split[1];
									auctionService.updateTime(con, roomInfo.getRoomId(), round, playerName, time);

									if (time == 0) {

										Thread.sleep(1000);
										break;
									}

									Thread.sleep(2000);
									
						
								}
							}

							PlayerStatus playerStatus = auctionService.playerStatus(con, roomInfo.getRoomId(), round);

							auctionService.addPlayerInfoAfterBid(con, roomInfo.getRoomId(), round, setsSplit[i],
									playerStatus, playerStatus.getTeam());

							if (!playerStatus.getTeam().equals("")) {

								playerStatus = auctionService.teamStatus(con, roomInfo.getRoomId(),
										playerStatus.getTeam(), playerStatus);

								auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
										playerStatus.getBudget() - playerStatus.getTotalPriceinLakhs(),
										"Budget_in_lakh");

								auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
										playerStatus.getTotal() + 1, "Total");

								String playerRoleColumn = "";

								int roleCount = 0;
								if (playerStatus.getPlayerRole().equals("Batsman")) {

									roleCount = playerStatus.getBatsman() + 1;
									playerRoleColumn = "Batsman";

								} else if (playerStatus.getPlayerRole().equals("Wicket Keeper")) {

									roleCount = playerStatus.getWicketKeepers() + 1;
									playerRoleColumn = "WicketKeepers";

								} else if (playerStatus.getPlayerRole().equals("All Rounder")) {

									roleCount = playerStatus.getAllRounders() + 1;
									playerRoleColumn = "AllRounders";

								} else if (playerStatus.getPlayerRole().equals("Fast Bowler")) {

									roleCount = playerStatus.getFastBowlers() + 1;
									playerRoleColumn = "FastBowlers";

								} else if (playerStatus.getPlayerRole().equals("Spin Bowler")) {

									roleCount = playerStatus.getSpinBowlers() + 1;
									playerRoleColumn = "SpinBowlers";

								}

								auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
										roleCount, playerRoleColumn);

								auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
										playerStatus.getForeigners(), "Foreigners");

							}

							auctionService.updatePlayerInfo(con, roomInfo.getRoomId(), round,
									playerStatus.getPlayerName(), playerStatus.getTeam(),
									playerStatus.getTotalPriceinLakhs());
							
					

						} else {

							break;
						}
					}

					String skip = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_SKIP);

					if (skip.equals(Constants.I_SKIP_CURRENT_SET)) {

						auctionService.updateSkip(con, roomInfo.getRoomId(), Constants.I_NO);
			
					}

					if (pause) {
						break;
					}

				}

				String skip = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_SKIP);

				if (skip.equals(Constants.I_SKIP_ENTIRE_ROUND)) {

					auctionService.updateSkip(con, roomInfo.getRoomId(), Constants.I_NO);
					con.commit();
				}

				if (!pause) {

					if (round.equalsIgnoreCase(Constants.I_ROUND1)) {

						auctionService.updateRound(con, roomInfo.getRoomId(), Constants.I_ROUND2);
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_WAITING_FOR_ROUND2);

					} else if (round.equalsIgnoreCase(Constants.I_ROUND2)) {

						auctionService.updateRound(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_WAITING_FOR_ROUND3);

					}

				}
			

			} catch (Exception exception) {
				exception.printStackTrace();
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}
	}

	public static ResponseMessage bid(Bid bid) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {
				
				PlayerStatus playerStatus = new PlayerStatus();
				playerStatus = auctionService.teamStatus(con, bid.getRoomId(), bid.getTeam(), playerStatus);
				String round = auctionService.getRoomResult(con, bid.getRoomId(), Constants.I_CURRENT_ROUND);
				String totalPriceTeam = auctionService.getPlayerPrice(con, bid.getRoomId(), round, bid.getPlayerName());
				String[] split = totalPriceTeam.split(",");
				int price = Integer.parseInt(split[0]);
				String team = split[1];
				
				

				if (!team.equals(Constants.I_NA)) {

					price = Functions.calculatePrice(price);

				}
				auctionService.updatePrice(con, bid.getRoomId(), round, bid.getPlayerName(), bid.getTeam(), price);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(bid.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(bid.getRoomId(), exception);
		}

		return responseMessage;
	}

	public static PlayerStatus auctionStatus(Team team) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerStatus playerStatus = new PlayerStatus();
		try {

			con = auctionService.getConnection(false);

			try {
				String round = auctionService.getRoomResult(con, team.getRoomId(), Constants.I_CURRENT_ROUND);
				playerStatus = auctionService.playerStatus(con, team.getRoomId(), round);

				playerStatus = auctionService.teamStatus(con, team.getRoomId(), team.getTeam(), playerStatus);
				playerStatus.setRound(round);
				playerStatus.setHostName(auctionService.getUserbyHost(con, team.getRoomId(), Constants.I_USERNAME));
				playerStatus.setRoomStatus(auctionService.getRoomResult(con, team.getRoomId(), Constants.I_ROOM_STATUS));
				
				team.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(team.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(team.getRoomId(), exception);

		}
		return playerStatus;
	}

	public static ResponseMessage pauseAuction(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {
				
				String status = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);
				
				if(status.equals(Constants.I_ONGOING_STATUS)) {

				auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_PAUSED_STATUS);
				
				} else {
					
					roomInfo.setMessage(Constants.ALREADY_PAUSED_MESSAGE);
				}
				roomInfo.setMessage(Constants.OK_MESSAGE);
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

	public static ResponseMessage playAuction(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				String status = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);

				if (status.equals(Constants.I_PAUSED_STATUS) || status.equals(Constants.I_HALT_STATUS) || status.equals(Constants.I_WAITING_FOR_ROUND2)
						|| status.equals(Constants.I_WAITING_FOR_ROUND3)) {

					String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);
					String playerNameTime = auctionService.getTime(con, roomInfo.getRoomId(), round);
					String[] split = playerNameTime.split(",");

					int time = 0;
					if (!playerNameTime.equals("")) {
						time = Integer.parseInt(split[0]);
					}

					if (time == 0) {

						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_ONGOING_STATUS);

						AuctionRunning auctionRunning = new AuctionRunning(roomInfo);
						new Thread(auctionRunning).start();
						
						roomInfo.setMessage(Constants.OK_MESSAGE);
						con.commit();
					} else {
						roomInfo.setMessage(Constants.ALREADY_PLAY_MESSAGE);
					}
				} else {
					roomInfo.setMessage(Constants.ALREADY_PLAY_MESSAGE);
				}

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

	public static ResponseMessage skipSets(SkipInfo skipInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {
				
				String skip = auctionService.getRoomResult(con, skipInfo.getRoomId(), Constants.I_SKIP);

				if(skipInfo.getSkipType().equals("Unskip")) {
					
					if(skip.contains(Constants.I_SKIP)) {
					
					auctionService.updateSkip(con, skipInfo.getRoomId(), Constants.I_NO);
					
					} else {
						
						skipInfo.setMessage(Constants.ALREADY_UNSKIPPED_MESSAGE);
					}
					
				} else {
					
					if(skip.contains(Constants.I_SKIP)) {
						
						skipInfo.setMessage(Constants.ALREADY_SKIPPED_MESSAGE);
						
					} else {
				
				auctionService.updateSkip(con, skipInfo.getRoomId(), skipInfo.getSkipType());
				
					}
				
				}
				
				skipInfo.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(skipInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(skipInfo.getRoomId(), exception);
		}

		return responseMessage;
	}

	public static NamesList getUsernamesList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();

		try {

			con = auctionService.getConnection(false);

			try {

				String roomStatus = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);
				namesList = auctionService.getUsernamesList(con, roomInfo.getRoomId(), roomStatus,
						roomInfo.getUsername());
				roomInfo.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return namesList;
	}

	public static NamesList getUnsoldPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();

		try {

			con = auctionService.getConnection(false);

			try {

				namesList = auctionService.getUnsoldPlayersList(con, roomInfo.getRoomId());
				roomInfo.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return namesList;
	}

	public static ResponseMessage makeHost(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				String[] splitUsername = roomInfo.getUsername().split(",");
				auctionService.updateHost(con, roomInfo.getRoomId(), splitUsername[0], Constants.I_NO);
				auctionService.updateHost(con, roomInfo.getRoomId(), splitUsername[1], Constants.I_YES);
				roomInfo.setMessage(Constants.OK_MESSAGE);
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

	public static ResponseMessage addUnsoldPlayers(NamesList namesList) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				while (namesList.getNamesList().size() != 0) {

					String playerName = namesList.getNamesList().remove(0);

					String playerNameCheck = auctionService.checkPlayerName(con, namesList.getRoomId(), playerName,
							"_round2_unsoldlistshort");

					if (playerNameCheck.equals(null) || playerNameCheck.equals("")) {

						PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName,
								namesList.getRoomId() + "_round2_unsoldlist");

						auctionService.addPlayerInfo(con, namesList.getRoomId(), Constants.I_ROUND2, Constants.I_SHORT_LIST, 0, playerInfo);

					}
				}
				
				namesList.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(namesList.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(namesList.getRoomId(), exception);
		}

		return responseMessage;
	}

	public static RoomStatus auctionBreakStatus(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomStatus roomStatus = new RoomStatus();

		try {

			con = auctionService.getConnection(false);

			try {

				roomStatus.setHostName(auctionService.getUserbyHost(con, roomInfo.getRoomId(), Constants.I_USERNAME));
				roomStatus.setRoomStatus(auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS));
				roomInfo.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				roomStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			roomStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return roomStatus;
	}

	public static NamesList getCurrentSetPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();

		try {

			con = auctionService.getConnection(false);

			try {

				String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);
				String playerNameTime = auctionService.getTime(con, roomInfo.getRoomId(), round);
				String[] splitPlayerNameTime = playerNameTime.split(",");
				auctionService.getCurrentSetPlayersList(con, splitPlayerNameTime[0]);
				roomInfo.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return namesList;
	}

	public static PlayerInfoList getTotalUnsoldPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();

		try {

			con = auctionService.getConnection(false);

			try {

				String unsoldType = "";

				String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);
				String status = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);
				roomInfo.setMessage(Constants.OK_MESSAGE);
				con.commit();

				if (round.equals(Constants.I_ROUND2)) {
					unsoldType = Constants.I_ROUND1;
				} else if (round.equals(Constants.I_ROUND3)) {
					if (status.equals("Finished")) {
						unsoldType = Constants.I_ROUND3;
					} else {
						unsoldType = Constants.I_ROUND2;
					}
				}

				playerInfoList = auctionService.playerInfoList(con, roomInfo.getRoomId(), unsoldType);

			} catch (Exception exception) {
				playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), exception);
		}

		return playerInfoList;
	}

	public static PlayerInfoList getTeamPlayersList(Team team) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();
		PlayerStatus playerStatus = new PlayerStatus();

		try {

			con = auctionService.getConnection(false);

			try {

				playerInfoList = auctionService.playerInfoList(con, team.getRoomId(), team.getTeam());
				playerStatus = auctionService.teamStatus(con, team.getRoomId(), team.getTeam(), playerStatus);
				playerInfoList.setBatsman(playerStatus.getBatsman());
				playerInfoList.setWicketKeepers(playerStatus.getWicketKeepers());
				playerInfoList.setAllRounders(playerStatus.getAllRounders());
				playerInfoList.setFastBowlers(playerStatus.getFastBowlers());
				playerInfoList.setSpinBowlers(playerStatus.getSpinBowlers());
				playerInfoList.setForeigners(playerStatus.getForeigners());
				playerInfoList.setTotal(playerStatus.getTotal());
				playerInfoList.setBudget(playerStatus.getBudget());
				team.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(team.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(team.getRoomId(), exception);
		}

		return playerInfoList;
	}

	public static ResponseMessage addPlayerToTeamAfterAuction(PlayerName playerName) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {
				
				playerName.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(playerName.getRoomId(), exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(playerName.getRoomId(), exception);
		}

		return responseMessage;
	}
	
	public static ResponseMessage a(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

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

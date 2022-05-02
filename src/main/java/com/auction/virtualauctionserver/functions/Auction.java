package com.auction.virtualauctionserver.functions;

import java.sql.Connection;

import com.auction.virtualauctionserver.model.AuctionInfo;
import com.auction.virtualauctionserver.model.Bid;
import com.auction.virtualauctionserver.model.NamesList;
import com.auction.virtualauctionserver.model.PlayerInfo;
import com.auction.virtualauctionserver.model.PlayerInfoList;
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

			con = auctionService.getConnection();

			try {

				String round = auctionService.getUsers(con, roomInfo.getRoomId(), "CurrentRound");

				String sets = "";
				if (round.equalsIgnoreCase("round2")) {

					sets = roomInfo.getRoomId() + "_round1_unsoldlist";

				} else if (round.equalsIgnoreCase("round3")) {

					sets = roomInfo.getRoomId() + "_round2_unsoldlistshort";

				} else {

					sets = auctionService.sets(con);
				}

				String[] setsSplit = sets.split(",");

				for (int i = 0; i < setsSplit.length; i++) {

					while (true) {

						String status = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");

						if (status.equals("TempPaused") || status.equals("Paused")) {

							pause = true;
							break;

						}

						String skip = auctionService.getUsers(con, roomInfo.getRoomId(), "Skip");

						String playerName = auctionService.checkPlayerName(con, roomInfo.getRoomId(), round,
								setsSplit[i]);

						if (!playerName.equals("")) {

							PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName, setsSplit[i]);

							if (skip.equalsIgnoreCase("SkipAll") || skip.equalsIgnoreCase("SkipOneSet")) {

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

					String skip = auctionService.getUsers(con, roomInfo.getRoomId(), "Skip");

					if (skip.equals("SkipOneSet")) {

						auctionService.updateSkip(con, roomInfo.getRoomId(), "No");
					}

					if (pause) {
						break;
					}

				}

				String skip = auctionService.getUsers(con, roomInfo.getRoomId(), "Skip");

				if (skip.equals("SkipAll")) {

					auctionService.updateSkip(con, roomInfo.getRoomId(), "No");
				}

				if (!pause) {

					if (round.equalsIgnoreCase("round1")) {

						auctionService.updateRound(con, roomInfo.getRoomId(), "round2");
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), "TempPausedForRound2");

					} else if (round.equalsIgnoreCase("round2")) {

						auctionService.updateRound(con, roomInfo.getRoomId(), "round3");
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), "TempPausedForRound3");

					}

				}

			} catch (Exception ex) {
				ex.printStackTrace();
				// playerStatus.setPlayerName("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {

		}
	}

	public static ResponseMessage bid(Bid bid) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {
				String round = auctionService.getUsers(con, bid.getRoomId(), "CurrentRound");
				String totalPriceTeam = auctionService.getPlayerPrice(con, bid.getRoomId(), round, bid.getPlayerName());
				String[] split = totalPriceTeam.split(",");
				int price = Integer.parseInt(split[0]);
				String team = split[1];

				if (!team.equals("NA")) {

					price = Functions.calculatePrice(price);

				}
				auctionService.updatePrice(con, bid.getRoomId(), round, bid.getPlayerName(), bid.getTeam(), price);

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

	public static PlayerStatus auctionStatus(Team team) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerStatus playerStatus = new PlayerStatus();
		try {

			con = auctionService.getConnection();

			try {
				String round = auctionService.getUsers(con, team.getRoomId(), "CurrentRound");
				playerStatus = auctionService.playerStatus(con, team.getRoomId(), round);

				playerStatus = auctionService.teamStatus(con, team.getRoomId(), team.getTeam(), playerStatus);
				playerStatus.setRound(round);
				playerStatus.setHostName(auctionService.getUserbyHost(con, team.getRoomId(), "Username"));
				playerStatus.setRoomStatus(auctionService.getUsers(con, team.getRoomId(), "RoomStatus"));

			} catch (Exception ex) {
				ex.printStackTrace();
				playerStatus.setPlayerName("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			playerStatus.setRoomId("Error");

		}
		return playerStatus;
	}

	public static ResponseMessage pauseAuction(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				auctionService.updateRoomStatus(con, roomInfo.getRoomId(), "TempPaused");

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

	public static ResponseMessage playAuction(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				String status = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");

				if (status.equals("TempPaused") || status.equals("Paused") || status.equals("TempPausedForRound2")
						|| status.equals("TempPausedForRound3")) {

					String round = auctionService.getUsers(con, roomInfo.getRoomId(), "CurrentRound");
					String playerNameTime = auctionService.getTime(con, roomInfo.getRoomId(), round);
					String[] split = playerNameTime.split(",");

					int time = 0;
					if (!playerNameTime.equals("")) {
						time = Integer.parseInt(split[0]);
					}

					if (time == 0) {

						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), "Ongoing");

						AuctionRunning auctionRunning = new AuctionRunning(roomInfo);
						new Thread(auctionRunning).start();
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

	public static ResponseMessage skipSets(SkipInfo skipInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				auctionService.updateSkip(con, skipInfo.getRoomId(), skipInfo.getSkipType());

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

	public static NamesList getUsernamesList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();

		try {

			con = auctionService.getConnection();

			try {

				String roomStatus = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");
				namesList = auctionService.getUsernamesList(con, roomInfo.getRoomId(), roomStatus,
						roomInfo.getUsername());

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				con.close();
			}

		} catch (Exception ex) {

		}

		return namesList;
	}

	public static NamesList getUnsoldPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();

		try {

			con = auctionService.getConnection();

			try {

				namesList = auctionService.getUnsoldPlayersList(con, roomInfo.getRoomId());

			} catch (Exception ex) {

			} finally {
				con.close();
			}

		} catch (Exception ex) {

		}

		return namesList;
	}

	public static ResponseMessage makeHost(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				String[] splitUsername = roomInfo.getUsername().split(",");
				auctionService.updateHost(con, roomInfo.getRoomId(), splitUsername[0], "No");
				auctionService.updateHost(con, roomInfo.getRoomId(), splitUsername[1], "Yes");

			} catch (Exception ex) {
				responseMessage.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			responseMessage.setMessage("Error");
		}

		return responseMessage;
	}

	public static ResponseMessage addUnsoldPlayers(NamesList namesList) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

				while (namesList.getNamesList().size() != 0) {

					String playerName = namesList.getNamesList().remove(0);

					String playerNameCheck = auctionService.checkPlayerName(con, namesList.getRoomId(), playerName,
							"_round2_unsoldlistshort");

					if (playerNameCheck.equals(null) || playerNameCheck.equals("")) {

						PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName,
								namesList.getRoomId() + "_round2_unsoldlist");

						auctionService.addPlayerInfo(con, namesList.getRoomId(), "round2", "ShortList", 0, playerInfo);

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

	public static RoomStatus auctionBreakStatus(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		RoomStatus roomStatus = new RoomStatus();

		try {

			con = auctionService.getConnection();

			try {

				roomStatus.setHostName(auctionService.getUserbyHost(con, roomInfo.getRoomId(), "Username"));
				roomStatus.setRoomStatus(auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus"));

			} catch (Exception ex) {
				roomStatus.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			roomStatus.setMessage("Error");
		}

		return roomStatus;
	}

	public static NamesList getCurrentSetPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();

		try {

			con = auctionService.getConnection();

			try {

				String round = auctionService.getUsers(con, roomInfo.getRoomId(), "CurrentRound");
				String playerNameTime = auctionService.getTime(con, roomInfo.getRoomId(), round);
				String[] splitPlayerNameTime = playerNameTime.split(",");
				auctionService.getCurrentSetPlayersList(con, splitPlayerNameTime[0]);

			} catch (Exception ex) {
				namesList.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			namesList.setMessage("Error");
		}

		return namesList;
	}

	public static PlayerInfoList getTotalUnsoldPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();

		try {

			con = auctionService.getConnection();

			try {

				String unsoldType = "";

				String round = auctionService.getUsers(con, roomInfo.getRoomId(), "CurrentRound");
				String status = auctionService.getUsers(con, roomInfo.getRoomId(), "RoomStatus");

				if (round.equals("round2")) {
					unsoldType = "round1";
				} else if (round.equals("round3")) {
					if (status.equals("Finished")) {
						unsoldType = "round3";
					} else {
						unsoldType = "round2";
					}
				}

				playerInfoList = auctionService.playerInfoList(con, roomInfo.getRoomId(), unsoldType);

			} catch (Exception ex) {
				playerInfoList.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			playerInfoList.setMessage("Error");
		}

		return playerInfoList;
	}

	public static PlayerInfoList getTeamPlayersList(Team team) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();
		PlayerStatus playerStatus = new PlayerStatus();

		try {

			con = auctionService.getConnection();

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

			} catch (Exception ex) {
				playerInfoList.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			playerInfoList.setMessage("Error");
		}

		return playerInfoList;
	}

	public static ResponseMessage a(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage playerInfoList = new ResponseMessage();

		try {

			con = auctionService.getConnection();

			try {

			} catch (Exception ex) {
				playerInfoList.setMessage("Error");
			} finally {
				con.close();
			}

		} catch (Exception ex) {
			playerInfoList.setMessage("Error");
		}

		return playerInfoList;
	}

}

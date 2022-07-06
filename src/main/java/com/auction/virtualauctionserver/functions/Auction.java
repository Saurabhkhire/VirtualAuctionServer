package com.auction.virtualauctionserver.functions;

import java.sql.Connection;
import java.util.ArrayList;

import com.auction.virtualauctionserver.model.Bid;
import com.auction.virtualauctionserver.model.NamesList;
import com.auction.virtualauctionserver.model.PlayerInfo;
import com.auction.virtualauctionserver.model.PlayerInfoList;
import com.auction.virtualauctionserver.model.PlayerName;
import com.auction.virtualauctionserver.model.PlayerStatus;
import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.model.RoomStatus;
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

				// Thread.sleep(1000);
				String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);

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

						String status = auctionService.getRoomResult(con, roomInfo.getRoomId(),
								Constants.I_ROOM_STATUS);

						if (status.equals(Constants.I_PAUSED_STATUS) || status.equals(Constants.I_HALT_STATUS)) {

							pause = true;
							break;

						}

						String skip = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_SKIP);

						String playerName = auctionService.checkPlayerName(con, roomInfo.getRoomId(), round,
								setsSplit[i]);

						if (!playerName.equals("")) {

							PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName,
									"ipl_auction_set_list");

							playerInfo = auctionService.getPlayerInfoToAddFromInformation(con, playerName, playerInfo);

							if (skip.equalsIgnoreCase(Constants.I_SKIP_ENTIRE_ROUND)
									|| skip.equalsIgnoreCase(Constants.I_SKIP_CURRENT_SET)) {

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
									System.out.println(time);

									if (time == 0) {

										System.out.println("yes");
										Thread.sleep(1000);
										break;
									}

									Thread.sleep(2000);

								}
							}

							PlayerStatus playerStatus = auctionService.playerStatus(con, roomInfo.getRoomId(), round);

							playerStatus = auctionService.playerStatusFromInformation(con, roomInfo.getRoomId(),
									playerStatus);

							auctionService.addPlayerInfoAfterBid(con, roomInfo.getRoomId(), round, setsSplit[i],
									playerStatus, playerStatus.getTeam());

							if (!playerStatus.getTeam().equals("")) {

								playerStatus = auctionService.teamStatus(con, roomInfo.getRoomId(),
										playerStatus.getTeam(), playerStatus);

								int budget = playerStatus.getBudgetInLakhs() - playerStatus.getTotalPriceinLakhs();
								auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
										budget, "Budget_in_lakh");

								auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
										budget, "Budget_in_crore");

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

								if (!playerStatus.getPlayerCountry().equals("India")) {
									auctionService.addTeamInfo(con, roomInfo.getRoomId(), round, playerStatus.getTeam(),
											playerStatus.getForeigners() + 1, "Foreigners");
								}

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

				}

				if (!pause) {

					if (round.equalsIgnoreCase(Constants.I_ROUND1)) {

						auctionService.updateRound(con, roomInfo.getRoomId(), Constants.I_ROUND2);
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_WAITING_FOR_ROUND2);

					} else if (round.equalsIgnoreCase(Constants.I_ROUND2)) {

						auctionService.updateRound(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_WAITING_FOR_ROUND3);

					} else if (round.equalsIgnoreCase(Constants.I_ROUND3)) {

						auctionService.updateRound(con, roomInfo.getRoomId(), Constants.I_ROUND3);
						auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_FINISHED_STATUS);

					}

					AuctionBreakThread auctionBreakThread = new AuctionBreakThread(roomInfo.getRoomId());
					new Thread(auctionBreakThread).start();

				}

			} catch (Exception exception) {
				exception.printStackTrace();
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
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
				playerStatus = auctionService.playerStatusFromInformation(con, bid.getRoomId(), playerStatus);
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
				FileUtil.createAndUpdateErrorLog(bid.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(bid.getRoomId(), "Auction#auction", exception);
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
				playerStatus = auctionService.playerStatusFromInformation(con, team.getRoomId(), playerStatus);
				playerStatus = auctionService.teamStatus(con, team.getRoomId(), team.getTeam(), playerStatus);
				playerStatus.setRound(round);
				playerStatus.setHostName(auctionService.getUserbyHost(con, team.getRoomId(), Constants.I_USERNAME));
				playerStatus
						.setRoomStatus(auctionService.getRoomResult(con, team.getRoomId(), Constants.I_ROOM_STATUS));
				playerStatus.setSkipType(auctionService.getRoomResult(con, team.getRoomId(), Constants.I_SKIP));
				auctionService.setUserDateTime(con, team.getRoomId(), team.getUsername());
				team.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(team.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(team.getRoomId(), "Auction#auction", exception);

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

				if (status.equals(Constants.I_ONGOING_STATUS)) {

					auctionService.updateRoomStatus(con, roomInfo.getRoomId(), Constants.I_PAUSED_STATUS);
					responseMessage.setMessage(Constants.AUCTION_PAUSED_MESSAGE);

				} else {

					responseMessage.setMessage(Constants.ALREADY_PAUSED_MESSAGE);
				}

				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
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

				if (status.equals(Constants.I_PAUSED_STATUS) || status.equals(Constants.I_HALT_STATUS)
						|| status.equals(Constants.I_WAITING_FOR_ROUND2)
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

						responseMessage.setMessage(Constants.AUCTION_PLAYED_MESSAGE);
						con.commit();
					} else {
						responseMessage.setMessage(Constants.ALREADY_PLAY_MESSAGE);
					}
				} else {
					responseMessage.setMessage(Constants.ALREADY_PLAY_MESSAGE);
				}

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}
		return responseMessage;
	}

	public static ResponseMessage skipSets(RoomStatus roomStatus) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				String skip = auctionService.getRoomResult(con, roomStatus.getRoomId(), Constants.I_SKIP);

				if (roomStatus.getSkipType().equals("Unskip")) {

					if (skip.contains(Constants.I_SKIP)) {

						auctionService.updateSkip(con, roomStatus.getRoomId(), Constants.I_NO);
						responseMessage.setMessage(Constants.OK_MESSAGE);

					} else {

						responseMessage.setMessage(Constants.ALREADY_UNSKIPPED_MESSAGE);
					}

				} else {

					if (skip.contains(Constants.I_SKIP)) {

						responseMessage.setMessage(Constants.ALREADY_SKIPPED_MESSAGE);

					} else {

						auctionService.updateSkip(con, roomStatus.getRoomId(), roomStatus.getSkipType());
						responseMessage.setMessage(Constants.OK_MESSAGE);
					}

				}

				
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomStatus.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomStatus.getRoomId(), "Auction#auction", exception);
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
				namesList.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return namesList;
	}

	public static NamesList getUnsoldPlayersList(Team team) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		NamesList namesList = new NamesList();
		namesList.setNamesList(new ArrayList<>());
		namesList.setPriceList(new ArrayList<>());

		try {

			con = auctionService.getConnection(false);

			try {

				String roomStatus = auctionService.getRoomResult(con, team.getRoomId(), Constants.I_ROOM_STATUS);

				if (roomStatus.equals(Constants.I_FINISHED_STATUS)) {

					int maxForeigners = Integer
							.parseInt(auctionService.getRoomResult(con, team.getRoomId(), Constants.I_MAX_FOREIGNERS));
					int maxTotal = Integer
							.parseInt(auctionService.getRoomResult(con, team.getRoomId(), Constants.I_MAX_TOTAL));

					PlayerStatus playerStatus = new PlayerStatus();
					playerStatus = auctionService.teamStatus(con, team.getRoomId(), team.getTeam(), playerStatus);

					String type = "Normal";
					if (playerStatus.getTotal() < maxTotal) {

						if (playerStatus.getForeigners() >= maxForeigners) {
							type = "ForiegnersExceed";
						}
						namesList = auctionService.getUnsoldPlayersList(con, team.getRoomId(), type,
								playerStatus.getBudgetInLakhs());
					}

				} else {
					namesList = auctionService.getUnsoldPlayersList(con, team.getRoomId(), "", 0);
				}
				team.setMessage(Constants.OK_MESSAGE);
				// Thread.sleep(3000);
				con.commit();

			} catch (Exception exception) {
				namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(team.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			namesList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(team.getRoomId(), "Auction#auction", exception);
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
				responseMessage.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
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
							"round2_unsoldlistshort");

					if (playerNameCheck.equals(null) || playerNameCheck.equals("")) {

						PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName,
								namesList.getRoomId() + "_round2_unsoldlist");

						playerInfo = auctionService.getPlayerInfoToAddFromInformation(con, playerName, playerInfo);

						auctionService.addPlayerInfo(con, namesList.getRoomId(), Constants.I_ROUND2,
								Constants.I_SHORT_LIST, 0, playerInfo);

					}
				}

				namesList.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(namesList.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(namesList.getRoomId(), "Auction#auction", exception);
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
				roomStatus.setAuctionBreakMinTime(
						Integer.parseInt(auctionService.getRoomResult(con, roomInfo.getRoomId(), "MinBreakTime")));
				roomStatus.setRoomStatus(
						auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS));
				auctionService.setUserDateTime(con, roomInfo.getRoomId(), roomInfo.getUsername());
				roomStatus.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				roomStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			roomStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return roomStatus;
	}

	public static PlayerInfoList getCurrentSetPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();
		playerInfoList.setPlayerNameList(new ArrayList<>());
		playerInfoList.setPlayerCountryList(new ArrayList<>());
		playerInfoList.setPlayerRoleList(new ArrayList<>());
		playerInfoList.setBattingStyleList(new ArrayList<>());
		playerInfoList.setBowlingStyleList(new ArrayList<>());
		playerInfoList.setBattingPositionList(new ArrayList<>());
		playerInfoList.setPriceinLakhsList(new ArrayList<>());
		playerInfoList.setPriceinCroresList(new ArrayList<>());

		try {

			con = auctionService.getConnection(false);

			try {

				String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);
				String type = "";
				if (round.equals(Constants.I_ROUND1)) {
					type = auctionService.getCurrentSet(con, roomInfo.getRoomId(), round);

				} else if (round.equals(Constants.I_ROUND2)) {
					type = "unsold1";
				} else if (round.equals(Constants.I_ROUND3)) {
					type = "unsold2short";
				}
				playerInfoList = auctionService.playerInfoList(con, roomInfo.getRoomId(), type);
				String playerListForDB = String.join(",", playerInfoList.getPlayerNameList());
				String playerListforOrder = "'" + playerListForDB.replaceAll(",", "','") + "'";
				playerInfoList = auctionService.playerInfoListFromInformation(con, roomInfo.getRoomId(),
						playerListForDB, playerListforOrder, playerInfoList);
				playerInfoList.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return playerInfoList;
	}

	public static PlayerInfoList getTotalUnsoldPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();
		playerInfoList.setPlayerNameList(new ArrayList<>());
		playerInfoList.setPlayerCountryList(new ArrayList<>());
		playerInfoList.setPlayerRoleList(new ArrayList<>());
		playerInfoList.setBattingStyleList(new ArrayList<>());
		playerInfoList.setBowlingStyleList(new ArrayList<>());
		playerInfoList.setBattingPositionList(new ArrayList<>());
		playerInfoList.setPriceinLakhsList(new ArrayList<>());
		playerInfoList.setPriceinCroresList(new ArrayList<>());

		try {

			con = auctionService.getConnection(false);

			try {

				String unsoldType = "";

				String round = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_CURRENT_ROUND);
				String status = auctionService.getRoomResult(con, roomInfo.getRoomId(), Constants.I_ROOM_STATUS);

				if (round.equals(Constants.I_ROUND2)) {
					unsoldType = "unsold1";
				} else if (round.equals(Constants.I_ROUND3)) {
					if (status.equals("Finished")) {
						unsoldType = "unsold3";
					} else {
						unsoldType = "unsold2";
					}
				}

				playerInfoList = auctionService.playerInfoList(con, roomInfo.getRoomId(), unsoldType);
				String playerListForDB = String.join(",", playerInfoList.getPlayerNameList());
				String playerListforOrder = "'" + playerListForDB.replaceAll(",", "','") + "'";
				playerInfoList = auctionService.playerInfoListFromInformation(con, roomInfo.getRoomId(),
						playerListForDB, playerListforOrder, playerInfoList);
				playerInfoList.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return playerInfoList;
	}

	public static PlayerInfoList getTeamPlayersList(Team team) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();
		PlayerStatus playerStatus = new PlayerStatus();
		playerInfoList.setPlayerNameList(new ArrayList<>());
		playerInfoList.setPlayerCountryList(new ArrayList<>());
		playerInfoList.setPlayerRoleList(new ArrayList<>());
		playerInfoList.setBattingStyleList(new ArrayList<>());
		playerInfoList.setBowlingStyleList(new ArrayList<>());
		playerInfoList.setBattingPositionList(new ArrayList<>());
		playerInfoList.setPriceinLakhsList(new ArrayList<>());
		playerInfoList.setPriceinCroresList(new ArrayList<>());

		try {

			con = auctionService.getConnection(false);

			try {

				playerInfoList = auctionService.playerInfoList(con, team.getRoomId(), team.getTeam());
				String playerListForDB = String.join(",", playerInfoList.getPlayerNameList());
				String playerListforOrder = "'" + playerListForDB.replaceAll(",", "','") + "'";
				playerInfoList = auctionService.playerInfoListFromInformation(con, team.getRoomId(), playerListForDB,
						playerListforOrder, playerInfoList);
				playerStatus = auctionService.teamStatus(con, team.getRoomId(), team.getTeam(), playerStatus);
				playerInfoList.setBatsman(playerStatus.getBatsman());
				playerInfoList.setWicketKeepers(playerStatus.getWicketKeepers());
				playerInfoList.setAllRounders(playerStatus.getAllRounders());
				playerInfoList.setFastBowlers(playerStatus.getFastBowlers());
				playerInfoList.setSpinBowlers(playerStatus.getSpinBowlers());
				playerInfoList.setForeigners(playerStatus.getForeigners());
				playerInfoList.setTotal(playerStatus.getTotal());
				playerInfoList.setBudgetInLakh(playerStatus.getBudgetInLakhs());
				playerInfoList.setBudgetInCrores(playerStatus.getBudgetInCrores());
				team.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(team.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerStatus.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(team.getRoomId(), "Auction#auction", exception);
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

				String player = auctionService.checkPlayerName(con, playerName.getRoomId(), playerName.getPlayerName(),
						"round3_auctionlist");

				if (player.equals("")) {

					PlayerInfo playerInfo = auctionService.getPlayerInfoToAdd(con, playerName.getPlayerName(),
							playerName.getRoomId() + "_round2_unsoldlist");

					playerInfo = auctionService.getPlayerInfoToAddFromInformation(con, playerName.getPlayerName(),
							playerInfo);

					auctionService.addPlayerInfo(con, playerName.getRoomId(), Constants.I_ROUND3,
							playerName.getRoomId() + "_round2_unsoldlist", 0, playerInfo);

					PlayerStatus playerStatus = auctionService.playerStatus(con, playerName.getRoomId(),
							Constants.I_ROUND3);

					playerStatus = auctionService.playerStatusFromInformation(con, playerName.getRoomId(),
							playerStatus);

					auctionService.addPlayerInfoAfterBid(con, playerName.getRoomId(), Constants.I_ROUND3,
							playerStatus.getSet(), playerStatus, playerName.getTeam());

					playerStatus = auctionService.teamStatus(con, playerName.getRoomId(), playerName.getTeam(),
							playerStatus);

					auctionService.addTeamInfo(con, playerName.getRoomId(), Constants.I_ROUND3, playerName.getTeam(),
							playerStatus.getBudgetInLakhs() - playerStatus.getTotalPriceinLakhs(), "Budget_in_lakh");

					auctionService.addTeamInfo(con, playerName.getRoomId(), Constants.I_ROUND3, playerName.getTeam(),
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

					auctionService.addTeamInfo(con, playerName.getRoomId(), Constants.I_ROUND3, playerName.getTeam(),
							roleCount, playerRoleColumn);

					auctionService.addTeamInfo(con, playerName.getRoomId(), Constants.I_ROUND3, playerName.getTeam(),
							playerStatus.getForeigners() + 1, "Foreigners");

					auctionService.updatePlayerInfo(con, playerName.getRoomId(), Constants.I_ROUND3,
							playerName.getPlayerName(), playerName.getTeam(), playerStatus.getTotalPriceinLakhs());

					responseMessage.setMessage(Constants.OK_MESSAGE);

				} else {

					responseMessage.setMessage(Constants.PLAYER_ALREADY_SELECTED_MESSAGE);
				}

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(playerName.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(playerName.getRoomId(), "Auction#auction", exception);
		}

		return responseMessage;
	}

	public static PlayerInfoList getTotalPlayersList(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		PlayerInfoList playerInfoList = new PlayerInfoList();
		playerInfoList.setPlayerNameList(new ArrayList<>());
		playerInfoList.setPlayerCountryList(new ArrayList<>());
		playerInfoList.setPlayerRoleList(new ArrayList<>());
		playerInfoList.setBattingStyleList(new ArrayList<>());
		playerInfoList.setBowlingStyleList(new ArrayList<>());
		playerInfoList.setBattingPositionList(new ArrayList<>());
		playerInfoList.setPriceinLakhsList(new ArrayList<>());
		playerInfoList.setPriceinCroresList(new ArrayList<>());

		try {

			con = auctionService.getConnection(false);

			try {

				playerInfoList = auctionService.playerInfoList(con, roomInfo.getRoomId(), "all");
				String playerListForDB = String.join(",", playerInfoList.getPlayerNameList());
				String playerListforOrder = "'" + playerListForDB.replaceAll(",", "','") + "'";
				playerInfoList = auctionService.playerInfoListFromInformation(con, roomInfo.getRoomId(),
						playerListForDB, playerListforOrder, playerInfoList);
				playerInfoList.setMessage(Constants.OK_MESSAGE);
				con.commit();

			} catch (Exception exception) {
				playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			playerInfoList.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return playerInfoList;
	}

	public static void lastLogin(String roomId) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		// PlayerStatus playerStatus = new PlayerStatus();

		try {

			con = auctionService.getConnection(true);

			while (true) {

				try {

					int users = 0;
					boolean exist = false;

					try {

						users = Integer.parseInt(auctionService.getRoomResult(con, roomId, Constants.I_USERS));

					} catch (Exception exception) {
						exist = true;
					}

					if (users <= 0 || exist) {

						System.out.println("true");
						break;
					}

					String list = auctionService.getUserOnlineDetails(con, roomId);
					String[] splitlist = list.split("\\|");

					for (int i = 0; i < splitlist.length; i++) {
						String[] split = splitlist[i].split(",");

						Functions.leaveInactiveUser(roomId, split[0], split[1]);

					}

					// con.commit();

				} catch (Exception exception) {
					exception.printStackTrace();
					FileUtil.createAndUpdateErrorLog(roomId, "LastLoginThread#last", exception);
				}

				Thread.sleep(2000);

			}

			con.close();

		} catch (Exception exception) {

		}

	}

	public static void auctionBreak(String roomId) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		// PlayerStatus playerStatus = new PlayerStatus();

		try {

			con = auctionService.getConnection(true);
			int time = 10000;
			//int time = 180000;

			while (true) {

				try {

					String status = auctionService.getRoomResult(con, roomId, Constants.I_ROOM_STATUS);

					time = time - 1;
					auctionService.updateAuctionBreakTime(con, roomId, time);

					if (status.equals(Constants.I_PAUSED_STATUS) || status.equals(Constants.I_ONGOING_STATUS)
							|| time <= 0) {

						break;
					}

					//con.commit();

				} catch (Exception exception) {
					exception.printStackTrace();
					FileUtil.createAndUpdateErrorLog(roomId, "LastLoginThread#last", exception);
				}

				Thread.sleep(1000);
			}

			con.close();

		} catch (Exception exception) {

		}

	}

	public static void deleteOldRooms() {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		// PlayerStatus playerStatus = new PlayerStatus();

		try {

			con = auctionService.getConnection(true);

			while (true) {

				try {

					// con.commit();

				} catch (Exception exception) {
					exception.printStackTrace();
					FileUtil.createAndUpdateErrorLog("", "LastLoginThread#last", exception);
				}

				Thread.sleep(2000);

			}

			// con.close();

		} catch (Exception exception) {

		}

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
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return responseMessage;
	}

	public static ResponseMessage getPlayerTeam(RoomInfo roomInfo) {

		Connection con = null;
		AuctionInterface auctionService = new AuctionService();
		ResponseMessage responseMessage = new ResponseMessage();

		try {

			con = auctionService.getConnection(false);

			try {

				System.out.println(roomInfo.getRoomId());

				con.commit();

			} catch (Exception exception) {
				exception.printStackTrace();
				responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
				FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
			} finally {
				con.close();
			}

		} catch (Exception exception) {
			responseMessage.setMessage(Constants.COMMON_ERROR_MESSAGE);
			FileUtil.createAndUpdateErrorLog(roomInfo.getRoomId(), "Auction#auction", exception);
		}

		return responseMessage;
	}

	// https://stackoverflow.com/questions/32530442/android-refresh-listview-every-minute

}

package com.auction.virtualauctionserver.functions;

import java.sql.Connection;
import java.sql.SQLException;

import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class Constants {
	
	public static final String LOG_FOLDER_LOCATION = "Location";
	public static final String ROOM_ERROR_LOG_FILE_NAME = "_Room_Error_Log";
	public static final String ROOM_ERROR_LOG_FILE_HEADING = "Heading";
	public static final String DATE_TIME_FORMAT = "yy-DD-MM HH:MM:SS";

	public static final String I_NA = "NA";
	public static final String I_USERS = "Users";
	public static final String I_ROOM_ID = "RoomId";
	public static final String I_ROOM_STATUS = "RoomStatus";
	public static final String I_HOST = "Host";
	public static final String I_USERNAME = "Username";
	public static final String I_CURRENT_ROUND = "CurrentRound";
	public static final String I_SKIP = "Skip";
	public static final String  I_UNSKIP = "Unskip";
	public static final String I_SKIP_ENTIRE_ROUND = "Skip Entire Round";
	public static final String I_SKIP_CURRENT_SET = "Skip Current Set";
	public static final String I_TEAMS_LIST = "csk|rcb|mi|gt|pbks|lsg|srh|rr|dc|kkr";
	
	public static final String I_PLAYER_NAME = "PlayerName";
	public static final String I_PLAYER_COUNTRY = "PlayerCountry";
	public static final String I_PLAYER_ROLE = "PlayerRole";
	public static final String I_BATTING_STYLE = "BattingStyle";
	public static final String I_BOWLING_STYLE = "BowlingStyle";
	public static final String I_BATTING_POSITION = "BattingPosition";
	public static final String I_BASE_PRICE_IN_LAKHS = "BasePrice_in_lakhs";
	public static final String I_BASE_PRICE_IN_CRORES = "BasePrice_in_crores";
	public static final String I_TOTAL_PRICE_IN_LAKHS = "TotalPrice_in_lakhs";
	public static final String I_TOTAL_PRICE_IN_CRORES = "TotalPrice_in_crores";
	
	public static final String I_PLAYER_NAME_HEADER = "Player Name";
	public static final String I_PLAYER_COUNTRY_HEADER = "Player Country";
	public static final String I_PLAYER_ROLE_HEADER = "Player Role";
	public static final String I_BATTING_STYLE_HEADER = "Batting Style";
	public static final String I_BOWLING_STYLE_HEADER = "Bowling Style";
	public static final String I_BATTING_POSITION_HEADER = "Batting Position";
	public static final String I_PRICE_IN_LAKHS_HEADER = "Price";
	public static final String I_PRICE_IN_CRORES_HEADER = "Price";

	public static final String I_START_STATUS = "Start";
	public static final String I_ONGOING_STATUS = "Ongoing";
	public static final String I_PAUSED_STATUS = "Paused";
	public static final String I_HALT_STATUS = "Halt";
	public static final String I_WAITING_FOR_ROUND2 = "Waiting For Round2";
	public static final String I_WAITING_FOR_ROUND3 = "Waiting For Round3";
	public static final String I_FINISHED_STATUS = "Finished";

	public static final String I_TEAM = "Team";
	public static final String I_MAX_FOREIGNERS = "MaxForeigners";
	public static final String I_MIN_TOTAL = "MinTotal";
	public static final String I_MAX_TOTAL = "MaxTotal";
	public static final String I_MAX_BUDGET = "MaxBudget";

	public static final String I_YES = "Yes";
	public static final String I_NO = "No";
	public static final String I_ROUND1 = "round1";
	public static final String I_ROUND2 = "round2";
	public static final String I_ROUND3 = "round3";
	public static final String I_SHORT_LIST = "ShortList";

	public static final String OK_MESSAGE = "Ok";

	// Exception messages

	public static final String COMMON_ERROR_MESSAGE = "Unexpected Error";
	public static final String ROOM_NOT_EXIST_MESSAGE = "Room does not exist";
	public static final String ROOM_NOT_FOUND_AVAILABLE = "No Room Available";
	public static final String ROOM_FULL_MESSAGE = "Room is Full";
	public static final String CANT_JOIN_ROOM_MESSAGE = "Sorry you cannot join the room";
	public static final String TEAM_ALREADY_SELECTED_MESSAGE = " is already Selected. Please select some other team";
	public static final String ALREADY_PAUSED_MESSAGE = "Auction is already Paused";
	public static final String ALREADY_PLAY_MESSAGE = "Auction is Ongoing";
	public static final String ALREADY_SKIPPED_MESSAGE = "Already Skipped";
	public static final String ALREADY_UNSKIPPED_MESSAGE = "Auction is already Unskipped";

}

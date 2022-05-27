package com.auction.virtualauctionserver.controller;

import java.sql.Connection;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auction.virtualauctionserver.functions.Auction;
import com.auction.virtualauctionserver.functions.AuctionRoom;
import com.auction.virtualauctionserver.functions.LoginRegister;
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
import com.auction.virtualauctionserver.model.RoomStatus;
import com.auction.virtualauctionserver.model.RoomStatusResponse;
import com.auction.virtualauctionserver.model.SkipInfo;
import com.auction.virtualauctionserver.model.Team;
import com.auction.virtualauctionserver.model.Username;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

@RestController
public class AuctionController {

	// @Autowired
	// PlayerSelected playerSelected;

	@PostMapping("/login/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage login(@RequestBody Login login) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = LoginRegister.login(login);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/register/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage register(@RequestBody Register register) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = LoginRegister.register(register);
		// con.close();

		return responseMessage;
	}

	//

	@PostMapping("/createroom/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public RoomInfo createRoom(@RequestBody Username username) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		RoomInfo roomInfo = AuctionRoom.createRoom(username);
		// con.close();

		return roomInfo;
	}
	
	@PostMapping("/joinrandomroom/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public RoomInfo joinRandomRoom(@RequestBody Username username) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		RoomInfo roomInfo = AuctionRoom.joinRandomRoom(username);
		// con.close();

		return roomInfo;
	}

	@PostMapping("/joinroom/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public RoomStatus joinRoom(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		RoomStatus roomStatusResponse = AuctionRoom.joinRoom(roomInfo);
		// con.close();

		return roomStatusResponse;
	}

	@PostMapping("/leaveroom/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage leaveRoom(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = AuctionRoom.leaveRoom(roomInfo);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/quitroom/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage quitRoom(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = AuctionRoom.quitRoom(roomInfo);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/roomstatus/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public RoomStatusResponse roomStatus(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		RoomStatusResponse roomStatusResponse = AuctionRoom.roomStatus(roomInfo);
		// con.close();

		return roomStatusResponse;
	}

	@PostMapping("/updateauctionparams/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage updateAuctionParams(@RequestBody AuctionParams auctionParams)
			throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = AuctionRoom.updateAuctionParams(auctionParams);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/startauction/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage startAuction(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = AuctionRoom.startAuction(roomInfo);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/playerstatus/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public PlayerStatus playerStatus(@RequestBody Team team) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		PlayerStatus playerStatus = Auction.auctionStatus(team);
		// con.close();

		return playerStatus;
	}

	@PostMapping("/bid/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage playerSelect(@RequestBody Bid price) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.bid(price);
		// con.close();
		return responseMessage;
	}

	@PostMapping("/skipsets/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage skipSets(@RequestBody SkipInfo skipInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.skipSets(skipInfo);
		// con.close();
		return responseMessage;
	}

	@PostMapping("/pauseauction/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage pauseAuction(@RequestBody Bid price) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.pauseAuction(price);
		// con.close();
		return responseMessage;
	}

	@PostMapping("/playauction/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage playAuction(@RequestBody Bid price) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.playAuction(price);
		// con.close();
		return responseMessage;
	}

	@PostMapping("/changehost/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage changeHost(@RequestBody Bid price) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.bid(price);
		// con.close();
		return responseMessage;
	}

	@PostMapping("/getusernames/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public NamesList getUsernames(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		NamesList namesList = Auction.getUsernamesList(roomInfo);
		// con.close();

		return namesList;
	}

	@PostMapping("/makehost/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage makeHost(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.makeHost(roomInfo);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/getunsoldplayerslist/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public NamesList getUnsoldPlayersList(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		NamesList namesList = Auction.getUnsoldPlayersList(roomInfo);
		// con.close();

		return namesList;
	}

	@PostMapping("/addunsoldplayers/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage addUnsoldPlayers(@RequestBody NamesList namesList)
			throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.addUnsoldPlayers(namesList);
		// con.close();

		return responseMessage;
	}

	@PostMapping("/auctionbreakstatus/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public RoomStatus auctionBreakStatus(@RequestBody RoomInfo roomInfo) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		RoomStatus roomStatus = Auction.auctionBreakStatus(roomInfo);
		// con.close();

		return roomStatus;
	}

	@PostMapping("/getcurrentsetplayerslist/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public NamesList getCurrentSetPlayersList(@RequestBody RoomInfo roomInfo)
			throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		NamesList namesList = Auction.getCurrentSetPlayersList(roomInfo);
		// con.close();

		return namesList;
	}

	@PostMapping("/gettotalunsoldplayerslist/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public PlayerInfoList getTotalUnsoldPlayersList(@RequestBody RoomInfo roomInfo)
			throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		PlayerInfoList playerInfoList = Auction.getTotalUnsoldPlayersList(roomInfo);
		// con.close();

		return playerInfoList;
	}

	@PostMapping("/getteamplayerslist/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public PlayerInfoList getTeamPlayersList(@RequestBody Team team) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		PlayerInfoList playerInfoList = Auction.getTeamPlayersList(team);
		// con.close();

		return playerInfoList;
	}
	
	@PostMapping("/addplayertoteamafterauction/") // http://localhost:8080/playerselect?set=set1_batsman&sessionId
	@ResponseBody
	public ResponseMessage addPlayerToTeamAfterAuction(@RequestBody PlayerName playerName) throws SQLException, ClassNotFoundException {

		// AuctionInterface auctionService = new AuctionService();

		// Connection con =
		// auctionService.getConnection("jdbc:mysql://localhost/virtualauction", "root",
		// "123456");
		ResponseMessage responseMessage = Auction.addPlayerToTeamAfterAuction(playerName);
		// con.close();

		return responseMessage;
	}

}

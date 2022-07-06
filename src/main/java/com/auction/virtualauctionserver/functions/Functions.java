package com.auction.virtualauctionserver.functions;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.auction.virtualauctionserver.model.ResponseMessage;
import com.auction.virtualauctionserver.model.RoomInfo;
import com.auction.virtualauctionserver.service.AuctionInterface;
import com.auction.virtualauctionserver.service.AuctionService;

public class Functions {

	public static int calculatePrice(int oldPrice) {

		int totalPrice = 0;

		int length = String.valueOf(oldPrice).length();
		if (length == 1) {

			totalPrice = oldPrice + 1;

		} else if (length == 2) {

			totalPrice = oldPrice + 5;

		} else if (length == 3 || length == 4) {

			if (oldPrice < 500) {

				if (oldPrice == 150) {
					totalPrice = oldPrice + 10;
				} else {
					totalPrice = oldPrice + 20;
				}

			} else {

				totalPrice = oldPrice + 50;

			}
		}

		return totalPrice;
	}

	public static String getRandomNumberString() {
		// It will generate 6 digit random Number.
		// from 0 to 999999
		Random rnd = new Random();
		int number = rnd.nextInt(999999);

		// this will convert any number sequence into 6 character.
		return String.format("%06d", number);
	}

	public static void sendOtpEmail(String emailId, int otp) throws Exception {
		final String username = "saurabhskhire@gmail.com";
		final String password = "nqbebxcawqvcjstl";

		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); // TLS

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("saurabhskhire@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailId));
			message.setSubject("Otp");
			message.setText("Your One time Password (Otp) for Ipl Auction App is " + otp);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public static void leaveInactiveUser(String roomId, String username, String lastDateStr) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date lastDate = sdf.parse(lastDateStr);
		Date currentDate = sdf.parse(LocalDateTime.now().toString());
		long diffrence = currentDate.getTime() - lastDate.getTime();

		//long diffrence = TimeUnit.MILLISECONDS.toMinutes(difference_In_Time) % 60;
		if (diffrence >= 120000) {

			RoomInfo roomInfo = new RoomInfo();
			roomInfo.setUsername(username);
			roomInfo.setRoomId(roomId);
			AuctionRoom.leaveRoom(roomInfo);

		}
	}

}

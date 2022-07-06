package com.auction.virtualauctionserver.functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtil {

	public static void createAndUpdateErrorLog(String roomId, String method, Exception exception) {

		File logFolder = new File(Constants.LOG_FOLDER_LOCATION);

		if (!logFolder.exists()) {
			logFolder.mkdir();
		}

		boolean roomLogFileExist = false;
		File roomLogFile = new File(Constants.LOG_FOLDER_LOCATION + "\\" + roomId + Constants.ROOM_ERROR_LOG_FILE_NAME);

		if (roomLogFile.exists()) {
			roomLogFileExist = true;
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				Constants.LOG_FOLDER_LOCATION + "\\" + roomId + Constants.ROOM_ERROR_LOG_FILE_NAME, true))) {

			if (!roomLogFileExist) {
				bufferedWriter.write(Constants.ROOM_ERROR_LOG_FILE_HEADING);
			}

			bufferedWriter.newLine();
			bufferedWriter.append(currentDateTime() + " " + method + " " + exception.getMessage());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String currentDateTime() {

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
		return dateTimeFormatter.format(LocalDateTime.now());
	}

}

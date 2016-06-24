/**
* Copyright (C) 2016 ShadowXCraft Server - All rights reserved.
*/

package net.shadowxcraft.rollbackcore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtilities {

	// Used to read a value to the file using the current storage way.
	public static int readShort(InputStream in) throws IOException {
		int temp = 0;

		temp += in.read() * 255;
		temp += in.read();

		return temp;
	}

	public static int readInt(InputStream in) throws IOException {
		int output = 0;
		for (int i = 0; i < 4; i++)
			output = ((in.read() & 0xFF) << i * 8) | output;
		return output;
	}

	public static void writeInt(OutputStream out, int input) throws IOException {
		for (int i = 0; i < 4; i++) {
			out.write((input >> i * 8) & 0xFF);
		}
	}

	// Used to write a value to the file using the current storage way.
	// Stores an unsigned short.
	public static void writeShort(OutputStream out, int input) throws IllegalArgumentException, IOException {
		if (input > 65280) {
			throw new IllegalArgumentException("Input " + input + " is out of the legal range (0 to 65,280)");
		}

		out.write(input / 255);
		out.write(input % 255);
	}

	public static void writeIDAndData(OutputStream out, int id, byte data)
			throws IllegalArgumentException, IOException {
		int storedValue = 0;
		storedValue = (short) (id << 4);
		storedValue = (short) (storedValue | (data & 15));
		writeShort(out, storedValue);
	}

	//public static void readIDAndData(InputStream in) throws IOException {
	//	int storedValue = readShort(in);
	//	System.out.println("ID: " + (storedValue >> 4));
	//	System.out.println("DATA: " + (storedValue & 15));
	//	System.out.println("\n");
	//}

	// DEBUG USE ONLY. WILL SPAM CONSOLE!
	public static void displayEntireFile(File file) {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			String total = "";
			while (stream.available() > 0) {
				total += stream.read() + " ";
			}
			Main.plugin.getLogger().info(total);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

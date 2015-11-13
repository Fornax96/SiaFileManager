package nl.fornax;

import java.util.Random;

/**
 * @author Fornax
 */
public class RandString {
	private static final String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
	
	public static String generate(int len){
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		
		// The chars String contains 64 characters. Currently, the file IDs are 6 chars 
		// long. This means that there are 64^6 = 68 719 476 736 unique file IDs available!
		for(int i = 0; i < len; i++){
			sb.append(
				chars.charAt(
					rand.nextInt(chars.length())
				)
			);
		}
		
		return sb.toString();
	}
}
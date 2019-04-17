/*
 * Copyright (c) 2019. zhangbohan.dell@gmail.com ,All Rights Reserved
 *
 */

package xyz.zhangbohan.hive.tools;

/**
 * file encoding: utf-8
 * Function :
 * Create : 4/16/2019 8:28 PM
 *
 * @author : zhangbohan.dell@gmail.com
 * @version : 1.0
 */
public class StringUtils {
	public static String upperAndChangeUnderline(String source) {
		char[] chars = source.toCharArray();
		if (chars[0] >= 'a' && chars[0] <= 'z') {
			chars[0] -= 32;
		}
		boolean flag = false;
		for (int i = 0; i < chars.length; i++) {
			if ('_'==chars[i]) {
				flag = true;
			}

			if (flag) {
				if (chars[i] >= 'a' && chars[i] <= 'z') {
					chars[i] -= 32;
					flag = false;
				}
			}
		}

		return new String(chars).replace("_", "");
	}
}

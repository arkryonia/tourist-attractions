/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.utils;

import java.util.Vector;
import javax.microedition.lcdui.Font;

/**
 * Utility for wrapping text to a certain width.
 */
public final class TextWrapper {

    /**
     * Wraps text
     *
     * @param text Text to be wrapped
     * @param wrapWidth Max width of one line in pixels
     * @param font Font to be used in calculating
     * @return
     */
    public static Vector wrapTextToWidth(String text, int wrapWidth,
            Font font) {
        if (wrapWidth < 20) {
            wrapWidth = 240;
        }

        Vector lines = new Vector();

        int start = 0;
        int position = 0;
        int length = text.length();
        int minCharWidth = font.charWidth('.');
        int minLineChars = wrapWidth / minCharWidth;

        while (position < length - 1) {
            start = position;
            position += minLineChars;
            if (position >= (length - 1)) {
                position = (length - 1);
            }
            for (;; position--) {
                char tmp = text.charAt(position);
                if (position == (length - 1) || tmp == ' ' || tmp == '\n') {
                    String s = (position == length - 1) ?
                            text.substring(start) :
                            text.substring(start, position);
                    if (font.stringWidth(s) <= wrapWidth) {
                        lines.addElement(s);
                        position += 1;
                        break;
                    }
                }
            }
        }

        return lines;
    }
}

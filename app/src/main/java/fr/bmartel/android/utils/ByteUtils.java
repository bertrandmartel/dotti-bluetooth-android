/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Bertrand Martel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.bmartel.android.utils;

import java.math.BigInteger;

/**
 * dealing with bytes
 *
 * @author  Bertrand Martel
 *
 */
public class ByteUtils {

    /**
     *
     * Convert integer to byte array of size 4
     *
     * @param i
     *            integer to convert
     * @return
     */
    public static byte[] convertIntToByte4Array(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i >> 0);
        return result;
    }

    public static byte[] convertIntToByteArray(int arg) {
        BigInteger bigInt = BigInteger.valueOf(arg);
        return bigInt.toByteArray();
    }
    /**
     *
     * Convert a byte array to integer
     *
     * @param array
     *            byte array
     * @return integer value
     */
    public static int convertByteArrayToInt(byte[] array) {
        int ret = 0;
        for (int i = 0; i < array.length; i++) {
            ret += (array[i] & 0xFF) << ((array.length - 1 - i) * 8);
        }
        return ret;
    }

    /**
     * Convert Little indian byte array to big endian byte array
     *
     * @param data
     * @return
     */
    public static byte[] convertLeToBe(byte[] data) {
        byte[] temp = new byte[data.length];

        for (int i = data.length - 1; i >= 0; i--) {
            temp[data.length - 1 - i] = data[i];
        }
        return temp;
    }


    /**
     * Build a byte array to string with a prefix message
     *
     * @param message
     *            message to print before byte array
     * @param array
     *            byte array to print
     * @param separator
     *            separator between byte values
     * @return string message
     */
    public static String byteArrayToStringMessage(String message, byte[] array,
                                                  char separator) {
        String log = "";
        if (!message.equals(""))
            log = message + " : ";
        if (array != null) {
            for (int count = 0; count < array.length; count++) {
                if (count == 0) {
                    log += (ByteUtils.convertFromIntToHexa(array[count])
                            + " " + separator + " ");
                } else if (count != array.length - 1) {
                    log += (ByteUtils.convertFromIntToHexa(array[count])
                            + " " + separator + " ");
                } else {
                    log += (ByteUtils.convertFromIntToHexa(array[count]));
                }
            }
        }

        return log;
    }
    /**
     * Convert from int data into String hexadecimal (ex 255 => "0xFF")
     *
     * @param data
     *            data to convert into hexa
     * @return
     *
     *         data converted into hexa
     */
    public static String convertFromIntToHexa(byte data) {
        int dataTmp = data & 0xFF;
		/* Put character in uppercase */
        String value = Integer.toHexString(dataTmp).toUpperCase();
		/* Add 0 if length equal to 1 */
        if (value.length() == 1) {
            value = "0" + value;
        }
        return value;
    }
}

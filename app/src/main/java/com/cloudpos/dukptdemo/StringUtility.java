
package com.cloudpos.dukptdemo;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class StringUtility
{



    /**
     * @author 杨工
     * @param strInput 传入String
     * @return boolean 传入的String是否为空
     */
    static public boolean isEmpty(String strInput)
    {
        /*
         * if(strInput == null) return true; return strInput.length() == 0 ?
         * true : false;
         */
        return TextUtils.isEmpty(strInput);

    }

    static protected boolean CheckByte(byte byteIn)
    {
        // '0' - '9'
        if (byteIn <= 0x39 && byteIn >= 0x30)
            return true;
        // 'A' - 'F'
        if (byteIn <= 0x46 && byteIn >= 0x41)
            return true;
        // 'a' - 'f'
        if (byteIn <= 0x66 && byteIn >= 0x61)
            return true;
        return false;
    }

    public static String byteArray2String(byte[] arryByte){
        String strOut = new String();
        for (int i = 0; i < arryByte.length; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }


    static protected boolean CheckString(String strInput)
    {
        strInput = strInput.trim();
        if (strInput.length() != 2)
            return false;
        byte[] byteArry = strInput.getBytes();
        for (int i = 0; i < 2; i++)
        {
            if (!CheckByte(byteArry[i]))
                return false;
        }
        return true;
    }

    static protected byte StringToByte(String strInput)
    {
        byte[] byteArry = strInput.getBytes();
        for (int i = 0; i < 2; i++)
        {

            if (byteArry[i] <= 0x39 && byteArry[i] >= 0x30)
            {
                byteArry[i] -= 0x30;
            }
            else if (byteArry[i] <= 0x46 && byteArry[i] >= 0x41)
            {
                byteArry[i] -= 0x37;
            }
            else if (byteArry[i] <= 0x66 && byteArry[i] >= 0x61)
            {
                byteArry[i] -= 0x57;
            }
        }
        // Log.i("APP", String.format("byteArry[0] = 0x%X\n", byteArry[0]));
        // Log.i("APP", String.format("byteArry[1] = 0x%X\n", byteArry[1]));
        return (byte) ((byteArry[0] << 4) | (byteArry[1] & 0x0F));
    }

    /**
     * @author 杨工
     * @param  strInput
     * @param  arryByte
     * @return int
     */
    static public int StringToByteArray(String strInput, byte[] arryByte)
    {
        strInput = strInput.trim();// 清除空白
        String[] arryString = strInput.split(" ");
        if (arryByte.length < arryString.length)
            return -1;
        for (int i = 0; i < arryString.length; i++)
        {
            if (!CheckString(arryString[i]))
                return -1;
            arryByte[i] = StringToByte(arryString[i]);
            // Log.i("APP", String.format("%02X", arryByte[i]));
        }

        return arryString.length;
    }

	static public byte[] StringToCreateByteArray(String strInput)
	{
        byte[] arryByte = null;
        try {
            strInput = strInput.trim();
            if (strInput.isEmpty()) {
//			arryByte = new byte[1];
                return null;
            }
            String[] arryString = strInput.split(" ");
            if (arryString.length == 0) {
//			arryByte = new byte[1];
                return null;
            }
            arryByte = new byte[arryString.length];
            for(int i = 0; i < arryString.length; i++)
            {
                Log.d("123", "String " + i + " = " + arryString[i]);
                if(!CheckString(arryString[i])) {
                    Log.d("123", "check null");
                    return null;
                }
                arryByte[i] = StringToByte(arryString[i]);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
		return arryByte;
	}

	static public byte[] StringToBCD(String s) {
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }

    static public String ByteArrayToString(byte[] arryByte, int nDataLength)
    {
        String strOut = new String();
        for (int i = 0; i < nDataLength; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }

    static public String ByteArrayToString(byte[] arryByte)
    {
        if (arryByte == null || arryByte.length == 0) {
            return "";
        }
        int nDataLength = arryByte.length;
        String strOut = new String();
        for (int i = 0; i < nDataLength; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }
    
    static public String ByteArrayToString(byte[] arryByte, int offset, int nDataLength)
    {
        String strOut = "";
        try {
            for(int i = offset; i < nDataLength; i++){
                strOut += String.format("%02X ", arryByte[i]);
            }
        } catch (Exception e) {
            strOut = "";
        }
        return strOut;
    }

    public static byte[] subBuffer(byte[] buf, int length) {
        byte[] result = new byte[length];
        if (buf.length >= length) {
            for (int i = 0; i < length; i++) {
                result[i] = buf[i];
            }
        }
        return result;
    }

    public static boolean compareByte(byte[] srcByte, byte[] dstByte) {
        Log.d("123", "src : " + ByteArrayToString(srcByte) + "\ndst : " + ByteArrayToString(dstByte));
        for (int i = 0; i < srcByte.length; i++) {
            if (srcByte[i] != dstByte[i]) {
                Log.d("123", "compare i : " + i + " |src = " + srcByte[i] + " |dst = " + dstByte[i]);
                return false;
            }
        }
        return true;
    }

    /**
     * @author john.li
     * @param  str 传入字符串
     * @param  reg 按照哪种方式或哪个字段拆分
     * @return Stringp[] 返回拆分后的数组。
     */
    static public String[] spiltStrings(String str, String reg) {
        String[] arrayStr = str.split(reg);
        return arrayStr;
    }
}

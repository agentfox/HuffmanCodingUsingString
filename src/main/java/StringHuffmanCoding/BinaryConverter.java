package StringHuffmanCoding;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BinaryConverter {
  public static String toBinaryString(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      String abc = toBinaryString(b);
      result.append(abc);
    }
    return result.toString();
  }

  public static String toBinaryString(byte b) {
    return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
  }

  public static String toBinaryStringFromFile(String fileName) throws IOException {
    BufferedInputStream filein = new BufferedInputStream(new FileInputStream(fileName));
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (true) {
      int b = filein.read();
      if (b == -1) {
        break; // end of input stream
      } else {
        buffer.write(b);
      }
    }
    byte[] bytes = buffer.toByteArray();
    return toBinaryString(bytes);
  }

  public static String integerToString(int num, int fixedNumberOfByte) {
    String convertedNum = Integer.toBinaryString(num);
    int numOfBits = fixedNumberOfByte * 8;
    StringBuilder padding = new StringBuilder();
    if (numOfBits > convertedNum.length()) {
      padding.append("0".repeat(numOfBits - convertedNum.length()));
    }
    return padding + convertedNum;
  }

  public static byte[] convertBinaryStringToBytes(String binaryString) {

    if (binaryString.length() == 0) {
      byte[] emptyByteArr;
      emptyByteArr = new byte[] {};
      return emptyByteArr;
    }
    int numOfPaddingBits = binaryString.length() % 8;
    if (numOfPaddingBits > 0) {
      binaryString = binaryString + "0".repeat(8 - numOfPaddingBits);
    }
    byte[] res = new byte[binaryString.length() / 8];
    for (int i = 0; i < binaryString.length() / 8; i++) {
      String byteString = binaryString.substring(i * 8, (i + 1) * 8);
      int val = Integer.parseInt(byteString, 2);
      res[i] = (byte) val;
    }
    return res;
  }

  public static int byteToInt(byte[] bytes, int length) {
    int val = 0;
    if(length>4) throw new RuntimeException("Too big to fit in int");
    for (int i = 0; i < length; i++) {
      val=val<<8;
      val=val|(bytes[i] & 0xFF);
    }
    return val;
  }

  public static void main(String[] args) throws IOException {
    //    BufferedOutputStream fileout = new BufferedOutputStream(new
    // FileOutputStream("abcjdb.txt"));
    //    fileout.write(65);
    //    fileout.write(92);
    //    fileout.write(90);
    //    fileout.write(10);
    //    fileout.write(66);
    //    fileout.close();

    String result = BinaryConverter.toBinaryStringFromFile("abcjdb.txt");

    System.out.println(result);
  }
}

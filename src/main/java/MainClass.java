import java.io.File;
import java.io.FileInputStream;import java.io.IOException;

public class MainClass {
  public static void main(String args[]) throws IOException {
    String compressedFile = "triangles.txt.huff";
    String fileName = "tri.txt";

//    File jabber =  new File(compressedFile);
//    FileInputStream testin = new FileInputStream(jabber);
//    BitReader testBR = new BitReader(testin);
//    Huffman testHF = new Huffman(testBR);
//    //testHF.decode(testHF.bytes, testBR, System.out);
//    testHF.decompress(compressedFile);
//    testin.close();

    File jabber2 =  new File(compressedFile);
    FileInputStream testout = new FileInputStream(jabber2);
    BitReader testBR2 = new BitReader(testout);
    Huffman testHF2 = new Huffman(testBR2);
    testHF2.compress(fileName);
    testout.close();


  }
}

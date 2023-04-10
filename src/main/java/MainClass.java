import BinaryHuffmanCoding.Huffman;
import StringHuffmanCoding.MyHuffman;
import java.io.IOException;

public class MainClass {
  public static void main(String[] args) throws IOException {
    String toCompressFile = "triangles.txt";
    String compressedFile = "triangles.txt.huff";

    String toCompressFile2 = "square.txt";
    String compressedFile2 = "square.txt.huff";

    Huffman.decompress(compressedFile2);
    Huffman.compress(toCompressFile);

    // can use xxd command in git bash to show binary content of file(xxd -b triangles.txt.huff)

    MyHuffman.compressFile(toCompressFile);
    MyHuffman.decompressFile(compressedFile);
    MyHuffman.compressFile(toCompressFile2);
    MyHuffman.decompressFile(compressedFile2);
  }
}

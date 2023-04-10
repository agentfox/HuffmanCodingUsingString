package StringHuffmanCoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class MyHuffman {
  public static final String HUFF_EXT = ".huff";

  public static void compressFile(String filename) throws IOException {
    String out = filename + HUFF_EXT;
    try (BufferedInputStream inputFile = new BufferedInputStream(new FileInputStream(filename));
        BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(out))) {
      compress(inputFile, outputFile);
    }
    // close streams, even if an IOException flies by
  }

  public static void compress(BufferedInputStream inputFile, BufferedOutputStream outputFile)
      throws IOException {
    // read the file, storing it in a byte array buffer
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (true) {
      int b = inputFile.read();
      if (b == -1) {
        break; // end of input stream
      } else {
        buffer.write(b);
      }
    }
    byte[] bytes = buffer.toByteArray();
    byte[] result = compress(bytes);
    outputFile.write(result);
  }

  public static byte[] compress(byte[] bytes) {
    MyHuffmanNode<Byte> root;
    // create frequency table for bytes
    Map<Byte, Integer> values = new HashMap<>();
    for (byte b : bytes) {
      if (values.containsKey(b)) {
        values.put(b, values.get(b) + 1);
      } else {
        values.put(b, 1);
      }
    }

    // load a priority queue with TreeNodes of all unique byte values &
    // corresponding counts
    Queue<MyHuffmanNode<Byte>> pq = new PriorityQueue<>();
    for (Byte b : values.keySet()) {
      pq.add(new MyHuffmanNode<>(b, values.get(b)));
    }
    // construct huffman tree
    while (pq.size() > 1) {
      pq.offer(new MyHuffmanNode<>(pq.poll(), pq.poll()));
    }
    root = pq.poll();

    // write length of original data as first 4 bytes
    String res = "";
    res += BinaryConverter.integerToString(bytes.length, 4);

    // write huffman tree
    String tree = convertTreeStructureToBinaryString(root);
    res += tree;

    // write encoded bytes
    String encodedBytes = encodeBytes(bytes, root);
    res += encodedBytes;
    return BinaryConverter.convertBinaryStringToBytes(res);
  }

  public static void decompressFile(String filename) throws IOException {
    if (!filename.endsWith(HUFF_EXT)) {
      throw new IllegalArgumentException(filename + " does not end in " + HUFF_EXT);
    }
    String out = filename.substring(0, filename.lastIndexOf(HUFF_EXT));
    try (BufferedInputStream inputFile = new BufferedInputStream(new FileInputStream(filename));
        BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(out))) {
      decompress(inputFile, outputFile);
    }
    // close streams, even if an IOException flies by
  }

  public static void decompress(BufferedInputStream inputFile, BufferedOutputStream outputFile)
      throws IOException {
    // read the file, storing it in a byte array buffer
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (true) {
      int b = inputFile.read();
      if (b == -1) {
        break; // end of input stream
      } else {
        buffer.write(b);
      }
    }
    byte[] bytes = buffer.toByteArray();
    byte[] result = decompress(bytes);
    outputFile.write(result);
  }

  public static byte[] decompress(byte[] bytes) {
    byte[] lengthBytes = getSliceOfArray(bytes, 0, 3);
    int bytesLength = BinaryConverter.byteToInt(lengthBytes, 4);

    // reconstruct huffman tree
    byte[] remainingBytes = getSliceOfArray(bytes, 4);
    String remaining = BinaryConverter.toBinaryString(remainingBytes);
    StringBuilder sb = new StringBuilder(remaining);
    MyHuffmanNode<Byte> root = reconstructByteTree(sb, 0);
    return decode(bytesLength,sb, root);
  }

  private static MyHuffmanNode<Byte> reconstructByteTree(StringBuilder bits, int nodeLevel) {
    if (bits.charAt(0) == '1') {
      String byteStr = bits.substring(1, 9);
      bits.delete(0, 9);
      Byte b = BinaryConverter.convertBinaryStringToBytes(byteStr)[0];
      return new MyHuffmanNode<>( b, nodeLevel);
    } else {
      nodeLevel++;
      bits.deleteCharAt(0);
      return new MyHuffmanNode<>(
          reconstructByteTree(bits, nodeLevel), reconstructByteTree(bits, nodeLevel));
    }
  }

  private static byte[] decode( int numberOfByte,  StringBuilder bits, MyHuffmanNode<Byte> node) {
    byte[] res = new byte[numberOfByte];
    for(int i=0;i<numberOfByte;i++){
      res[i] = codeToByte(node, bits);
    }
    return res;
  }

  private static byte codeToByte( MyHuffmanNode<Byte> node, StringBuilder bits ) {
    if(node.getData() != null){
      return node.getData();
    }
    else{
      if(bits.charAt(0) == '1'){
        bits.deleteCharAt(0);
        return codeToByte(node.getRight(), bits);
      }
      else{
        bits.deleteCharAt(0);
        return codeToByte(node.getLeft(), bits);
      }
    }
  }

  private static String encodeBytes(byte[] bytes, MyHuffmanNode<Byte> root) {
    StringBuilder encodedBytes = new StringBuilder();
    Map<Byte, String> dict = new HashMap<>();
    loadBytePaths(dict, root, new ArrayDeque<>());
    for (byte b : bytes) {
      String path = dict.get(b);
      encodedBytes.append(path);
    }
    return encodedBytes.toString();
  }

  private static void loadBytePaths(
      Map<Byte, String> paths, MyHuffmanNode<Byte> node, Deque<String> path) {
    if (node == null) {
      assert false : "Fell off the tree, which should never happen.";
    } else if (node.getData() == null) { // this is an internal node
      // first, go left
      path.addLast("0"); // 0
      loadBytePaths(paths, node.getLeft(), path);
      path.removeLast();

      // now go right
      path.addLast("1"); // 1
      loadBytePaths(paths, node.getRight(), path);
      path.removeLast();
    } else {
      // a leaf node, so save copy of path into paths map
      paths.put(node.getData(), String.join("", new ArrayList<>(path)));
    }
  }

  private static String convertTreeStructureToBinaryString(MyHuffmanNode<Byte> node) {
    String tree = "";
    if (node == null) {
      return "";
    } else {
      if (node.getData() == null) {
        // internal node
        tree += "0";
      } else {
        // leaf node
        tree += "1";
        String b = BinaryConverter.toBinaryString(node.getData());
        tree += b;
      }
      tree += convertTreeStructureToBinaryString(node.getLeft());
      tree += convertTreeStructureToBinaryString(node.getRight());
    }
    return tree;
  }
  public static byte[] getSliceOfArray(byte[] arr, int start) {
    return getSliceOfArray(arr, start, arr.length - 1);
  }

  public static byte[] getSliceOfArray(byte[] arr, int start, int end) {
    byte[] slice = new byte[end + 1 - start];
    System.arraycopy(arr,start,slice,0,slice.length);
    return slice;
  }
}

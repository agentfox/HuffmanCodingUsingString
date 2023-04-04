import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Models a Huffman tree and provides both Huffman coding and decoding services.
 * <p>
 * This Huffman tree is not generic; it only works with bytes.  All Huffman
 * instances are full trees (meaning every node has 0 or 2 children).  They
 * have at least one internal node.  That is, the root node may never also be
 * a leaf node.
 * <p>
 * When encoding, a Huffman tree writes the following data to the output stream:
 * <ul>
 * <li>the number of bytes to be encoded (as an int, so 4 bytes, big-endian)
 * <li>the Huffman tree used for the encoding.  This is specified using a
 * pre-order DFS traversal of the tree, indicating each internal nodes with a
 * 0 bit and each leaf node a 1 bit followed immediately by the byte (8 bit)
 * value in that leaf node.
 * <li>the encoding data, which consists of variable-length prefix-free codes
 * describing paths through the given Huffman tree to the the leaf node
 * corresponding to each original data byte.
 * </ul>
 * <p>
 * When decoding, the input stream must provide the data in exactly the format
 * as output by the encoding algorithm.  If not, the resulting behavior is
 * undefined.
 *
 * @author Zach Tomaszewski
 * @since  15 Nov 2012
 */
public class Huffman {

  public static final String HUFF_EXT = ".huff";
  private HuffmanNode<Byte> root;
  public int bytes;//keeps the number of bytes available


  /**
   * Builds a Huffman tree suitable for encoding the given byte array.
   * <p>
   * The tree will contain 1 leaf node for each unique byte value and each
   * leaf will contain a count of that byte value's frequency in the data.
   * <p>
   * If the byte array is of size 0, no tree is constructed (ie, the root
   * will still be null.)
   * <p>
   * The root node cannot be a leaf node.  This is because the encoding is
   * comprised of left and right movements from the root, and so no bit
   * pattern corresponds to the root node itself.  Therefore, if there
   * is only a single unique byte, an extra dummy leaf node with a byte
   * value of 0 and a count of 0 will be created but not used.
   *
   * @param bytes  The complete data to be encoded using this tree.
   */
  public Huffman(byte[] bytes) {
    // map each byte value to its frequency count
    Map<Byte, Integer> values = new HashMap<Byte, Integer>();
    for (byte b : bytes) {
      if (values.containsKey(b)) {
        values.put(b, values.get(b) + 1);  //add one to current count
      }else {
        values.put(b, 1);
      }
    }

    // load a priority queue with TreeNodes of all unique byte values &
    // corresponding counts
    Queue<HuffmanNode<Byte>> pq = new PriorityQueue<HuffmanNode<Byte>>();
    for (Byte b : values.keySet()) {
      pq.add(new HuffmanNode<Byte>(b, values.get(b)));
    }

    while (pq.size() < 2) {
      // add an extra dummy leaf node with count of 0
      pq.add(new HuffmanNode<Byte>((byte) 0, 0));
    }

    // build tree of nodes by grabbing next two smallest counts from front of
    // of PQ, building a parent node to connect the two, and putting the new
    // parent back into the PQ.  Repeat until only one node left: root of the
    // new Huffman tree
    while (pq.size() > 1) {
      pq.offer(new HuffmanNode<Byte>(pq.poll(), pq.poll()));
    }

    // store root of finished tree (maybe to null if pq is empty)
    this.root = (pq.size() > 0) ? pq.poll() : null;
  }

  /**
   * Builds a Huffman tree as read from the given input bit stream.
   * <p>
   * The stream must be at the start of the tree data, after the byte count
   * header.  Reads from the stream until the end of the tree, which leaves
   * the given BitReader at the start of the encoding data.
   *
   * @throws IOException If cannot read from stream.
   */
  public Huffman(BitReader input) throws IOException {

/**The public Huffman(BitReader input) constructor.
 * The constructor initializes the Huffman tree starting with the pre-order traversal of the Huffman tree.
 * You should create a private recursive method that returns the root of the Huffman Tree and call this method in the constructor.
 * Look at the constructors for the HuffmanNode class (especially public HuffmanNode(E data, int count) and public HuffmanNode(HuffmanNode<E> left, HuffmanNode<E> right)).
 * Nodes in a Huffman Tree are either internal nodes with no data or leaf nodes, with no children, holding the data.
 */
    String byteLength = "";
    String curByte = "";
    Integer treeLength = input.readInt();
    bytes = treeLength;
    root = inputToTreeBuild(input,0);
  }


  /**A recursive helper method to create a huffman tree from the input stream
   *
   * @param input
   * @param internalNodeLevel the level of an internal node
   * @return a new internal or leaf node
   */
  private HuffmanNode<Byte> inputToTreeBuild(BitReader input,int internalNodeLevel){
    if(input.read()){

      Byte data = input.readByte();
      return new HuffmanNode(data,internalNodeLevel);
    }
    else{
      internalNodeLevel++;
      return  new HuffmanNode(inputToTreeBuild(input,internalNodeLevel),inputToTreeBuild(input,internalNodeLevel));
    }
  }

  /**A method to find the byte value of a huffman code
   *
   * @param node
   * @param input
   * @return
   */
  private Byte codeToByte(HuffmanNode<Byte> node, BitReader input){
    if(node.getData()!=null){
      return node.getData();
    }
    else{
      if(input.read()){
        return codeToByte(node.getRight(), input);
      }
      else{
        return codeToByte(node.getLeft(), input);
      }
    }
  }

  /**A method to write an input stream code to a string
   *
   * @param input
   * @param byteNum
   * @return
   */
  public String codeToString(BitReader input, int byteNum){
    String out = "";
    for(int i=0;i<byteNum;i++){
      out = out + String.valueOf(Character.toChars(codeToByte(root, input)));
    }
    return out;
  }

  /**
   * Reads bits from the given reader, decoding the given number of
   * byte values before stopping.  Writes decoded bytes to the given
   * output stream.
   *
   * @param bytes  The number of value to decode according to this tree
   * @param in     The reader to read bits from
   * @param out    Where to write decode byte values
   * @throws IOException  If can't read/write from/to streams
   */
  public void decode(int bytes, BitReader in, OutputStream out)
      throws IOException {

    for(int i=0;i<bytes;i++){
      out.write(codeToByte(root, in));
    }
  }

  /**
   * Encodes the given bytes based on this tree's structure, writing the
   * resulting bits to the given output stream.
   *
   * @throws IOException  If there is a problem writing to stream.
   */
  public void encode(byte[] bytes, BitWriter out) throws IOException {
    if (this.root == null) {
      return;  //can't encode anything
    }

    // get a dictionary mapping of byte values to bit-paths to leaf node
    Map<Byte, List<Boolean>> dict = new HashMap<Byte, List<Boolean>>();
    loadPaths(dict, this.root, new ArrayDeque<Boolean>());

    // use map to write out encoded bytes
    for (byte b : bytes) {
      List<Boolean> path = dict.get(b);
      for (boolean bit : path) {
        out.write(bit);
      }
    }
  }

  /**
   * Loads the given map with paths through this tree to each unique leaf-node
   * byte value.  Paths are given as false and true booleans for 0/left and
   * 1/right, respectively.
   *
   * @param paths  The map to load
   * @param node   The current node to consider in a path from root to leaf
   * @param path   The path so far from root to the current node; should be
   *               empty in the initial call to this recursive method.
   */
  private static void loadPaths(Map<Byte, List<Boolean>> paths,
      HuffmanNode<Byte> node, Deque<Boolean> path) {
    if (node == null) {
      assert false: "Fell off the tree, which should never happen.";
      return;
    }else if (node.getData() == null) { //this is an internal node
      //first, go left
      path.addLast(false);  //0
      loadPaths(paths, node.getLeft(), path);
      path.removeLast();

      //now go right
      path.addLast(true);  //1
      loadPaths(paths, node.getRight(), path);
      path.removeLast();
      return;
    }else {
      //a leaf node, so save copy of path into paths map
      paths.put(node.getData(), new ArrayList<Boolean>(path));
    }
  }

  /**
   * Writes this tree in byte-encoded form to the given bit writer.
   */
  public void write(BitWriter out) throws IOException {
    write(this.root, out);
  }

  private static void write(HuffmanNode<Byte> node, BitWriter out)
      throws IOException {
    if (node == null) {
      return;
    }else {
      if (node.getData() == null) {
        //internal node
        out.write(0);
      }else {
        //leaf node
        out.write(1);
        out.writeByte(node.getData());
      }
      write(node.getLeft(), out);
      write(node.getRight(), out);
    }
  }

  /**
   * Returns a multiline pre-order traversal this Huffman tree.
   */
  @Override
  public String toString() {
    return (this.root == null) ? "" : this.root.toFullString("|");
  }


  //--static methods--

  /**
   * Compresses the file named by the given filename.  Produces the output
   * filename by appending ".huff" to the given filename.
   *
   * @param filename  Name of the file to compress.
   * @throws IOException If cannot read/write files.
   *
   * @see #compress(InputStream, OutputStream)
   */
  public static void compress(String filename) throws IOException {
    String out = filename + HUFF_EXT;
    BufferedInputStream filein = new BufferedInputStream(
        new FileInputStream(filename));
    BufferedOutputStream fileout = new BufferedOutputStream(
        new FileOutputStream(out));
    try {
      compress(filein, fileout);
    }finally {
      //close streams, even if an IOException flies by
      filein.close();
      fileout.close();
    }
  }

  /**
   * Compresses the given input stream, writing to the given output stream.
   * <p>
   * Writes all required parts of the output file format: the byte count
   * header, the encoded tree, and then the encoded data.
   *
   * @throws IOException  If there are any read/write error.
   */
  public static void compress(InputStream in, OutputStream out)
      throws IOException {

    //read the file, storing it in a byte array buffer
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    while (true) {
      int b = in.read();
      if (b == -1) {
        break;  //end of input stream
      }else {
        buffer.write(b);
      }
    }

    //build a tree from the bytes
    byte[] bytes = buffer.toByteArray();
    Huffman tree = new Huffman(bytes);
    //debug check:
    //System.out.println(tree);

    //write output, starting with byte count as a 4-byte int
    for (int i = 3; i >= 0; i--) {
      out.write(bytes.length >> (i * 8));
    }
    BitWriter bitStream = new BitWriter(out);
    tree.write(bitStream);
    tree.encode(bytes, bitStream);
    bitStream.flush();
  }

  /**
   * Decompresses the file named by the given filename.  Produces the output
   * filename by removing ".huff" from the given filename.
   *
   * @throw IOException  If cannot read/write the files.
   * @throw IllegalArgumentException  If filename does not end in HUFF_EXT
   */
  public static void decompress(String filename) throws IOException {
    if (!filename.endsWith(HUFF_EXT)) {
      throw new IllegalArgumentException(filename + " does not end in " +
          HUFF_EXT);
    }
    String out = filename.substring(0, filename.lastIndexOf(HUFF_EXT));
    BufferedInputStream filein = new BufferedInputStream(
        new FileInputStream(filename));
    BufferedOutputStream fileout = new BufferedOutputStream(
        new FileOutputStream(out));
    try {
      decompress(filein, fileout);
    }finally {
      //close streams, even if an IOException flies by
      filein.close();
      fileout.close();
    }
  }

  /**
   * Decompresses the given input stream, writing to the given output stream.
   *
   * @throws IOException  If there are any read/write error.
   */

  public static void decompress(InputStream in, OutputStream out)
      throws IOException {
    //read in byte count from input stream
    //wrap input stream in a BitReader
    BitReader br =  new BitReader(in);

    //use tree to decode given number of byte from bitreader
    Huffman tree = new Huffman(br);
    tree.decode(tree.bytes, br, out);
  }
  /**An early method to make a tree from just an input streamo
   *
   * @param input
   */
  private void inputToTreeString(BitReader input){
    System.out.println(inputToTreeStringBuild(input,"",0));
  }
  /**
   *
   * @param input
   * @param nodeChain
   * @param internalNodeLevel
   * @return
   */
  private String inputToTreeStringBuild(BitReader input, String nodeChain,int internalNodeLevel){
    if(input.read()){

      Byte data = input.readByte();
      return
          //nodeChain +
          data.toString();
    }
    else{

      nodeChain = nodeChain + internalNodeLevel + ":";
      return  inputToTreeStringBuild(input, nodeChain,(internalNodeLevel+1)) +
          //"\n" +
          "," +
          inputToTreeStringBuild(input, nodeChain,(internalNodeLevel+1));
    }
  }
  /**A method to write the huffman tree of the class to a string
   *
   * @return
   */
  public String huffmanTreeToString(){

    return huffmanTreeToString(root, "" ,0);
  }
  /**A helpermethod for wroting the huffman tree to a string
   *
   * @param in
   * @return
   */
  private String huffmanTreeToString(HuffmanNode<Byte> in, String nodeChain,int internalNodeLevel){
    if(in.getData()!=null){
      String outValue = String.valueOf(Character.toChars(in.getData())[0]);
      if(outValue.matches("\\s")){
        outValue = Character.getName(in.getData());
      }
      return nodeChain + in.getData() + "= " + outValue;
    }
    else{
      nodeChain = nodeChain + internalNodeLevel + ":";
      return huffmanTreeToString(in.getLeft(), nodeChain, ++internalNodeLevel) + "\n" + huffmanTreeToString(in.getRight(), nodeChain, ++internalNodeLevel);
    }
  }
}
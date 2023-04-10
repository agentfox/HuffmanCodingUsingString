package StringHuffmanCoding;

public class MyHuffmanNode<E> implements Comparable<MyHuffmanNode<E>> {
  private E data;
  private int count;
  private MyHuffmanNode<E> left;
  private MyHuffmanNode<E> right;
  public MyHuffmanNode(E data, int count,
      MyHuffmanNode<E> left, MyHuffmanNode<E> right) {
    this.data = data;
    this.count = count;
    this.left = left;
    this.right = right;
  }
  public MyHuffmanNode(E data, int count) {
    this(data, count, null, null);
  }
  public MyHuffmanNode(MyHuffmanNode<E> left, MyHuffmanNode<E> right) {
    this(null, 0, left, right);
    count += (this.getLeft() == null) ? 0 : this.getLeft().getCount();
    count += (this.getRight() == null) ? 0 : this.getRight().getCount();
  }
  public MyHuffmanNode() {
    this(null, 0, null, null);
  }
  public int getCount() {
    return count;
  }
  public void setCount(int count) {
    this.count = count;
  }
  public E getData() {
    return data;
  }
  public void setData(E data) {
    this.data = data;
  }
  public MyHuffmanNode<E> getLeft() {
    return left;
  }
  public void setLeft(MyHuffmanNode<E> left) {
    this.left = left;
  }
  public MyHuffmanNode<E> getRight() {
    return right;
  }
  public void setRight(MyHuffmanNode<E> right) {
    this.right = right;
  }
  public boolean isLeaf() {
    return this.left == null && this.right == null;
  }
  @Override
  public int compareTo(MyHuffmanNode<E> other) {
    //smaller counts before larger
    return this.count - other.count;
  }
  @Override
  public String toString() {
    String str = "";
    if (this.data == null) {
      str += "*";
    }else {
      //in standard printable ASCII range
      str += this.data;
      if (this.data instanceof Byte) {
        byte b = (Byte) this.data;
        if (b >= 32 && b < 127) {
          str += " '" + (char) b + "'";
        }else if (b == 9) {
          str += " 'TAB'"; //tab
        }else if (b == 10) {
          str += " 'LF'"; //line feed (\n)
        }else if (b == 13) {
          str += " 'CR'"; //carriage return (\r)
        }
      }
    }
    str += " (x " + this.count + ")";
    return str;
  }
  public String toFullString(String prefix) {
    String str = prefix + this.toString() + "\n";
    if (this.getLeft() != null) {
      str += this.getLeft().toFullString(prefix + "<");
    }
    if (this.getRight() != null) {
      str += this.getRight().toFullString(prefix + ">");
    }
    return str;
  }
}

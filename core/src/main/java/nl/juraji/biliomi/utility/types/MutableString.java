package nl.juraji.biliomi.utility.types;

import java.io.Serializable;
import java.util.stream.IntStream;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class MutableString implements Serializable, CharSequence {
  private final StringBuilder buffer;

  public MutableString() {
    this.buffer = new StringBuilder();
  }

  public MutableString(Object string) {
    this.buffer = new StringBuilder(String.valueOf(string));
  }

  public MutableString append(Object obj) {
    buffer.append(obj);
    return this;
  }

  public MutableString append(String str) {
    buffer.append(str);
    return this;
  }

  public MutableString append(StringBuilder sb) {
    buffer.append(sb);
    return this;
  }

  public MutableString append(CharSequence s) {
    buffer.append(s);
    return this;
  }

  public MutableString append(CharSequence s, int start, int end) {
    buffer.append(s, start, end);
    return this;
  }

  public MutableString append(char[] str) {
    buffer.append(str);
    return this;
  }

  public MutableString append(char[] str, int offset, int len) {
    buffer.append(str, offset, len);
    return this;
  }

  public MutableString append(boolean b) {
    buffer.append(b);
    return this;
  }

  public MutableString append(char c) {
    buffer.append(c);
    return this;
  }

  public MutableString append(int i) {
    buffer.append(i);
    return this;
  }

  public MutableString append(long lng) {
    buffer.append(lng);
    return this;
  }

  public MutableString append(float f) {
    buffer.append(f);
    return this;
  }

  public MutableString append(double d) {
    buffer.append(d);
    return this;
  }

  public MutableString appendCodePoint(int codePoint) {
    buffer.appendCodePoint(codePoint);
    return this;
  }

  public MutableString appendNewLine() {
    return append('\n');
  }

  public MutableString appendSpace() {
    return append(' ');
  }

  public MutableString appendSpace(int count) {
    int c = 0;
    while (c < count) {
      appendSpace();
      c++;
    }
    return this;
  }

  public MutableString delete(int start, int end) {
    buffer.delete(start, end);
    return this;
  }

  public MutableString deleteCharAt(int index) {
    buffer.deleteCharAt(index);
    return this;
  }

  public MutableString replace(int start, int end, String str) {
    buffer.replace(start, end, str);
    return this;
  }

  public MutableString insert(int index, char[] str, int offset, int len) {
    buffer.insert(index, str, offset, len);
    return this;
  }

  @Override
  public char charAt(int index) {
    return buffer.charAt(index);
  }

  public int codePointAt(int index) {
    return buffer.codePointAt(index);
  }

  public int codePointBefore(int index) {
    return buffer.codePointBefore(index);
  }

  public int codePointCount(int beginIndex, int endIndex) {
    return buffer.codePointCount(beginIndex, endIndex);
  }

  public int offsetByCodePoints(int index, int codePointOffset) {
    return buffer.offsetByCodePoints(index, codePointOffset);
  }

  public MutableString getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
    buffer.getChars(srcBegin, srcEnd, dst, dstBegin);
    return this;
  }

  public MutableString setCharAt(int index, char ch) {
    buffer.setCharAt(index, ch);
    return this;
  }

  public String substring(int start) {
    return buffer.substring(start);
  }

  @Override
  public IntStream chars() {
    return buffer.chars();
  }

  @Override
  public IntStream codePoints() {
    return buffer.codePoints();
  }

  @Override
  public int length() {
    return buffer.length();
  }

  public int capacity() {
    return buffer.capacity();
  }

  public MutableString ensureCapacity(int minimumCapacity) {
    buffer.ensureCapacity(minimumCapacity);
    return this;
  }

  public MutableString trimToSize() {
    buffer.trimToSize();
    return this;
  }

  public MutableString setLength(int newLength) {
    buffer.setLength(newLength);
    return this;
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return buffer.subSequence(start, end);
  }

  public String substring(int start, int end) {
    return buffer.substring(start, end);
  }

  @Override
  public String toString() {
    return buffer.toString();
  }

  public MutableString replace(String search, Object replacement) {
    int i = buffer.indexOf(search);
    String replString = String.valueOf(replacement);

    while (i != -1) {
      buffer.replace(i, i + search.length(), replString);
      i = buffer.indexOf(search);
    }

    return this;
  }

  public MutableString appendIfMissing(String suffix) {
    if (!endsWith(suffix)) {
      buffer.append(suffix);
    }
    return this;
  }

  public MutableString prepend(String prefix) {
    buffer.insert(0, prefix);
    return this;
  }

  public MutableString prependIfMissing(String prefix) {
    if (!startsWith(prefix)) {
      buffer.insert(0, prefix);
    }
    return this;
  }

  public MutableString insert(int position, Object o) {
    buffer.insert(position, o);
    return this;
  }

  public MutableString insert(int offset, String str) {
    buffer.insert(offset, str);
    return this;
  }

  public MutableString insert(int offset, char[] str) {
    buffer.insert(offset, str);
    return this;
  }

  public MutableString insert(int dstOffset, CharSequence s) {
    buffer.insert(dstOffset, s);
    return this;
  }

  public MutableString insert(int dstOffset, CharSequence s, int start, int end) {
    buffer.insert(dstOffset, s, start, end);
    return this;
  }

  public MutableString insert(int offset, boolean b) {
    buffer.insert(offset, b);
    return this;
  }

  public MutableString insert(int offset, char c) {
    buffer.insert(offset, c);
    return this;
  }

  public MutableString insert(int offset, int i) {
    buffer.insert(offset, i);
    return this;
  }

  public MutableString insert(int offset, long l) {
    buffer.insert(offset, l);
    return this;
  }

  public MutableString insert(int offset, float f) {
    buffer.insert(offset, f);
    return this;
  }

  public MutableString insert(int offset, double d) {
    buffer.insert(offset, d);
    return this;
  }

  public int indexOf(String str) {
    return buffer.indexOf(str);
  }

  public int indexOf(String str, int fromIndex) {
    return buffer.indexOf(str, fromIndex);
  }

  public int lastIndexOf(String str) {
    return buffer.lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return buffer.lastIndexOf(str, fromIndex);
  }

  public MutableString reverse() {
    buffer.reverse();
    return this;
  }

  public boolean startsWith(String string) {
    return buffer.indexOf(string) == 0;
  }

  public boolean endsWith(String string) {
    return buffer.indexOf(string) == buffer.length() - string.length();
  }

  public boolean contains(String search) {
    return buffer.indexOf(search) > -1;
  }

  public void clear() {
    this.buffer.delete(0, this.buffer.length());
  }

  public static String replace(String subject, String search, Object replacement) {
    return new MutableString(subject).replace(search, replacement).toString();
  }
}

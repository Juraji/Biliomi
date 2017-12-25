package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.utility.calculate.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Juraji on 25-12-2017.
 * Biliomi
 */
public class NeverEndingList<T> {
  private final List<T> list;
  private final AtomicInteger index;

  public NeverEndingList() {
    this(new ArrayList<>());
  }

  public NeverEndingList(List<T> list) {
    this.list = list;
    this.index = new AtomicInteger();
  }

  public T next() {
    int size = list.size();
    int i = index.getAndIncrement();

    if (size > 0 && i >= size) {
      reset();
    }

    return list.get(i);
  }

  public T previous() {
    int size = list.size();

    if (size == 0) {
      return null;
    }

    int i = index.getAndDecrement();

    if (i < 0) {
      i = size - 1;
      index.set(i);
    }

    return list.get(i);
  }

  public T random() {
    index.set(MathUtils.rand(list.size()));
    return list.get(index.get());
  }

  public void reset() {
    this.index.set(0);
  }

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public boolean contains(T o) {
    return list.contains(o);
  }

  public boolean add(T t) {
    return list.add(t);
  }

  public boolean remove(T o) {
    return list.remove(o);
  }

  public T get(int index) {
    return list.get(index);
  }
}

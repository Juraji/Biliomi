package nl.juraji.biliomi.utility.types.collections;

import java.util.ArrayList;

/**
 * Created by Juraji on 7-5-2017.
 * Biliomi v3
 */
public class FastList<T> extends ArrayList<T> {

  @Override
  public boolean contains(Object o) {
    return super.indexOf(o) > -1;
  }

  @Override
  public boolean remove(Object o) {
    int i = super.indexOf(o);
    if (i > -1) {
      super.remove(i);
      return true;
    } else {
      return false;
    }
  }

  public T pollFirst() {
    if (size() > 0) {
      return remove(0);
    }
    return null;
  }
}

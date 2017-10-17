package nl.juraji.biliomi.utility.estreams;

import nl.juraji.biliomi.utility.estreams.einterface.*;
import nl.juraji.biliomi.utility.estreams.types.RethrowEInterface;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface EBiStream<K, V, E extends Exception> {

  Stream<Map.Entry<K, V>> entries();

  static <K, V, E extends Exception> EBiStream<K, V, E> from(Map<K, V> map) {
    return from(map.entrySet().stream());
  }

  static <K, V, E extends Exception> EBiStream<K, V, E> from(Stream<Map.Entry<K, V>> stream) {
    return () -> stream;
  }

  static <K, V, E extends Exception> EBiStream<K, V, E> from(K[] array, EFunction<? super K, ? extends V, E> valueMapper) {
    return from(Arrays.stream(array), valueMapper);
  }

  static <O, K, V, E extends Exception> EBiStream<K, V, E> from(O[] array, EFunction<? super O, ? extends K, E> keyMapper, EFunction<? super O, ? extends V, E> valueMapper) {
    return from(Arrays.stream(array), keyMapper, valueMapper);
  }

  static <K, V, E extends Exception> EBiStream<K, V, E> from(Collection<K> collection, EFunction<? super K, ? extends V, E> valueMapper) {
    return from(collection.stream(), valueMapper);
  }

  static <O, K, V, E extends Exception> EBiStream<K, V, E> from(Collection<O> collection, EFunction<? super O, ? extends K, E> keyMapper, EFunction<? super O, ? extends V, E> valueMapper) {
    return from(collection.stream(), keyMapper, valueMapper);
  }

  static <K, V, E extends Exception> EBiStream<K, V, E> from(Stream<K> stream, EFunction<? super K, ? extends V, E> valueMapper) {
    return () -> stream.map(k -> new AbstractMap.SimpleImmutableEntry<>(k, RethrowEInterface.function(valueMapper).apply(k)));
  }

  static <O, K, V, E extends Exception> EBiStream<K, V, E> from(Stream<O> stream, EFunction<? super O, ? extends K, E> keyMapper, EFunction<? super O, ? extends V, E> valueMapper) {
    return () -> stream.map(k -> new AbstractMap.SimpleImmutableEntry<>(RethrowEInterface.function(keyMapper).apply(k), RethrowEInterface.function(valueMapper).apply(k)));
  }

  default EBiStream<K, V, E> distinct() {
    return from(entries().distinct());
  }

  default EBiStream<K, V, E> peek(EBiConsumer<? super K, ? super V, E> biConsumer) {
    return from(entries().peek(e -> RethrowEInterface.biConsumber(biConsumer).accept(e.getKey(), e.getValue())));
  }

  default EBiStream<K, V, E> skip(long n) {
    return from(entries().skip(n));
  }

  default EBiStream<K, V, E> limit(long maxSize) {
    return from(entries().limit(maxSize));
  }

  default EBiStream<K, V, E> filterKey(EPredicate<? super K, E> predicate) {
    return from(entries().filter(e -> RethrowEInterface.predicate(predicate).test(e.getKey())));
  }

  default EBiStream<K, V, E> filterValue(EPredicate<? super V, E> predicate) {
    return from(entries().filter(e -> RethrowEInterface.predicate(predicate).test(e.getValue())));
  }

  default EBiStream<K, V, E> filter(EBiPredicate<? super K, ? super V, E> biPredicate) {
    return from(entries().filter(e -> RethrowEInterface.biPredicate(biPredicate).test(e.getKey(), e.getValue())));
  }

  default EBiStream<V, K, E> invert() {
    return from(entries().map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getValue(), e.getKey())));
  }

  default <R> EBiStream<R, V, E> mapKey(EFunction<? super K, ? extends R, E> function) {
    return from(entries().map(e -> new AbstractMap.SimpleImmutableEntry<>(
        RethrowEInterface.function(function).apply(e.getKey()), e.getValue()
    )));
  }

  default <R> EBiStream<R, V, E> mapKey(EBiFunction<? super K, ? super V, ? extends R, E> biFunction) {
    return from(entries().map(e -> new AbstractMap.SimpleImmutableEntry<>(
        RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue()), e.getValue()
    )));
  }

  default <R> EBiStream<K, R, E> mapValue(EFunction<? super V, ? extends R, E> function) {
    return from(entries().map(e -> new AbstractMap.SimpleImmutableEntry<>(
        e.getKey(), RethrowEInterface.function(function).apply(e.getValue())
    )));
  }

  default <R> EBiStream<K, R, E> mapValue(EBiFunction<? super K, ? super V, ? extends R, E> biFunction) {
    return from(entries().map(e -> new AbstractMap.SimpleImmutableEntry<>(
        e.getKey(), RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue())
    )));
  }

  default <R> EStream<R, E> map(EBiFunction<? super K, ? super V, ? extends R, E> biFunction) {
    return EStream.from(entries().map(e -> RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue())));
  }

  default DoubleStream mapToDouble(ToDoubleBiFunction<? super K, ? super V> mapper) {
    return entries().mapToDouble(e -> mapper.applyAsDouble(e.getKey(), e.getValue()));
  }

  default IntStream mapToInt(ToIntBiFunction<? super K, ? super V> mapper) {
    return entries().mapToInt(e -> mapper.applyAsInt(e.getKey(), e.getValue()));
  }

  default LongStream mapToLong(ToLongBiFunction<? super K, ? super V> mapper) {
    return entries().mapToLong(e -> mapper.applyAsLong(e.getKey(), e.getValue()));
  }

  default <L, W> EBiStream<L, W, E> flatMap(
      EBiFunction<? super K, ? super V, ? extends EBiStream<L, W, E>, E> biFunction) {
    return from(entries().flatMap(
        e -> RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue()).entries()));
  }

  default <R> Stream<R> flatMapToObj(
      EBiFunction<? super K, ? super V, ? extends Stream<R>, E> mapper) {
    return entries().flatMap(e -> RethrowEInterface.biFunction(mapper).apply(e.getKey(), e.getValue()));
  }

  default DoubleStream flatMapToDouble(
      EBiFunction<? super K, ? super V, ? extends DoubleStream, E> biFunction) {
    return entries().flatMapToDouble(e -> RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue()));
  }

  default IntStream flatMapToInt(
      EBiFunction<? super K, ? super V, ? extends IntStream, E> biFunction) {
    return entries().flatMapToInt(e -> RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue()));
  }

  default LongStream flatMapToLong(
      EBiFunction<? super K, ? super V, ? extends LongStream, E> biFunction) {
    return entries().flatMapToLong(e -> RethrowEInterface.biFunction(biFunction).apply(e.getKey(), e.getValue()));
  }

  default EBiStream<K, V, E> sortedByKey(EComparator<? super K, E> comparator) {
    return from(entries().sorted(Map.Entry.comparingByKey(RethrowEInterface.comparator(comparator))));
  }

  default EBiStream<K, V, E> sortedByValue(EComparator<? super V, E> comparator) {
    return from(entries().sorted(Map.Entry.comparingByValue(RethrowEInterface.comparator(comparator))));
  }

  default boolean allMatch(EBiPredicate<? super K, ? super V, E> biPredicate) {
    return entries().allMatch(e -> RethrowEInterface.biPredicate(biPredicate).test(e.getKey(), e.getValue()));
  }

  default boolean anyMatch(EBiPredicate<? super K, ? super V, E> biPredicate) {
    return entries().anyMatch(e -> RethrowEInterface.biPredicate(biPredicate).test(e.getKey(), e.getValue()));
  }

  default boolean noneMatch(EBiPredicate<? super K, ? super V, E> biPredicate) {
    return entries().noneMatch(e -> RethrowEInterface.biPredicate(biPredicate).test(e.getKey(), e.getValue()));
  }

  default long count() {
    return entries().count();
  }

  default Stream<K> keys() {
    return entries().map(Map.Entry::getKey);
  }

  default Stream<V> values() {
    return entries().map(Map.Entry::getValue);
  }

  default Optional<Map.Entry<K, V>> maxByKey(EComparator<? super K, E> comparator) {
    return entries().max(Map.Entry.comparingByKey(RethrowEInterface.comparator(comparator)));
  }

  default Optional<Map.Entry<K, V>> maxByValue(EComparator<? super V, E> comparator) {
    return entries().max(Map.Entry.comparingByValue(RethrowEInterface.comparator(comparator)));
  }

  default Optional<Map.Entry<K, V>> minByKey(EComparator<? super K, E> comparator) {
    return entries().min(Map.Entry.comparingByKey(RethrowEInterface.comparator(comparator)));
  }

  default Optional<Map.Entry<K, V>> minByValue(EComparator<? super V, E> comparator) {
    return entries().min(Map.Entry.comparingByValue(RethrowEInterface.comparator(comparator)));
  }

  default void forEach(EBiConsumer<? super K, ? super V, E> biConsumer) {
    entries().forEach(e -> RethrowEInterface.biConsumber(biConsumer).accept(e.getKey(), e.getValue()));
  }

  default void forEachOrdered(EBiConsumer<? super K, ? super V, E> biConsumer) {
    entries().forEachOrdered(e -> RethrowEInterface.biConsumber(biConsumer).accept(e.getKey(), e.getValue()));
  }

  default <R> EStream<R, E> toExceptionalStream(EBiFunction<K, V, R, E> valueMapper) {
    return EStream.from(entries().map(e -> RethrowEInterface.biFunction(valueMapper).apply(e.getKey(), e.getValue())));
  }

  default Map<K, V> toMap() {
    return entries().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  default Map<K, V> toMap(BinaryOperator<V> valAccum) {
    return entries().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, valAccum));
  }
}

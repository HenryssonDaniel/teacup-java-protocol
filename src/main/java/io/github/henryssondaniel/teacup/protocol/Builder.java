package io.github.henryssondaniel.teacup.protocol;

/**
 * Builder.
 *
 * @param <T> the type
 * @since 1.0
 */
@FunctionalInterface
public interface Builder<T> {
  /**
   * Build.
   *
   * @return thr type
   * @since 1.0
   */
  T build();
}

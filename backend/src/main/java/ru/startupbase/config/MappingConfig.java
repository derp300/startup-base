package ru.startupbase.config;

import java.util.Arrays;
import java.util.HashSet;

public class MappingConfig {
  private final HashSet<Class<?>> mappings = new HashSet<>();

  public MappingConfig(Class<?> ...classes) {
    mappings.addAll(Arrays.asList(classes));
  }

  public Class<?>[] getMappings() {
    return mappings.toArray(new Class<?>[mappings.size()]);
  }
}
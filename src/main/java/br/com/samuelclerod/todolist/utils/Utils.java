package br.com.samuelclerod.todolist.utils;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

  public static void copyNonNullProperties(Object source, Object target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);

    return Stream.of(src.getPropertyDescriptors())
        .map(FeatureDescriptor::getName)
        .filter(propertyName -> src.getPropertyValue(propertyName) == null)
        .toArray(String[]::new);
  }

}

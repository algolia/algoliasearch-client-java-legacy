package com.algolia.search.inputs.partial_update;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class DecrementValueOperation implements PartialUpdateOperation {

  private final Map<String, ?> map;
  private final String objectID;

  /**
   *
   * @param objectID
   * @param attributeName
   * @param decrement
   */
  public DecrementValueOperation(String objectID, String attributeName, int decrement) {
    this.objectID = objectID;
    this.map = ImmutableMap.of(
      "objectID", objectID,
      attributeName, new NumericOperation(decrement, "Decrement")
    );
  }

  @Override
  public String getObjectID() {
    return objectID;
  }

  @Override
  public Map<String, ?> toSerialize() {
    return map;
  }

}

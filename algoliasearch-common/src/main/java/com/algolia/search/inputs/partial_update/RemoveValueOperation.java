package com.algolia.search.inputs.partial_update;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class RemoveValueOperation implements PartialUpdateOperation {

  private final Map<String, ?> map;
  private final String objectID;

  /**
   *
   * @param objectID
   * @param attributeToRemoveIn
   * @param valueToRemove
   */
  public RemoveValueOperation(String objectID, String attributeToRemoveIn, String valueToRemove) {
    this.objectID = objectID;
    this.map = ImmutableMap.of(
      "objectID", objectID,
      attributeToRemoveIn, new TagOperation(valueToRemove, "Remove")
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

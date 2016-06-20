package com.algolia.search.inputs.partial_update;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AddValueOperation implements PartialUpdateOperation {

  private final Map<String, ?> map;
  private final String objectID;

  /**
   *
   * @param objectID
   * @param attributeToAddIn
   * @param valueToAdd
   */
  public AddValueOperation(String objectID, String attributeToAddIn, String valueToAdd) {
    this.objectID = objectID;
    this.map = ImmutableMap.of(
      "objectID", objectID,
      attributeToAddIn, new TagOperation(valueToAdd, "Add")
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

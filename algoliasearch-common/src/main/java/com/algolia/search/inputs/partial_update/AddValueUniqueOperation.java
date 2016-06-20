package com.algolia.search.inputs.partial_update;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AddValueUniqueOperation implements PartialUpdateOperation {

  private final Map<String, ?> map;
  private final String objectID;

  /**
   *
   * @param objectID
   * @param attributeToAddIn
   * @param valueToAddIfDoesntExists
   */
  public AddValueUniqueOperation(String objectID, String attributeToAddIn, String valueToAddIfDoesntExists) {
    this.objectID = objectID;
    this.map = ImmutableMap.of(
      "objectID", objectID,
      attributeToAddIn, new TagOperation(valueToAddIfDoesntExists, "AddUnique")
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

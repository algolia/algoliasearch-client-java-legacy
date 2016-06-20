package com.algolia.search.inputs.synonym;

import java.util.List;

public class Placeholder implements AbstractSynonym {

  private String objectID;
  private String placeholder;
  private List<String> replacements;

  @Override
  public String getObjectID() {
    return null;
  }

  public Placeholder setObjectID(String objectID) {
    this.objectID = objectID;
    return this;
  }

  @Override
  public String getType() {
    return "placeholder";
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public Placeholder setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
    return this;
  }

  public List<String> getReplacements() {
    return replacements;
  }

  public Placeholder setReplacements(List<String> replacements) {
    this.replacements = replacements;
    return this;
  }
}

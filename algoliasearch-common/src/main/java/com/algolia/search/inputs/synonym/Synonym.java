package com.algolia.search.inputs.synonym;

import java.util.List;

public class Synonym implements AbstractSynonym {

  private String objectID;
  private List<String> synonyms;

  @Override
  public String getObjectID() {
    return null;
  }

  public Synonym setObjectID(String objectID) {
    this.objectID = objectID;
    return this;
  }

  @Override
  public String getType() {
    return "altCorrection1";
  }

  public List<String> getSynonyms() {
    return synonyms;
  }

  public Synonym setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
    return this;
  }

}

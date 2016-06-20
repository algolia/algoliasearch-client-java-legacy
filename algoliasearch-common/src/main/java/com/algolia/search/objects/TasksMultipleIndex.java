package com.algolia.search.objects;

import com.algolia.search.APIClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TasksMultipleIndex extends GenericTask<Map<String, Long>> {

  private List<String> objectIDs;
  private String createdAt;

  @SuppressWarnings("unused")
  public List<String> getObjectIDs() {
    return objectIDs;
  }

  @SuppressWarnings("unused")
  public TasksMultipleIndex setObjectIDs(List<String> objectIDs) {
    this.objectIDs = objectIDs;
    return this;
  }

  @SuppressWarnings("unused")
  public String getCreatedAt() {
    return createdAt;
  }

  @SuppressWarnings("unused")
  public TasksMultipleIndex setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  @Override
  public TasksMultipleIndex setAttributes(String indexName, APIClient apiClient) {
    super.setAttributes(indexName, apiClient);
    return this;
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public Long getTaskIDToWaitFor() {
    return getTaskID().values().stream().max(Comparator.comparingLong(Long::longValue)).get();
  }
}

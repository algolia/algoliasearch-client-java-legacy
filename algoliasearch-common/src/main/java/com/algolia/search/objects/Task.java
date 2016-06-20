package com.algolia.search.objects;

import com.algolia.search.APIClient;
import com.algolia.search.exceptions.AlgoliaException;

public class Task extends GenericTask<Long> {

  public Task setAttributes(String indexName, APIClient apiClient) {
    this.apiClient = apiClient;
    this.indexName = indexName;

    return this;
  }

  /**
   * Wait for the completion of this task
   *
   * @throws AlgoliaException
   */
  public void waitForCompletion() throws AlgoliaException {
    apiClient.waitTask(indexName, this, 100);
  }

  /**
   * Wait for the completion of this task
   *
   * @param timeToWait the time to wait in milliseconds
   * @throws AlgoliaException
   */
  public void waitForCompletion(long timeToWait) throws AlgoliaException {
    apiClient.waitTask(indexName, this, timeToWait);
  }

  @Override
  public Long getTaskIDToWaitFor() {
    return getTaskID();
  }

}

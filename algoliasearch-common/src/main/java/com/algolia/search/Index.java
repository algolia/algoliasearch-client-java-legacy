package com.algolia.search;

import com.algolia.search.inputs.BatchOperation;
import com.algolia.search.inputs.partial_update.PartialUpdateOperation;
import com.algolia.search.objects.*;
import com.algolia.search.responses.CreateUpdateKey;
import com.algolia.search.responses.DeleteKey;
import com.algolia.search.responses.SearchResult;

import java.util.List;
import java.util.Optional;

public class Index<T> {

  @SuppressWarnings("WeakerAccess")
  /**
   * Index name
   */
  private final String name;
  @SuppressWarnings("WeakerAccess")

  /**
   * The type of the objects in this Index
   */
  private final Class<T> klass;

  private final APIClient client;

  Index(String name, Class<T> klass, APIClient client) {
    this.name = name;
    this.klass = klass;
    this.client = client;
  }

  public String getName() {
    return name;
  }

  public Class<T> getKlass() {
    return klass;
  }

  /**
   * Add an object in this index
   *
   * @param object object to add
   * @return the related Task
   * @throws AlgoliaException
   */
  public TaskIndexing addObject(T object) throws AlgoliaException {
    return client.addObject(name, object);
  }

  /**
   * Add an object in this index with a unique identifier
   *
   * @param objectID the objectID associated to this object
   *                 (if this objectID already exist the old object will be overriden)
   * @param object   object to add
   * @return the related Task
   * @throws AlgoliaException
   */
  public TaskIndexing addObject(String objectID, T object) throws AlgoliaException {
    return client.addObject(name, objectID, object);
  }

  /**
   * Add several objects
   *
   * @param objects objects to add
   * @return the related Task
   * @throws AlgoliaException
   */
  public TaskSingleIndex addObjects(List<T> objects) throws AlgoliaException {
    return client.addObjects(name, objects);
  }

  /**
   * Get an object from this index
   *
   * @param objectID the unique identifier of the object to retrieve
   * @return The object
   * @throws AlgoliaException
   */
  public Optional<T> getObject(String objectID) throws AlgoliaException {
    return client.getObject(name, objectID, klass);
  }

  /**
   * Get several objects from this index
   *
   * @param objectIDs the list of unique identifier of objects to retrieve
   * @return the list of objects
   * @throws AlgoliaException
   */
  public List<T> getObjects(List<String> objectIDs) throws AlgoliaException {
    return client.getObjects(name, objectIDs, klass);
  }

  /**
   * Wait for the completion of a task
   *
   * @param task       task to wait for
   * @param timeToWait the time to wait in milliseconds
   * @throws AlgoliaException
   */
  public void waitTask(Task task, long timeToWait) throws AlgoliaException {
    client.waitTask(name, task, timeToWait);
  }

  /**
   * Wait for the completion of a task, for 100ms
   *
   * @param task task to wait for
   * @throws AlgoliaException
   */
  public void waitTask(Task task) throws AlgoliaException {
    client.waitTask(name, task, 100);
  }

  /**
   * Deletes the index
   *
   * @return the related Task
   * @throws AlgoliaException
   */
  public Task delete() throws AlgoliaException {
    return client.deleteIndex(name);
  }

  /**
   * Delete the index content without removing settings and index specific API keys.
   *
   * @return the related Task
   * @throws AlgoliaException
   */
  public Task clear() throws AlgoliaException {
    return client.clearIndex(name);
  }

  /**
   * Override the content of object
   *
   * @param objectID the unique identifier of the object to retrieve
   * @param object   the object to update
   * @return the related Task
   * @throws AlgoliaException
   */
  public Task saveObject(String objectID, T object) throws AlgoliaException {
    return client.saveObject(name, objectID, object);
  }

  /**
   * Override the content the list of objects
   *
   * @param objects the list objects to update
   * @return the related Task
   * @throws AlgoliaException
   */
  public TaskSingleIndex saveObjects(List<T> objects) throws AlgoliaException {
    return client.saveObjects(name, objects);
  }

  /**
   * Delete an object from the index
   *
   * @param objectID the unique identifier of the object to retrieve
   * @return the related Task
   * @throws AlgoliaException
   */
  public Task deleteObject(String objectID) throws AlgoliaException {
    return client.deleteObject(name, objectID);
  }

  /**
   * Delete objects from the index
   *
   * @param objectIDs the list of unique identifier of the object to retrieve
   * @return the related Task
   * @throws AlgoliaException
   */
  public TaskSingleIndex deleteObjects(List<String> objectIDs) throws AlgoliaException {
    return client.deleteObjects(name, objectIDs);
  }

  /**
   * Get settings of this index
   *
   * @return the settings
   * @throws AlgoliaException
   */
  public IndexSettings getSettings() throws AlgoliaException {
    return client.getSettings(name);
  }

  /**
   * Set settings of this index
   *
   * @param settings the settings to set
   * @return the related Task
   * @throws AlgoliaException
   */
  public Task setSettings(IndexSettings settings) throws AlgoliaException {
    return client.setSettings(name, settings);
  }

  /**
   * List keys of this index
   *
   * @return the list of keys
   * @throws AlgoliaException
   */
  public List<ApiKey> listKeys() throws AlgoliaException {
    return client.listKeys(name);
  }

  /**
   * Get a key by name from this index
   *
   * @param key the key name
   * @return the key
   * @throws AlgoliaException
   */
  public Optional<ApiKey> getKey(String key) throws AlgoliaException {
    return client.getKey(name, key);
  }

  /**
   * Delete a key by name from this index
   *
   * @param key the key name
   * @return the deleted key
   * @throws AlgoliaException
   */
  public DeleteKey deleteKey(String key) throws AlgoliaException {
    return client.deleteKey(name, key);
  }

  /**
   * Add a key to this index
   *
   * @param key the key
   * @return the created key
   * @throws AlgoliaException
   */
  public CreateUpdateKey addKey(ApiKey key) throws AlgoliaException {
    return client.addKey(name, key);
  }

  /**
   * Update a key by name from this index
   *
   * @param keyName the key name
   * @param key the key to update
   * @return the updated key
   * @throws AlgoliaException
   */
  public CreateUpdateKey updateKey(String keyName, ApiKey key) throws AlgoliaException {
    return client.updateKey(name, keyName, key);
  }

  /**
   * Moves an existing index
   *
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overriten if it already exist)
   * @return The task associated
   * @throws AlgoliaException
   */
  public Task moveTo(String dstIndexName) throws AlgoliaException {
    return client.moveIndex(name, dstIndexName);
  }

  /**
   * Copy an existing index
   *
   * @param dstIndexName the new index name that will contains a copy of srcIndexName (destination will be overridden if it already exist)
   * @return The task associated
   * @throws AlgoliaException
   */
  public Task copyTo(String dstIndexName) throws AlgoliaException {
    return client.copyIndex(name, dstIndexName);
  }

  /**
   * Search in the index
   *
   * @param query the query
   * @return the result of the search
   * @throws AlgoliaException
   */
  public SearchResult<T> search(Query query) throws AlgoliaException {
    return client.search(name, query, klass);
  }

  /**
   * Custom batch
   *
   * All operations must have index name set to <code>null</code>
   *
   * @see BatchOperation & subclasses
   *
   * @param operations the list of operations to perform on this index
   * @return the associated task
   * @throws AlgoliaException
   */
  public TaskSingleIndex batch(List<BatchOperation> operations) throws AlgoliaException {
    return client.batch(name, operations);
  }

  /**
   * Partially update an object
   *
   * @see PartialUpdateOperation & subclasses
   *
   * @param operation the operation to perform on this object
   * @return the associated task
   * @throws AlgoliaException
   */
  public TaskSingleIndex partialUpdate(PartialUpdateOperation operation) throws AlgoliaException {
    return client.partialUpdate(name, operation);
  }

  public static class Attributes {
    private String name;
    private String createdAt;
    private String updatedAt;
    private Integer entries;
    private Integer dataSize;
    private Integer fileSize;
    private Integer lastBuildTimeS;
    private Integer numberOfPendingTask;
    private Boolean pendingTask;

    public String getName() {
      return name;
    }

    public String getCreatedAt() {
      return createdAt;
    }

    public String getUpdatedAt() {
      return updatedAt;
    }

    public Integer getEntries() {
      return entries;
    }

    public Integer getDataSize() {
      return dataSize;
    }

    public Integer getFileSize() {
      return fileSize;
    }

    public Integer getLastBuildTimeS() {
      return lastBuildTimeS;
    }

    public Integer getNumberOfPendingTask() {
      return numberOfPendingTask;
    }

    public Boolean getPendingTask() {
      return pendingTask;
    }
  }

}

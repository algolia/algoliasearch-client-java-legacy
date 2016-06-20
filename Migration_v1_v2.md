# Changelog

* Builder
* listIndexes => listIndices
* index.addObject(obj, objectId) => index.addObject(objectId, obj)
* searchDisjunctiveFaceting removed
* removed getLogs(int offset, int length) throws AlgoliaException => LOG_ALL
* removed getLogs(int offset, int length, boolean onlyErrors) throws AlgoliaException => LOG_ERROR
* client.copyIndex/client.moveIndex => index.copyTo(dst), index.moveTo(dst)
* index.clearIndex() => index.clear();
* task.waitFor(long)
* T getObject => Optional<T> getObject
* partialUpdateObject => specific class
* remove enableRateLimitForward
* userkey => key


# TODO

* partialUpdateObjectSSSS
* add Preconditions
* Update apache http client
* browse
* deleteByQuery
* custom query param
* custom index settings
* gae tests
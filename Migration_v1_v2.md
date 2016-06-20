# Changelog

* Builder
* listIndexes => listIndices
* index.addObject(obj, objectId) => index.addObject(objectId, obj)
* searchDisjunctiveFaceting removed
* removed getLogs(int offset, int length) throws AlgoliaException => LOG_ALL
* removed getLogs(int offset, int length, boolean onlyErrors) throws AlgoliaException => LOG_ERROR
* client.copyIndex/client.moveIndex => index.copyTo(dst), index.moveTo(dst)
* task.waitFor(long)
* T getObject => Optional<T> getObject 


# TODO

* Update apache http client
* updatePartial
* synonyms v2
* browse
* deleteByQuery
* custom query param
* custom index settings
* have mulitple exception instead of one big
* gae tests
* create if not exists (update partial)
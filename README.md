# Algolia Search API Client for Java

[Algolia Search](https://www.algolia.com) is a hosted full-text, numerical, and faceted search engine capable of delivering realtime results from the first keystroke.
The **Algolia Search API Client for Java** lets you easily use the [Algolia Search REST API](https://www.algolia.com/doc/rest-api/search) from your Java code.

[![Build Status](https://travis-ci.org/algolia/algoliasearch-client-java.png?branch=master)](https://travis-ci.org/algolia/algoliasearch-client-java) [![GitHub version](https://badge.fury.io/gh/algolia%2Falgoliasearch-client-java.png)](http://badge.fury.io/gh/algolia%2Falgoliasearch-client-java) [![Coverage Status](https://coveralls.io/repos/algolia/algoliasearch-client-java/badge.svg)](https://coveralls.io/r/algolia/algoliasearch-client-java)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.algolia/algoliasearch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.algolia/algoliasearch/)


**DEPRECATION WARNING**
The version 1.x is no longer under active development. It will still supported for bug fixes, and new query parameters & index settings.

**Migration note from v1.x to v2.x**
In June 2016, we released the [v2 of our Java client](https://github.com/algolia/algoliasearch-client-java-2/). If you were using version 1.x of the client, read the [migration guide to version 2.x](https://github.com/algolia/algoliasearch-client-java-2/wiki/Migration-guide-from-1.x-to-2.x).

**WARNING:**
The JVM has an infinite cache on successful DNS resolution. As our hostnames points to multiple IPs, the load could be not evenly spread among our machines, and you might also target a dead machine.

You should change this TTL by setting the property `networkaddress.cache.ttl`. For example to set the cache to 60 seconds:
```java
java.security.Security.setProperty("networkaddress.cache.ttl", "60");
```








# Getting Started



## Install

If you're using `Maven`, add the following dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.algolia</groupId>
    <artifactId>algoliasearch</artifactId>
    <version>[1,]</version>
</dependency>
```

## Init Index

To begin, you will need to initialize the client. In order to do this you will need your **Application ID** and **API Key**.
You can find both on [your Algolia account](https://www.algolia.com/api-keys).

```java
APIClient client = new APIClient("YourApplicationID", "YourAPIKey");
```



# Search



## Search an index - `Search` 

To perform a search, you need to initialize the index and perform a call to the search function.

The search query only allows for the retrieval of up to 1000 hits.
If you need to retrieve more than 1000 hits (e.g. for SEO), you can either leverage the [Backup / Export an index](https://www.algolia.com/doc/api-client/java1/advanced/#backup--export-an-index)
method or increase the [`paginationLimitedTo`](https://www.algolia.com/doc/api-reference/api-parameters/paginationLimitedTo/) parameter.

```java
Index index = client.initIndex("contacts");
System.out.println(index.search(new Query("query string")));
System.out.println(index.search(new Query("query string").
             setAttributesToRetrieve(Arrays.asList("firstname", "lastname")).
             setNbHitsPerPage(50)));
```

If you are building a web application, you may be more interested in one
of our [frontend search UI librairies](https://www.algolia.com/doc/guides/search-ui/search-libraries/)

It brings several benefits:
  * Your users will see a better response time as the request will not need to go through your servers
  * You will be able to offload unnecessary tasks from your servers

### Building search UIs

To build a search user interface on top of the Algolia API, we recommend using one of our
[frontend search UI librairies](https://www.algolia.com/doc/guides/search-ui/search-libraries/) instead of using the API client directly.

You might be interested in the following tutorials on getting started with building search UIs:

<a href="/doc/tutorials/search-ui/instant-search/build-an-instant-search-results-page/instantsearchjs/">

<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
  <title>Search UI</title>
  <defs>
    <linearGradient x1="50%" y1="-227.852%" x2="77.242%" y2="191.341%" id="a">
      <stop stop-color="#8995C7" offset="0%" />
      <stop stop-color="#F1F4FD" offset="100%" />
    </linearGradient>
  </defs>
  <g fill="none" fill-rule="evenodd">
    <path d="M21 20.385a.62.62 0 0 1-.615.615.603.603 0 0 1-.433-.183l-1.65-1.644A3.383 3.383 0 0 1 13 16.385 3.383 3.383 0 0 1 16.386 13a3.383 3.383 0 0 1 2.788 5.303l1.65 1.649c.11.11.177.27.177.433zm-2.667-4.052c0-1.102-.897-2-2-2-1.102 0-2 .898-2 2 0 1.103.898 2 2 2 1.103 0 2-.897 2-2zM14 5h4a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1h-5V6a1 1 0 0 1 1-1zm-8 8h5v5a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1v-4a1 1 0 0 1 1-1zm0-8h4a1 1 0 0 1 1 1v5H6a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1z" fill="url(#a)" />
  </g>
</svg>

Tutorials

Building an instant search result page

</a><a href="/doc/tutorials/search-ui/autocomplete/auto-complete/">

<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
  <title>Search UI</title>
  <defs>
    <linearGradient x1="50%" y1="-227.852%" x2="77.242%" y2="191.341%" id="a">
      <stop stop-color="#8995C7" offset="0%" />
      <stop stop-color="#F1F4FD" offset="100%" />
    </linearGradient>
  </defs>
  <g fill="none" fill-rule="evenodd">
    <path d="M21 20.385a.62.62 0 0 1-.615.615.603.603 0 0 1-.433-.183l-1.65-1.644A3.383 3.383 0 0 1 13 16.385 3.383 3.383 0 0 1 16.386 13a3.383 3.383 0 0 1 2.788 5.303l1.65 1.649c.11.11.177.27.177.433zm-2.667-4.052c0-1.102-.897-2-2-2-1.102 0-2 .898-2 2 0 1.103.898 2 2 2 1.103 0 2-.897 2-2zM14 5h4a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1h-5V6a1 1 0 0 1 1-1zm-8 8h5v5a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1v-4a1 1 0 0 1 1-1zm0-8h4a1 1 0 0 1 1 1v5H6a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1z" fill="url(#a)" />
  </g>
</svg>

Tutorials

Autocomplete

</a>

## Search Response Format

### Sample

The server response will look like:

```json
{
  "hits": [
    {
      "firstname": "Jimmie",
      "lastname": "Barninger",
      "objectID": "433",
      "_highlightResult": {
        "firstname": {
          "value": "<em>Jimmie</em>",
          "matchLevel": "partial"
        },
        "lastname": {
          "value": "Barninger",
          "matchLevel": "none"
        },
        "company": {
          "value": "California <em>Paint</em> & Wlpaper Str",
          "matchLevel": "partial"
        }
      }
    }
  ],
  "page": 0,
  "nbHits": 1,
  "nbPages": 1,
  "hitsPerPage": 20,
  "processingTimeMS": 1,
  "query": "jimmie paint",
  "params": "query=jimmie+paint&attributesToRetrieve=firstname,lastname&hitsPerPage=50"
}
```

### Fields

- `hits` (array): The hits returned by the search, sorted according to the ranking formula.

    Hits are made of the JSON objects that you stored in the index; therefore, they are mostly schema-less. However, Algolia does enrich them with a few additional fields:

    - `_highlightResult` (object, optional): Highlighted attributes. *Note: Only returned when [`attributesToHighlight`](https://www.algolia.com/doc/api-reference/api-parameters/attributesToHighlight/) is non-empty.*

        - `${attribute_name}` (object): Highlighting for one attribute.

            - `value` (string): Markup text with occurrences highlighted. The tags used for highlighting are specified via [`highlightPreTag`](https://www.algolia.com/doc/api-reference/api-parameters/highlightPreTag/) and [`highlightPostTag`](https://www.algolia.com/doc/api-reference/api-parameters/highlightPostTag/).

            - `matchLevel` (string, enum) = {`none` \| `partial` \| `full`}: Indicates how well the attribute matched the search query.

            - `matchedWords` (array): List of words *from the query* that matched the object.

            - `fullyHighlighted` (boolean): Whether the entire attribute value is highlighted.

    - `_snippetResult` (object, optional): Snippeted attributes. *Note: Only returned when [`attributesToSnippet`](https://www.algolia.com/doc/api-reference/api-parameters/attributesToSnippet/) is non-empty.*

        - `${attribute_name}` (object): Snippeting for the corresponding attribute.

            - `value` (string): Markup text with occurrences highlighted and optional ellipsis indicators. The tags used for highlighting are specified via [`highlightPreTag`](https://www.algolia.com/doc/api-reference/api-parameters/highlightPreTag/) and [`highlightPostTag`](https://www.algolia.com/doc/api-reference/api-parameters/highlightPostTag/). The text used to indicate ellipsis is specified via [`snippetEllipsisText`](https://www.algolia.com/doc/api-reference/api-parameters/snippetEllipsisText/).

            - `matchLevel` (string, enum) = {`none` \| `partial` \| `full`}: Indicates how well the attribute matched the search query.

    - `_rankingInfo` (object, optional): Ranking information. *Note: Only returned when [`getRankingInfo`](https://www.algolia.com/doc/api-reference/api-parameters/getRankingInfo/) is `true`.*

        - `nbTypos` (integer): Number of typos encountered when matching the record. Corresponds to the `typos` ranking criterion in the ranking formula.

        - `firstMatchedWord` (integer): Position of the most important matched attribute in the attributes to index list. Corresponds to the `attribute` ranking criterion in the ranking formula.

        - `proximityDistance` (integer): When the query contains more than one word, the sum of the distances between matched words (in meters). Corresponds to the `proximity` criterion in the ranking formula.

        - `userScore` (integer): Custom ranking for the object, expressed as a single numerical value. Conceptually, it's what the position of the object would be in the list of all objects sorted by custom ranking. Corresponds to the `custom` criterion in the ranking formula.

        - `geoDistance` (integer): Distance between the geo location in the search query and the best matching geo location in the record, divided by the geo precision (in meters).

        - `geoPrecision` (integer): Precision used when computed the geo distance, in meters. All distances will be floored to a multiple of this precision.

        - `nbExactWords` (integer): Number of exactly matched words. If `alternativeAsExact` is set, it may include plurals and/or synonyms.

        - `words` (integer): Number of matched words, including prefixes and typos.

        - `filters` (integer): *This field is reserved for advanced usage.* It will be zero in most cases.

        - `matchedGeoLocation` (object): Geo location that matched the query. *Note: Only returned for a geo search.*

            - `lat` (float): Latitude of the matched location.

            - `lng` (float): Longitude of the matched location.

            - `distance` (integer): Distance between the matched location and the search location (in meters). **Caution:** Contrary to `geoDistance`, this value is *not* divided by the geo precision.

    - `_distinctSeqID` (integer): *Note: Only returned when [`distinct`](https://www.algolia.com/doc/api-reference/api-parameters/distinct/) is non-zero.* When two consecutive results have the same value for the attribute used for "distinct", this field is used to distinguish between them.

- `nbHits` (integer): Number of hits that the search query matched.

- `page` (integer): Index of the current page (zero-based). See the [`page`](https://www.algolia.com/doc/api-reference/api-parameters/page/) search parameter. *Note: Not returned if you use `offset`/`length` for pagination.*

- `hitsPerPage` (integer): Maximum number of hits returned per page. See the [`hitsPerPage`](https://www.algolia.com/doc/api-reference/api-parameters/hitsPerPage/) search parameter. *Note: Not returned if you use `offset`/`length` for pagination.*

- `nbPages` (integer): Number of pages corresponding to the number of hits. Basically, `ceil(nbHits / hitsPerPage)`. *Note: Not returned if you use `offset`/`length` for pagination.*

- `processingTimeMS` (integer): Time that the server took to process the request, in milliseconds. *Note: This does not include network time.*

- `exhaustiveNbHits` (boolean): Whether the `nbHits` is exhaustive (`true`) or approximate (`false`). *Note: An approximation is done when the query takes more than 50ms to be processed (this can happen when doing complex filters on millions on records).*

- `query` (string): An echo of the query text. See the [`query`](https://www.algolia.com/doc/api-reference/api-parameters/query/) search parameter.

- `queryAfterRemoval` (string, optional): *Note: Only returned when [`removeWordsIfNoResults`](https://www.algolia.com/doc/api-reference/api-parameters/removeWordsIfNoResults/) is set to `lastWords` or `firstWords`.* A markup text indicating which parts of the original query have been removed in order to retrieve a non-empty result set. The removed parts are surrounded by `<em>` tags.

- `params` (string, URL-encoded): An echo of all search parameters.

- `message` (string, optional): Used to return warnings about the query.

- `aroundLatLng` (string, optional): *Note: Only returned when [`aroundLatLngViaIP`](https://www.algolia.com/doc/api-reference/api-parameters/aroundLatLngViaIP/) is set.* The computed geo location. **Warning: for legacy reasons, this parameter is a string and not an object.** Format: `${lat},${lng}`, where the latitude and longitude are expressed as decimal floating point numbers.

- `automaticRadius` (integer, optional): *Note: Only returned for geo queries without an explicitly specified radius (see `aroundRadius`).* The automatically computed radius. **Warning: for legacy reasons, this parameter is a string and not an integer.**

When [`getRankingInfo`](https://www.algolia.com/doc/api-reference/api-parameters/getRankingInfo/) is set to `true`, the following additional fields are returned:

- `serverUsed` (string): Actual host name of the server that processed the request. (Our DNS supports automatic failover and load balancing, so this may differ from the host name used in the request.)

- `parsedQuery` (string): The query string that will be searched, after normalization. Normalization includes removing stop words (if [`removeStopWords`](https://www.algolia.com/doc/api-reference/api-parameters/removeStopWords/) is enabled), and transforming portions of the query string into phrase queries (see [`advancedSyntax`](https://www.algolia.com/doc/api-reference/api-parameters/advancedSyntax/)).

- `timeoutCounts` (boolean) - DEPRECATED: Please use `exhaustiveFacetsCount` in remplacement.

- `timeoutHits` (boolean) - DEPRECATED: Please use `exhaustiveFacetsCount` in remplacement.

... and ranking information is also added to each of the hits (see above).

When [`facets`](https://www.algolia.com/doc/api-reference/api-parameters/facets/) is non-empty, the following additional fields are returned:

- `facets` (object): Maps each facet name to the corresponding facet counts:

    - `${facet_name}` (object): Facet counts for the corresponding facet name:

        - `${facet_value}` (integer): Count for this facet value.

- `facets_stats` (object, optional): *Note: Only returned when at least one of the returned facets contains numerical values.* Statistics for numerical facets:

    - `${facet_name}` (object): The statistics for a given facet:

        - `min` (integer \| float): The minimum value in the result set.

        - `max` (integer \| float): The maximum value in the result set.

        - `avg` (integer \| float): The average facet value in the result set.

        - `sum` (integer \| float): The sum of all values in the result set.

- `exhaustiveFacetsCount` (boolean): Whether the counts of the facet values are exhaustive (`true`) or approximate (`false`). *Note: In some conditions, when a large number of results are matching, the facet counts may not always be exhaustive.*

## Search Parameters

Here is the list of parameters you can use with the search method (`search` [scope](https://www.algolia.com/doc/api-reference/api-parameters#scope)):
Parameters that can also be used in a setSettings also have the `indexing` [scope](https://www.algolia.com/doc/api-reference/api-parameters#scope)

#### Search

#### Attributes

#### Filtering / Faceting

#### Highlighting / Snippeting

#### Pagination

#### Typos

#### Geo-Search

#### Query Strategy

#### Advanced

## Search multiple indices - `multipleQueries` 

You can send multiple queries with a single API call using a batch of queries:

```java
// perform 3 queries in a single API call:
//  - 1st query targets index `categories`
//  - 2nd and 3rd queries target index `products`

List<APIClient.IndexQuery> queries = new ArrayList<APIClient.IndexQuery>();

queries.add(new APIClient.IndexQuery("categories", new Query(myQueryString).setHitsPerPage(3)));
queries.add(new APIClient.IndexQuery("products", new Query(myQueryString).setHitsPerPage(3).setFilters("_tags:promotion"));
queries.add(new APIClient.IndexQuery("products", new Query(myQueryString).setHitsPerPage(10)));

JSONObject res = client.multipleQueries(queries);

System.out.println(res.getJSONArray("results").toString())
```

You can specify a `strategy` parameter to optimize your multiple queries:

- `none`: Execute the sequence of queries until the end.
- `stopIfEnoughMatches`: Execute queries one by one, but stop as soon as the cumulated number of hits is at least `hitsPerPage`.

### Response

The resulting JSON contains the following fields:

- `results` (array): The results for each request, in the order they were submitted. The contents are the same as in [Search an index](https://www.algolia.com/doc/api-client/java1/search/#search-an-index).
    Each result also includes the following additional fields:

    - `index` (string): The name of the targeted index.
    - `processed` (boolean, optional): *Note: Only returned when `strategy` is `stopIfEnoughmatches`.* Whether the query was processed.

## Get Objects - `getObjects` 

You can easily retrieve an object using its `objectID` and optionally specify a comma separated list of attributes you want:

```java
// Retrieves all attributes
index.getObject("myID");
// Retrieves only the firstname attribute
index.getObject("myID", Arrays.asList("firstname"));
```

You can also retrieve a set of objects:

```java
index.getObjects(Arrays.asList("myObj1", "myObj2"));
```

## Search for facet values - `searchForFacetValues` 

When there are many facet values for a given facet, it may be useful to search within them. For example, you may have dozens of 'brands' for a given index of 'clothes'. Rather than displaying all of the brands, it is often best to only display the most popular and add a search input to allow the user to search for the brand that they are looking for.

Searching on facet values is different than a regular search because you are searching only on *facet values*, not *objects*.

The results are sorted by decreasing count. By default, maximum 10 results are returned. This can be adjusted via [`maxFacetHits`](https://www.algolia.com/doc/api-reference/api-parameters/maxFacetHits/). No pagination is possible.

The facet search can optionally take regular search query parameters.
In that case, it will return only facet values that both:

1. match the facet query
2. are contained in objects matching the regular search query.

**Warning:** For a facet to be searchable, it must have been declared with the `searchable()` modifier in the [`attributesForFaceting`](https://www.algolia.com/doc/api-reference/api-parameters/attributesForFaceting/) index setting.

#### Example

Let's imagine we have objects similar to this one:

```json
{
    "name": "iPhone 7 Plus",
    "brand": "Apple",
    "category": [
        "Mobile phones",
        "Electronics"
    ]
}
```

Then:

```java
// Search the values of the "category" facet matching "phone".
System.out.println(index.searchForFacetValues("category", "phone", null));
```

... could return:

```json
{
    "facetHits": [
        {
            "value": "Mobile phones",
            "highlighted": "Mobile <em>phone</em>s",
            "count": 507
        },
        {
            "value": "Phone cases",
            "highlighted": "<em>Phone</em> cases",
            "count": 63
        }
    ]
}
```

Let's filter with an additional, regular search query:

```java
Query query = new Query().setFilters("brand:Apple");

// Search the "category" facet for values matching "phone" in records
// having "Apple" in their "brand" facet.
System.out.println(index.searchFacet("category", "phone", query));
```

... could return:

```json
{
    "facetHits": [
        {
            "value": "Mobile phones",
            "highlighted": "Mobile <em>phone</em>s",
            "count": 41
        }
    ]
}
```

**Warning:** **Building your search implementation in Javascript?** Look at the
[Filtering & Faceting guide](https://www.algolia.com/doc/guides/searching/faceting)
to see how to use Search for facet values from the front-end.



# Indexing



## Add Objects - `addObjects` 

Each entry in an index has a unique identifier called `objectID`.

There are two ways to add an entry to the index:

1. Supplying an `objectID`.
    1. If the `objectID` does not exist in the index, the record will be created
    1. If the `objectID` already exists the record will be replaced
1. Not supplying an `objectID`. Algolia will automatically assign an `objectID` and you will be able to access it in the response.

Using your own unique IDs when creating records is a good way to make future updates easier without having to keep track of Algolia's generated IDs.
The value you provide for objectIDs can be an integer or a string.

You don't need to explicitly create an index, it will be automatically created the first time you add an object.
Objects are schema less so you don't need any configuration to start indexing.
If you wish to configure things, the settings section provides details about advanced settings.

Example with automatic `objectID` assignments:

```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach"));
index.addObjects(array);
```

Example with manual `objectID` assignments:

```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("objectID", "myID1").put("firstname", "Jimmie").put("lastname", "Barninger"));
array.add(new JSONObject().put("objectID", "myID2").put("firstname", "Warren").put("lastname", "Speach"));
index.addObjects(array);
```

To add a single object, use the following method:

```java
JSONObject obj = index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger"), "myID");
System.out.println(obj.getString("objectID"));
```

## Update objects - `saveObjects` 

You have three options when updating an existing object:

 1. Replace all its attributes.
 2. Replace only some attributes.
 3. Apply an operation to some attributes.

**Warning:** Each record needs to contain the `objectID` key.

**Examples**:

To replace all attributes existing objects:

```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "myID"));
array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "myID2"));
index.saveObjects(array);
```

To update a single object, you can use the following method:

```java
index.saveObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("city", "New York"), "myID");
```

## Partial update objects - `partialUpdateObjects` 

You have many ways to update an object's attributes:

 1. Set the attribute value
 2. Add a string or number element to an array
 3. Remove an element from an array
 4. Add a string or number element to an array if it doesn't exist
 5. Increment an attribute
 6. Decrement an attribute

**Warning:** Nested attributes cannot be individually updated. If you specify a nested attribute, it will be treated as a
replacement of its first-level ancestor.

Example to update only the city attribute of an existing object:

```java
index.partialUpdateObject(new JSONObject().put("city", "San Francisco"), "myID");
```

Example to add a tag:

```java
index.partialUpdateObject(new JSONObject().put("_tags", new JSONObject().put("value", "MyTags").put("_operation", "Add")), "myID");
```

Example to remove a tag:

```java
index.partialUpdateObject(new JSONObject().put("_tags", new JSONObject().put("value", "MyTags").put("_operation", "Remove")), "myID");
```

Example to add a tag if it doesn't exist:

```java
index.partialUpdateObject(new JSONObject().put("_tags", new JSONObject().put("value", "MyTags").put("_operation", "AddUnique")), "myID");
```

Example to increment a numeric value:

```java
index.partialUpdateObject(new JSONObject().put("price", new JSONObject().put("value", 42).put("_operation", "Increment")), "myID");
```

Note: Here we are incrementing the value by `42`. To increment just by one, put
`value:1`.

Example to decrement a numeric value:

```java
index.partialUpdateObject(new JSONObject().put("price", new JSONObject().put("value", 42).put("_operation", "Decrement")), "myID");
```

Note: Here we are decrementing the value by `42`. To decrement just by one, put
`value:1`.

To partial update multiple objects using one API call, you can use the following method:

```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("firstname", "Jimmie").put("objectID", "myID"));
array.add(new JSONObject().put("firstname", "Warren").put("objectID", "myID2"));
index.partialUpdateObjects(array);
```

## Delete objects - `deleteObjects` 

You can delete objects using their `objectID`:

```java
List<String> ids = new ArrayList<String>();
ids.add("myID1");
ids.add("myID2");
index.deleteObjects(ids);
```

To delete a single object, you can use the following method:

```java
index.deleteObject("myID");
```

## Delete by query - `deleteByQuery` 

The "delete by query" helper deletes all objects matching a query. Internally, the API client will browse the index (as in [Backup / Export an index](https://www.algolia.com/doc/api-client/java1/advanced/#backup--export-an-index)), delete all matching hits, and wait until all deletion tasks have been applied.

**Warning:** Be careful when using this method. Calling it with an empty query will result in cleaning the index of all its records.

```java
Query query = /* [ ... ] */;
index.deleteByQuery(query);
```

## Wait for operations - `waitTask` 

All write operations in Algolia are asynchronous by design.

It means that when you add or update an object to your index, our servers will
reply to your request with a `taskID` as soon as they understood the write
operation.

The actual insert and indexing will be done after replying to your code.

You can wait for a task to complete using the `waitTask` method on the `taskID` returned by a write operation.

For example, to wait for indexing of a new object:

```java
JSONObject res = index.addObject(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger"));
index.waitTask(String.valueOf(res.getLong("taskID")));
```

If you want to ensure multiple objects have been indexed, you only need to check
the biggest `taskID`.



# Settings



## Get settings - `getSettings` 

You can retrieve settings:

```java
System.out.println(index.getSettings());
```

## Set settings - `` 

```java
index.setSettings(new JSONObject().append("customRanking", "desc(followers)"));
```

You can find the list of parameters you can set in the [Settings Parameters](#index-settings-parameters) section

**Warning**

Performance wise, it's better to do a `` before pushing the data

### Replica settings

You can forward all settings updates to the replicas of an index by using the `forwardToReplicas` option:

```java
index.setSettings(new JSONObject().append("customRanking", "desc(followers)"), true);
```

## Index settings parameters

You can see the full list of settings parameters here:
[https://www.algolia.com/doc/api-reference/api-parameters/](https://www.algolia.com/doc/api-reference/api-parameters/)



# Manage Indices



## Create an index
  
You don’t need to explicitly create an index, it will be automatically created the first time you [add an object](https://www.algolia.com/doc/api-client/java1/indexing/#add-objects) or [set settings](https://www.algolia.com/doc/api-client/java1/settings/#set-settings).

## List indices - `listIndexes` 

You can list all your indices along with their associated information (number of entries, disk size, etc.) with the `listIndexes` method:

```java
client.listIndexes();
```

## Delete an index - `deleteIndex` 

You can delete an index using its name:

```java
client.deleteIndex("contacts");
```

## Clear an index - `clearIndex` 

You can delete the index contents without removing settings and index specific API keys by using the `clearIndex` command:

```java
index.clearIndex();
```

## Copy index - `copyTo` 

You can copy an existing index using the `copy` command.

**Warning**: The copy command will overwrite the destination index.

```java
// Copy MyIndex in MyIndexCopy
client.copyIndex("MyIndex", "MyIndexCopy");
```

## Move index - `moveIndex` 

In some cases, you may want to totally reindex all your data. In order to keep your existing service
running while re-importing your data we recommend the usage of a temporary index plus an atomical
move using the `moveIndex` method.

```java
// Rename MyTmpIndex to MyIndex (and overwrite it)
client.moveIndex("MyTmpIndex", "MyIndex");
```

**Note:** The moveIndex method overrides the destination index, and deletes the temporary one.
  In other words, there is no need to call the `clearIndex` or `deleteIndex` methods to clean the temporary index.
It also overrides all the settings of the destination index (except the [`replicas`](https://www.algolia.com/doc/api-reference/api-parameters/replicas/) parameter that need to not be part of the temporary index settings).

**Recommended steps**
If you want to fully update your index `MyIndex` every night, we recommend the following process:

 1. Get settings and synonyms from the old index using [Get settings](https://www.algolia.com/doc/api-client/java1/settings/#get-settings)
  and [Get synonym](https://www.algolia.com/doc/api-client/java1/synonyms/#get-synonym).
 1. Apply settings and synonyms to the temporary index `MyTmpIndex`, (this will create the `MyTmpIndex` index)
  using [Set settings](https://www.algolia.com/doc/api-client/java1/settings/#set-settings) and [Batch synonyms](https://www.algolia.com/doc/api-client/java1/synonyms/#batch-synonyms) ([!] Make sure to remove the [`replicas`](https://www.algolia.com/doc/api-reference/api-parameters/replicas/) parameter from the settings if it exists.
 1. Import your records into a new index using [Add Objects](https://www.algolia.com/doc/api-client/java1/indexing/#add-objects)).
 1. Atomically replace the index `MyIndex` with the content and settings of the index `MyTmpIndex`
 using the [Move index](https://www.algolia.com/doc/api-client/java1/manage-indices/#move-index) method.
 This will automatically override the old index without any downtime on the search.
 
 You'll end up with only one index called `MyIndex`, that contains the records and settings pushed to `MyTmpIndex`
 and the replica-indices that were initially attached to `MyIndex` will be in sync with the new data.



# Api keys



## Generate key - `generateSecuredApiKey` 

When you need to restrict the scope of the *Search Key*, we recommend to use *Secured API Key*.
You can generate a *Secured API Key* from the *Search Only API Key* or any search *User API Key*

There is a few things to know about *Secured API Keys*
- They always need to be generated **on your backend** using one of our API Client
- You can generate them on the fly (without any call to the API)
- They will not appear on the dashboard as they are generated without any call to the API
- The key you use to generate it **needs to become private** and you should not use it in your frontend.
- The generated secured API key **will inherit any restriction from the search key it has been generated from**

You can then use the key in your frontend code

```js
var client = algoliasearch('YourApplicationID', 'YourPublicAPIKey');

var index = client.initIndex('indexName')

index.search('something', function(err, content) {
  if (err) {
    console.error(err);
    return;
  }

  console.log(content);
});
```

#### Filters

Every filter set in the API key will always be applied. On top of that [`filters`](https://www.algolia.com/doc/api-reference/api-parameters/filters/) can be applied
in the query parameters.

```java
// generate a public API key for user 42. Here, records are tagged with:
//  - 'user_XXXX' if they are visible by user XXXX
String publicKey = client.generateSecuredApiKey("YourSearchOnlyApiKey", new Query().setFilters("_tags:user_42"));
```

**Warning**:

If you set filters in the key `groups:admin`, and `groups:press OR groups:visitors` in the query parameters,
this will be equivalent to `groups:admin AND (groups:press OR groups:visitors)`

##### Having one API Key per User

One of the usage of secured API keys, is to have allow users to see only part of an index, when this index
contains the data of all users.
In that case, you can tag all records with their associated `user_id` in order to add a `user_id=42` filter when
generating the *Secured API Key* to retrieve only what a user is tagged in.

**Warning**

If you're generating *Secured API Keys* using the [JavaScript client](http://github.com/algolia/algoliasearch-client-javascript) in your frontend,
it will result in a security breach since the user is able to modify the filters you've set
by modifying the code from the browser.

#### Valid Until

You can set a Unix timestamp used to define the expiration date of the API key

```java
# generate a public API key that is valid for 1 hour:
long validUntil = System.currentTimeMillis()/1000 + 3600
String publicKey = client.generateSecuredApiKey("YourSearchOnlyApiKey", new Query().setValidUntil(validUntil));
```

#### Index Restriction

You can restrict the key to a list of index names allowed for the secured API key

```java
# generate a public API key that is restricted to 'index1' and 'index2':
List<String> restrictIndices = new ArrayList<>();
restrictIndices.add('index1');
restrictIndices.add('index2');
String publicKey = client.generateSecuredApiKey("YourSearchOnlyApiKey", new Query().setRestrictIndices(restrictIndices));
```

#### Rate Limiting

If you want to rate limit a secured API Key, the API key you generate the secured api key from need to be rate-limited.
You can do that either via the dashboard or via the API using the
[Add API key](https://www.algolia.com/doc/api-client/java1/api-keys/#add-api-key) or [Update api key](https://www.algolia.com/doc/api-client/java1/api-keys/#update-api-key) method

##### User Rate Limiting

By default the rate limits will only use the `IP`.

This can be an issue when several of your end users are using the same IP.
To avoid that, you can set a `userToken` query parameter when generating the key.

When set, a unique user will be identified by his `IP + user_token` instead of only by his `IP`.

This allows you to restrict a single user to performing a maximum of `N` API calls per hour,
even if he shares his `IP` with another user.

```java
// generate a public API key for user 42. Here, records are tagged with:
//  - 'user_XXXX' if they are visible by user XXXX
String publicKey = client.generateSecuredApiKey("YourSearchOnlyApiKey", new Query().setFilters("_tags:user_42").setUserToken("42"));
```

#### Network restriction

For more protection against API key leaking and reuse you can restrict the key to be valid only from specific IPv4 networks

```java
String publicKey = client.generateSecuredApiKey("YourSearchOnlyApiKey", new Query().setRestrictSources("192.168.1.0/24"));
```

## Add API key - `addApiKey` 

To create API keys:

```java
// Creates a new API key that can only perform search actions
JSONObject res = client.addApiKey(Arrays.asList("search"));
System.out.println("Key: " + res.getString("key"));
// Creates a new API key that can only perform search action on this index
JSONObject res = index.addApiKey(Arrays.asList("search"));
System.out.println("Key: " + res.getString("key"));
```

##### ACLs

You need to specify the set of ACLs the key will have.

The following rights can be used:

- `search`: allows to search the index
- `browse`: allows to retrieve all index content via the browse API
- `addObject`: allows to add/update an object in the index (copy/move index are also allowed with this right)
- `deleteObject`: allows to delete objects from the index
- `deleteIndex`: allows to delete or clear index content
- `settings`: allows to get index settings
- `editSettings`: allows to change index settings
- `analytics`: allows to retrieve the analytics through the Analytics API
- `listIndexes`: allows to list all accessible indices

#### Avanced Settings

You can also create an API Key with advanced settings:

##### validity

Add a validity period. The key will be valid for a specific period of time (in seconds).

##### maxQueriesPerIPPerHour

Specify the maximum number of API calls allowed from an IP address per hour. Each time an API call is performed with this key, a check is performed. If the IP at the source of the call did more than this number of calls in the last hour, a 403 code is returned. Defaults to 0 (no rate limit). This parameter can be used to protect you from attempts at retrieving your entire index contents by massively querying the index.

  

Note: If you are sending the query through your servers, you must use the `enableRateLimitForward("TheAdminAPIKey", "EndUserIP", "APIKeyWithRateLimit")` function to enable rate-limit.

##### maxHitsPerQuery

Specify the maximum number of hits this API key can retrieve in one call. Defaults to 0 (unlimited). This parameter can be used to protect you from attempts at retrieving your entire index contents by massively querying the index.

##### indexes

Specify the list of targeted indices. You can target all indices starting with a prefix or ending with a suffix using the '\*' character. For example, "dev\_\*" matches all indices starting with "dev\_" and "\*\_dev" matches all indices ending with "\_dev". Defaults to all indices if empty or blank.

##### referers

Specify the list of referers. You can target all referers starting with a prefix, ending with a suffix using the '\*' character. For example, "https://algolia.com/\*" matches all referers starting with "https://algolia.com/" and "\*.algolia.com" matches all referers ending with ".algolia.com". If you want to allow the domain algolia.com you can use "\*algolia.com/\*". Defaults to all referers if empty or blank.

##### queryParameters

Specify the list of query parameters. You can force the query parameters for a query using the url string format (param1=X&param2=Y...).

##### description

Specify a description to describe where the key is used.

```java
// Creates a new API key that is valid for 300 seconds
JSONObject param = new JSONObject();
param.put("acl", Arrays.asList("search"));
param.put("maxHitsPerQuery", 20);
param.put("maxQueriesPerIPPerHour", 100);
param.put("validity", 300);
param.put("indexes", Arrays.asList("myIndex"));
param.put("referers", Arrays.asList("algolia.com/*"));
param.put("queryParameters", "typoTolerance=strict&ignorePlurals=false");
param.put("description", "Limited search only API key for algolia.com");

JSONObject res = client.addApiKey(param);
System.out.println("Key: " + res.getString("key"));
// Creates a new index specific API key valid for 300 seconds, with a rate limit of 100 calls per hour per IP and a maximum of 20 hits
JSONObject res = index.addApiKey(param);
System.out.println("Key: " + res.getString("key"));
```

## Update api key - `updateApiKey` 

To update the permissions of an existing key:

```java
// Creates a new API key that is valid for 300 seconds
JSONObject res = client.updateApiKey("myAPIKey", Arrays.asList("search"), 300, 0, 0);
Log.d("debug", "Key: " + res.getString("key"));
// Update a index specific API key valid for 300 seconds, with a rate limit of 100 calls per hour per IP and a maximum of 20 hits
JSONObject res = index.updateApiKey("myAPIKey", Arrays.asList("search"), 300, 100, 20);
Log.d("debug", "Key: " + res.getString("key"));
```

To get the permissions of a given key:

```java
// Gets the rights of a key
client.getApiKey("f420238212c54dcfad07ea0aa6d5c45f");
// Gets the rights of an index specific key
index.getApiKey("71671c38001bf3ac857bc82052485107");
```

## Delete api key - `deleteApiKey` 

To delete an existing key:

```java
// Deletes a key
client.deleteApiKey("f420238212c54dcfad07ea0aa6d5c45f");
// Deletes an index specific key
index.deleteApiKey("71671c38001bf3ac857bc82052485107");
```

## Get key permissions - `getApiKey` 

To get the permissions of a given key:

```java
// Gets the rights of a key
client.getApiKey("f420238212c54dcfad07ea0aa6d5c45f");
// Gets the rights of an index specific key
index.getApiKey("71671c38001bf3ac857bc82052485107");
```

## List user keys - `listApiKeys` 

To list existing keys, you can use:

```java
// Lists API Keys
client.listApiKeys();
// Lists API Keys that can access only to this index
index.listApiKeys();
```

Each key is defined by a set of permissions that specify the authorized actions. The different permissions are:

* **search**: Allowed to search.
* **browse**: Allowed to retrieve all index contents via the browse API.
* **addObject**: Allowed to add/update an object in the index.
* **deleteObject**: Allowed to delete an existing object.
* **deleteIndex**: Allowed to delete index content.
* **settings**: allows to get index settings.
* **editSettings**: Allowed to change index settings.
* **analytics**: Allowed to retrieve analytics through the analytics API.
* **listIndexes**: Allowed to list all accessible indexes.



# Synonyms



## Overview

Synonyms tell the engine about sets of words and expressions that should be considered equal with regard to textual relevance.

All synonym records have a type attribute. The two most used types are:

- (Regular) Synonyms - `synonym`: Regular synonyms are the most common, all words or expressions are considered equals

  ```json
  {
     "objectID": "NAME",
     "type": "synonym",
     "synonyms":[
        "tv",
        "television",
        "tv set"
     ]
  }
  ```

- One-way Synonym - `oneWaySynonym`: When the `input` is searched all words or expressions in synonyms are considered equals to the input

  ```json
  {
     "objectID": "NAME",
     "type": "oneWaySynonym",
     "input": "tablet",
     "synonyms":[
        "ipad",
        "galaxy note"
     ]
  }
  ```

If you're looking for other types of synonyms or want more details you can have a look at our [synonyms guide](https://www.algolia.com/doc/guides/textual-relevance/synonyms)

## Save synonym - `saveSynonym` 

This method saves a single synonym record into the index.

In this example, we specify true to forward the creation to replica indices.
By default the behavior is to save only on the specified index.

```java
index.saveSynonym("a-unique-identifier", new Synonym()
           .setSynonyms(Arrays.asList("car", "vehicle", "auto")), true);
```

## Batch synonyms - `batchSynonyms` 

Use the batch method to create a large number of synonyms at once,
forward them to replica indices if desired,
and optionally replace all existing synonyms
on the index with the content of the batch using the replaceExistingSynonyms parameter.

You should always use replaceExistingSynonyms to atomically replace all synonyms
on a production index. This is the only way to ensure the index always
has a full list of synonyms to use during the indexing of the new list.

```java
// Batch synonyms, with replica forwarding and atomic replacement of existing synonyms
index.batchSynonyms(Arrays.asList(
      new Synonym()
           .setObjectID("a-unique-identifier")
           .setSynonyms(Arrays.asList("car", "vehicle", "auto")),
      new Synonym()
           .setObjectID("another-unique-identifier")
           .setSynonyms(Arrays.asList("street", "st"))
), true);
```

## Editing Synonyms

Updating the value of a specific synonym record is the same as creating one.
Make sure you specify the same objectID used to create the record and the synonyms
will be updated.
When updating multiple synonyms in a batch call (but not all synonyms),
make sure you set replaceExistingSynonyms to false (or leave it out,
false is the default value).
Otherwise, the entire synonym list will be replaced only partially with the records
in the batch update.

## Delete synonym - `deleteSynonym` 

Use the normal index delete method to delete synonyms,
specifying the objectID of the synonym record you want to delete.
Forward the deletion to replica indices by setting the forwardToReplicas parameter to true.

```java
// Delete and forward to replicas
index.deleteSynonym("a-unique-identifier", true);
```

## Clear all synonyms - `clearSynonyms` 

This is a convenience method to delete all synonyms at once.
It should not be used on a production index to then push a new list of synonyms:
there would be a short period of time during which the index would have no synonyms
at all.

To atomically replace all synonyms of an index,
use the batch method with the replaceExistingSynonyms parameter set to true.

```java
// Clear synonyms and forward to replicas
index.clearSynonyms(true);
```

## Get synonym - `getSynonym` 

Search for synonym records by their objectID or by the text they contain.
Both methods are covered here.

```java
Optional<AbstractSynonym> synonym = index.getSynonym("a-unique-identifier");
```

## Search synonyms - `searchSynonyms` 

Search for synonym records similar to how you’d search normally.

Accepted search parameters:
- `query`: the actual search query to find synonyms. Use an empty query to browse all the synonyms of an index.
- `type`: restrict the search to a specific type of synonym. Use an empty string to search all types (default behavior). Multiple types can be specified using a comma-separated list or an array.
- `page`: the page to fetch when browsing through several pages of results. This value is zero-based.
- `hitsPerPage`: the number of synonyms to return for each call. The default value is 100.

```java
// Searching for "street" in synonyms and one-way synonyms; fetch the second page with 10 hits per page
SearchSynonymResult results = index.searchSynonyms(new SynonymQuery("street").setTypes(Arrays.asList("synonym", "one_way")).setPage(1).setHitsPerPage(10));
```



# Advanced



## Custom batch - `batch` 

You may want to perform multiple operations with one API call to reduce latency.

If you have one index per user, you may want to perform a batch operations across several indices.
We expose a method to perform this type of batch:

```java
List<JSONObject> array = new ArrayList<JSONObject>();
array.add(new JSONObject().put("action". "addObject").put("indexName", "index1")
  .put("body", new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger")));
array.add(new JSONObject().put("action". "addObject").put("indexName", "index2")
  .put("body", new JSONObject().put("firstname", "Warren").put("lastname", "Speach")));
client.batch(array);
```

The attribute **action** can have these values:

- addObject
- updateObject
- partialUpdateObject
- partialUpdateObjectNoCreate
- deleteObject

## Backup / Export an index - `browse` 

The `search` method cannot return more than 1,000 results. If you need to
retrieve all the content of your index (for backup, SEO purposes or for running
a script on it), you should use the `browse` method instead. This method lets
you retrieve objects beyond the 1,000 limit.

This method is optimized for speed. To make it fast, distinct, typo-tolerance,
word proximity, geo distance and number of matched words are disabled. Results
are still returned ranked by attributes and custom ranking.

#### Response Format

##### Sample

```json
{
  "hits": [
    {
      "firstname": "Jimmie",
      "lastname": "Barninger",
      "objectID": "433"
    }
  ],
  "processingTimeMS": 7,
  "query": "",
  "params": "filters=level%3D20",
  "cursor": "ARJmaWx0ZXJzPWxldmVsJTNEMjABARoGODA4OTIzvwgAgICAgICAgICAAQ=="
}
```

##### Fields

- `cursor` (string, optional): A cursor to retrieve the next chunk of data. If absent, it means that the end of the index has been reached.
- `query` (string): Query text used to filter the results.
- `params` (string, URL-encoded): Search parameters used to filter the results.
- `processingTimeMS` (integer): Time that the server took to process the request, in milliseconds. *Note: This does not include network time.*

The following fields are provided for convenience purposes, and **only when the browse is not filtered**:

- `nbHits` (integer): Number of objects in the index.
- `page` (integer): Index of the current page (zero-based).
- `hitsPerPage` (integer): Maximum number of hits returned per page.
- `nbPages` (integer): Number of pages corresponding to the number of hits. Basically, `ceil(nbHits / hitsPerPage)`.

#### Example

```java
// Iterate with a filter over the index
Iterator<JSONObject> it = index.browse(new Query("text").setFilters("i<42"));

// Retrieve the next cursor from the browse method
Iterator<JSONObject> it  = index.browseFrom(new Query("text").setFilters("i<42"), "");
System.out.println(it.getCursor());
```

## Get latest logs - `getLogs` 

You can retrieve the latest logs via this API. Each log entry contains:

* Timestamp in ISO-8601 format
* Client IP
* Request Headers (API Key is obfuscated)
* Request URL
* Request method
* Request body
* Answer HTTP code
* Answer body
* SHA1 ID of entry

You can retrieve the logs of your last 1,000 API calls and browse them using the offset/length parameters:

#### offset

Specify the first entry to retrieve (0-based, 0 is the most recent log entry). Defaults to 0.

#### length

Specify the maximum number of entries to retrieve starting at the offset. Defaults to 10. Maximum allowed value: 1,000.

#### onlyErrors

Retrieve only logs with an HTTP code different than 200 or 201. (deprecated)

#### type

Specify the type of logs to retrieve:

* `query`: Retrieve only the queries.
* `build`: Retrieve only the build operations.
* `error`: Retrieve only the errors (same as `onlyErrors` parameters).

```java
// Get last 10 log entries
client.getLogs();
// Get last 100 log entries
client.getLogs(0, 100);
```

## Retry logic

Algolia's architecture is heavily redundant, to provide optimal reliability. Every application is hosted on at least three different servers. As a developer, however, you don't need to worry about those details. The API Client handles them for you:

- It leverages our dynamic DNS to perform automatic **load balancing** between servers.
- Its **retry logic** switches the targeted server whenever it detects that one of them is down or unreachable. Therefore, a given request will not fail unless all servers are down or unreachable at the same time.

**Note:** Application-level errors (e.g. invalid query) are still reported without retry.

## Error handling

Requests can fail for two main reasons:

1. **Network issues:** the server could not be reached, or did not answer within the timeout.
2. **Application error:** the server rejected the request.

In the latter case, the error reported by the API client contains:

- an HTTP **status code** indicating the type of error;
- an error **message** indicating the cause of the error.

**Caution:** The error message is purely informational and intended for the developer. You should never rely on its content programmatically, as it may change without notice.

## Configuring timeouts

Network & DNS resolution can be slow. That is why we have pre-configured timeouts. We do not advise to change them, but it could make sense to change them in some special cases:

```java
  APIClient client = new APIClient(...);
  client.setTimeout(
    4000, /* connectTimeout */
    4000, /* readTimeout */
  );
  client.setHostDownTimeoutMS(4000);
```



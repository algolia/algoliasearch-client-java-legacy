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





## Table of Contents





# Missing title



## Install

If you're using Maven, add the following dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.algolia</groupId>
    <artifactId>algoliasearch</artifactId>
    <version>[1,]</version>
</dependency>
```

## Quick Start

In 30 seconds, this quick start tutorial will show you how to index and search objects.

### Initialize the client

You first need to initialize the client. For that you need your **Application ID** and **API Key**.
You can find both of them on [your Algolia account](https://www.algolia.com/api-keys).

```java
APIClient client = new APIClient("YourApplicationID", "YourAPIKey");
```

### Push data

Without any prior configuration, you can start indexing contacts in the ```contacts``` index using the following code:
```java
Index index = client.initIndex("contacts");
index.addObject(new JSONObject()
      .put("firstname", "Jimmie")
      .put("lastname", "Barninger")
      .put("followers", 93)
      .put("company", "California Paint"));
index.addObject(new JSONObject()
      .put("firstname", "Warren")
      .put("lastname", "Speach")
      .put("followers", 42)
      .put("company", "Norwalk Crmc"));
```

### Search

You can now search for contacts using firstname, lastname, company, etc. (even with typos):

```java
// search by firstname
System.out.println(index.search(new Query("jimmie")));
// search a firstname with typo
System.out.println(index.search(new Query("jimie")));
// search for a company
System.out.println(index.search(new Query("california paint")));
// search for a firstname & company
System.out.println(index.search(new Query("jimmie paint")));
```

### Configure

Settings can be customized to tune the search behavior. For example, you can add a custom sort by number of followers to the already great built-in relevance:

```java
index.setSettings(new JSONObject().append("customRanking", "desc(followers)"));
```

You can also configure the list of attributes you want to index by order of importance (first = most important):

**Note:** Since the engine is designed to suggest results as you type, you'll generally search by prefix.
In this case the order of attributes is very important to decide which hit is the best:

### Frontend search

**Note:** If you are building a web application, you may be more interested in using our [JavaScript client](https://github.com/algolia/algoliasearch-client-javascript) to perform queries.

It brings two benefits:
  * Your users get a better response time by not going through your servers
  * It will offload unnecessary tasks from your servers

```html
<script src="https://cdn.jsdelivr.net/algoliasearch/3/algoliasearch.min.js"></script>
<script>
var client = algoliasearch('ApplicationID', 'apiKey');
var index = client.initIndex('indexName');

// perform query "jim"
index.search('jim', searchCallback);

// the last optional argument can be used to add search parameters
index.search(
  'jim', {
    hitsPerPage: 5,
    facets: '*',
    maxValuesPerFacet: 10
  },
  searchCallback
);

function searchCallback(err, content) {
  if (err) {
    console.error(err);
    return;
  }

  console.log(content);
}
</script>
```

## Getting Help

- **Need help**? Ask a question to the [Algolia Community](https://discourse.algolia.com/) or on [Stack Overflow](http://stackoverflow.com/questions/tagged/algolia).
- **Found a bug?** You can open a [GitHub issue](https://github.com/algolia/algoliasearch-client-java/issues).


# Missing title



## Search an index - `Search` 

**Notes:** If you are building a web application, you may be more interested in using our [JavaScript client](https://github.com/algolia/algoliasearch-client-javascript) to perform queries. It brings two benefits:
  * Your users get a better response time by not going through your servers
  * It will offload unnecessary tasks from your servers.

To perform a search, you only need to initialize the index and perform a call to the search function.

The search query allows only to retrieve 1000 hits. If you need to retrieve more than 1000 hits (e.g. for SEO), you can use [Backup / Export an index](#backup--export-an-index).

```java
Index index = client.initIndex("contacts");
System.out.println(index.search(new Query("query string")));
System.out.println(index.search(new Query("query string").
             setAttributesToRetrieve(Arrays.asList("firstname", "lastname")).
             setNbHitsPerPage(50)));
```

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

    - `_highlightResult` (object, optional): Highlighted attributes. *Note: Only returned when [attributesToHighlight](/doc/api-client/java1/parameters/attributesToHighlight/) is non-empty.*

        - `${attribute_name}` (object): Highlighting for one attribute.

            - `value` (string): Markup text with occurrences highlighted. The tags used for highlighting are specified via [highlightPreTag](/doc/api-client/java1/parameters/highlightPreTag/) and [highlightPostTag](/doc/api-client/java1/parameters/highlightPostTag/).

            - `matchLevel` (string, enum) = {`none` \| `partial` \| `full`}: Indicates how well the attribute matched the search query.

            - `matchedWords` (array): List of words *from the query* that matched the object.

            - `fullyHighlighted` (boolean): Whether the entire attribute value is highlighted.

    - `_snippetResult` (object, optional): Snippeted attributes. *Note: Only returned when [attributesToSnippet](/doc/api-client/java1/parameters/attributesToSnippet/) is non-empty.*

        - `${attribute_name}` (object): Snippeting for the corresponding attribute.

            - `value` (string): Markup text with occurrences highlighted and optional ellipsis indicators. The tags used for highlighting are specified via [highlightPreTag](/doc/api-client/java1/parameters/highlightPreTag/) and [highlightPostTag](/doc/api-client/java1/parameters/highlightPostTag/). The text used to indicate ellipsis is specified via [snippetEllipsisText](/doc/api-client/java1/parameters/snippetEllipsisText/).

            - `matchLevel` (string, enum) = {`none` \| `partial` \| `full`}: Indicates how well the attribute matched the search query.

    - `_rankingInfo` (object, optional): Ranking information. *Note: Only returned when [getRankingInfo](/doc/api-client/java1/parameters/getRankingInfo/) is `true`.*

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

    - `_distinctSeqID` (integer): *Note: Only returned when [distinct](/doc/api-client/java1/parameters/distinct/) is non-zero.* When two consecutive results have the same value for the attribute used for "distinct", this field is used to distinguish between them.

- `nbHits` (integer): Number of hits that the search query matched.

- `page` (integer): Index of the current page (zero-based). See the [page](/doc/api-client/java1/parameters/page/) search parameter. *Note: Not returned if you use `offset`/`length` for pagination.*

- `hitsPerPage` (integer): Maximum number of hits returned per page. See the [hitsPerPage](/doc/api-client/java1/parameters/hitsPerPage/) search parameter. *Note: Not returned if you use `offset`/`length` for pagination.*

- `nbPages` (integer): Number of pages corresponding to the number of hits. Basically, `ceil(nbHits / hitsPerPage)`. *Note: Not returned if you use `offset`/`length` for pagination.*

- `processingTimeMS` (integer): Time that the server took to process the request, in milliseconds. *Note: This does not include network time.*

- `exhaustiveNbHits` (boolean): Whether the `nbHits` is exhaustive (`true`) or approximate (`false`). *Note: An approximation is done when the query takes more than 50ms to be processed (this can happen when doing complex filters on millions on records).*

- `query` (string): An echo of the query text. See the [query](/doc/api-client/java1/parameters/query/) search parameter.

- `queryAfterRemoval` (string, optional): *Note: Only returned when [removeWordsIfNoResults](/doc/api-client/java1/parameters/removeWordsIfNoResults/) is set to `lastWords` or `firstWords`.* A markup text indicating which parts of the original query have been removed in order to retrieve a non-empty result set. The removed parts are surrounded by `<em>` tags.

- `params` (string, URL-encoded): An echo of all search parameters.

- `message` (string, optional): Used to return warnings about the query.

- `aroundLatLng` (string, optional): *Note: Only returned when [aroundLatLngViaIP](/doc/api-client/java1/parameters/aroundLatLngViaIP/) is set.* The computed geo location. **Warning: for legacy reasons, this parameter is a string and not an object.** Format: `${lat},${lng}`, where the latitude and longitude are expressed as decimal floating point numbers.

- `automaticRadius` (integer, optional): *Note: Only returned for geo queries without an explicitly specified radius (see `aroundRadius`).* The automatically computed radius. **Warning: for legacy reasons, this parameter is a string and not an integer.**

When [getRankingInfo](/doc/api-client/java1/parameters/getRankingInfo/) is set to `true`, the following additional fields are returned:

- `serverUsed` (string): Actual host name of the server that processed the request. (Our DNS supports automatic failover and load balancing, so this may differ from the host name used in the request.)

- `parsedQuery` (string): The query string that will be searched, after normalization. Normalization includes removing stop words (if [removeStopWords](/doc/api-client/java1/parameters/removeStopWords/) is enabled), and transforming portions of the query string into phrase queries (see [advancedSyntax](/doc/api-client/java1/parameters/advancedSyntax/)).

- `timeoutCounts` (boolean) - DEPRECATED: Please use `exhaustiveFacetsCount` in remplacement.

- `timeoutHits` (boolean) - DEPRECATED: Please use `exhaustiveFacetsCount` in remplacement.

... and ranking information is also added to each of the hits (see above).

When [facets](/doc/api-client/java1/parameters/facets/) is non-empty, the following additional fields are returned:

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

Here is the list of parameters you can use with the search method (`search` [scope](#scope)):
Parameters that can also be used in a setSettings also have the `indexing` [scope](#scope)

#### Search

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/query/">query</a><br /><code>search</code></p>

        </td>
        <td>
          <p>The text to search for in the index.</p>

        </td>
      </tr>
</table>

#### Attributes

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/attributesToRetrieve/">attributesToRetrieve</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>List of object attributes you want to retrieve.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/restrictSearchableAttributes/">restrictSearchableAttributes</a><br /><code>search</code></p>

        </td>
        <td>
          <p>List of attributes to be considered for textual search.</p>

        </td>
      </tr>
</table>

#### Filtering / Faceting

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/filters/">filters</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Filter the query with numeric, facet and/or tag filters.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/facets/">facets</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Facets to retrieve.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/maxValuesPerFacet/">maxValuesPerFacet</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Maximum number of facet values returned for each facet.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/facetFilters/">facetFilters</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Filter hits by facet value.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/facetingAfterDistinct/">facetingAfterDistinct</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Force faceting to be applied after de-duplication.</p>

        </td>
      </tr>
</table>

#### Highlighting / Snippeting

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/attributesToHighlight/">attributesToHighlight</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>List of attributes to highlight.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/attributesToSnippet/">attributesToSnippet</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>List of attributes to snippet, with an optional maximum number of words to snippet.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/highlightPreTag/">highlightPreTag</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>String inserted before highlighted parts in highlight and snippet results.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/highlightPostTag/">highlightPostTag</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>String inserted after highlighted parts in highlight and snippet results.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/snippetEllipsisText/">snippetEllipsisText</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>String used as an ellipsis indicator when a snippet is truncated.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/restrictHighlightAndSnippetArrays/">restrictHighlightAndSnippetArrays</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Restrict arrays in highlight and snippet results to items that matched the query.</p>

        </td>
      </tr>
</table>

#### Pagination

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/page/">page</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Number of the page to retrieve.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/hitsPerPage/">hitsPerPage</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Maximum number of hits per page.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/offset/">offset</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Offset of the first hit to return (zero-based).</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/length/">length</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Maximum number of hits to return. (1000 is the maximum)</p>

        </td>
      </tr>
</table>

#### Typos

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/minWordSizefor1Typo/">minWordSizefor1Typo</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Minimum number of characters a word in the query string must contain to accept matches with one typo.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/minWordSizefor2Typos/">minWordSizefor2Typos</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Minimum number of characters a word in the query string must contain to accept matches with two typos.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/typoTolerance/">typoTolerance</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Controls whether typo tolerance is enabled and how it is applied:</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/allowTyposOnNumericTokens/">allowTyposOnNumericTokens</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Whether to allow typos on numbers (“numeric tokens”) in the query string.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/ignorePlurals/">ignorePlurals</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Consider singular and plurals forms a match without typo.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/disableTypoToleranceOnAttributes/">disableTypoToleranceOnAttributes</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>List of attributes on which you want to disable typo tolerance</p>

        </td>
      </tr>
</table>

#### Geo-Search

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/aroundLatLng/">aroundLatLng</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Search for entries around a given location.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/aroundLatLngViaIP/">aroundLatLngViaIP</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Search for entries around a given location automatically computed from the requester’s IP address.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/aroundRadius/">aroundRadius</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Maximum radius for geo search (in meters).</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/aroundPrecision/">aroundPrecision</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Precision of geo search (in meters).</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/minimumAroundRadius/">minimumAroundRadius</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Minimum radius (in meters) used for a geo search when <a href="/doc/api-client/java1/parameters/aroundRadius/">aroundRadius</a> is not set.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/insideBoundingBox/">insideBoundingBox</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Search inside a rectangular area (in geo coordinates).</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/insidePolygon/">insidePolygon</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Search inside a polygon (in geo coordinates).</p>

        </td>
      </tr>
</table>

#### Query Strategy

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/queryType/">queryType</a><br /><code>search</code> <code>settings</code></p>

        </td>
        <td>
          <p>Controls if and how query words are interpreted as prefixes.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/removeWordsIfNoResults/">removeWordsIfNoResults</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Selects a strategy to remove words from the query when it doesn’t match any hits.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/advancedSyntax/">advancedSyntax</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Enables the advanced query syntax.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/optionalWords/">optionalWords</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>List of words that should be considered as optional when found in the query.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/removeStopWords/">removeStopWords</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Remove stop words from the query <strong>before</strong> executing it.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/disableExactOnAttributes/">disableExactOnAttributes</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>List of attributes on which you want to disable computation of the <code>exact</code> ranking criterion</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/exactOnSingleWordQuery/">exactOnSingleWordQuery</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Controls how the <code>exact</code> ranking criterion is computed when the query contains only one word.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/alternativesAsExact/">alternativesAsExact</a><br /><code>setting</code> <code>search</code></p>

        </td>
        <td>
          <p>List of alternatives that should be considered an exact match by the <code>exact</code> ranking criterion.</p>

        </td>
      </tr>
</table>

#### Advanced

<table class='table table-parameters-list-small'>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/distinct/">distinct</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Controls de-duplication of results.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/getRankingInfo/">getRankingInfo</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Enables detailed ranking information.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/numericFilters/">numericFilters</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Filter hits based on values of numeric attributes.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/tagFilters/">tagFilters</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Filter hits by tags.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/analytics/">analytics</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Whether the current query will be taken into account in the Analytics.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/analyticsTags/">analyticsTags</a><br /><code>search</code></p>

        </td>
        <td>
          <p>List of tags to apply to the query in the Analytics.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/synonyms/">synonyms</a><br /><code>search</code></p>

        </td>
        <td>
          <p>Whether to take into account synonyms defined for the targeted index.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/replaceSynonymsInHighlight/">replaceSynonymsInHighlight</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Whether to replace words matched via synonym expansion by the matched synonym in highlight and snippet results.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/minProximity/">minProximity</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Precision of the <code>proximity</code> ranking criterion.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/responseFields/">responseFields</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Choose which fields the response will contain. Applies to search and browse queries.</p>

        </td>
      </tr>
      <tr>
        <td>
          <p><a href="/doc/api-client/java1/parameters/maxFacetHits/">maxFacetHits</a><br /><code>settings</code> <code>search</code></p>

        </td>
        <td>
          <p>Maximum number of facet hits to return during a search for facet values.</p>

        </td>
      </tr>
</table>

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
- `stopIfEnoughMatches`: Execute the sequence of queries until the number of hits is reached by the sum of hits.

### Response

The resulting JSON contains the following fields:

- `results` (array): The results for each request, in the order they were submitted. The contents are the same as in [Search an index](#search-an-index).
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

The results are sorted by decreasing count. By default, maximum 10 results are returned. This can be adjusted via [maxFacetHits](/doc/api-client/java1/parameters/maxFacetHits/). No pagination is possible.

The facet search can optionally take regular search query parameters.
In that case, it will return only facet values that both:

1. match the facet query
2. are contained in objects matching the regular search query.

**Warning:** For a facet to be searchable, it must have been declared with the `searchable()` modifier in the [attributesForFaceting](/doc/api-client/java1/parameters/attributesForFaceting/) index setting.

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

**Warning:** **Building your search implementation in Javascript?** Look at the [Filtering & Faceting guide](/doc/guides/search/filtering-faceting) to see how to use Search for facet values from the front-end.


# Missing title



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
array.add(new JSONObject().put("objectID", "1").put("firstname", "Jimmie").put("lastname", "Barninger"));
array.add(new JSONObject().put("objectID", "2").put("firstname", "Warren").put("lastname", "Speach"));
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
array.add(new JSONObject().put("firstname", "Jimmie").put("lastname", "Barninger").put("objectID", "SFO"));
array.add(new JSONObject().put("firstname", "Warren").put("lastname", "Speach").put("objectID", "LA"));
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
array.add(new JSONObject().put("firstname", "Jimmie").put("objectID", "SFO"));
array.add(new JSONObject().put("firstname", "Warren").put("objectID", "LA"));
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

The "delete by query" helper deletes all objects matching a query. Internally, the API client will browse the index (as in [Backup / Export an index](#backup--export-an-index)), delete all matching hits, and wait until all deletion tasks have been applied.

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


# Missing title



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
[https://www.algolia.com/doc/api-client/java1/parameters/](https://www.algolia.com/doc/api-client/java1/parameters/)


# Missing title



## Create an index

To create an index, you need to perform any indexing operation like:
- set settings
- add object

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
// Rename MyNewIndex in MyIndex (and overwrite it)
client.moveIndex("MyNewIndex", "MyIndex");
```

**Note:** The moveIndex method overrides the destination index, and deletes the temporary one.
  In other words, there is no need to call the `clearIndex` or `deleteIndex` methods to clean the temporary index.
It also overrides all the settings of the destination index (except the [replicas](/doc/api-client/java1/parameters/replicas/) parameter that need to not be part of the temporary index settings).

**Recommended steps**
If you want to fully update your index `MyIndex` every night, we recommend the following process:

 1. Get settings and synonyms from the old index using [Get settings](#get-settings)
  and [Get synonym](#get-synonym).
 1. Apply settings and synonyms to the temporary index `MyTmpIndex`, (this will create the `MyTmpIndex` index)
  using [Set settings](#set-settings) and [Batch synonyms](#batch-synonyms) ([!] Make sure to remove the [replicas](/doc/api-client/java1/parameters/replicas/) parameter from the settings if it exists.
 1. Import your records into a new index using [Add Objects](#add-objects)).
 1. Atomically replace the index `MyIndex` with the content and settings of the index `MyTmpIndex`
 using the [Move index](#move-index) method.
 This will automatically override the old index without any downtime on the search.
 
 You'll end up with only one index called `MyIndex`, that contains the records and settings pushed to `MyTmpIndex`
 and the replica-indices that were initially attached to `MyIndex` will be in sync with the new data.


# Missing title



## Overview

When creating your Algolia Account, you'll notice there are 3 different API Keys:

- **Admin API Key** - it provides full control of all your indices.
*The admin API key should always be kept secure;
do NOT give it to anybody; do NOT use it in any application and always create a new key that will be more restricted*

- **Search-Only API Key** - It allows you to search on every indices.

- **Monitoring API Key** - It allows you to access the [Monitoring API](https://www.algolia.com/doc/rest-api/monitoring)
  
API keys are very sensitive part of your application and should follow our recommendations of [best practices](/doc/guides/security/best-security-practices/#api-key-security).

### Other types of API keys

The *Admin API Key* and *Search-Only API Key* both have really large scope and sometimes you want to give a key to
someone that have restricted permissions, can it be an index, a rate limit, a validity limit, ...

To address those use-cases we have two different type of keys:

- **Secured API Keys**

When you need to restrict the scope of the *Search Key*, we recommend to use *Secured API Key*.
You can generate them on the fly (without any call to the API)
from the *Search Only API Key* or any search *User Key* using the [Generate key](#generate-key) method

- **User API Keys**

If *Secured API Keys* does not meet your requirements, you can make use of *User keys*.
Managing and especially creating those keys requires a call to the API.

We have several methods to manage them:

- [Add API key](#add-api-key)
- [Update api key](#update-api-key)
- [Delete api key](#delete-api-key)
- [List user keys](#list-user-keys)
- [Get key permissions](#get-key-permissions)

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

Every filter set in the API key will always be applied. On top of that [filters](/doc/api-client/java1/parameters/filters/) can be applied
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
[Add API key](#add-api-key) or [Update api key](#update-api-key) method

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


# Missing title



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

If you're looking for other types of synonyms or want more details you can have a look at our [synonyms guide](https://www.algolia.com/doc/guides/relevance/synonyms)

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
- query: the actual search query to find synonyms. Use an empty query to browse all the synonyms of an index.
- type: restrict the search to a specific type of synonym. Use an empty string to search all types (default behavior). Multiple types can be specified using a comma-separated list or an array.
- page: the page to fetch when browsing through several pages of results. This value is zero-based.
hitsPerPage: the number of synonyms to return for each call. The default value is 100.

```java
// Searching for "street" in synonyms and one-way synonyms; fetch the second page with 10 hits per page
SearchSynonymResult results = index.searchSynonyms(new SynonymQuery("street").setTypes(Arrays.asList("synonym", "one_way")).setPage(1).setHitsPerPage(10));
```


# Missing title



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




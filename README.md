# Algolia Search API Client for Java

[Algolia Search](https://www.algolia.com) is a hosted full-text, numerical, and faceted search engine capable of delivering realtime results from the first keystroke.

The **Algolia Search API Client for Java**
lets you easily use the [Algolia Search REST API](https://www.algolia.com/doc/rest-api/search) from
your Java code.

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






# Install the API client



## Install

If you're using `Maven`, add the following dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>com.algolia</groupId>
    <artifactId>algoliasearch</artifactId>
    <version>[1,]</version>
</dependency>
```

## Language-specific notes

## Init Index

To begin, you will need to initialize the client. In order to do this you will need your **Application ID** and **API Key**.
You can find both on [your Algolia account](https://www.algolia.com/api-keys).

```java
APIClient client = new APIClient("YourApplicationID", "YourAPIKey");
Index index = client.initIndex("your_index_name");
```



# Search



## Methods

## Building search UIs

If you are building a web application, we recommend using one
of our [frontend search UI libraries](https://www.algolia.com/doc/guides/search-ui/search-libraries/) instead of the API client directly.

It brings several benefits:
  * Your users will see a better response time as the request will not need to go through your servers
  * You will be able to offload unnecessary tasks from your servers

To get started with building search UIs, take a look at these tutorials:

<a href="/doc/tutorials/search-ui/instant-search/build-an-instant-search-results-page/instantsearchjs/" class="flex-container">

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

</a><a href="/doc/tutorials/search-ui/autocomplete/auto-complete/" class="flex-container">

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

## Related Resources

<a href="/doc/api-reference/search-api-parameters/" class="flex-container">

<svg width="80" height="80" viewBox="0 0 80 80" xmlns="http://www.w3.org/2000/svg">
  <title>View API Reference</title>
  <defs>
    <linearGradient x1="31.234%" y1="-9.229%" x2="81.126%" y2="104.898%" id="aaf41">
      <stop stop-color="#B84592" offset="0%" />
      <stop stop-color="#FF4F81" offset="100%" />
    </linearGradient>
    <linearGradient x1="12.497%" y1="50%" y2="40.413%" id="baf41">
      <stop stop-color="#B84592" offset="0%" />
      <stop stop-color="#FF4F81" offset="100%" />
    </linearGradient>
  </defs>
  <g fill="none" fill-rule="evenodd">
    <rect fill="#FFF" width="80" height="80" rx="6" />
    <g class="animatable">
      <path d="M11.535 14.464l7.612 4.307c.523.297.846.85.846 1.452v8.556c0 .6-.323 1.155-.846 1.45l-7.612 4.308a1.664 1.664 0 0 1-1.64 0L2.28 30.23a1.674 1.674 0 0 1-.845-1.452v-8.556c0-.6.324-1.155.846-1.45l7.614-4.308a1.664 1.664 0 0 1 1.64 0z" fill="url(#aaf41)" transform="translate(25 23.333)" />
    </g>
    <g class="animatable">
      <path d="M23.44 7.464l7.612 4.307c.523.297.846.85.846 1.452v8.556c0 .6-.323 1.155-.846 1.45l-7.612 4.308a1.668 1.668 0 0 1-1.642 0l-7.612-4.307a1.668 1.668 0 0 1-.846-1.452v-8.556c0-.6.323-1.155.846-1.45l7.612-4.308a1.668 1.668 0 0 1 1.642 0z" fill="url(#baf41)" transform="translate(25 23.333)" />
    </g>
    <g class="animatable">
      <path d="M11.535.464l7.612 4.307c.523.297.846.85.846 1.452v8.556c0 .6-.323 1.155-.846 1.45l-7.612 4.308a1.664 1.664 0 0 1-1.64 0L2.28 16.23a1.674 1.674 0 0 1-.845-1.452V6.222c0-.6.324-1.155.846-1.45L9.895.463a1.664 1.664 0 0 1 1.64 0z" fill="url(#aaf41)" transform="translate(25 23.333)" />
    </g>
  </g>
</svg>

API reference

Search API parameters

</a>



# Indexing



## Methods

## Creating indexes

You don't need to explicitly create an index, as it will be automatically created the first time you add an object.

Objects are schema-less so you don't need any pre-configuration to start indexing.

If you wish to configure your index, the [settings section](https://www.algolia.com/doc/api-client/settings) provides details about advanced settings.

## Add, Update and Partial Update differences

### Add Objects

The [`Add Objects`](https://www.algolia.com/doc/api-reference/api-methods/add-objects/) method does not require an `objectID`.

- If you do supply an `objectID`:
  - If the `objectID` does not exist in the index, the record will be created
  - If the `objectID` already exists, the record will be replaced
- If you do not supply an `objectID`:
  - Algolia will automatically assign an `objectID`, which will be returned in the response

### Update Objects

The [`Update Objects`](https://www.algolia.com/doc/api-reference/api-methods/update-objects/) method requires an `objectID`.

- If the `objectID` exists, the record will be replaced
- If the `objectID` does not exist, the record is created 
- If no `objectID` is supplied, the method returns an error

**Note:** Update Object is also known as `Save Object`. In this context, the terms are used interchangeably.

### Partial Update Ojects

The [`Partial Update Objects`](https://www.algolia.com/doc/api-reference/api-methods/partial-update-objects/) method requires an `objectID`.

- If the `objectID` exists, the attributes will be replaced
- If the `objectID` does not exist, the record is created
- If no `objectID` is specified, the method returns an error

**Note:** `Partial Update` does not replace the whole object, it only adds, removes, or updates the attributes mentioned; the remaining attributes are left untouched. This is different from `Add Object` and `Update Object`, both of which replace the whole object.

### For all three

- The method for all three can be singular or plural.
  - If singular (e.g. AddObject), the method accepts only one object as a parameter
  - If plural (e.g. AddObjects), the method can accept one or many objects

**Note:** See the indvidual methods for more information on syntax and usage.

## Terminology

### Object / Record
We use the words "object" and "record" interchangeably. Sometimes within the same sentence. 
While they are certainly different, depending on the context, for us, they are the same. 
So don't place any significance on their usage:

- Indexes contain "objects" or "records"
- JSON contains "objects" or "records"

### Indexes / Indices

We use these words interchangeably. 
Sometimes the API itself will use one or the other. 
In the documentation, we always use "indexes" - unless the underlying API is using "indices", in which case we adopt that usage.

In either case, don't place any significance on their usage.
  
### Attribute
All objects and records contain attributes. Sometimes we refer to them as fields, or elements. Within the search and indexing contexts, we often speak of settings and parameters. Again, these terms are mostly interchangeable.

Some attributes are simple key/value pairs. But others can be more complex, making it look more like a collection or an object.



# Settings



## Methods

## The *scope* of settings (and parameters)

Settings are set on the index and/or during a particular query. In both cases, they are sent to Algolia using *parameters*.
- For the index, we use the [set settings](https://www.algolia.com/doc/api-reference/api-methods/set-settings/) method.
- For the search, we use the [search](https://www.algolia.com/doc/api-reference/api-methods/search/) method.

Importantly, each parameter has a scope (See [API Parameters](https://www.algolia.com/doc/api-reference/api-parameters)). There are 3 scopes:

#### `settings`

The setting can only be used in the [set settings](https://www.algolia.com/doc/api-reference/api-methods/set-settings/) method. Meaning that it is not available as a search parameter.

Index settings are built directly into your index at indexing time, and they impact every search.

#### `search`

Individual queries can be parameterized. To do this, you pass search parameters to the [search](https://www.algolia.com/doc/api-reference/api-methods/search) method. These parameters affect only those queries that use it. And there is not index default for them.

#### `settings` `search`

When applying both, you create a default/override logic: you set an index default using the [set settings](https://www.algolia.com/doc/api-reference/api-methods/set-settings/) method that can be overriden by your [search](https://www.algolia.com/doc/api-reference/api-methods/search/) method.

**Note:** Note that, if a setting or a parameter is not used, the system will apply an engine level default.

### Example

Just to make it more concrete, here is an example of an index setting, where all queries performed on this index will use a `queryType` of `prefixLast`:

```js
index.setSettings({
  queryType: 'prefixLast'
});
```

And here is a query that overrides that index setting with `prefixAll`:

```js
index.search({
  query: 'query',
  queryType: 'prefixAll'
});
```

### Categories

As you start fine-tuning Algolia, you will want to use more of its settings.
Mastering these settings will enable you to get the best out of Algolia.

To help you navigate our list of settings, we've created the following setting categories:

- [Search](https://www.algolia.com/doc/api-reference/api-parameters/#search)
- [Attributes](https://www.algolia.com/doc/api-reference/api-parameters/#attributes)
- [Ranking](https://www.algolia.com/doc/api-reference/api-parameters/#ranking)
- [Faceting](https://www.algolia.com/doc/api-reference/api-parameters/#faceting)
- [Filtering](https://www.algolia.com/doc/api-reference/api-parameters/#filtering)
- [Highlighting / Snippeting](https://www.algolia.com/doc/api-reference/api-parameters/#highlighting--snippeting)
- [Pagination](https://www.algolia.com/doc/api-reference/api-parameters/#pagination)
- [Typos](https://www.algolia.com/doc/api-reference/api-parameters/#typos)
- [Geo-Search](https://www.algolia.com/doc/api-reference/api-parameters/#geo-search)
- [Query Strategy](https://www.algolia.com/doc/api-reference/api-parameters/#query-strategy)
- [Performance](https://www.algolia.com/doc/api-reference/api-parameters/#performance)
- [Advanced](https://www.algolia.com/doc/api-reference/api-parameters/#advanced)

### For a full list of settings:

<a href="/doc/api-reference/settings-api-parameters/" class="flex-container">

<svg width="80" height="80" viewBox="0 0 80 80" xmlns="http://www.w3.org/2000/svg">
  <title>View API Reference</title>
  <defs>
    <linearGradient x1="31.234%" y1="-9.229%" x2="81.126%" y2="104.898%" id="aaf41">
      <stop stop-color="#B84592" offset="0%" />
      <stop stop-color="#FF4F81" offset="100%" />
    </linearGradient>
    <linearGradient x1="12.497%" y1="50%" y2="40.413%" id="baf41">
      <stop stop-color="#B84592" offset="0%" />
      <stop stop-color="#FF4F81" offset="100%" />
    </linearGradient>
  </defs>
  <g fill="none" fill-rule="evenodd">
    <rect fill="#FFF" width="80" height="80" rx="6" />
    <g class="animatable">
      <path d="M11.535 14.464l7.612 4.307c.523.297.846.85.846 1.452v8.556c0 .6-.323 1.155-.846 1.45l-7.612 4.308a1.664 1.664 0 0 1-1.64 0L2.28 30.23a1.674 1.674 0 0 1-.845-1.452v-8.556c0-.6.324-1.155.846-1.45l7.614-4.308a1.664 1.664 0 0 1 1.64 0z" fill="url(#aaf41)" transform="translate(25 23.333)" />
    </g>
    <g class="animatable">
      <path d="M23.44 7.464l7.612 4.307c.523.297.846.85.846 1.452v8.556c0 .6-.323 1.155-.846 1.45l-7.612 4.308a1.668 1.668 0 0 1-1.642 0l-7.612-4.307a1.668 1.668 0 0 1-.846-1.452v-8.556c0-.6.323-1.155.846-1.45l7.612-4.308a1.668 1.668 0 0 1 1.642 0z" fill="url(#baf41)" transform="translate(25 23.333)" />
    </g>
    <g class="animatable">
      <path d="M11.535.464l7.612 4.307c.523.297.846.85.846 1.452v8.556c0 .6-.323 1.155-.846 1.45l-7.612 4.308a1.664 1.664 0 0 1-1.64 0L2.28 16.23a1.674 1.674 0 0 1-.845-1.452V6.222c0-.6.324-1.155.846-1.45L9.895.463a1.664 1.664 0 0 1 1.64 0z" fill="url(#aaf41)" transform="translate(25 23.333)" />
    </g>
  </g>
</svg>

API reference

Settings API parameters

</a>



# Manage Indices



## Methods

## Create an index

You don’t need to explicitly create an index, it will be automatically created the first time you
[add an object](https://www.algolia.com/doc/api-reference/api-methods/add-objects)
or
[set settings](https://www.algolia.com/doc/api-reference/api-methods/set-settings).



# Api keys



## Methods

## *Adding* and *Generating* API keys

It is important to understand the difference between the [`Add API Key`](https://www.algolia.com/doc/api-reference/api-methods/add-api-key) and [`Generate secured API Key`](https://www.algolia.com/doc/api-reference/api-methods/generate-secured-api-key/)  methods.

For example:
- `Add API key` is executed on the Algolia server; `Generate Secured API key` is executed on your own server, not Algolia's.
- Keys *added* appear on the dashboard; keys *generated* don't.
- You *add* keys that are fixed and have very precise permissions. They are often used to target specific indexes, users, or application use-cases. They are also used to *generate* Secured API Keys.

For a full discussion:

<a href="/doc/guides/security/api-keys/" class="flex-container">

<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
  <title>Security</title>
  <defs>
    <linearGradient x1="50%" y1="-227.852%" x2="77.242%" y2="191.341%" id="a">
      <stop stop-color="#8995C7" offset="0%" />
      <stop stop-color="#F1F4FD" offset="100%" />
    </linearGradient>
  </defs>
  <g fill="none" fill-rule="evenodd">
    <path d="M11.247 5.122a2.524 2.524 0 0 1 1.506-.014l4.76 1.443c.29.088.487.35.487.642v1.735c0 3.937-2.008 7.626-5.369 9.886-.38.248-.882.248-1.262 0C8.008 16.554 6 12.864 6 8.928v-1.72c0-.292.198-.555.487-.642l4.76-1.444zM12 13a2 2 0 1 0 0-4 2 2 0 0 0 0 4z" fill="url(#a)" fill-rule="nonzero" />
  </g>
</svg>

Algolia Concepts

API Keys

</a>



# Synonyms



## Methods

## Algolia synonyms

A *synonym record* includes an objectID, the type of synonym, and a list of words
as synonyms.

```
{
  objectID: 'a-unique-identifier',
  type: 'synonym',
  synonyms: ['car', 'vehicle', 'auto']
}
```

There are 4 types of synonym:
  - `bi-directional`, where the words are interchangeable
  - `one-way`, where a master word is used to find the other words, but not vice-versa
  - `altcorrection1` or `altcorrection2`, where synonyms are used to resolve [typos of 1 or 2 degrees](https://www.algolia.com/doc/guides/textual-relevance/typo-tolerance/#how-typos-are-calculated-based-on-damerau-levenshtein-distance)
  - `placeholder`, where you define tokens (variables) that take any value from a list of defined words. See [placeholders](https://www.algolia.com/doc/guides/textual-relevance/synonyms/#placeholders).

For more detail:

<a href="/doc/guides/textual-relevance/synonyms/" class="flex-container">

<svg width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
  <title>Relevance</title>
  <defs>
    <linearGradient x1="50%" y1="-227.852%" x2="77.242%" y2="191.341%" id="a">
      <stop stop-color="#8995C7" offset="0%" />
      <stop stop-color="#F1F4FD" offset="100%" />
    </linearGradient>
  </defs>
  <g fill="none" fill-rule="evenodd">
    <path d="M12 20a8 8 0 1 1 0-16 8 8 0 0 1 0 16zm0-2a6 6 0 1 0 0-12 6 6 0 0 0 0 12zm0-2a4 4 0 1 1 0-8 4 4 0 0 1 0 8zm0-2a2 2 0 1 0 0-4 2 2 0 0 0 0 4z" fill="url(#a)" />
  </g>
</svg>

Algolia Concepts

Synonyms

</a>



# Query Rules



## Methods

## Overview

**Query Rules** allows performing some ad hoc pre- and post-processing on queries matching specific patterns.
For more details, please refer to our [Rules guide](https://www.algolia.com/doc/guides/query-rules/query-rules-overview/).

### Miscellaneous

As their name implies, Query Rules apply at query time.
Therefore, some [search parameters](https://www.algolia.com/doc/api-reference/api-parameters/#query-rules) control how rules are applied.

Most of the methods manipulate rule objects, described in detail in the [Rule Schema](#rule-schema) below.

Just like for objects or synonyms, write methods for rules are asynchronous: they return a `taskID` that can be used by [Wait for operations](https://www.algolia.com/doc/api-reference/api-methods/wait-task/).

## Rule Schema

A rule is described by a JSON object, containing the following fields:

- `objectID` (string): Unique identifier for the rule (format: `[A-Za-z0-9_-]+`)

- `consequence` (object): **Consequence** of the rule. *At least one* of the following must be used:
    - `params` (object) [optional]: Additional search parameters. Any valid search parameter is allowed. Specific treatment is applied to these fields:
        - `query` (string or object) [optional]: When a string, it replaces the entire query string. When an object, describes incremental edits to be made to the query string. (Of course, replacing and editing is incompatible.) The following edits are supported:
            - `remove` (array of strings) [optional]: Tokens (literals or placeholders) from the query pattern that should be removed from the query string.
        - `automaticFacetFilters` (array of strings) [optional]: Names of facets to which automatic filtering must be applied; they must match the facet name of a facet value placeholder in the query pattern. Ex. `facetName1, facetName2`. You can specify a score: `facetName1<score=5>, facetName2<score=1>`.
        - `automaticOptionalFacetFilters` (array of strings) [optional]: Same syntax as `automaticFacetFilters`, but behaves like `optionalFacetFilters`.
    - `promote` (array of objects) [optional]: Objects to promote as hits. Each object must contain the following fields:
        - `objectID` (string): Unique identifier of the object to promote
        - `position` (integer): Promoted rank for the object (zero-based)
    - `userData` (object) [optional]: Custom JSON object that will be appended to the `userData` array in the response. This object is not interpreted by the API. It is limited to 1kB of minified JSON.
- `description` (string) [optional]: This field is intended for rule management purposes, in particular to ease searching for rules and presenting them to human readers. It is not interpreted by the API.

### Query pattern syntax

Query patterns are expressed as a string with a specific syntax. A pattern is a sequence of one or more tokens, which can be either:

- **Facet value placeholder**: `{facet:$facet_name}`. Example: `{facet:brand}`.
- **Literal**: the world itself. Example: `Algolia`.

Special characters (`{`, `}`, `:` and `\`) must be escaped by preceding them with a backslash (`\`) if they are to be treated as literals.



# MultiClusters API Client



## Methods

## A Brief Technical Overview

### How to split the data (Logical Split)

The data is split *logically*. We decided not to go with a *hash-based* split, which requires the aggregation of answers from multiple servers and adds network latency to the response time. Normally, the data will be user-partitioned - split according to a user-id.

### Uses a single appID

If we were to follow the logic of using one appID per cluster, multi-clusters would require many appIDs. However, this would be difficult to manage, especially when moving data from one cluster to another in order to balance the load. Our API therefore relies on a single appID: the engine routes requests to a specific destination cluster, using a new HTTP header, `X-ALGOLIA-USER-ID`, and a mapping that associates a `userID` to a cluster.

### What MCM doesn't do

As mentioned, the data is broken up logically. The split is done in such a way that requires only one server to perform a complete search. This API doesn't aggregate the response from multiple clusters. We designed the multi-clusters feature in order to stay fast even with a lot of clusters in multiple regions.

### Shared configuration

With MCM, all the settings, rules, synonyms and api keys operations are replicated on all the machine in order to have the same configuration inside the clusters. Only the records stored in the index are different between two clusters.

### Shared data

For some use cases, there are two types of data:

 - Public data
 - Private user data

The public data can be searched at the same time as private user data. With MCM, it's possible to create public records with the multi-clusters using the special userID value \* in order to replicate the record on all the clusters and make it available for search. We show this in our [Public / Private data tutorial](https://www.algolia.com/doc/tutorials/infra/multi-clusters/multi-clusters).

### ObjectIDs

The objectIDs needs to be unique between userID to avoid a record of one userID to override the record of another userID. The objectID needs to be uniq also because of the shared data which can be retrieved at the same time than the data of one specific customer. We recommend to append to the objectID, the userID of the specific user to be sure the objectID is unique.

### Number of indices

MCM is design to work on a small number of indices (< 100). This limitation is mainly here to preserve the performance of the user migration. To migrate a user from one cluster to another, the engine needs to enumerate all the records of this specific user in order to send it to the destination cluster and so loop on all the indices, the cost of the operation is directly linked to the number of indices.

A small number of indices also allow the engine to optimize more the indexing operations by batching the operation of one index together.

### Check out our Tutorial

Perhaps the best way to understand the MultiClusters API is to check out our [MCM tutorial], where explain, with code samples, the most important endpoints.

### Limitation v0.1

For v0.1, the assignment of users to clusters won't be automatic: if a user is not properly assigned, or not found, the call will be rejected.

**Warning:** As you will notice, the documentation is actually using the REST API endpoints directly. We will soon be rolling out our API clients methods.

### How to get the feature

MCM needs to be enabled on your cluster. You can contact support@algolia.com for more information.

## MultiCluster usage

With a multi-cluster setup, the userID needs to be specified for each of the following methods:

  - [Search an index](https://www.algolia.com/doc/api-reference/api-methods/search/)
  - [Search multiple indexes](https://www.algolia.com/doc/api-reference/api-methods/multiple-queries/)
  - [Search for facet values](https://www.algolia.com/doc/api-reference/api-methods/search-for-facet-values/)
  - [Browse an index](https://www.algolia.com/doc/api-reference/api-methods/browse/)
  - [Add objects](https://www.algolia.com/doc/api-reference/api-methods/add-objects/)
  - [Delete objects](https://www.algolia.com/doc/api-reference/api-methods/delete-objects/)
  - [Delete by query](https://www.algolia.com/doc/api-reference/api-methods/delete-by-query/)
  - [Partial update objects](https://www.algolia.com/doc/api-reference/api-methods/partial-update-objects/)
  - [Get objects](https://www.algolia.com/doc/api-reference/api-methods/get-objects/)
  - [Custom batch](https://www.algolia.com/doc/api-reference/api-methods/batch/)
  - [Wait for operations](https://www.algolia.com/doc/api-reference/api-methods/wait-task/)

Each of these methods allows you to pass any `extra header` to the request. We'll make use of the `X-Algolia-User-ID` header.

Here is an example of the `search` method, but the principle is the same for all the methods listed above:

search_multi_cluster

You can find an example of how to pass `extra headers` for the other methods in their respective documentation.



# Advanced



## Methods

## Retry logic

Algolia's architecture is heavily redundant, to provide optimal reliability. Every application is hosted on at least three different servers. As a developer, however, you don't need to worry about those details. The API Client handles them for you:

- It leverages our dynamic DNS to perform automatic **load balancing** between servers.
- Its [**retry logic**](https://www.algolia.com/doc/guides/infrastructure/dsn/#retries-and-fallback-failover-logic) switches the targeted server whenever it detects that one of them is down or unreachable. Therefore, a given request will not fail unless all servers are down or unreachable at the same time.

**Note:** Application-level errors (e.g. invalid query) are still reported without retry.

## Error handling

Requests can fail for two main reasons:

1. **Network issues:** the server could not be reached, or did not answer within the timeout.
2. **Application error:** the server rejected the request.

In the latter case, the error reported by the API client contains:

- **message**: an error message indicating the cause of the error
- **status**: an HTTP status code indicating the type of error

Here's an example:

```json
{
  "message":"Invalid Application ID",
  "status":404
}
```

**Caution:** The error message is purely informational and intended for the developer. You should never rely on its content programmatically, as it may change without notice.




# JsonUtil

## Introduction

> Provides the client code with accessible Json manipulation and analysis without reliance on bean model classes, as commonly needed in Gson. This allows for convenient manipulation and interpretation of data dynamically, allowing similarly structured Jsons to be used with very different data (i.e.: converting a JsonArray to a CSV for loading into a database from a SharePoint REST call).

## Code Samples

### Printing a Json to a CSV
> Especially useful when trying to load data from a Json into a spreadsheet or database table, the utility can convert a Json array without a Java Bean defiining which members are within the Json.

#### No defined key sequence
> The most flexible, yet the most unpredictable method is by passing only the Json and a Writer instance (i.e.: StringWriter or FileWriter). This approach does not allow the client to define the order of columns within the CSV. Column order is determined only by the first element within the array (for considerations of runtime on large datasets) and headers are implicitly written. If the header is not wanted but you don't want to define the column sequence (perhaps you made the Json yourself or the Json is guaranteed to have consistent key order and structure), you must remove it yourself. In most instances, it is recommended to at least log the header and ensure you're getting the headers and the sequence you are expecting.
```Java
final StringWriter writer = new StringWriter();
JsonUtil.printToCsv(JsonUtil.toJson(SOME_JSON_ARRAY_STRING).asJsonArray(), writer);
 ```
> The Writer now has a CSV printed to it. The data is encapsulated with `\"` characters, regardless of if the data contains `\"`, and are separated by a `,`. Remember that the sequence of columns is typically unpredictable using this method. This approach is only recommended if the Json is garunteed to have all relevant keys within the first entry; if this is not the case, consider using `JsonUtil#printToCsvVerbose(JsonArray, Writer, boolean)`.

#### Key sequence is defned
> Defining the key sequence introduces reliability into the structure of the data. This ensures that columns are in consistent order between different executions or between different executions. Because the sequence is not determined by the data, not all data within the Json is garunteed to be represented in the CSV; this is useful when only certain data returned from a service is relevant.
```Java
final StringWriter writer = new StringWriter();
final String[] sequence = {"name","dob","phone"};
JsonUtil.printToCsv(JsonUtil.toJson(SOME_JSON_ARRAY_STRING).asJsonArray(), writer, sequence);
 ```
>By default, headers are no longer written when a sequence is passed. Headers may be written by using the overloaded method `JsonUtil#printToCsv(JsonArray, Writer, String[], boolean)`.
```Java
// Other code
JsonUtil.printToCsv(JsonUtil.toJson(SOME_JSON_ARRAY_STRING).asJsonArray(), writer, sequence, true);
 ```

#### Documentation
> See the `JsonUtil#printToCsv(JsonArray, Writer, ...)` JavaDoc, and the JUnit test cases for further explanation of expected use cases.


## Installation

> The installation instructions are low priority in the readme and should come at the bottom. The first part answers all their objections and now that they want to use it, show them how.

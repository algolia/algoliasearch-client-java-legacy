package com.algolia.search.integration;

import com.algolia.search.exceptions.AlgoliaException;
import com.algolia.search.AlgoliaObject;
import com.algolia.search.Index;
import com.algolia.search.inputs.BatchOperation;
import com.algolia.search.inputs.batch.BatchDeleteIndexOperation;
import com.algolia.search.objects.Query;
import com.algolia.search.responses.SearchResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchTest extends AlgoliaIntegrationTest {

  private static List<String> indicesNames = Arrays.asList(
    "index1"
  );

  @BeforeClass
  @AfterClass
  public static void cleanUp() throws AlgoliaException {
    List<BatchOperation> clean = indicesNames.stream().map(BatchDeleteIndexOperation::new).collect(Collectors.toList());
    client.batch(clean).waitForCompletion();
  }

  @Test
  public void search() throws AlgoliaException {
    Index<AlgoliaObject> index = client.initIndex("index1", AlgoliaObject.class);

    index
      .addObjects(Arrays.asList(
        new AlgoliaObject("algolia1", 1),
        new AlgoliaObject("algolia2", 1),
        new AlgoliaObject("toto", 1)
      ))
      .waitForCompletion();

    SearchResult<AlgoliaObject> search = index.search(new Query("algolia"));
    assertThat(search.getHits()).hasSize(2).extractingResultOf("getClass").containsOnly(AlgoliaObject.class);
  }

}

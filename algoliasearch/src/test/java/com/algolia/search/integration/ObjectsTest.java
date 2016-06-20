package com.algolia.search.integration;

import com.algolia.search.AlgoliaException;
import com.algolia.search.AlgoliaObject;
import com.algolia.search.AlgoliaObjectWithID;
import com.algolia.search.Index;
import com.algolia.search.inputs.BatchOperation;
import com.algolia.search.inputs.batch.BatchDeleteIndexOperation;
import org.junit.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectsTest extends AlgoliaIntegrationTest {

  private static List<String> indicesNames = Arrays.asList(
    "index1",
    "index2",
    "index3",
    "index4",
    "index5",
    "index6",
    "index7"
  );

  @BeforeClass
  @AfterClass
  public static void after() throws AlgoliaException {
    List<BatchOperation> clean = indicesNames.stream().map(BatchDeleteIndexOperation::new).collect(Collectors.toList());
    client.batch(clean).waitForCompletion();
  }

  @Test
  public void getAnObject() throws AlgoliaException {
    Index<AlgoliaObjectWithID> index = client.initIndex("index1", AlgoliaObjectWithID.class);
    AlgoliaObjectWithID objectWithID = new AlgoliaObjectWithID("1", "algolia", 4);
    index.addObject(objectWithID).waitForCompletion();

    Optional<AlgoliaObjectWithID> result = index.getObject("1");

    assertThat(objectWithID).isEqualToComparingFieldByField(result.get());
  }

  @Test
  public void getAnObjectWithId() throws AlgoliaException {
    Index<AlgoliaObject> index = client.initIndex("index2", AlgoliaObject.class);
    AlgoliaObject object = new AlgoliaObject("algolia", 4);
    index.addObject("2", object).waitForCompletion();

    Optional<AlgoliaObject> result = index.getObject("2");

    assertThat(object).isEqualToComparingFieldByField(result.get());
  }

  @Test
  public void addObjects() throws AlgoliaException {
    Index<AlgoliaObjectWithID> index = client.initIndex("index3", AlgoliaObjectWithID.class);
    List<AlgoliaObjectWithID> objectsWithID = Arrays.asList(
      new AlgoliaObjectWithID("1", "algolia", 4),
      new AlgoliaObjectWithID("2", "algolia", 4)
    );
    index.addObjects(objectsWithID).waitForCompletion();

    List<AlgoliaObjectWithID> objects = index.getObjects(Arrays.asList("1", "2"));

    assertThat(objects).extracting("objectID").containsOnly("1", "2");
  }

  @Test
  public void saveObject() throws AlgoliaException {
    Index<AlgoliaObject> index = client.initIndex("index4", AlgoliaObject.class);
    AlgoliaObject object = new AlgoliaObject("algolia", 4);

    index.addObject("1", object).waitForCompletion();

    index.saveObject("1", new AlgoliaObject("algolia", 5)).waitForCompletion();
    Optional<AlgoliaObject> result = index.getObject("1");

    assertThat(result.get()).isEqualToComparingFieldByField(new AlgoliaObject("algolia", 5));
  }

  @Test
  public void saveObjects() throws AlgoliaException {
    Index<AlgoliaObject> index = client.initIndex("index5", AlgoliaObject.class);
    AlgoliaObject obj1 = new AlgoliaObject("algolia1", 4);
    AlgoliaObject obj2 = new AlgoliaObject("algolia2", 4);

    index.addObject("1", obj1).waitForCompletion();
    index.addObject("2", obj2).waitForCompletion();

    index.saveObjects(Arrays.asList(
      new AlgoliaObjectWithID("1", "algolia1", 5),
      new AlgoliaObjectWithID("2", "algolia1", 5)
    )).waitForCompletion();

    Optional<AlgoliaObject> result = index.getObject("1");
    assertThat(result.get()).isEqualToComparingFieldByField(new AlgoliaObject("algolia1", 5));

    result = index.getObject("2");
    assertThat(result.get()).isEqualToComparingFieldByField(new AlgoliaObject("algolia1", 5));
  }

  @Test
  public void deleteObject() throws AlgoliaException {
    Index<AlgoliaObject> index = client.initIndex("index6", AlgoliaObject.class);
    AlgoliaObject object = new AlgoliaObject("algolia", 4);
    index.addObject("1", object).waitForCompletion();

    index.deleteObject("1").waitForCompletion();

    assertThat(index.getObject("1")).isEmpty();
  }

  @Test
  public void deleteObjects() throws AlgoliaException {
    Index<AlgoliaObject> index = client.initIndex("index7", AlgoliaObject.class);
    AlgoliaObject obj1 = new AlgoliaObject("algolia1", 4);
    AlgoliaObject obj2 = new AlgoliaObject("algolia2", 4);

    index.addObject("1", obj1).waitForCompletion();
    index.addObject("2", obj2).waitForCompletion();

    index.deleteObjects(Arrays.asList("1", "2")).waitForCompletion();

    assertThat(index.getObject("1")).isEmpty();
    assertThat(index.getObject("2")).isEmpty();
  }

}

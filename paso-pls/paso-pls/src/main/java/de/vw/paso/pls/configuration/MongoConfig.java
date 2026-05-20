package de.vw.paso.pls.configuration;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import de.vw.paso.pls.model.domain.CorruptedImport;
import de.vw.paso.pls.model.domain.ProductData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.beans.Introspector;

@Configuration
@EnableMongoRepositories(basePackages = "de.vw.paso.pls.repository")
public class MongoConfig {
  public static final String BUCKET = "bucket";
  public static final String ERROR_BUCKET = "errorBucket";

  @Bean(BUCKET)
  public GridFSBucket gridFSBucket(MongoTemplate template) throws Exception {
    final String bucketName = Introspector.decapitalize(ProductData.class.getSimpleName());
    return GridFSBuckets.create(template.getDb(), bucketName);
  }

  @Bean(ERROR_BUCKET)
  public GridFSBucket gridFSErrorBucket(MongoTemplate template) throws Exception {
    final String bucketName = Introspector.decapitalize(CorruptedImport.class.getSimpleName());
    return GridFSBuckets.create(template.getDb(), bucketName);
  }
}

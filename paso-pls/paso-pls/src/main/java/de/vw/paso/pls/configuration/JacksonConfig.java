package de.vw.paso.pls.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.PackageVersion;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.io.Serializable;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper(final Jackson2ObjectMapperBuilder builder) {
    final ObjectMapper objectMapper = builder.build();

    objectMapper.registerModule(new ObjectIdModule());

    return objectMapper;
  }

  @EqualsAndHashCode(callSuper = true)
  private static class ObjectIdModule extends Module implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String MODULE_NAME = "MongoObjectIdModule";

    @Override
    public String getModuleName() {
      return MODULE_NAME;
    }

    @Override
    public Version version() {
      return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(final SetupContext context) {
      final SimpleSerializers serializers = new SimpleSerializers();

      serializers.addSerializer(ObjectId.class, new ObjectIdSerializer());

      context.addSerializers(serializers);
    }

  }

  private static class ObjectIdSerializer extends StdSerializer<ObjectId> {

    ObjectIdSerializer() {
      this(ObjectId.class);
    }

    private ObjectIdSerializer(final Class<ObjectId> t) {
      super(t);
    }

    @Override
    public void serialize(
      final ObjectId value, final JsonGenerator gen, final SerializerProvider provider
    ) throws IOException {
      gen.writeString((value == null) ? null : value.toHexString());
    }

  }

}

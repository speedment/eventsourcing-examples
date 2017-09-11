package com.speedment.example.eventsourcing.schedule.event.generated;

import com.speedment.common.annotation.GeneratedCode;
import com.speedment.runtime.core.internal.AbstractApplicationMetadata;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link com.speedment.runtime.core.ApplicationMetadata} class for the {@link
 * com.speedment.runtime.config.Project} named booking_demo. This class contains
 * the meta data present at code generation time.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public class GeneratedBookingsMetadata extends AbstractApplicationMetadata {
    
    private final static String METADATA = init();
    
    private static String init() {
        final StringBuilder sb = new StringBuilder();
        initPart0(sb);
        initPart1(sb);
        return sb.toString();
    }
    
    @Override
    protected Optional<String> getMetadata() {
        return Optional.of(METADATA);
    }
    
    private static void initPart0(StringBuilder sb) {
        Stream.of(
            "{",
            "  \"config\" : {",
            "    \"expanded\" : true,",
            "    \"appId\" : \"2c5e779d-4fd2-4838-afa0-d8da039c4d5d\",",
            "    \"companyName\" : \"speedment\",",
            "    \"name\" : \"bookings\",",
            "    \"packageLocation\" : \"src/main/java/\",",
            "    \"id\" : \"booking_demo\",",
            "    \"packageName\" : \"com.speedment.example.eventsourcing.booking.event\",",
            "    \"dbmses\" : [",
            "      {",
            "        \"expanded\" : true,",
            "        \"port\" : 3306,",
            "        \"schemas\" : [",
            "          {",
            "            \"expanded\" : true,",
            "            \"tables\" : [",
            "              {",
            "                \"expanded\" : true,",
            "                \"primaryKeyColumns\" : [",
            "                  {",
            "                    \"expanded\" : true,",
            "                    \"name\" : \"id\",",
            "                    \"id\" : \"id\",",
            "                    \"ordinalPosition\" : 1",
            "                  }",
            "                ],",
            "                \"indexes\" : [",
            "                  {",
            "                    \"expanded\" : false,",
            "                    \"unique\" : true,",
            "                    \"name\" : \"PRIMARY\",",
            "                    \"indexColumns\" : [",
            "                      {",
            "                        \"orderType\" : \"ASC\",",
            "                        \"expanded\" : true,",
            "                        \"name\" : \"id\",",
            "                        \"id\" : \"id\",",
            "                        \"ordinalPosition\" : 1",
            "                      }",
            "                    ],",
            "                    \"id\" : \"PRIMARY\",",
            "                    \"enabled\" : true",
            "                  }",
            "                ],",
            "                \"isView\" : false,",
            "                \"columns\" : [",
            "                  {",
            "                    \"databaseType\" : \"java.lang.Long\",",
            "                    \"expanded\" : true,",
            "                    \"typeMapper\" : \"com.speedment.runtime.typemapper.primitive.PrimitiveTypeMapper\",",
            "                    \"nullable\" : false,",
            "                    \"autoIncrement\" : true,",
            "                    \"name\" : \"id\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"id\",",
            "                    \"ordinalPosition\" : 1,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.lang.Object\",",
            "                    \"expanded\" : true,",
            "                    \"typeMapper\" : \"com.speedment.runtime.typemapper.other.BinaryToUuidMapper\",",
            "                    \"nullable\" : false,",
            "                    \"autoIncrement\" : false,",
            "                    \"name\" : \"booking\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"booking\",",
            "                    \"ordinalPosition\" : 2,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.lang.String\",",
            "                    \"expanded\" : true,",
            "                    \"typeMapper\" : \"com.speedment.plugins.enums.StringToEnumTypeMapper\",",
            "                    \"nullable\" : false,",
            "                    \"enumConstants\" : \"BOOK,UPDATE,CANCEL\",",
            "                    \"autoIncrement\" : false,",
            "                    \"name\" : \"type\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"type\",",
            "                    \"ordinalPosition\" : 3,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.lang.Byte\",",
            "                    \"expanded\" : true,",
            "                    \"typeMapper\" : \"com.speedment.runtime.typemapper.primitive.PrimitiveTypeMapper\",",
            "                    \"nullable\" : false,",
            "                    \"autoIncrement\" : false,",
            "                    \"name\" : \"version\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"version\",",
            "                    \"ordinalPosition\" : 4,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.lang.Integer\",",
            "                    \"expanded\" : true,",
            "                    \"nullable\" : true,",
            "                    \"autoIncrement\" : false,"
        ).forEachOrdered(sb::append);
    }
    
    private static void initPart1(StringBuilder sb) {
        Stream.of(
            "                    \"name\" : \"userId\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"userId\",",
            "                    \"ordinalPosition\" : 5,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.lang.String\",",
            "                    \"expanded\" : true,",
            "                    \"nullable\" : true,",
            "                    \"autoIncrement\" : false,",
            "                    \"name\" : \"resource\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"resource\",",
            "                    \"ordinalPosition\" : 6,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.sql.Timestamp\",",
            "                    \"expanded\" : true,",
            "                    \"typeMapper\" : \"com.speedment.runtime.typemapper.time.TimestampToLocalDateTimeMapper\",",
            "                    \"nullable\" : true,",
            "                    \"autoIncrement\" : false,",
            "                    \"name\" : \"bookFrom\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"bookFrom\",",
            "                    \"ordinalPosition\" : 7,",
            "                    \"enabled\" : true",
            "                  },",
            "                  {",
            "                    \"databaseType\" : \"java.sql.Timestamp\",",
            "                    \"expanded\" : true,",
            "                    \"typeMapper\" : \"com.speedment.runtime.typemapper.time.TimestampToLocalDateTimeMapper\",",
            "                    \"nullable\" : true,",
            "                    \"autoIncrement\" : false,",
            "                    \"name\" : \"bookTo\",",
            "                    \"nullableImplementation\" : \"OPTIONAL\",",
            "                    \"id\" : \"bookTo\",",
            "                    \"ordinalPosition\" : 8,",
            "                    \"enabled\" : true",
            "                  }",
            "                ],",
            "                \"name\" : \"booking\",",
            "                \"alias\" : \"booking_event\",",
            "                \"id\" : \"booking\",",
            "                \"packageName\" : \"com.speedment.example.eventsourcing.booking.event.booking_event\",",
            "                \"enabled\" : true",
            "              }",
            "            ],",
            "            \"name\" : \"booking_demo\",",
            "            \"id\" : \"booking_demo\",",
            "            \"enabled\" : true",
            "          }",
            "        ],",
            "        \"typeName\" : \"MySQL\",",
            "        \"ipAddress\" : \"127.0.0.1\",",
            "        \"name\" : \"booking_demo\",",
            "        \"id\" : \"booking_demo\",",
            "        \"enabled\" : true,",
            "        \"username\" : \"root\"",
            "      }",
            "    ],",
            "    \"enabled\" : true",
            "  }",
            "}"
        ).forEachOrdered(sb::append);
    }
}
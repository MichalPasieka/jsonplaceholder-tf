package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.TestException;

import java.io.File;
import java.io.IOException;

public class JsonUtils {

    public static <T> T readObjectFromJson(String filePath, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(new File(filePath), valueType);
        } catch (IOException e) {
            throw new TestException(e);
        }
    }
}
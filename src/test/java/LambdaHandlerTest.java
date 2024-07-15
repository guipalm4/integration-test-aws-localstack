import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.TestcontainersExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SECRETSMANAGER;

@Testcontainers
@ExtendWith(TestcontainersExtension.class)
public class LambdaHandlerTest {
    
    @Container
    public static LocalStackContainer localStack = new LocalStackContainer("localstack/localstack:latest")
            .withServices(DYNAMODB, SECRETSMANAGER)
            .withExposedPorts(4566, 4571);
    
    private static LambdaHandler handler;
    
    @BeforeAll
    public static void setUp() {
        handler = new LambdaHandler();
    }
    
    @Test
    public void testLambdaHandler() {
        // Invoke Lambda function
        Map<String, Object> input = new HashMap<>();
        String result = handler.handleRequest(input, null);
        
        // Validate result
        assertEquals("Lambda executed successfully with secret: MySecretValue", result);
    }
}
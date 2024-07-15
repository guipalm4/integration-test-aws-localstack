
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.net.URI;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {
    
    private final DynamoDbClient dynamoDbClient;
    private final SecretsManagerClient secretsManagerClient;
    
    public LambdaHandler() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create("test", "test");
        
        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.US_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        
        this.secretsManagerClient = SecretsManagerClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.US_WEST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
    
    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        String tableName = "TestTable";
        String secretName = "TestSecret";
        
        // DynamoDB logic
        try {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("id").attributeType(ScalarAttributeType.S).build())
                    .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(1L).writeCapacityUnits(1L).build())
                    .build();
            dynamoDbClient.createTable(createTableRequest);
        } catch (ResourceInUseException e) {
            // Table already exists
        }
        
        // Secrets Manager logic
        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build();
        GetSecretValueResponse getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);
        
        return "Lambda executed successfully with secret: " + getSecretValueResponse.secretString();
    }
}
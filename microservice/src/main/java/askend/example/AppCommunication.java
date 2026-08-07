package askend.example;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.depended.communication")
@Data
public class AppCommunication {

    private List<String> hosts = new ArrayList<>();
}

package music.app.backend;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(title = "Music App API Docs",
                version = "1.0",
                description = "This is the official documentation of the API of Music App " +
                        "developed by Shankhadeep Ghoshal",
                license = @License(name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(email = "ghoshalshankhadeep@hotmail.com",
                        name = "Shankhadeep Ghoshal"))
)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}
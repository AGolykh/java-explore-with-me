package ru.practicum;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "\"Explore With Me\" Comments",
                description = "Documentation \"Explore With Me\" Comments API v1.0", version = "1.0",
                contact = @Contact(
                        name = "Golykh Aleksandr",
                        email = "a.golykh@yandex.ru"
                )
        )
)
public class OpenApiConfig {

}
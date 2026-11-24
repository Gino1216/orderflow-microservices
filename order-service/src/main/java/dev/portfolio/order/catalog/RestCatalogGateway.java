package dev.portfolio.order.catalog;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestCatalogGateway implements CatalogGateway {
    private final RestClient client;

    public RestCatalogGateway(RestClient.Builder builder,
                              @Value("${services.catalog.base-url}") String baseUrl) {
        this.client = builder.baseUrl(baseUrl).build();
    }

    @Override
    public ReservedProduct reserve(Long productId, int quantity) {
        try {
            return client.post()
                    .uri("/api/products/{id}/reservations", productId)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new CatalogReservationException(
                                "Catalog rejected the reservation with status " + response.getStatusCode());
                    })
                    .body(ReservedProduct.class);
        } catch (RestClientException exception) {
            throw new CatalogUnavailableException("Catalog service is unavailable", exception);
        }
    }
}

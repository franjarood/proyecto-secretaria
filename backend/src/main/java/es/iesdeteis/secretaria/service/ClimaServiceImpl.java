package es.iesdeteis.secretaria.service;

import es.iesdeteis.secretaria.dto.ClimaResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio para obtener información del clima desde OpenWeatherMap API.
 * Oculta la API key en el backend por seguridad.
 */
@Service
public class ClimaServiceImpl implements ClimaService {

    @Value("${openweather.api.key:CAMBIAR_POR_TU_API_KEY}")
    private String apiKey;

    @Value("${openweather.base-url:https://api.openweathermap.org/data/2.5/weather}")
    private String baseUrl;

    @Value("${openweather.lat}")
    private String latitud;

    @Value("${openweather.lon}")
    private String longitud;

    private final RestTemplate restTemplate;

    public ClimaServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene el clima actual de la ubicación configurada (IES de Teis)
     */
    @Override
    public ClimaResponseDTO obtenerClimaActual() {
        // Verificar si la API key está configurada
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("CAMBIAR_POR_TU_API_KEY")) {
            return crearRespuestaFallback();
        }

        try {
            // Construir URL con parámetros
            String url = baseUrl + "?lat=" + latitud + "&lon=" + longitud +
                    "&appid=" + apiKey + "&units=metric&lang=es";

            // Llamar a la API
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null) {
                return mapearRespuestaOpenWeather(response);
            } else {
                return crearRespuestaFallback();
            }

        } catch (Exception e) {
            System.err.println("Error al obtener clima de OpenWeather: " + e.getMessage());
            return crearRespuestaFallback();
        }
    }

    /**
     * Mapea la respuesta de OpenWeather a nuestro DTO
     */
    @SuppressWarnings("unchecked")
    private ClimaResponseDTO mapearRespuestaOpenWeather(Map<String, Object> response) {
        try {
            String ciudad = (String) response.get("name");

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            Double temperatura = null;
            if (main != null) {
                Object tempObj = main.get("temp");
                if (tempObj instanceof Number) {
                    temperatura = ((Number) tempObj).doubleValue();
                }
            }

            List<Map<String, Object>> weatherList = null;
            Object weatherObj = response.get("weather");
            if (weatherObj instanceof List) {
                weatherList = (List<Map<String, Object>>) weatherObj;
            }
            String descripcion = null;
            String iconoCode = null;

            if (weatherList != null && !weatherList.isEmpty() && weatherList.get(0) != null) {
                Map<String, Object> w0 = weatherList.get(0);

                Object descObj = w0.get("description");
                if (descObj != null) {
                    descripcion = String.valueOf(descObj);
                }

                Object iconObj = w0.get("icon");
                if (iconObj != null) {
                    iconoCode = String.valueOf(iconObj);
                }
            }

            String icono = mapearIconoAEmoji(iconoCode);

            return new ClimaResponseDTO(
                    ciudad != null ? ciudad : "Vigo",
                    temperatura,
                    descripcion != null ? capitalizar(descripcion) : "Información no disponible",
                    icono,
                    true
            );

        } catch (Exception e) {
            System.err.println("Error al mapear respuesta de OpenWeather: " + e.getMessage());
            return crearRespuestaFallback();
        }
    }

    /**
     * Crea una respuesta fallback cuando no hay API key o falla la llamada
     */
    private ClimaResponseDTO crearRespuestaFallback() {
        return new ClimaResponseDTO(
                "Vigo",
                null,
                "Clima no disponible",
                "✨",
                false
        );
    }

    /**
     * Mapea el código de icono de OpenWeather a emojis
     */
    private String mapearIconoAEmoji(String iconCode) {
        if (iconCode == null) return "🌤️";

        return switch (iconCode) {
            case "01d" -> "☀️";  // clear sky day
            case "01n" -> "🌙";  // clear sky night
            case "02d", "02n" -> "🌤️";  // few clouds
            case "03d", "03n" -> "☁️";  // scattered clouds
            case "04d", "04n" -> "☁️";  // broken clouds
            case "09d", "09n" -> "🌧️";  // shower rain
            case "10d", "10n" -> "🌦️";  // rain
            case "11d", "11n" -> "⛈️";  // thunderstorm
            case "13d", "13n" -> "❄️";  // snow
            case "50d", "50n" -> "🌫️";  // mist
            default -> "🌤️";
        };
    }

    /**
     * Capitaliza la primera letra de cada palabra
     */
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        String[] palabras = texto.split(" ");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)));
                if (palabra.length() > 1) {
                    resultado.append(palabra.substring(1).toLowerCase());
                }
                resultado.append(" ");
            }
        }

        return resultado.toString().trim();
    }
}


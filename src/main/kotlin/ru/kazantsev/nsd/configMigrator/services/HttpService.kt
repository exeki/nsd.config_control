package ru.kazantsev.nsd.configMigrator.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import ru.kazantsev.nsd.configMigrator.data.model.Installation


@Service
@Deprecated(message = "не нужон")
class HttpService {

    companion object {
        const val BASIC_REST_PATH = "sd/services/rest"
        const val BASIC_OPERATOR_PATH = "sd/operator"
    }

    @Bean
    fun restTemplate(objectMapper : ObjectMapper): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.messageConverters = listOf(MappingJackson2HttpMessageConverter(objectMapper))
        return restTemplate
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
    }

    fun getBasicRestUriBuilder (installation: Installation) : UriComponentsBuilder {
        return UriComponentsBuilder.newInstance()
            .scheme(installation.protocol)
            .host(installation.host)
            .pathSegment(BASIC_REST_PATH)
            .queryParam("accessKey", installation.accessKey)
    }

    fun getBasicModuleUriBuilder(installation: Installation, func : String, post : Boolean = false) : UriComponentsBuilder {
        return getBasicRestUriBuilder(installation)
            .pathSegment(if(post) "exec-post" else "exec")
            .queryParam("func", func)
            .queryParam("params", "request,response,user")
    }
}
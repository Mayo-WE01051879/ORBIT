package io.infinite.orbit.services

import groovy.transform.ToString
import groovy.util.logging.Slf4j
import io.infinite.blackbox.BlackBox
import io.infinite.carburetor.CarburetorLevel
import io.infinite.orbit.entities.Template
import io.infinite.orbit.model.TemplateSelectionData
import io.infinite.orbit.other.OrbitException
import io.infinite.orbit.other.TemplateTypes
import io.infinite.orbit.repositories.NamespaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@ToString(includeNames = true, includeFields = true)
@BlackBox(level = CarburetorLevel.METHOD)
@Slf4j
@Component
class TemplateService {

    @Autowired
    NamespaceRepository namespaceRepository

    String executeTemplate(TemplateSelectionData templateSelectionData, String namespace, TemplateTypes templateType, Map<String, String> templateValues) {
        Set<Template> templates = namespaceRepository.matchPriorityOne(
                templateSelectionData.templateName,
                namespace,
                templateType.value(),
                templateSelectionData.language
        )
        if (templates.isEmpty()) {
            templates = namespaceRepository.matchPriorityTwo(
                    templateSelectionData.templateName,
                    namespace,
                    templateType.value()
            )
            if (templates.isEmpty()) {
                throw new OrbitException("Template not found: ${templateType.value()} - $templateSelectionData")
            }
        }
        String result = templates.first().text
        templateValues.each { k, v ->
            result = result.replace("\${" + k + "}", v)
        }
        return result
    }

}

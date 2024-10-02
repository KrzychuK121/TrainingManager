package springweb.training_manager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CopyEntityService {
    private static final Logger logger = LoggerFactory.getLogger(CopyEntityService.class);

    public static <E> void copy(
        E source,
        E target
    ) {
        copy(source, target, (List<String>) null);
    }

    public static <E> void copy(
        E source,
        E target,
        String... ignoreFields
    ) {
        copy(source, target, List.of(ignoreFields));
    }

    public static <E> void copy(
        E source,
        E target,
        List<String> ignoredFields
    ) {
        var sourceClass = source.getClass();
        var targetClass = target.getClass();

        Map<String, Field> sourceFields = getFieldsMap(sourceClass, ignoredFields);
        Map<String, Field> targetFields = getFieldsMap(targetClass, ignoredFields);

        sourceFields.forEach(
            (fieldName, sourceField) -> {
                sourceField.setAccessible(true);
                try {
                    Object value = sourceField.get(source);
                    if (!targetFields.containsKey(fieldName))
                        return;
                    var targetField = targetFields.get(fieldName);
                    targetField.setAccessible(true);
                    if (!sourceField.getType().isInstance(targetField.getType()))
                        logger.warn(
                            "Field {} could not be initialized because types are not identical ({} != {}).",
                            fieldName,
                            sourceField.getType().toString(),
                            targetField.getType().toString()
                        );
                    else
                        sourceField.set(target, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    private static String getNormalizedFieldName(String fieldName) {
        var lastDotIndex = fieldName.lastIndexOf('.');
        return fieldName.substring(lastDotIndex + 1)
            .toLowerCase();
    }

    private static String getNormalizedFieldName(Field field) {
        return getNormalizedFieldName(field.getName());
    }

    private static Map<String, Field> getFieldsMap(
        Class<?> sourceClass,
        List<String> ignoredFields
    ) {
        Map<String, Field> fieldsMap = new HashMap<>();

        while (sourceClass != null) {
            fieldsMap.putAll(
                Arrays.stream(sourceClass.getDeclaredFields())
                    .collect(Collectors.toMap(
                        CopyEntityService::getNormalizedFieldName,
                        value -> value
                    ))
            );
            sourceClass = sourceClass.getSuperclass();
        }

        if (ignoredFields != null)
            filterFields(fieldsMap, ignoredFields);

        return fieldsMap;
    }

    private static void filterFields(
        Map<String, Field> fields,
        List<String> ignoredFields
    ) {
        HashSet<String> filterhashSet = ignoredFields.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toCollection(HashSet::new));

        filterhashSet.forEach(
            field -> {
                if (fields.containsKey(field))
                    fields.remove(getNormalizedFieldName(field));
            }
        );
    }
}

package springweb.training_manager.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CopyEntityService {

    public static <E> void copy(
        E source,
        E target
    ) {
        copy(source, target, null);
    }

    public static <E> void copy(
        E source,
        E target,
        List<String> ignoredFields
    ) {
        var sourceClass = source.getClass();
        var targetClass = target.getClass();

        if (!sourceClass.equals(targetClass))
            throw new IllegalArgumentException("Objects must be of the same type.");
        List<Field> fields = new ArrayList<>();

        while (sourceClass != null) {
            fields.addAll(
                List.of(sourceClass.getDeclaredFields())
            );
            sourceClass = sourceClass.getSuperclass();
        }

        if (ignoredFields != null)
            filterFields(fields, ignoredFields);

        fields.forEach(
            field -> {
                field.setAccessible(true);
                try {
                    Object value = field.get(source);
                    field.set(target, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        );
    }

    private static void filterFields(
        List<Field> fields,
        List<String> ignoredFields
    ) {
        HashSet<String> filterhashSet = ignoredFields.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toCollection(HashSet::new));

        fields.removeIf(
            field -> {
                var lastDotIndex = field.getName().lastIndexOf('.');
                var onlyFieldName = field.getName()
                    .substring(lastDotIndex + 1)
                    .toLowerCase();
                System.out.println(onlyFieldName);
                return filterhashSet.contains(
                    onlyFieldName
                );
            }
        );
    }
}

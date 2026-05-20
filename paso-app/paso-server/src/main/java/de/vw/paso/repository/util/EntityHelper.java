package de.vw.paso.repository.util;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityHelper {

    //	private static Map<Class<?>, String> classFieldsMap = new HashMap<>();

    public static Collection<Class> findTablesByName(@NotNull final EntityManager entityManager, final String pattern) {
        final Collection<Class> tables = new ArrayList<>();

        Table table;

        for (final EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
            table = entity.getJavaType().getAnnotation(Table.class);

            if (table.name().toLowerCase().matches(pattern)) {
                tables.add(entity.getJavaType());
            }
        }

        return tables;
    }

    //	public static String getDbColumnNames(final Class<?> clazz) {
    //		if (classFieldsMap.containsKey(clazz)) {
    //			return classFieldsMap.get(clazz);
    //		}
    //
    //		String dbColumnNames = "";
    //
    //		final List<Field> fields = getDeclaredAndInheritedFields(clazz, new ArrayList<>());
    //
    //		for (Field field : fields) {
    //			final Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
    //
    //			inner: for (final Annotation anno : declaredAnnotations) {
    //				if (anno.annotationType().getName().equals(Column.class.getName())) {
    //					if (dbColumnNames.equals("")) {
    //						dbColumnNames = ((Column) anno).name();
    //					} else {
    //						dbColumnNames = dbColumnNames + ", " + ((Column) anno).name();
    //					}
    //
    //					break inner;
    //				} else if (anno.annotationType().getName().equals(JoinColumn.class.getName())) {
    //					if (dbColumnNames.equals("")) {
    //						dbColumnNames = ((JoinColumn) anno).name();
    //					} else {
    //						dbColumnNames = dbColumnNames + ", " + ((JoinColumn) anno).name();
    //					}
    //
    //					break inner;
    //				}
    //			}
    //		}
    //
    //		classFieldsMap.put(clazz, dbColumnNames);
    //
    //		return dbColumnNames;
    //	}
    //
    //	private static List<Field> getDeclaredAndInheritedFields(final Class<?> clazz, List<Field> fields) {
    //		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    //
    //		final Class<?> superClass = clazz.getSuperclass();
    //
    //		if (superClass != null) {
    //			fields = getDeclaredAndInheritedFields(superClass, fields);
    //		}
    //
    //		return fields;
    //	}

}

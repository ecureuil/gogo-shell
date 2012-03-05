package org.ow2.chameleon.shell.gogo.internal.converter;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.service.command.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ow2.chameleon.shell.gogo.IConverterManager;
import org.ow2.chameleon.shell.gogo.TypeBasedConverter;
import org.ow2.chameleon.shell.gogo.internal.blueprint.GenericType;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Loris Bouzonnet
 */
@Component
@Provides
public class CollectionConverter implements TypeBasedConverter {

    private IConverterManager converterManager;

    @Bind
    public void bindConverterManager(IConverterManager converterManager) {
        this.converterManager = converterManager;
    }

    @Unbind
    public void unbindConverterManager(IConverterManager converterManager) {
        this.converterManager = null;
    }

    public Object convert(Class<?> toType, Object in) throws Exception {
        return convert(new GenericType(toType), in);
    }
    public Object convert(ReifiedType type, Object fromValue) throws Exception {
        if (toClass(type).isArray() && (fromValue instanceof Collection || fromValue.getClass().isArray())) {
            return convertToArray(fromValue, type);
        } else if (Map.class.isAssignableFrom(toClass(type)) && (fromValue instanceof Map || fromValue instanceof Dictionary)) {
            return convertToMap(fromValue, type);
        } else if (Dictionary.class.isAssignableFrom(toClass(type)) && (fromValue instanceof Map || fromValue instanceof Dictionary)) {
            return convertToDictionary(fromValue, type);
        } else if (Collection.class.isAssignableFrom(toClass(type)) && (fromValue instanceof Collection || fromValue.getClass().isArray())) {
            return convertToCollection(fromValue, type);
        }
        return null;
    }

    public CharSequence format(Object o, int i, Converter converter) throws Exception {
        return null;
    }

    private Object convertToCollection(Object obj, ReifiedType type) throws Exception {
        ReifiedType valueType = type.getActualTypeArgument(0);
        Collection newCol = (Collection) getCollection(toClass(type)).newInstance();
        if (obj.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj); i++) {
                try {
                    newCol.add(converterManager.convert(valueType, Array.get(obj, i)));
                } catch (Exception t) {
                    throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting array element)", t);
                }
            }
        } else {
            for (Object item : (Collection) obj) {
                try {
                    newCol.add(convert(valueType, item));
                } catch (Exception t) {
                    throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting collection entry)", t);
                }
            }
        }
        return newCol;
    }

    private Object convertToDictionary(Object obj, ReifiedType type) throws Exception {
        ReifiedType keyType = type.getActualTypeArgument(0);
        ReifiedType valueType = type.getActualTypeArgument(1);
        Dictionary newDic = new Hashtable();
        if (obj instanceof Dictionary) {
            Dictionary dic = (Dictionary) obj;
            for (Enumeration keyEnum = dic.keys(); keyEnum.hasMoreElements();) {
                Object key = keyEnum.nextElement();
                try {
                    newDic.put(converterManager.convert(keyType, key), converterManager.convert(valueType, dic.get(key)));
                } catch (Exception t) {
                    throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting map entry)", t);
                }
            }
        } else {
            for (Map.Entry e : ((Map<Object,Object>) obj).entrySet()) {
                try {
                    newDic.put(converterManager.convert(keyType, e.getKey()), converterManager.convert(valueType, e.getValue()));
                } catch (Exception t) {
                    throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting map entry)", t);
                }
            }
        }
        return newDic;
    }

    private Object convertToMap(Object obj, ReifiedType type) throws Exception {
        ReifiedType keyType = type.getActualTypeArgument(0);
        ReifiedType valueType = type.getActualTypeArgument(1);
        Map newMap = (Map) getMap(toClass(type)).newInstance();
        if (obj instanceof Dictionary) {
            Dictionary dic = (Dictionary) obj;
            for (Enumeration keyEnum = dic.keys(); keyEnum.hasMoreElements();) {
                Object key = keyEnum.nextElement();
                try {
                    newMap.put(converterManager.convert(keyType, key), converterManager.convert(valueType, dic.get(key)));
                } catch (Exception t) {
                    throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting map entry)", t);
                }
            }
        } else {
            for (Map.Entry e : ((Map<Object,Object>) obj).entrySet()) {
                try {
                    newMap.put(converterManager.convert(keyType, e.getKey()), converterManager.convert(valueType, e.getValue()));
                } catch (Exception t) {
                    throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting map entry)", t);
                }
            }
        }
        return newMap;
    }

    private Object convertToArray(Object obj, ReifiedType type) throws Exception {
        if (obj instanceof Collection) {
            obj = ((Collection) obj).toArray();
        }
        if (!obj.getClass().isArray()) {
            throw new Exception("Unable to convert from " + obj + " to " + type);
        }
        ReifiedType componentType;
        if (type.size() > 0) {
            componentType = type.getActualTypeArgument(0);
        } else {
            componentType = new GenericType(type.getRawClass().getComponentType());
        }
        Object array = Array.newInstance(toClass(componentType), Array.getLength(obj));
        for (int i = 0; i < Array.getLength(obj); i++) {
            try {
                Array.set(array, i, converterManager.convert(componentType, Array.get(obj, i)));
            } catch (Exception t) {
                throw new Exception("Unable to convert from " + obj + " to " + type + "(error converting array element)", t);
            }
        }
        return array;
    }


    private static boolean hasDefaultConstructor(Class type) {
        if (!Modifier.isPublic(type.getModifiers())) {
            return false;
        }
        if (Modifier.isAbstract(type.getModifiers())) {
            return false;
        }
        Constructor[] constructors = type.getConstructors();
        for (Constructor constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers()) &&
                    constructor.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }

    private static Class getMap(Class<?> type) {
        if (hasDefaultConstructor(type)) {
            return type;
        } else if (SortedMap.class.isAssignableFrom(type)) {
            return TreeMap.class;
        } else if (ConcurrentMap.class.isAssignableFrom(type)) {
            return ConcurrentHashMap.class;
        } else {
            return LinkedHashMap.class;
        }
    }

    private static Class getCollection(Class<?> type) {
        if (hasDefaultConstructor(type)) {
            return type;
        } else if (SortedSet.class.isAssignableFrom(type)) {
            return TreeSet.class;
        } else if (Set.class.isAssignableFrom(type)) {
            return LinkedHashSet.class;
        } else if (List.class.isAssignableFrom(type)) {
            return ArrayList.class;
        } else if (Queue.class.isAssignableFrom(type)) {
            return LinkedList.class;
        } else {
            return ArrayList.class;
        }
    }

    private Class toClass(ReifiedType type) {
        return type.getRawClass();
    }
}

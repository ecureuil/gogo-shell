package org.ow2.chameleon.shell.gogo.test.converter;

import org.apache.felix.service.command.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.blueprint.container.ReifiedType;
import org.ow2.chameleon.shell.gogo.internal.blueprint.GenericType;
import org.ow2.chameleon.shell.gogo.internal.converter.CollectionConverter;
import org.ow2.chameleon.shell.gogo.internal.converter.ConverterManagerImpl;
import org.ow2.chameleon.shell.gogo.internal.converter.NumberConverter;
import org.ow2.chameleon.shell.gogo.internal.converter.StringConverter;
import org.springframework.osgi.mock.MockBundleContext;
import org.springframework.osgi.mock.MockServiceReference;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Loris Bouzonnet
 */
public class CollectionConverterTestCase {

    private ConverterManagerImpl converterManager;

    List<Dictionary<Float, Class<?>>> result = new ArrayList<Dictionary<Float, Class<?>>>();

    @BeforeClass
    public void setUp() throws InvalidSyntaxException {
        BundleContext bundleContext = new MyMockBundleContext();
        converterManager = new ConverterManagerImpl(bundleContext);

        StringConverter stringConverter = new StringConverter();
        Properties stringConvProps = new Properties();
        stringConvProps.put(Converter.CONVERTER_CLASSES, String.class.getName());
        bundleContext.registerService(Converter.class.getName(), stringConverter, stringConvProps);

        NumberConverter nbConverter = new NumberConverter();
        Properties nbConvProps = new Properties();
        nbConvProps.put(Converter.CONVERTER_CLASSES, Number.class.getName());
        bundleContext.registerService(Converter.class.getName(), nbConverter, nbConvProps);

        CollectionConverter collectionConverter = new CollectionConverter();
        collectionConverter.bindConverterManager(converterManager);
        bundleContext.registerService(Converter.class.getName(), collectionConverter, null);

        ServiceReference[] refs = bundleContext.getServiceReferences(Converter.class.getName(), null);
        Assert.assertEquals(refs.length, 3);
        for (ServiceReference ref : refs) {
            converterManager.addConverter(ref);
        }
    }

    @Test
    public void testConvertString() throws Exception {
        Assert.assertEquals(converterManager.convert(Integer.class, "42"), Integer.valueOf(42), "String to Integer");

        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(42, Integer.class.getName());
        Object[] in = new Object[] {map};

        Field field = CollectionConverterTestCase.class.getDeclaredField("result");
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        ReifiedType reifiedType = new GenericType(listType);

        Hashtable<Float, Class<?>> hasht = new Hashtable<Float, Class<?>>();
        hasht.put(42f, Integer.class);
        result.add(hasht);

        Assert.assertEquals(converterManager.convert(reifiedType, in), result, "Array to List");
    }

    private static class MyMockBundleContext extends MockBundleContext {

        private final Map<ServiceReference, Object> serviceForRef = new HashMap<ServiceReference, Object>();

        @Override
        public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
            return serviceForRef.keySet().toArray(new ServiceReference[serviceForRef.size()]);
        }

        @Override
        public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
            MockServiceReference ref = new MockServiceReference();
            serviceForRef.put(ref, service);
            return super.registerService(clazzes, service, properties);
        }

        @Override
        public Object getService(ServiceReference reference) {
            return serviceForRef.get(reference);
        }
    }
}

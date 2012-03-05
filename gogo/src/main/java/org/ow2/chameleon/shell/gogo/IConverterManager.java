package org.ow2.chameleon.shell.gogo;

import org.osgi.service.blueprint.container.ReifiedType;

import java.lang.reflect.Type;

/**
 * @author Loris Bouzonnet
 */
public interface IConverterManager {

    Object convert(Type desiredType, Object in) throws Exception;

    Object convert(ReifiedType desiredType, Object in) throws Exception;

}

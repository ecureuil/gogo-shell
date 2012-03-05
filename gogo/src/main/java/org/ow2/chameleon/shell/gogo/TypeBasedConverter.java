package org.ow2.chameleon.shell.gogo;

import org.apache.felix.service.command.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

import java.lang.reflect.Type;

/**
 * @author Loris Bouzonnet
 */
public interface TypeBasedConverter extends Converter {

    Object convert(ReifiedType desiredType, Object in) throws Exception;
}

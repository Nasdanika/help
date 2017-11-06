package org.nasdanika.help.markdown;

import java.util.Map;

public interface WikiLinkResolver {

	String resolve(String spec, Map<Object, Object> environment); 
	
}

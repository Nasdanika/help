package org.nasdanika.help.markdown;

import java.util.Map;

public interface WikiLinkResolver {

	String resolve(String spec, String docRoutePath, Map<Object, Object> environment); 
	
}

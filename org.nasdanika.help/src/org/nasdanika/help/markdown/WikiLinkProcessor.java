package org.nasdanika.help.markdown;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pegdown.LinkRenderer.Rendering;

/**
 * Parses wiki link ([[...]]) in format <code>renderer(config):resolver>location|text</code>, e.g.
 * <UL>
 * <LI><code>[[global>urn:org.nasdanika.cdo.security/Realm]]</code> - resolver is 'global' - resolves a link to Realm EClass in 
 * a package with NsURI urn:org.nasdanika.cdo.security in the global package registry.</LI>
 * <LI><code>[[global>urn:org.nasdanika.cdo.security/Realm|Realm package]]</code> - same as above with link text 'Realm package' 
 * <LI><code>[[lightbox:someimage.png]]</code> - renders lightbox link to an image located in the same directory with the source page.  
 * </UL>
 * Use backslash to escape control characters - &gt; and |.
 * All parts are optional.
 * @author Pavel
 *
 */
public class WikiLinkProcessor {
	
	public interface Renderer {
		
		interface Registry {
			
			Renderer getRenderer(String name);
			
		}
		
		Rendering render(
				String href, 
				String content, 
				String config,
				boolean isMissing);
		
	}
	
	public interface Resolver {
		
		interface Registry {
			
			Resolver getResolver(String name);
			
		}
		
		String resolve (String href);
		
	}
	
	/**
	 * Provides information about internal links.
	 * @author Pavel
	 *
	 */
	public interface LinkInfo {
		
		interface Registry {
			
			LinkInfo getLinkInfo(String location);
			
		}
		
		String getIconTag();
		
		String getLabel();
		
		boolean isMissing();
		
	}
		
	private Renderer.Registry rendererRegistry;
	private Resolver.Registry resolverRegistry;
	private LinkInfo.Registry linkRegistry;
		
	public WikiLinkProcessor(
			Renderer.Registry rendererRegistry, 
			Resolver.Registry resolverRegistry,
			LinkInfo.Registry linkRegistry) {
		this.rendererRegistry = rendererRegistry;
		this.resolverRegistry = resolverRegistry;
		this.linkRegistry = linkRegistry;
	}
	
	public Rendering wikiLinkToRendering(String wikiLink) {
		Renderer renderer = null;
		String rendererConfig = null;
		int colonIdx = DocUtil.indexOf(wikiLink, 0, ':');
		if (colonIdx!=-1) {
			String rendererSpec = wikiLink.substring(0, colonIdx);
			int lParIdx = DocUtil.indexOf(rendererSpec, 0, '(');
			if (lParIdx==-1) {
				renderer = rendererRegistry.getRenderer(DocUtil.unescape(rendererSpec));
			} else {
				int rParIdx = DocUtil.indexOf(rendererSpec, lParIdx, ')');
				if (rParIdx!=-1) {
					rendererConfig = DocUtil.unescape(rendererSpec.substring(lParIdx+1, rParIdx));
					renderer = rendererRegistry==null ? null : rendererRegistry.getRenderer(DocUtil.unescape(rendererSpec.substring(0, lParIdx)));					
				}
			}
			if (renderer==null) {
				colonIdx = -1; // Not a renderer spec, but perhaps URL, e.g. http://
			}
		}
		
		Resolver resolver = null;
		int gtIdx = DocUtil.indexOf(wikiLink, colonIdx+1, '>');
		if (gtIdx!=-1) {			
			resolver = resolverRegistry==null ? null : resolverRegistry.getResolver(DocUtil.unescape(wikiLink.substring(colonIdx+1, gtIdx)));
			if (resolver==null) {
				gtIdx = -1;
			}
		}	
		if (gtIdx==-1) {
			gtIdx = colonIdx;
		}
		
		String href;
		String text = null;
		int pipeIdx = DocUtil.indexOf(wikiLink, gtIdx+1, '|');
		if (pipeIdx==-1) {
			href = DocUtil.unescape(wikiLink.substring(gtIdx+1));	
		} else {
			href = DocUtil.unescape(wikiLink.substring(gtIdx+1, pipeIdx));
			text = wikiLink.substring(pipeIdx+1);
		}
		if (resolver!=null) {
			href = resolver.resolve(href);
		}
		LinkInfo linkInfo = linkRegistry==null || href==null ? null : linkRegistry.getLinkInfo(href);
		if (DocUtil.isBlank(text)) {
			if (linkInfo!=null) {
				text = linkInfo.getLabel(); 
			}
			if (DocUtil.isBlank(text)) {
				text = href;
				if (DocUtil.isBlank(text)) {
					text = wikiLink;
				} else {
					int slashIdx = text.lastIndexOf('/');
					if (slashIdx!=-1) {
						text = text.substring(slashIdx+1);				
					}
					int dotIdx = text.lastIndexOf('.');
					if (dotIdx!=-1) {
						text = text.substring(0, dotIdx);
					}
					text = text.replace('_', ' ');
				}
			}
		}
		
		boolean isMissing = href==null || (linkInfo!=null && linkInfo.isMissing());
		String iconTag = linkInfo==null ? null : linkInfo.getIconTag();
		String linkContent = (DocUtil.isBlank(iconTag) ? "" : iconTag) + StringEscapeUtils.escapeHtml4(text);
		
		if (renderer==null && resolver instanceof Renderer) {
			renderer = (Renderer) resolver;
		}
		
		if (renderer==null) {
			Rendering ret = new Rendering(href==null ? "#" : href, linkContent);
			if (isMissing) {
				ret.withAttribute("style", "color:red;border-bottom:1px dashed");
			}
			return ret;
		}
		
		return renderer.render(href, linkContent, rendererConfig, isMissing);
	}
	
}
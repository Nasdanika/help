package org.nasdanika.help.markdown;

import static org.pegdown.FastEncoder.encode;

import java.net.URI;

import org.apache.commons.lang3.StringEscapeUtils;
import org.nasdanika.help.markdown.WikiLinkProcessor.LinkInfo;
import org.nasdanika.help.markdown.WikiLinkProcessor.Resolver;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.WikiLinkNode;

public class MarkdownLinkRenderer extends LinkRenderer {

	private WikiLinkProcessor wikiLinkProcessor;

	public MarkdownLinkRenderer() {
		this.wikiLinkProcessor = new WikiLinkProcessor(
				getRendererRegistry(), 
				getResolverRegistry(), 
				getLinkRegistry());
	}
	
	protected Rendering createRendering(String href, String text, String title, boolean rewriteURL) {
		if (href!=null && getResolverRegistry()!=null) {
			int idx = href.indexOf(':');
			if (idx!=-1) {
				Resolver resolver = getResolverRegistry().getResolver(href.substring(0, idx));
				if (resolver!=null) {
					String newHref = resolver.resolve(href.substring(idx+1));
					if (newHref!=null) {
						href = newHref;
					}
				}
			}
		}
		LinkInfo linkInfo = getLinkRegistry()==null || href==null ? null : getLinkRegistry().getLinkInfo(href);
		if (DocUtil.isBlank(text)) {
			if (linkInfo!=null) {
				text = linkInfo.getLabel(); 
			}
			if (DocUtil.isBlank(text)) {
				text = href;
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
		
		if (href!=null) {
			if (rewriteURL && getURLRewriter()!=null) {
				href = getURLRewriter().rewrite(href);
			} else if (getBaseURI() != null) {
				href = getBaseURI().resolve(href).toString();
			}
		}		
		
		boolean isMissing = href==null || (linkInfo!=null && linkInfo.isMissing());
		String iconTag = linkInfo==null ? null : linkInfo.getIconTag();
		String linkContent = (DocUtil.isBlank(iconTag) ? "" : iconTag) + StringEscapeUtils.escapeHtml4(text);
		
		Rendering ret = new Rendering(href==null ? "#" : href, linkContent);
		
		if (isMissing) {
			ret.withAttribute("style", "color:red;border-bottom:1px dashed");
		}
		
		String target = getLinkTarget(href);
        if (!DocUtil.isBlank(target)) {
        	ret.withAttribute("target", target);
        }
        
        if (!DocUtil.isBlank(title)) {
        	ret.withAttribute("title", encode(title));
        }
		
        return ret;		
	}
		
    protected URLRewriter getURLRewriter() {
		return null;
	}

	public Rendering render(ExpLinkNode node, String text) {
    	return createRendering(node.url, text, node.title, true);
    }

    public Rendering render(ExpImageNode node, String text) {
    	return createRendering(node.url, text, node.title, false);
    }

    public Rendering render(RefLinkNode node, String url, String title, String text) {
    	return createRendering(url, text, title, true);
    }

    public Rendering render(RefImageNode node, String url, String title, String alt) {
    	return createRendering(url, alt, title, false);
    }

    public Rendering render(WikiLinkNode node) {
    	return wikiLinkProcessor.wikiLinkToRendering(node.getText());
    }
    	
	/**
	 * Subclasses can override this method to add "target" attribute to links. 
	 * @param href
	 * @return
	 */
	protected String getLinkTarget(String href) {
		return null;
	}

	protected WikiLinkProcessor.Resolver.Registry getResolverRegistry() {
		return null;
	}

	protected WikiLinkProcessor.Renderer.Registry getRendererRegistry() {
		return null;
	}

	protected WikiLinkProcessor.LinkInfo.Registry getLinkRegistry() {
		return null;
	}

	protected URI getBaseURI() {
		return null;
	}
		
//	/**
//	 * Subclasses can override this method to look up wiki link icon.
//	 * @param href
//	 * @return
//	 */
//	protected String linkIcon(String href) {
//		return null;
//	}
//
//	
//	protected String resolve(String resolver, String url) {
//		if ("url".equals(resolver)) {
//			return url;
//		}
//		if ("bundle".equals(resolver)) {
//			return "bundle://"+url;
//		}
//		return resolver+">"+url;
//	}
//	
//	/**
//	 * Subclasses can override this method to render broken links in red
//	 * @param href
//	 * @return
//	 */
//	protected boolean isMissing(String href) {
//		return false;
//	}
	
}



package org.nasdanika.help.markdown;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.help.IHelpContentProducer;
import org.osgi.framework.Bundle;
import org.pegdown.Extensions;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;

/**
 * Serves file.md.html references by converting file.md to HTML if file.md is present.
 * @author Pavel
 *
 */
public class MarkdownContentProducer implements IHelpContentProducer {
	
	public static final int MARKDOWN_OPTIONS = 	Extensions.ALL ^ Extensions.HARDWRAPS ^ Extensions.SUPPRESS_HTML_BLOCKS ^ Extensions.SUPPRESS_ALL_HTML ^ Extensions.ANCHORLINKS;
	
	private PegDownProcessor pegDownProcessor = new PegDownProcessor(MARKDOWN_OPTIONS);

	@Override
	public InputStream getInputStream(String pluginID, String href, Locale locale) {
		int idx = href.indexOf("?");
		if (idx != -1) {
			href = href.substring(0, idx);			
		}
		if (href.endsWith(".md.html")) {
			Bundle bundle = Platform.getBundle(pluginID);
			URL mdEntry = bundle.getEntry(href.substring(0, href.length()-".html".length()));
			if (mdEntry != null) {
				try (Reader r = new BufferedReader(new InputStreamReader(mdEntry.openStream()))) {
					StringWriter sw = new StringWriter();
					int ch;
					while ((ch = r.read()) != -1) {
						sw.write(ch);
					}
					sw.close();					
					String html = pegDownProcessor.markdownToHtml(preProcessMarkdown(sw.toString(), pluginID, href, locale), createMarkdownLinkRenderer(pluginID, href, locale));
					return new ByteArrayInputStream(html.getBytes());
				} catch (Exception e) {
					return new ByteArrayInputStream(("Exception: "+e).getBytes());
				}
			}
		}
		return null;
	}
	
	protected LinkRenderer createMarkdownLinkRenderer(String pluginID, String href, Locale locale) {
		return new LinkRenderer();
	}
	
	protected String preProcessMarkdown(String source, String pluginID, String href, Locale locale) {
		return source;
	}

}

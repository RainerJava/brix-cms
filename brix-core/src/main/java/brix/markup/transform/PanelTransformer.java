package brix.markup.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import brix.markup.MarkupSource;
import brix.markup.tag.Item;
import brix.markup.tag.Tag;
import brix.markup.tag.simple.SimpleTag;

/**
 * Transformer that makes the markup usable with wicket panel. All &lt;head&gt;
 * sections are grouped in one &lt;wicket:head&gt; section and the rest of
 * markup (except for the &lt;html&gt;tag if present) is grouped in a
 * &lt;wicket:panel&gt; section.
 * 
 * Also removes all &lt;body&gt; and &lt;wicket:panel&gt; tags in markup.
 * 
 * @author Matej Knopp
 */
public class PanelTransformer extends HeadTransformer
{
	public PanelTransformer(MarkupSource delegate)
	{
		super(delegate);
	}

	private boolean shouldFilter(String tagName)
	{
		return "html".equals(tagName) || "body".equals(tagName) || "wicket:panel".equals(tagName);
	}

	private List<Item> filter(List<Item> items)
	{
		List<Item> result = new ArrayList<Item>();

		for (Item i : items)
		{
			if (i instanceof Tag)
			{
				Tag tag = (Tag) i;
				if (shouldFilter(tag.getName()))
				{
					continue;
				}
			}
			result.add(i);
		}

		return result;
	}

	@Override
	protected List<Item> transform(List<Item> originalItems)
	{
		List<Item> headContent = extractHeadContent(originalItems);
		List<Item> body = filter(transform(originalItems, null));

		Map<String, String> emptyMap = Collections.emptyMap();
		List<Item> result = new ArrayList<Item>();

		result.add(new SimpleTag("wicket:head", Tag.Type.OPEN, emptyMap));
		result.addAll(headContent);
		result.add(new SimpleTag("wicket:head", Tag.Type.CLOSE, emptyMap));

		result.add(new SimpleTag("wicket:panel", Tag.Type.OPEN, emptyMap));
		result.addAll(body);
		result.add(new SimpleTag("wicket:panel", Tag.Type.CLOSE, emptyMap));

		return result;
	}
	
	@Override
	public String getDoctype()
	{
		return null;
	}
}

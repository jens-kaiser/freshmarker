package org.freshmarker.core.output;

import org.freshmarker.core.model.primitive.TemplateString;

public class HtmlOutputFormat implements OutputFormat {

  private final String apos;

  public HtmlOutputFormat(String apos) {
    this.apos = apos;
  }

  public static final HtmlOutputFormat HTML = new HtmlOutputFormat("&#39;");
  public static final HtmlOutputFormat XML = new HtmlOutputFormat("&apos;");

  @Override
  public TemplateString escape(String value) {
    StringBuilder builder = new StringBuilder();
    for (char c : value.toCharArray()) {
      switch (c) {
        case '<':
          builder.append("&lt;");
          break;
        case '>':
          builder.append("&gt;");
          break;
        case '&':
          builder.append("&amp;");
          break;
        case '"':
          builder.append("&quot;");
          break;
        case '\'':
          builder.append("&#39;");
          break;
        default:
          builder.append(c);
      }
    }
    return new TemplateString(builder.toString());
  }
}

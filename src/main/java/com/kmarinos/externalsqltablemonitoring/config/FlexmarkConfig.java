package com.kmarinos.externalsqltablemonitoring.config;

import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension;
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FlexmarkConfig {

  private final Parser parser;
  private final HtmlRenderer renderer;
  public FlexmarkConfig(){
    MutableDataSet options = new MutableDataSet();
    options.set(Parser.EXTENSIONS,
        Arrays.asList(TablesExtension.create(),
            StrikethroughSubscriptExtension.create(),
            AttributesExtension.create(),
            EmojiExtension.create(),
            FootnoteExtension.create(),
            SuperscriptExtension.create()));

    options.set(HtmlRenderer.SOFT_BREAK,"<br />\n");
    options.set(Parser.CODE_SOFT_LINE_BREAKS, true);

    this.parser = Parser.builder(options).build();
    this.renderer = HtmlRenderer.builder(options).build();
  }
  @Bean
  public Parser MDParser(){return parser;}
  @Bean
  public HtmlRenderer HtmlRenderer(){return this.renderer;}
}

import org.example.common.PageItems;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.example.common.ProcessResult;
import org.example.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class NewsParser extends Parser {
    public NewsParser() {
        super("newParser");
    }

    @Override
    public void process(PageResponse pageResponse, ProcessResult processResult) {
        String rawText = pageResponse.getRawTextFromBody();
        Document document = Jsoup.parse(rawText, "UTF-8");
        Element element = document.getElementsByClass("hotnews").get(0);
        Elements elements = element.getElementsByTag("a");

        List<String> urls = elements.stream().map(s -> s.attr("href")).collect(Collectors.toList());

        for (String url : urls) {
            processResult.add(PageRequest.url(url, "hotNewsParser"));
        }
    }

    @Override
    public void onException(PageResponse pageResponse, Throwable throwable) {

    }
}

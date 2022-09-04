import org.example.common.PageItems;
import org.example.common.PageResponse;
import org.example.common.ProcessResult;
import org.example.parser.Parser;

public class HotNewsParser extends Parser {
    public HotNewsParser() {
        super("hotNewsParser");
    }

    @Override
    public void process(PageResponse pageResponse, ProcessResult processResult) {
        String rawText = pageResponse.getRawTextFromBody();
        processResult.add(PageItems.byName("newsPipeline").item("rawText", rawText));
    }

    @Override
    public void onException(PageResponse pageResponse, Throwable throwable) {

    }
}

import org.example.common.PageItems;
import org.example.pipeline.Pipeline;

public class NewsPipeline extends Pipeline {
    public NewsPipeline() {
        super("newsPipeline");
    }

    @Override
    public void process(PageItems pageItems) {
        String rawText = pageItems.get("rawText");
        System.out.println(rawText);
    }

    @Override
    public void onException(PageItems pageItems, Throwable throwable) {

    }
}

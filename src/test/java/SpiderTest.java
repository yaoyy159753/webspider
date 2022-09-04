import org.example.common.PageRequest;
import org.example.engine.Spider;

public class SpiderTest {

    public static void main(String[] args) throws InterruptedException {
        Spider spider = Spider.builder().addParser(new NewsParser()).addPipeline(new NewsPipeline()).addParser(new HotNewsParser())
                .start();
        PageRequest pageRequest = PageRequest.url("https://news.baidu.com/", "newParser");
        spider.addTask(pageRequest);
        Thread.sleep(5000);
        spider.shutdown();
    }

}
